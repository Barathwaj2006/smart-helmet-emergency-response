package com.smarthelmet.emergency.service

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.smarthelmet.emergency.data.model.RiderProfile

data class RecipientStatus(
    val recipientName: String,
    val role: String, // Contact 1, Contact 2, Nearest Hospital, EMS
    val phoneNumber: String,
    val status: String, // SENT, ACTION_READY, CALL_INITIATED, FAILED
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

    fun sanitizePhoneNumber(raw: String): String {
        return raw.filter { it.isDigit() || it == '+' }
    }

    fun generateAlertMessage(
        incidentId: String,
        timestampStr: String,
        profile: RiderProfile,
        location: LocationResult,
        nearestHospital: NearestHospital? = null
    ): EmergencyAlertMessage {
        val coordsStr = if (location.latitude != 0.0) {
            "${String.format(java.util.Locale.US, "%.6f", location.latitude)}, ${String.format(java.util.Locale.US, "%.6f", location.longitude)}"
        } else {
            "GPS UNAVAILABLE"
        }
        val mapUrl = location.toGoogleMapsUrl()

        val activeHospitalName = nearestHospital?.name?.ifBlank { profile.hospitalName } ?: profile.hospitalName.ifBlank { "Nearest Emergency Trauma Center" }
        val activeHospitalPhone = nearestHospital?.phone?.ifBlank { profile.hospitalPhone } ?: profile.hospitalPhone.ifBlank { "108" }

        val fullText = buildString {
            append("🚨 EMERGENCY ALERT - SMART HELMET ACCIDENT DETECTED\n")
            append("Incident ID: $incidentId\n")
            append("Timestamp: $timestampStr\n")
            append("Rider: ${profile.fullName.ifBlank { "Motorcycle Rider" }} (Ph: ${profile.mobileNumber.ifBlank { "N/A" }})\n")
            append("Blood Group: ${profile.bloodGroup.ifBlank { "Unknown" }}\n")
            if (profile.medicalNotes.isNotBlank()) {
                append("Medical Notes: ${profile.medicalNotes}\n")
            }
            append("Incident Coordinates: $coordsStr\n")
            if (location.latitude != 0.0) {
                append("Live GPS Map: $mapUrl\n")
            }
            if (location.addressLine.isNotBlank()) {
                append("Location: ${location.addressLine}, ${location.cityArea}\n")
            }
            append("Nearest Confirmed Hospital: $activeHospitalName (Ph: $activeHospitalPhone)\n")
            if (nearestHospital != null && nearestHospital.distanceKm > 0f) {
                append("Hospital Distance: ${nearestHospital.distanceKm} km (ETA ~${nearestHospital.etaMinutes} mins)\n")
            }
            append("-- Automated High-Priority Alert dispatched by Smart Helmet Emergency System --")
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
            preferredHospital = activeHospitalName,
            fullText = fullText
        )
    }

    /**
     * Directly initiates a phone call or opens the dialer with the sanitized number.
     * Guaranteed to work with or without CALL_PHONE runtime permission.
     */
    fun openDialerIntent(phoneNumber: String): Boolean {
        val clean = sanitizePhoneNumber(phoneNumber)
        if (clean.isBlank()) {
            Toast.makeText(context, "No emergency phone number configured", Toast.LENGTH_SHORT).show()
            return false
        }

        return try {
            val hasCallPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED

            val intent = if (hasCallPermission) {
                Intent(Intent.ACTION_CALL, Uri.parse("tel:$clean")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            } else {
                Intent(Intent.ACTION_DIAL, Uri.parse("tel:$clean")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }
            context.startActivity(intent)
            true
        } catch (_: SecurityException) {
            // Fallback to dialer if direct call security check tripped
            try {
                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$clean")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(dialIntent)
                true
            } catch (e2: Exception) {
                Toast.makeText(context, "Could not launch phone dialer", Toast.LENGTH_SHORT).show()
                false
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Could not initiate call: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    /**
     * Opens the SMS messenger app with prefilled emergency alert body and recipient.
     */
    fun openSmsAppIntent(phoneNumber: String, messageText: String): Boolean {
        val clean = sanitizePhoneNumber(phoneNumber)
        return try {
            val uri = if (clean.isNotBlank()) Uri.parse("smsto:$clean") else Uri.parse("smsto:")
            val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                putExtra("sms_body", messageText)
                putExtra(Intent.EXTRA_TEXT, messageText)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            try {
                val fallbackIntent = Intent(Intent.ACTION_VIEW).apply {
                    type = "vnd.android-dir/mms-sms"
                    putExtra("address", clean)
                    putExtra("sms_body", messageText)
                    putExtra(Intent.EXTRA_TEXT, messageText)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(fallbackIntent)
                true
            } catch (e2: Exception) {
                Toast.makeText(context, "Could not open messaging application", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }

    /**
     * Attempts direct SMS transmission via Android SmsManager if permission is granted,
     * otherwise automatically launches the SMS messaging app with prefilled payload.
     */
    fun sendDirectSmsOrOpenApp(phoneNumber: String, messageText: String): Boolean {
        val clean = sanitizePhoneNumber(phoneNumber)
        if (clean.isBlank()) return false

        val hasSmsPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED

        if (hasSmsPermission) {
            try {
                val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    context.getSystemService(SmsManager::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    SmsManager.getDefault()
                }
                val parts = smsManager.divideMessage(messageText)
                smsManager.sendMultipartTextMessage(clean, null, parts, null, null)
                Toast.makeText(context, "Emergency SOS SMS sent to $clean", Toast.LENGTH_LONG).show()
                return true
            } catch (e: Exception) {
                // If SmsManager fails, fallback to opening SMS app
            }
        }

        return openSmsAppIntent(clean, messageText)
    }
}
