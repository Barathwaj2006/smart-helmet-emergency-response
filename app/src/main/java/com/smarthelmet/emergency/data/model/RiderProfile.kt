package com.smarthelmet.emergency.data.model

data class RiderProfile(
    val fullName: String = "Rahul Sharma",
    val mobileNumber: String = "+91 98450 12345",
    val vehiclePlate: String = "KA 01 EQ 4290",
    val vehicleModel: String = "Royal Enfield Hunter 350",
    val bloodGroup: String = "O+",
    val medicalNotes: String = "Penicillin allergy, Asthma (Inhaler in backpack)",
    val healthId: String = "ABHA-9821-4432-1109",
    val contact1Name: String = "Rajesh Sharma",
    val contact1Relationship: String = "Father · Kin 1",
    val contact1Mobile: String = "+91 98450 67890",
    val contact2Name: String = "Neha Verma",
    val contact2Relationship: String = "Spouse · Kin 2",
    val contact2Mobile: String = "+91 99801 54321",
    val hospitalName: String = "Manipal Hospital",
    val hospitalPhone: String = "+91 80 2502 4444",
    val hospitalAddress: String = "HAL Old Airport Rd, Kodihalli, Bengaluru",
    val emergencyServicePhone: String = "108",
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
