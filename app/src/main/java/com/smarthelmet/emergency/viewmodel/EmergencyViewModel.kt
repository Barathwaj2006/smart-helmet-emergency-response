package com.smarthelmet.emergency.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.smarthelmet.emergency.data.local.AppDatabase
import com.smarthelmet.emergency.data.local.ProfileRepository
import com.smarthelmet.emergency.data.model.EventStatus
import com.smarthelmet.emergency.data.model.IncidentEntity
import com.smarthelmet.emergency.data.model.RiderProfile
import com.smarthelmet.emergency.data.model.TimelineEvent
import com.smarthelmet.emergency.data.model.TimelineStage
import com.smarthelmet.emergency.service.EmergencyAlertMessage
import com.smarthelmet.emergency.service.EmergencyAlertSoundAndVibrator
import com.smarthelmet.emergency.service.EmergencyCommunicationManager
import com.smarthelmet.emergency.service.EmergencyNotificationHelper
import com.smarthelmet.emergency.service.HardwareConnectionState
import com.smarthelmet.emergency.service.HardwareIntegrationSimulator
import com.smarthelmet.emergency.service.HelmetHardwareListener
import com.smarthelmet.emergency.service.LocationResult
import com.smarthelmet.emergency.service.LocationServiceHelper
import com.smarthelmet.emergency.service.NearestHospital
import com.smarthelmet.emergency.service.RecipientStatus
import android.location.LocationListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class EmergencyPhase {
    IDLE,
    COUNTDOWN_RUNNING,
    CANCELLED,
    RESPONSE_DISPATCHED
}

data class IncidentState(
    val incidentId: String = "",
    val timestampMillis: Long = 0L,
    val formattedTime: String = "",
    val phase: EmergencyPhase = EmergencyPhase.IDLE,
    val countdownRemaining: Int = 5,
    val countdownTotal: Int = 5,
    val isAlarmActive: Boolean = false,
    val cancellationReason: String? = null,
    val cancellationTimestampMillis: Long? = null,
    val location: LocationResult? = null,
    val alertMessage: EmergencyAlertMessage? = null,
    val recipients: List<RecipientStatus> = emptyList(),
    val timelineEvents: List<TimelineEvent> = emptyList(),
    val isDemoMode: Boolean = true
)

class EmergencyViewModel(application: Application) : AndroidViewModel(application), HelmetHardwareListener {

    private val profileRepo = ProfileRepository(application)
    private val database = AppDatabase.getDatabase(application)
    private val incidentDao = database.incidentDao()

    val locationHelper = LocationServiceHelper(application)
    val soundVibrator = EmergencyAlertSoundAndVibrator(application)
    val notificationHelper = EmergencyNotificationHelper(application)
    val communicationManager = EmergencyCommunicationManager(application)
    val hardwareSimulator = HardwareIntegrationSimulator()

    val profileFlow: StateFlow<RiderProfile> = profileRepo.profileFlow
    val incidentHistoryFlow: StateFlow<List<IncidentEntity>> = incidentDao.getAllIncidents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hardwareState: StateFlow<HardwareConnectionState> = hardwareSimulator.connectionState

    private val _incidentState = MutableStateFlow(IncidentState())
    val incidentState: StateFlow<IncidentState> = _incidentState.asStateFlow()

    private val _currentDeviceLocation = MutableStateFlow<LocationResult?>(null)
    val currentDeviceLocation: StateFlow<LocationResult?> = _currentDeviceLocation.asStateFlow()

    private val _nearestHospital = MutableStateFlow(
        locationHelper.findNearestHospital(0.0, 0.0)
    )
    val nearestHospital: StateFlow<NearestHospital> = _nearestHospital.asStateFlow()

    private var countdownJob: Job? = null
    private var timelineAutoAdvanceJob: Job? = null
    private var continuousLocationListener: LocationListener? = null

    init {
        hardwareSimulator.registerListener(this)
        startContinuousTracking()
        refreshDeviceLocation()
    }

    private fun startContinuousTracking() {
        continuousLocationListener = locationHelper.startContinuousLocationUpdates(
            intervalMs = 3000L,
            minDistanceMeters = 5f
        ) { loc, hosp ->
            _currentDeviceLocation.value = loc
            _nearestHospital.value = hosp
        }
    }

    fun refreshDeviceLocation() {
        viewModelScope.launch {
            val loc = locationHelper.getCurrentRealLocation()
            _currentDeviceLocation.value = loc
            if (loc.latitude != 0.0) {
                _nearestHospital.value = locationHelper.findNearestHospital(loc.latitude, loc.longitude)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundVibrator.stop()
        notificationHelper.dismissNotification()
        hardwareSimulator.unregisterListener()
        countdownJob?.cancel()
        timelineAutoAdvanceJob?.cancel()
        locationHelper.stopContinuousLocationUpdates(continuousLocationListener)
    }

    override fun onAccidentImpactDetected(
        source: String,
        impactGForce: Float,
        angularVelocityDegSec: Float,
        sensorTimestampMillis: Long
    ) {
        val detail = "Triggered by $source (Force: ${String.format("%.1f", impactGForce)}G, Gyro: ${String.format("%.0f", angularVelocityDegSec)}°/s)"
        triggerAccident(sourceDescription = detail)
    }

    fun triggerAccident(sourceDescription: String = "Manual Demonstration Trigger") {
        val current = _incidentState.value
        // Prevent duplicate trigger if already active
        if (current.phase == EmergencyPhase.COUNTDOWN_RUNNING || current.phase == EmergencyPhase.RESPONSE_DISPATCHED) {
            return
        }

        val profile = profileRepo.getProfile()
        val now = System.currentTimeMillis()
        val formattedTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(now))
        val incidentId = "INC-" + (now % 1000000).toString().padStart(6, '0')
        val totalCountdown = profile.countdownDurationSeconds.coerceIn(5, 7)

        countdownJob?.cancel()
        timelineAutoAdvanceJob?.cancel()

        val initialEvents = listOf(
            TimelineEvent(
                stage = TimelineStage.ACCIDENT_TRIGGERED,
                title = TimelineStage.ACCIDENT_TRIGGERED.defaultTitle,
                description = sourceDescription,
                timestampMillis = now,
                isSimulated = profile.isDemoMode,
                status = EventStatus.COMPLETED
            ),
            TimelineEvent(
                stage = TimelineStage.COUNTDOWN_STARTED,
                title = TimelineStage.COUNTDOWN_STARTED.defaultTitle,
                description = "Emergency countdown: $totalCountdown seconds before automatic assistance dispatch.",
                timestampMillis = now,
                isSimulated = profile.isDemoMode,
                status = EventStatus.ACTIVE
            )
        )

        _incidentState.value = IncidentState(
            incidentId = incidentId,
            timestampMillis = now,
            formattedTime = formattedTime,
            phase = EmergencyPhase.COUNTDOWN_RUNNING,
            countdownRemaining = totalCountdown,
            countdownTotal = totalCountdown,
            isAlarmActive = true,
            isDemoMode = profile.isDemoMode,
            timelineEvents = initialEvents
        )

        // Start alarm tone & vibration
        soundVibrator.startAlarmAndVibration(viewModelScope)
        notificationHelper.showCountdownNotification(totalCountdown, incidentId)

        // Start Countdown Loop
        countdownJob = viewModelScope.launch {
            for (remaining in totalCountdown downTo 1) {
                _incidentState.value = _incidentState.value.copy(countdownRemaining = remaining)
                notificationHelper.showCountdownNotification(remaining, incidentId)
                delay(1000L)
            }
            // If loop finishes without cancellation:
            _incidentState.value = _incidentState.value.copy(countdownRemaining = 0)
            onCountdownExpired()
        }
    }

    fun cancelEmergency(reason: String = "False alarm / Rider override") {
        val current = _incidentState.value
        if (current.phase != EmergencyPhase.COUNTDOWN_RUNNING) return

        // 1. Immediately cancel the countdown job
        countdownJob?.cancel()
        countdownJob = null

        // 2. Stop alarm and vibration immediately
        soundVibrator.stop()

        // 3. Dismiss system notification
        notificationHelper.dismissNotification()

        val cancelTime = System.currentTimeMillis()
        val updatedEvents = current.timelineEvents.map {
            if (it.stage == TimelineStage.COUNTDOWN_STARTED) it.copy(status = EventStatus.CANCELLED) else it
        } + TimelineEvent(
            stage = TimelineStage.COUNTDOWN_CANCELLED,
            title = TimelineStage.COUNTDOWN_CANCELLED.defaultTitle,
            description = "Emergency response stopped. Reason: $reason",
            timestampMillis = cancelTime,
            isSimulated = current.isDemoMode,
            status = EventStatus.COMPLETED
        )

        _incidentState.value = current.copy(
            phase = EmergencyPhase.CANCELLED,
            isAlarmActive = false,
            cancellationReason = reason,
            cancellationTimestampMillis = cancelTime,
            timelineEvents = updatedEvents
        )

        // Persist cancelled incident to Room database
        viewModelScope.launch {
            val profile = profileRepo.getProfile()
            val entity = IncidentEntity(
                incidentId = current.incidentId,
                timestampMillis = current.timestampMillis,
                riderName = profile.fullName,
                riderPhone = profile.mobileNumber,
                bloodGroup = profile.bloodGroup,
                medicalNotes = profile.medicalNotes,
                isDemoMode = current.isDemoMode,
                outcome = "CANCELLED",
                cancellationReason = reason,
                cancellationTimestampMillis = cancelTime,
                isLocationSimulated = true,
                locationLabel = "Cancelled before GPS fix",
                rawEventsJson = serializeEvents(updatedEvents)
            )
            incidentDao.insertIncident(entity)
        }
    }

    private suspend fun onCountdownExpired() {
        // Stop audio alarm and vibration
        soundVibrator.stop()

        val current = _incidentState.value
        val profile = profileRepo.getProfile()
        val isDemo = profile.isDemoMode

        // Mark countdown expired
        val afterCountdownEvents = current.timelineEvents.map {
            if (it.stage == TimelineStage.COUNTDOWN_STARTED) it.copy(status = EventStatus.COMPLETED) else it
        } + TimelineEvent(
            stage = TimelineStage.COUNTDOWN_EXPIRED,
            title = TimelineStage.COUNTDOWN_EXPIRED.defaultTitle,
            description = "Countdown elapsed without cancellation. Activating emergency dispatch workflow.",
            timestampMillis = System.currentTimeMillis(),
            isSimulated = isDemo,
            status = EventStatus.COMPLETED
        )

        _incidentState.value = current.copy(
            phase = EmergencyPhase.RESPONSE_DISPATCHED,
            isAlarmActive = false,
            timelineEvents = afterCountdownEvents
        )

        // Obtain location and confirm nearest hospital
        val location = locationHelper.getCurrentOrDemoLocation(isDemo)
        val confirmedHospital = locationHelper.findNearestHospital(location.latitude, location.longitude)
        _nearestHospital.value = confirmedHospital

        val locationEvent = if (location.latitude != 0.0) {
            TimelineEvent(
                stage = TimelineStage.LOCATION_ACQUIRED,
                title = TimelineStage.LOCATION_ACQUIRED.defaultTitle,
                description = "${location.statusDescription} (${String.format(Locale.US, "%.5f", location.latitude)}, ${String.format(Locale.US, "%.5f", location.longitude)})",
                timestampMillis = System.currentTimeMillis(),
                isSimulated = location.isSimulated,
                status = EventStatus.COMPLETED
            )
        } else {
            TimelineEvent(
                stage = TimelineStage.LOCATION_UNAVAILABLE,
                title = TimelineStage.LOCATION_UNAVAILABLE.defaultTitle,
                description = location.statusDescription,
                timestampMillis = System.currentTimeMillis(),
                isSimulated = isDemo,
                status = EventStatus.FAILED
            )
        }

        // Generate Alert Message Payload with Confirmed Nearest Hospital
        val alertMessage = communicationManager.generateAlertMessage(
            incidentId = current.incidentId,
            timestampStr = current.formattedTime,
            profile = profile,
            location = location,
            nearestHospital = confirmedHospital
        )

        // Setup Recipients with Confirmed Nearest Hospital & Kin
        val recipients = listOf(
            RecipientStatus(
                recipientName = profile.contact1Name.ifBlank { "Emergency Kin 1" },
                role = "Primary Kin (${profile.contact1Relationship.ifBlank { "Family" }})",
                phoneNumber = profile.contact1Mobile,
                status = if (profile.contact1Mobile.isNotBlank()) "SOS_DISPATCHED" else "ACTION_READY",
                isSimulated = false,
                statusDetail = if (profile.contact1Mobile.isNotBlank()) "Direct SMS & calling line activated" else "Configure in Emergency settings"
            ),
            RecipientStatus(
                recipientName = profile.contact2Name.ifBlank { "Emergency Kin 2" },
                role = "Secondary Kin (${profile.contact2Relationship.ifBlank { "Emergency Contact" }})",
                phoneNumber = profile.contact2Mobile,
                status = if (profile.contact2Mobile.isNotBlank()) "SOS_DISPATCHED" else "ACTION_READY",
                isSimulated = false,
                statusDetail = if (profile.contact2Mobile.isNotBlank()) "Direct SMS & calling line activated" else "Configure in Emergency settings"
            ),
            RecipientStatus(
                recipientName = confirmedHospital.name,
                role = "Confirmed Nearest Trauma Center (${confirmedHospital.distanceKm} km)",
                phoneNumber = confirmedHospital.phone,
                status = "CASUALTY_NOTIFIED",
                isSimulated = false,
                statusDetail = "${confirmedHospital.address} · ETA ~${confirmedHospital.etaMinutes} mins"
            ),
            RecipientStatus(
                recipientName = "Ambulance & Paramedic Dispatch (108)",
                role = "Emergency Medical Services",
                phoneNumber = "108",
                status = "DISPATCH_READY",
                isSimulated = false,
                statusDetail = "Paramedic & Trauma Response Helpline (108)"
            )
        )

        // Automatic dispatch: Send SMS details & initiate emergency contact call
        if (profile.contact1Mobile.isNotBlank()) {
            communicationManager.sendDirectSmsOrOpenApp(profile.contact1Mobile, alertMessage.fullText)
        }
        if (profile.contact2Mobile.isNotBlank()) {
            communicationManager.sendDirectSmsOrOpenApp(profile.contact2Mobile, alertMessage.fullText)
        }

        // Auto initiate emergency call to nearest hospital or primary kin
        val targetCallPhone = when {
            confirmedHospital.phone.isNotBlank() -> confirmedHospital.phone
            profile.contact1Mobile.isNotBlank() -> profile.contact1Mobile
            else -> "108"
        }
        communicationManager.openDialerIntent(targetCallPhone)

        val comEvents = listOf(
            locationEvent,
            TimelineEvent(
                stage = TimelineStage.HOSPITAL_NOTIFICATION,
                title = "Nearest Hospital Confirmed",
                description = "Confirmed: ${confirmedHospital.name} (${confirmedHospital.distanceKm} km away, ETA ~${confirmedHospital.etaMinutes}m). Direct trauma line: ${confirmedHospital.phone}",
                timestampMillis = System.currentTimeMillis(),
                isSimulated = false,
                status = EventStatus.COMPLETED
            ),
            TimelineEvent(
                stage = TimelineStage.FAMILY_NOTIFICATION,
                title = TimelineStage.FAMILY_NOTIFICATION.defaultTitle,
                description = "Emergency SOS details & GPS coordinates transmitted to contacts & hospital.",
                timestampMillis = System.currentTimeMillis(),
                isSimulated = false,
                status = EventStatus.COMPLETED
            )
        )

        val fullEvents = afterCountdownEvents + comEvents

        _incidentState.value = _incidentState.value.copy(
            location = location,
            alertMessage = alertMessage,
            recipients = recipients,
            timelineEvents = fullEvents
        )

        notificationHelper.showDispatchedNotification(current.incidentId, isDemo)

        // Save active state to Room Database
        saveIncidentToDb(
            incidentId = current.incidentId,
            timestampMillis = current.timestampMillis,
            profile = profile,
            outcome = "CONFIRMED_ASSISTANCE",
            location = location,
            recipients = recipients,
            events = fullEvents
        )

        // In Demo mode, start auto-advancing subsequent stages
        if (isDemo) {
            startDemoTimelineAutoAdvance()
        }
    }

    private fun startDemoTimelineAutoAdvance() {
        timelineAutoAdvanceJob?.cancel()
        timelineAutoAdvanceJob = viewModelScope.launch {
            val stages = listOf(
                Pair(TimelineStage.AMBULANCE_DISPATCHED, "Ambulance Unit 4 dispatched from Central EMS Station. ETA: 7 mins."),
                Pair(TimelineStage.PATIENT_PICKUP, "First responders on scene. Vitals stabilized, rider secured for transit."),
                Pair(TimelineStage.HOSPITAL_ARRIVAL, "Ambulance arrived at Metro General Trauma Bay 3."),
                Pair(TimelineStage.PATIENT_ASSESSMENT, "Patient under preliminary trauma evaluation. Stable condition."),
                Pair(TimelineStage.FAMILY_UPDATE, "Automated hospital confirmation update delivered to primary family contact.")
            )

            for (stageInfo in stages) {
                delay(3500L) // 3.5 seconds per demo step
                if (!isActive) break

                val newEvent = TimelineEvent(
                    stage = stageInfo.first,
                    title = stageInfo.first.defaultTitle,
                    description = "[DEMO SIMULATION] ${stageInfo.second}",
                    timestampMillis = System.currentTimeMillis(),
                    isSimulated = true,
                    status = EventStatus.COMPLETED
                )

                val updatedEvents = _incidentState.value.timelineEvents + newEvent
                _incidentState.value = _incidentState.value.copy(timelineEvents = updatedEvents)

                val current = _incidentState.value
                val profile = profileRepo.getProfile()
                current.location?.let { loc ->
                    saveIncidentToDb(
                        incidentId = current.incidentId,
                        timestampMillis = current.timestampMillis,
                        profile = profile,
                        outcome = if (stageInfo.first == TimelineStage.FAMILY_UPDATE) "RESOLVED" else "IN_PROGRESS",
                        location = loc,
                        recipients = current.recipients,
                        events = updatedEvents
                    )
                }
            }
        }
    }

    fun manualAdvanceNextStage() {
        val currentEvents = _incidentState.value.timelineEvents
        val existingStages = currentEvents.map { it.stage }.toSet()

        val nextStage = when {
            TimelineStage.AMBULANCE_DISPATCHED !in existingStages ->
                Pair(TimelineStage.AMBULANCE_DISPATCHED, "Ambulance Unit 4 dispatched from Central EMS Station. ETA: 7 mins.")
            TimelineStage.PATIENT_PICKUP !in existingStages ->
                Pair(TimelineStage.PATIENT_PICKUP, "First responders on scene. Vitals stabilized, rider secured for transit.")
            TimelineStage.HOSPITAL_ARRIVAL !in existingStages ->
                Pair(TimelineStage.HOSPITAL_ARRIVAL, "Ambulance arrived at Metro General Trauma Bay 3.")
            TimelineStage.PATIENT_ASSESSMENT !in existingStages ->
                Pair(TimelineStage.PATIENT_ASSESSMENT, "Patient under preliminary trauma evaluation. Stable condition.")
            TimelineStage.FAMILY_UPDATE !in existingStages ->
                Pair(TimelineStage.FAMILY_UPDATE, "Automated hospital confirmation update delivered to primary family contact.")
            else -> null
        }

        if (nextStage != null) {
            val newEvent = TimelineEvent(
                stage = nextStage.first,
                title = nextStage.first.defaultTitle,
                description = "[DEMO SIMULATION] ${nextStage.second}",
                timestampMillis = System.currentTimeMillis(),
                isSimulated = true,
                status = EventStatus.COMPLETED
            )
            val updatedEvents = currentEvents + newEvent
            _incidentState.value = _incidentState.value.copy(timelineEvents = updatedEvents)

            val current = _incidentState.value
            val profile = profileRepo.getProfile()
            current.location?.let { loc ->
                saveIncidentToDb(
                    incidentId = current.incidentId,
                    timestampMillis = current.timestampMillis,
                    profile = profile,
                    outcome = if (nextStage.first == TimelineStage.FAMILY_UPDATE) "RESOLVED" else "IN_PROGRESS",
                    location = loc,
                    recipients = current.recipients,
                    events = updatedEvents
                )
            }
        }
    }

    private fun saveIncidentToDb(
        incidentId: String,
        timestampMillis: Long,
        profile: RiderProfile,
        outcome: String,
        location: LocationResult,
        recipients: List<RecipientStatus>,
        events: List<TimelineEvent>
    ) {
        viewModelScope.launch {
            val entity = IncidentEntity(
                incidentId = incidentId,
                timestampMillis = timestampMillis,
                riderName = profile.fullName,
                riderPhone = profile.mobileNumber,
                bloodGroup = profile.bloodGroup,
                medicalNotes = profile.medicalNotes,
                isDemoMode = profile.isDemoMode,
                outcome = outcome,
                cancellationReason = null,
                latitude = location.latitude,
                longitude = location.longitude,
                accuracyMeters = location.accuracyMeters,
                isLocationSimulated = location.isSimulated,
                locationLabel = location.statusDescription,
                googleMapsUrl = location.toGoogleMapsUrl(),
                contact1Status = recipients.getOrNull(0)?.status ?: "SIMULATED",
                contact2Status = recipients.getOrNull(1)?.status ?: "SIMULATED",
                hospitalStatus = recipients.getOrNull(2)?.status ?: "SIMULATED",
                emergencyServiceStatus = recipients.getOrNull(3)?.status ?: "SIMULATED",
                rawEventsJson = serializeEvents(events)
            )
            incidentDao.insertIncident(entity)
        }
    }

    fun resetIncidentToIdle() {
        countdownJob?.cancel()
        timelineAutoAdvanceJob?.cancel()
        soundVibrator.stop()
        notificationHelper.dismissNotification()
        _incidentState.value = IncidentState()
    }

    fun saveRiderProfile(profile: RiderProfile) {
        profileRepo.saveProfile(profile)
    }

    fun setDemoMode(isDemo: Boolean) {
        profileRepo.setDemoMode(isDemo)
    }

    fun resetProfileToSample() {
        profileRepo.resetToDefaultSample()
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            incidentDao.clearAll()
        }
    }

    private fun serializeEvents(events: List<TimelineEvent>): String {
        return events.joinToString("|||") { event ->
            "${event.stage.name}###${event.title}###${event.description}###${event.timestampMillis}###${event.isSimulated}###${event.status.name}"
        }
    }

    fun parseEvents(raw: String): List<TimelineEvent> {
        if (raw.isBlank()) return emptyList()
        return try {
            raw.split("|||").mapNotNull { item ->
                val parts = item.split("###")
                if (parts.size >= 6) {
                    TimelineEvent(
                        stage = TimelineStage.valueOf(parts[0]),
                        title = parts[1],
                        description = parts[2],
                        timestampMillis = parts[3].toLongOrNull() ?: 0L,
                        isSimulated = parts[4].toBoolean(),
                        status = EventStatus.valueOf(parts[5])
                    )
                } else null
            }
        } catch (_: Exception) {
            emptyList()
        }
    }
}
