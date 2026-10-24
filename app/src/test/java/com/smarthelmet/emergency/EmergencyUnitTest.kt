package com.smarthelmet.emergency

import com.smarthelmet.emergency.data.model.EventStatus
import com.smarthelmet.emergency.data.model.RiderProfile
import com.smarthelmet.emergency.data.model.TimelineEvent
import com.smarthelmet.emergency.data.model.TimelineStage
import com.smarthelmet.emergency.service.HardwareConnectionState
import com.smarthelmet.emergency.service.HardwareIntegrationSimulator
import com.smarthelmet.emergency.service.HelmetHardwareListener
import com.smarthelmet.emergency.service.LocationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EmergencyUnitTest {

    @Test
    fun testRiderProfileDefaultStartsEmptyForUserFill() {
        val blankProfile = RiderProfile()
        assertEquals("", blankProfile.fullName)
        assertEquals("", blankProfile.mobileNumber)
        assertEquals("", blankProfile.contact1Name)
        assertFalse("Blank profile should not be configured until user fills details", blankProfile.isConfigured())
    }

    @Test
    fun testRiderProfileWhenConfiguredByUser() {
        val userProfile = RiderProfile(
            fullName = "User Name",
            mobileNumber = "+91 9876543210",
            contact1Name = "Emergency Kin",
            contact1Mobile = "+91 9876543211",
            hospitalName = "General Hospital",
            hospitalPhone = "+91 80 12345678"
        )
        assertTrue("User configured profile should pass isConfigured check", userProfile.isConfigured())
    }

    @Test
    fun testRiderProfileValidationCatchesMissingRequiredFields() {
        val incompleteProfile = RiderProfile(
            fullName = "",
            mobileNumber = "",
            contact1Name = "",
            contact1Mobile = "",
            hospitalName = "",
            hospitalPhone = ""
        )
        assertFalse("Incomplete profile should fail isConfigured() check", incompleteProfile.isConfigured())
    }

    @Test
    fun testHardwareSimulatorTriggersListenerWithAccurateTelemetry() {
        val simulator = HardwareIntegrationSimulator()
        var listenerInvoked = false
        var capturedGForce = 0f
        var capturedGyro = 0f
        var capturedSource = ""

        simulator.registerListener(object : HelmetHardwareListener {
            override fun onAccidentImpactDetected(
                source: String,
                impactGForce: Float,
                angularVelocityDegSec: Float,
                sensorTimestampMillis: Long
            ) {
                listenerInvoked = true
                capturedSource = source
                capturedGForce = impactGForce
                capturedGyro = angularVelocityDegSec
            }
        })

        simulator.triggerHardwareImpactEvent(gForce = 6.4f, gyroDelta = 320.0f)

        assertTrue("Listener must be called when impact event is triggered", listenerInvoked)
        assertEquals("ESP32_IMU_BROADCAST", capturedSource)
        assertEquals(6.4f, capturedGForce, 0.01f)
        assertEquals(320.0f, capturedGyro, 0.01f)
        assertEquals(HardwareConnectionState.IMPACT_SIGNAL_RECEIVED, simulator.connectionState.value)

        simulator.resetHardwareState()
        assertEquals(HardwareConnectionState.NOT_CONNECTED, simulator.connectionState.value)
    }

    @Test
    fun testLocationResultGoogleMapsUrlGeneration() {
        val location = LocationResult(
            latitude = 37.774929,
            longitude = -122.419416,
            accuracyMeters = 3.5f,
            isSimulated = true,
            provider = "SIMULATED_DEMO",
            statusDescription = "Springfield Corridor Demo GPS"
        )

        val mapUrl = location.toGoogleMapsUrl()
        assertEquals("https://maps.google.com/?q=37.774929,-122.419416", mapUrl)
        assertTrue(location.isSimulated)
        assertEquals("SIMULATED_DEMO", location.provider)
    }

    @Test
    fun testTimelineEventCreationAndStatusTransitions() {
        val initialEvent = TimelineEvent(
            stage = TimelineStage.ACCIDENT_TRIGGERED,
            title = TimelineStage.ACCIDENT_TRIGGERED.defaultTitle,
            description = "Impact threshold breached",
            status = EventStatus.COMPLETED
        )

        val countdownEvent = TimelineEvent(
            stage = TimelineStage.COUNTDOWN_STARTED,
            title = TimelineStage.COUNTDOWN_STARTED.defaultTitle,
            description = "5 seconds remaining",
            status = EventStatus.ACTIVE
        )

        val cancelledEvent = countdownEvent.copy(status = EventStatus.CANCELLED)

        assertEquals(EventStatus.COMPLETED, initialEvent.status)
        assertEquals(EventStatus.ACTIVE, countdownEvent.status)
        assertEquals(EventStatus.CANCELLED, cancelledEvent.status)
        assertEquals(TimelineStage.COUNTDOWN_STARTED, cancelledEvent.stage)
    }

    @Test
    fun testLocationUnavailableStateFallback() {
        val unavailableLocation = LocationResult(
            latitude = 0.0,
            longitude = 0.0,
            accuracyMeters = 0f,
            isSimulated = false,
            provider = "UNAVAILABLE",
            statusDescription = "GPS signal unavailable (No satellite lock)"
        )

        assertEquals(0.0, unavailableLocation.latitude, 0.0001)
        assertEquals(0.0, unavailableLocation.longitude, 0.0001)
        assertFalse(unavailableLocation.isSimulated)
        assertTrue(unavailableLocation.statusDescription.contains("unavailable"))
    }

    @Test
    fun testEventSerializationFormatIntegrity() {
        val events = listOf(
            TimelineEvent(
                stage = TimelineStage.ACCIDENT_TRIGGERED,
                title = "Accident Detected",
                description = "Manual trigger",
                timestampMillis = 1700000000000L,
                isSimulated = true,
                status = EventStatus.COMPLETED
            ),
            TimelineEvent(
                stage = TimelineStage.COUNTDOWN_CANCELLED,
                title = "Incident Cancelled",
                description = "Reason: False alarm",
                timestampMillis = 1700000005000L,
                isSimulated = true,
                status = EventStatus.COMPLETED
            )
        )

        // Verify serializing and deserializing
        val serialized = events.joinToString("|||") { event ->
            "${event.stage.name}###${event.title}###${event.description}###${event.timestampMillis}###${event.isSimulated}###${event.status.name}"
        }

        val parsed = serialized.split("|||").map { item ->
            val parts = item.split("###")
            TimelineEvent(
                stage = TimelineStage.valueOf(parts[0]),
                title = parts[1],
                description = parts[2],
                timestampMillis = parts[3].toLong(),
                isSimulated = parts[4].toBoolean(),
                status = EventStatus.valueOf(parts[5])
            )
        }

        assertEquals(2, parsed.size)
        assertEquals(TimelineStage.ACCIDENT_TRIGGERED, parsed[0].stage)
        assertEquals("Accident Detected", parsed[0].title)
        assertEquals(TimelineStage.COUNTDOWN_CANCELLED, parsed[1].stage)
        assertEquals("Reason: False alarm", parsed[1].description)
    }
}
