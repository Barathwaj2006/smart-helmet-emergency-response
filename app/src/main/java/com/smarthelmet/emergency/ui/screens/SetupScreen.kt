package com.smarthelmet.emergency.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarthelmet.emergency.data.model.RiderProfile
import com.smarthelmet.emergency.ui.theme.AccentCyan
import com.smarthelmet.emergency.ui.theme.EmergencyRed
import com.smarthelmet.emergency.ui.theme.NavyBorder
import com.smarthelmet.emergency.ui.theme.NavyCard
import com.smarthelmet.emergency.ui.theme.NavyDeep
import com.smarthelmet.emergency.ui.theme.NavySurface
import com.smarthelmet.emergency.ui.theme.SafeGreen
import com.smarthelmet.emergency.ui.theme.TextMuted
import com.smarthelmet.emergency.ui.theme.TextPrimary
import com.smarthelmet.emergency.ui.theme.TextSecondary
import com.smarthelmet.emergency.ui.theme.WarningAmber

@Composable
fun SetupScreen(
    currentProfile: RiderProfile,
    onSaveProfile: (RiderProfile) -> Unit,
    onResetDefaults: () -> Unit,
    onDone: () -> Unit
) {
    val scrollState = rememberScrollState()

    var fullName by remember(currentProfile) { mutableStateOf(currentProfile.fullName) }
    var mobileNumber by remember(currentProfile) { mutableStateOf(currentProfile.mobileNumber) }
    var bloodGroup by remember(currentProfile) { mutableStateOf(currentProfile.bloodGroup) }
    var medicalNotes by remember(currentProfile) { mutableStateOf(currentProfile.medicalNotes) }

    var contact1Name by remember(currentProfile) { mutableStateOf(currentProfile.contact1Name) }
    var contact1Rel by remember(currentProfile) { mutableStateOf(currentProfile.contact1Relationship) }
    var contact1Mobile by remember(currentProfile) { mutableStateOf(currentProfile.contact1Mobile) }

    var contact2Name by remember(currentProfile) { mutableStateOf(currentProfile.contact2Name) }
    var contact2Rel by remember(currentProfile) { mutableStateOf(currentProfile.contact2Relationship) }
    var contact2Mobile by remember(currentProfile) { mutableStateOf(currentProfile.contact2Mobile) }

    var hospitalName by remember(currentProfile) { mutableStateOf(currentProfile.hospitalName) }
    var hospitalPhone by remember(currentProfile) { mutableStateOf(currentProfile.hospitalPhone) }
    var hospitalAddress by remember(currentProfile) { mutableStateOf(currentProfile.hospitalAddress) }
    var emergencyPhone by remember(currentProfile) { mutableStateOf(currentProfile.emergencyServicePhone) }

    var countdownSeconds by remember(currentProfile) {
        mutableFloatStateOf(currentProfile.countdownDurationSeconds.toFloat())
    }

    var saveSuccessMessage by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = AccentCyan,
        unfocusedBorderColor = NavyBorder,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        focusedLabelColor = AccentCyan,
        unfocusedLabelColor = TextSecondary,
        cursorColor = AccentCyan
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeep)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        Text(
            text = "RIDER & EMERGENCY CONFIGURATION",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
        )
        Text(
            text = "Configure rider medical notes, designated family emergency contacts, and trauma center recipient endpoints.",
            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
        )

        if (saveSuccessMessage) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SafeGreen.copy(alpha = 0.2f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, SafeGreen),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Saved", tint = SafeGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Configuration saved successfully & persisted.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = SafeGreen, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        if (validationError != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EmergencyRed.copy(alpha = 0.2f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmergencyRed),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = "Error", tint = EmergencyRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = validationError ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(color = EmergencyRed, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        // Section 1: Rider Information
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Rider", tint = AccentCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RIDER DETAILS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Rider Full Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    singleLine = true
                )

                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = { mobileNumber = it },
                    label = { Text("Rider Mobile Number *") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = bloodGroup,
                        onValueChange = { bloodGroup = it },
                        label = { Text("Blood Group") },
                        modifier = Modifier.weight(1f),
                        colors = textFieldColors,
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = emergencyPhone,
                        onValueChange = { emergencyPhone = it },
                        label = { Text("EMS Service (911/112)") },
                        modifier = Modifier.weight(1f),
                        colors = textFieldColors,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = medicalNotes,
                    onValueChange = { medicalNotes = it },
                    label = { Text("Emergency Medical Notes / Allergies") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    maxLines = 3
                )
            }
        }

        // Section 2: Emergency Contact 1
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.ContactPhone, contentDescription = "Contact 1", tint = SafeGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "EMERGENCY CONTACT 1 (PRIMARY) *",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = contact1Name,
                        onValueChange = { contact1Name = it },
                        label = { Text("Contact Name *") },
                        modifier = Modifier.weight(1.2f),
                        colors = textFieldColors,
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = contact1Rel,
                        onValueChange = { contact1Rel = it },
                        label = { Text("Relationship") },
                        modifier = Modifier.weight(0.8f),
                        colors = textFieldColors,
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = contact1Mobile,
                    onValueChange = { contact1Mobile = it },
                    label = { Text("Contact Mobile Number *") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )
            }
        }

        // Section 3: Emergency Contact 2
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.ContactPhone, contentDescription = "Contact 2", tint = TextSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "EMERGENCY CONTACT 2 (SECONDARY)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = contact2Name,
                        onValueChange = { contact2Name = it },
                        label = { Text("Contact Name") },
                        modifier = Modifier.weight(1.2f),
                        colors = textFieldColors,
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = contact2Rel,
                        onValueChange = { contact2Rel = it },
                        label = { Text("Relationship") },
                        modifier = Modifier.weight(0.8f),
                        colors = textFieldColors,
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = contact2Mobile,
                    onValueChange = { contact2Mobile = it },
                    label = { Text("Contact Mobile Number") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )
            }
        }

        // Section 4: Preferred Hospital
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.LocalHospital, contentDescription = "Hospital", tint = EmergencyRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PREFERRED HOSPITAL / TRAUMA CENTER *",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }

                OutlinedTextField(
                    value = hospitalName,
                    onValueChange = { hospitalName = it },
                    label = { Text("Hospital Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    singleLine = true
                )

                OutlinedTextField(
                    value = hospitalPhone,
                    onValueChange = { hospitalPhone = it },
                    label = { Text("Emergency Ward Phone *") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )

                OutlinedTextField(
                    value = hospitalAddress,
                    onValueChange = { hospitalAddress = it },
                    label = { Text("Hospital Address / Location") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    singleLine = true
                )
            }
        }

        // Section 5: Countdown Duration Setting
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Timer, contentDescription = "Timer", tint = WarningAmber)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ACCIDENT CANCELLATION COUNTDOWN",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }
                    Text(
                        text = "${countdownSeconds.toInt()}s",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = WarningAmber
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Configurable window (5 to 10 seconds) allowing rider to abort false alarms before emergency dispatch.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, fontSize = 12.sp)
                )

                Slider(
                    value = countdownSeconds,
                    onValueChange = { countdownSeconds = it },
                    valueRange = 5f..10f,
                    steps = 4,
                    colors = SliderDefaults.colors(
                        thumbColor = WarningAmber,
                        activeTrackColor = WarningAmber,
                        inactiveTrackColor = NavyBorder
                    )
                )
            }
        }

        // Save & Reset Buttons
        Button(
            onClick = {
                // Validation
                if (fullName.isBlank()) {
                    validationError = "Please enter rider full name."
                    return@Button
                }
                if (mobileNumber.isBlank()) {
                    validationError = "Please enter rider phone number."
                    return@Button
                }
                if (contact1Name.isBlank() || contact1Mobile.isBlank()) {
                    validationError = "Emergency Contact 1 name and mobile number are required."
                    return@Button
                }
                if (hospitalName.isBlank() || hospitalPhone.isBlank()) {
                    validationError = "Preferred hospital name and phone number are required."
                    return@Button
                }

                validationError = null
                val updated = currentProfile.copy(
                    fullName = fullName.trim(),
                    mobileNumber = mobileNumber.trim(),
                    bloodGroup = bloodGroup.trim(),
                    medicalNotes = medicalNotes.trim(),
                    contact1Name = contact1Name.trim(),
                    contact1Relationship = contact1Rel.trim(),
                    contact1Mobile = contact1Mobile.trim(),
                    contact2Name = contact2Name.trim(),
                    contact2Relationship = contact2Rel.trim(),
                    contact2Mobile = contact2Mobile.trim(),
                    hospitalName = hospitalName.trim(),
                    hospitalPhone = hospitalPhone.trim(),
                    hospitalAddress = hospitalAddress.trim(),
                    emergencyServicePhone = emergencyPhone.trim(),
                    countdownDurationSeconds = countdownSeconds.toInt()
                )
                onSaveProfile(updated)
                saveSuccessMessage = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = NavyDeep)
        ) {
            Icon(imageVector = Icons.Default.Save, contentDescription = "Save")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Save Profile & Emergency Settings", fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
            onClick = {
                onResetDefaults()
                saveSuccessMessage = true
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
            border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Reset to Sample Demonstration Data")
        }
    }
}
