package com.smarthelmet.emergency.data.model

data class RiderProfile(
    val fullName: String = "",
    val mobileNumber: String = "",
    val vehiclePlate: String = "",
    val vehicleModel: String = "",
    val bloodGroup: String = "",
    val medicalNotes: String = "",
    val healthId: String = "",
    val contact1Name: String = "",
    val contact1Relationship: String = "",
    val contact1Mobile: String = "",
    val contact2Name: String = "",
    val contact2Relationship: String = "",
    val contact2Mobile: String = "",
    val hospitalName: String = "",
    val hospitalPhone: String = "",
    val hospitalAddress: String = "",
    val emergencyServicePhone: String = "108",
    val countdownDurationSeconds: Int = 7,
    val isDemoMode: Boolean = false
) {
    fun isConfigured(): Boolean {
        return fullName.isNotBlank() &&
                mobileNumber.isNotBlank() &&
                contact1Name.isNotBlank() &&
                contact1Mobile.isNotBlank() &&
                hospitalName.isNotBlank() &&
                hospitalPhone.isNotBlank()
    }

    companion object {
        fun defaultSample(): RiderProfile = RiderProfile()
    }
}
