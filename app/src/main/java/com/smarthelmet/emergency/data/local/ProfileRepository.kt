package com.smarthelmet.emergency.data.local

import android.content.Context
import android.content.SharedPreferences
import com.smarthelmet.emergency.data.model.RiderProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("rider_profile_prefs", Context.MODE_PRIVATE)

    private val _profileFlow = MutableStateFlow(loadProfile())
    val profileFlow: StateFlow<RiderProfile> = _profileFlow.asStateFlow()

    fun getProfile(): RiderProfile = _profileFlow.value

    fun saveProfile(profile: RiderProfile) {
        prefs.edit().apply {
            putString(KEY_FULL_NAME, profile.fullName)
            putString(KEY_MOBILE, profile.mobileNumber)
            putString(KEY_PLATE, profile.vehiclePlate)
            putString(KEY_MODEL, profile.vehicleModel)
            putString(KEY_BLOOD_GROUP, profile.bloodGroup)
            putString(KEY_MEDICAL_NOTES, profile.medicalNotes)
            putString(KEY_HEALTH_ID, profile.healthId)
            putString(KEY_CONTACT1_NAME, profile.contact1Name)
            putString(KEY_CONTACT1_REL, profile.contact1Relationship)
            putString(KEY_CONTACT1_MOBILE, profile.contact1Mobile)
            putString(KEY_CONTACT2_NAME, profile.contact2Name)
            putString(KEY_CONTACT2_REL, profile.contact2Relationship)
            putString(KEY_CONTACT2_MOBILE, profile.contact2Mobile)
            putString(KEY_HOSPITAL_NAME, profile.hospitalName)
            putString(KEY_HOSPITAL_PHONE, profile.hospitalPhone)
            putString(KEY_HOSPITAL_ADDR, profile.hospitalAddress)
            putString(KEY_EMERGENCY_PHONE, profile.emergencyServicePhone)
            putInt(KEY_COUNTDOWN_SEC, profile.countdownDurationSeconds)
            putBoolean(KEY_DEMO_MODE, profile.isDemoMode)
            apply()
        }
        _profileFlow.value = profile
    }

    fun setDemoMode(isDemo: Boolean) {
        val updated = _profileFlow.value.copy(isDemoMode = isDemo)
        saveProfile(updated)
    }

    fun resetToDefaultSample() {
        saveProfile(RiderProfile.defaultSample())
    }

    private fun loadProfile(): RiderProfile {
        val default = RiderProfile.defaultSample()
        return RiderProfile(
            fullName = prefs.getString(KEY_FULL_NAME, default.fullName) ?: default.fullName,
            mobileNumber = prefs.getString(KEY_MOBILE, default.mobileNumber) ?: default.mobileNumber,
            vehiclePlate = prefs.getString(KEY_PLATE, default.vehiclePlate) ?: default.vehiclePlate,
            vehicleModel = prefs.getString(KEY_MODEL, default.vehicleModel) ?: default.vehicleModel,
            bloodGroup = prefs.getString(KEY_BLOOD_GROUP, default.bloodGroup) ?: default.bloodGroup,
            medicalNotes = prefs.getString(KEY_MEDICAL_NOTES, default.medicalNotes) ?: default.medicalNotes,
            healthId = prefs.getString(KEY_HEALTH_ID, default.healthId) ?: default.healthId,
            contact1Name = prefs.getString(KEY_CONTACT1_NAME, default.contact1Name) ?: default.contact1Name,
            contact1Relationship = prefs.getString(KEY_CONTACT1_REL, default.contact1Relationship) ?: default.contact1Relationship,
            contact1Mobile = prefs.getString(KEY_CONTACT1_MOBILE, default.contact1Mobile) ?: default.contact1Mobile,
            contact2Name = prefs.getString(KEY_CONTACT2_NAME, default.contact2Name) ?: default.contact2Name,
            contact2Relationship = prefs.getString(KEY_CONTACT2_REL, default.contact2Relationship) ?: default.contact2Relationship,
            contact2Mobile = prefs.getString(KEY_CONTACT2_MOBILE, default.contact2Mobile) ?: default.contact2Mobile,
            hospitalName = prefs.getString(KEY_HOSPITAL_NAME, default.hospitalName) ?: default.hospitalName,
            hospitalPhone = prefs.getString(KEY_HOSPITAL_PHONE, default.hospitalPhone) ?: default.hospitalPhone,
            hospitalAddress = prefs.getString(KEY_HOSPITAL_ADDR, default.hospitalAddress) ?: default.hospitalAddress,
            emergencyServicePhone = prefs.getString(KEY_EMERGENCY_PHONE, default.emergencyServicePhone) ?: default.emergencyServicePhone,
            countdownDurationSeconds = prefs.getInt(KEY_COUNTDOWN_SEC, default.countdownDurationSeconds),
            isDemoMode = prefs.getBoolean(KEY_DEMO_MODE, default.isDemoMode)
        )
    }

    companion object {
        private const val KEY_FULL_NAME = "full_name"
        private const val KEY_MOBILE = "mobile_number"
        private const val KEY_PLATE = "vehicle_plate"
        private const val KEY_MODEL = "vehicle_model"
        private const val KEY_BLOOD_GROUP = "blood_group"
        private const val KEY_MEDICAL_NOTES = "medical_notes"
        private const val KEY_HEALTH_ID = "health_id"
        private const val KEY_CONTACT1_NAME = "contact1_name"
        private const val KEY_CONTACT1_REL = "contact1_rel"
        private const val KEY_CONTACT1_MOBILE = "contact1_mobile"
        private const val KEY_CONTACT2_NAME = "contact2_name"
        private const val KEY_CONTACT2_REL = "contact2_rel"
        private const val KEY_CONTACT2_MOBILE = "contact2_mobile"
        private const val KEY_HOSPITAL_NAME = "hospital_name"
        private const val KEY_HOSPITAL_PHONE = "hospital_phone"
        private const val KEY_HOSPITAL_ADDR = "hospital_address"
        private const val KEY_EMERGENCY_PHONE = "emergency_phone"
        private const val KEY_COUNTDOWN_SEC = "countdown_sec"
        private const val KEY_DEMO_MODE = "demo_mode"
    }
}
