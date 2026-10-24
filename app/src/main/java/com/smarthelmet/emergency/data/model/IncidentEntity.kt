package com.smarthelmet.emergency.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incidents")
data class IncidentEntity(
    @PrimaryKey
    val incidentId: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val riderName: String,
    val riderPhone: String,
    val bloodGroup: String,
    val medicalNotes: String,
    val isDemoMode: Boolean,
    val outcome: String, // "CANCELLED", "CONFIRMED", "IN_PROGRESS"
    val cancellationReason: String? = null,
    val cancellationTimestampMillis: Long? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val accuracyMeters: Float? = null,
    val isLocationSimulated: Boolean = true,
    val locationLabel: String = "",
    val googleMapsUrl: String? = null,
    val contact1Status: String = "SIMULATED",
    val contact2Status: String = "SIMULATED",
    val hospitalStatus: String = "SIMULATED",
    val emergencyServiceStatus: String = "SIMULATED",
    val rawEventsJson: String = ""
)
