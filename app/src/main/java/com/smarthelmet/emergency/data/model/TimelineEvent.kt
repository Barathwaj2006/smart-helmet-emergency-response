package com.smarthelmet.emergency.data.model

enum class EventStatus {
    PENDING,
    ACTIVE,
    COMPLETED,
    CANCELLED,
    FAILED
}

enum class TimelineStage(val defaultTitle: String) {
    ACCIDENT_TRIGGERED("Accident Impact Detected"),
    COUNTDOWN_STARTED("Emergency Countdown Commenced"),
    COUNTDOWN_CANCELLED("Incident Cancelled by Rider"),
    COUNTDOWN_EXPIRED("Countdown Expired - Assistance Confirmed"),
    LOCATION_ACQUIRED("GPS Coordinates Acquired"),
    LOCATION_UNAVAILABLE("GPS Fix Unavailable"),
    FAMILY_NOTIFICATION("Emergency Contacts Alerted"),
    HOSPITAL_NOTIFICATION("Trauma Center Alerted"),
    EMERGENCY_SERVICES("EMS Dispatch Contact Initiated"),
    AMBULANCE_DISPATCHED("Ambulance Unit Dispatched"),
    PATIENT_PICKUP("Paramedics on Scene & Patient Secured"),
    HOSPITAL_ARRIVAL("Arrival at Emergency Ward"),
    PATIENT_ASSESSMENT("Triage & Medical Assessment Underway"),
    FAMILY_UPDATE("Family Notification Update Sent")
}

data class TimelineEvent(
    val stage: TimelineStage,
    val title: String,
    val description: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val isSimulated: Boolean = true,
    val status: EventStatus = EventStatus.COMPLETED
)
