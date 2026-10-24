package com.smarthelmet.emergency.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.smarthelmet.emergency.data.model.RiderProfile

data class RecipientStatus(
    val recipientName: String,
    val role: String, // Contact 1, Contact 2, Hospital, EMS
    val phoneNumber: String,
    val status: String, // SIMULATED_SENT, QUEUED, ATTEMPTED, MANUAL_INTENT_LAUNCHED, FAILED
    val isSimulated: Boolean,
    val statusDetail: String,
    val lastUpdatedMillis: Long = System.currentTimeMillis()
)

data class EmergencyAlertMessage(
    val incidentId: String,
    val timestamp: String,
    val riderName: String,
    val riderPhone: String,
    val bloodGroup: String,
    val medicalNotes: String,
    val coordinates: String,
    val mapLink: String,
    val preferredHospital: String,
    val fullText: String
)

class EmergencyCommunicationManager(private val context: Context) {

    fun generateAlertMessage(
        incidentId: String,
        timestampStr: String,
        profile: RiderProfile,
        location: LocationResult
    ): EmergencyAlertMessage {
        val coordsStr = if (location.latitude != 0.0) {
            "${String.format("%.6f", location.latitude)}, ${String.format("%.6f", location.longitude)}"
        } else {
            "GPS UNAVAILABLE"
        }
        val mapUrl = location.toGoogleMapsUrl()

        val fullText = buildString {
            append("🚨 EMERGENCY ALERT - SMART HELMET ACCIDENT DETECTED\n")
            append("Incident ID: $incidentId\n")
            append("Timestamp: $timestampStr\n")
            append("Rider: ${profile.fullName} (Ph: ${profile.mobileNumber})\n")
            append("Blood Group: ${profile.bloodGroup.ifBlank { "Not specified" }}\n")
            if (profile.medicalNotes.isNotBlank()) {
                append("Medical Notes: ${profile.medicalNotes}\n")
            }
            append("Location: $coordsStr\n")
            if (location.latitude != 0.0) {
                append("Live Map: $mapUrl\n")
            }
            append("Status: ${location.statusDescription}\n")
            append("Designated Hospital: ${profile.hospitalName} (Ph: ${profile.hospitalPhone})\n")
            append("-- Automated Alert dispatched by Smart Helmet Emergency Response System --")
        }

        return EmergencyAlertMessage(
            incidentId = incidentId,
            timestamp = timestampStr,
            riderName = profile.fullName,
            riderPhone = profile.mobileNumber,
            bloodGroup = profile.bloodGroup,
            medicalNotes = profile.medicalNotes,
            coordinates = coordsStr,
            mapLink = mapUrl,
            preferredHospital = profile.hospitalName,
            fullText = fullText
        )
    }

    fun openDialerIntent(phoneNumber: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${phoneNumber.trim()}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun openSmsAppIntent(phoneNumber: String, messageText: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:${phoneNumber.trim()}")
                putExtra("sms_body", messageText)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }
}
