package com.smarthelmet.emergency.data.model

data class RiderProfile(
    val fullName: String = "Johnathan Doe",
    val mobileNumber: String = "+1 (555) 234-5678",
    val bloodGroup: String = "O+",
    val medicalNotes: String = "No known allergies. Asthmatic (inhaler in jacket).",
    val contact1Name: String = "Sarah Doe",
    val contact1Relationship: String = "Spouse",
    val contact1Mobile: String = "+1 (555) 876-5432",
    val contact2Name: String = "Robert Doe",
    val contact2Relationship: String = "Brother",
    val contact2Mobile: String = "+1 (555) 987-6543",
    val hospitalName: String = "Metro General Trauma Center",
    val hospitalPhone: String = "+1 (555) 123-4567",
    val hospitalAddress: String = "742 Evergreen Terrace, Medical District, Springfield",
    val emergencyServicePhone: String = "911",
    val countdownDurationSeconds: Int = 5,
    val isDemoMode: Boolean = true
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
