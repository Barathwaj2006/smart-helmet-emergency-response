package com.smarthelmet.emergency.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarthelmet.emergency.data.model.RiderProfile
import com.smarthelmet.emergency.ui.theme.EmeraldDark
import com.smarthelmet.emergency.ui.theme.EmeraldLight
import com.smarthelmet.emergency.ui.theme.EmeraldLive
import com.smarthelmet.emergency.ui.theme.OnPrimaryContainer
import com.smarthelmet.emergency.ui.theme.OnSurface
import com.smarthelmet.emergency.ui.theme.OnTertiaryContainer
import com.smarthelmet.emergency.ui.theme.OutlineVariant
import com.smarthelmet.emergency.ui.theme.PrimaryContainer
import com.smarthelmet.emergency.ui.theme.PrimaryGolden
import com.smarthelmet.emergency.ui.theme.Surface
import com.smarthelmet.emergency.ui.theme.SurfaceContainer
import com.smarthelmet.emergency.ui.theme.SurfaceContainerHigh
import com.smarthelmet.emergency.ui.theme.SurfaceContainerHighest
import com.smarthelmet.emergency.ui.theme.SurfaceContainerLow
import com.smarthelmet.emergency.ui.theme.SurfaceContainerLowest
import com.smarthelmet.emergency.ui.theme.TertiaryContainer
import com.smarthelmet.emergency.ui.theme.TertiaryRed
import com.smarthelmet.emergency.ui.theme.TextMutedColor
import com.smarthelmet.emergency.ui.theme.TextSecondaryColor

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
    var vehiclePlate by remember(currentProfile) { mutableStateOf(currentProfile.vehiclePlate) }
    var vehicleModel by remember(currentProfile) { mutableStateOf(currentProfile.vehicleModel) }
    var bloodGroup by remember(currentProfile) { mutableStateOf(currentProfile.bloodGroup) }
    var medicalNotes by remember(currentProfile) { mutableStateOf(currentProfile.medicalNotes) }
    var healthId by remember(currentProfile) { mutableStateOf(currentProfile.healthId) }

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

    var showSyncSuccess by remember { mutableStateOf(false) }

    val bloodOptions = listOf("O+", "A+", "B+", "AB+", "O-", "AB-")

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = PrimaryGolden,
        unfocusedBorderColor = SurfaceContainerHigh,
        focusedTextColor = OnSurface,
        unfocusedTextColor = OnSurface,
        focusedContainerColor = SurfaceContainerLow,
        unfocusedContainerColor = SurfaceContainerLow,
        focusedLabelColor = PrimaryGolden,
        unfocusedLabelColor = TextSecondaryColor,
        cursorColor = PrimaryGolden
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Banner Section
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = "Vault",
                    tint = PrimaryGolden,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "EMERGENCY VAULT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGolden,
                        letterSpacing = 1.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Rider & Emergency Setup",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    letterSpacing = (-0.5).sp
                )
            )
            Text(
                text = "Critical information used to notify family and dispatch medical responders in accidents.",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondaryColor)
            )
        }

        // Real-time Sync Status Notification
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            shape = RoundedCornerShape(14.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(EmeraldLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = "Synced",
                        tint = EmeraldDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Synced to Smart Helmet Memory",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    )
                    Text(
                        text = "Offline cached via Bluetooth Flash (v3.2)",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryColor)
                    )
                }
            }
        }

        if (showSyncSuccess) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(EmeraldLight)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = EmeraldDark,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Safety profile written to Helmet Flash storage.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDark
                    )
                )
            }
        }

        // Section A: Rider Details
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(PrimaryContainer)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Rider Identity",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerHighest)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "GOVT VERIFIED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextSecondaryColor,
                            fontSize = 9.sp
                        )
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Rider Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(10.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = "Name", tint = TextSecondaryColor)
                        }
                    )

                    OutlinedTextField(
                        value = mobileNumber,
                        onValueChange = { mobileNumber = it },
                        label = { Text("Primary Contact (Indian SIM)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        leadingIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                            ) {
                                Text(
                                    text = "🇮🇳 +91",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        },
                        trailingIcon = {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Valid", tint = EmeraldLive)
                        }
                    )

                    OutlinedTextField(
                        value = vehiclePlate,
                        onValueChange = { vehiclePlate = it },
                        label = { Text("Two-Wheeler Reg. Plate") },
                        placeholder = { Text("e.g. KA 01 EQ 4290 / MH 02 AB 1234") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(10.dp),
                        leadingIcon = {
                            Icon(Icons.Default.TwoWheeler, contentDescription = "Plate", tint = TextSecondaryColor)
                        },
                        trailingIcon = {
                            Box(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(SurfaceContainerHigh)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "IND",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    )

                    OutlinedTextField(
                        value = vehicleModel,
                        onValueChange = { vehicleModel = it },
                        label = { Text("Vehicle Make & Model") },
                        placeholder = { Text("e.g. Royal Enfield Hunter 350 / Honda Activa") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(10.dp),
                        leadingIcon = {
                            Icon(Icons.Default.TwoWheeler, contentDescription = "Model", tint = TextSecondaryColor)
                        }
                    )
                }
            }
        }

        // Section B: Emergency Medical Data
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(TertiaryRed)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Emergency Medical Data",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(TertiaryContainer.copy(alpha = 0.5f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "First Responder Priority",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TertiaryRed,
                            fontSize = 9.sp
                        )
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Blood Group Pills
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Blood Group",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Emergency,
                                    contentDescription = "Alert",
                                    tint = TertiaryRed,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Universal Recipient Ready",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TertiaryRed,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            bloodOptions.forEach { group ->
                                val isSelected = bloodGroup.equals(group, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) TertiaryRed else SurfaceContainerLow)
                                        .clickable { bloodGroup = group }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = group,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else OnSurface
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Known Allergies
                    OutlinedTextField(
                        value = medicalNotes,
                        onValueChange = { medicalNotes = it },
                        label = { Text("Known Allergies / Pre-existing Conditions") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(10.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Warning, contentDescription = "Allergies", tint = TertiaryRed)
                        },
                        maxLines = 3
                    )

                    // ABHA Health ID
                    OutlinedTextField(
                        value = healthId,
                        onValueChange = { healthId = it },
                        label = { Text("Ayushman Bharat (ABHA) / Health ID") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(10.dp),
                        leadingIcon = {
                            Icon(Icons.Default.HealthAndSafety, contentDescription = "Health ID", tint = TextSecondaryColor)
                        },
                        trailingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "Secure", tint = EmeraldLive)
                        }
                    )
                }
            }
        }

        // Section C: Emergency Contacts (2 Configured)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(PrimaryContainer)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Emergency Contacts",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    )
                }
                val configuredCount = (if (contact1Name.isNotBlank() && contact1Mobile.isNotBlank()) 1 else 0) +
                        (if (contact2Name.isNotBlank() && contact2Mobile.isNotBlank()) 1 else 0)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryContainer.copy(alpha = 0.35f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "$configuredCount Configured",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnPrimaryContainer,
                            fontSize = 9.sp
                        )
                    )
                }
            }

            // Contact 1 Card (Editable)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Primary Emergency Kin",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(EmeraldLight)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Primary SOS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDark
                                )
                            )
                        }
                    }

                    // Contact 1 Name
                    OutlinedTextField(
                        value = contact1Name,
                        onValueChange = { contact1Name = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Primary Kin Full Name") },
                        placeholder = { Text("Enter full legal name") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = "Name", tint = PrimaryGolden)
                        },
                        colors = textFieldColors,
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    // Contact 1 Relationship
                    OutlinedTextField(
                        value = contact1Rel,
                        onValueChange = { contact1Rel = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Relationship") },
                        placeholder = { Text("e.g. Father, Mother, Spouse, Friend") },
                        colors = textFieldColors,
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    // Contact 1 Phone
                    OutlinedTextField(
                        value = contact1Mobile,
                        onValueChange = { contact1Mobile = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Mobile Number (SMS & Calls)") },
                        placeholder = { Text("e.g. +91 98765 43210") },
                        leadingIcon = {
                            Icon(Icons.Default.Call, contentDescription = "Phone", tint = PrimaryGolden)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }
            }

            // Contact 2 Card (Editable)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Secondary Emergency Kin",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceContainerHigh)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Secondary",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondaryColor
                                )
                            )
                        }
                    }

                    // Contact 2 Name
                    OutlinedTextField(
                        value = contact2Name,
                        onValueChange = { contact2Name = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Secondary Kin Full Name") },
                        placeholder = { Text("Enter secondary contact name") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = "Name", tint = PrimaryGolden)
                        },
                        colors = textFieldColors,
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    // Contact 2 Relationship
                    OutlinedTextField(
                        value = contact2Rel,
                        onValueChange = { contact2Rel = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Relationship") },
                        placeholder = { Text("e.g. Sibling, Spouse, Relative, Colleague") },
                        colors = textFieldColors,
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    // Contact 2 Phone
                    OutlinedTextField(
                        value = contact2Mobile,
                        onValueChange = { contact2Mobile = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Mobile Number (SMS & Calls)") },
                        placeholder = { Text("e.g. +91 99801 54321") },
                        leadingIcon = {
                            Icon(Icons.Default.Call, contentDescription = "Phone", tint = PrimaryGolden)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }
            }
        }

        // Section D: Preferred Hospital & Trauma Facility (Editable)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(TextSecondaryColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Preferred Trauma Facility",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Hospital Name
                    OutlinedTextField(
                        value = hospitalName,
                        onValueChange = { hospitalName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Preferred Hospital / Trauma Center") },
                        placeholder = { Text("e.g. City General Hospital or Apollo") },
                        leadingIcon = {
                            Icon(Icons.Default.LocalHospital, contentDescription = "Hospital", tint = TertiaryRed)
                        },
                        colors = textFieldColors,
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    // Hospital Emergency Phone
                    OutlinedTextField(
                        value = hospitalPhone,
                        onValueChange = { hospitalPhone = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Trauma Desk / Emergency Phone") },
                        placeholder = { Text("e.g. +91 80 1234 5678 or 108") },
                        leadingIcon = {
                            Icon(Icons.Default.Call, contentDescription = "Hospital Phone", tint = PrimaryGolden)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    // Hospital Address
                    OutlinedTextField(
                        value = hospitalAddress,
                        onValueChange = { hospitalAddress = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Hospital Address / Landmark") },
                        placeholder = { Text("e.g. Main Hospital Road, Near Bus Terminus") },
                        leadingIcon = {
                            Icon(Icons.Default.Navigation, contentDescription = "Address", tint = PrimaryGolden)
                        },
                        colors = textFieldColors,
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    // Geofence notice
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceContainerLow)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = "Nav",
                            tint = PrimaryGolden,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Direct ambulance geofence registered with 108 Emergency EMS",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryColor)
                        )
                    }
                }
            }
        }

        // Section E: Countdown Setting Slider
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cancellation Window",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    )
                    Text(
                        text = "${countdownSeconds.toInt()}s",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = PrimaryGolden
                        )
                    )
                }
                Slider(
                    value = countdownSeconds,
                    onValueChange = { countdownSeconds = it },
                    valueRange = 5f..10f,
                    steps = 4,
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryGolden,
                        activeTrackColor = PrimaryGolden,
                        inactiveTrackColor = SurfaceContainerHigh
                    )
                )
            }
        }

        // SAVE & SYNC PROFILE Action Button
        Column(
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    val updated = currentProfile.copy(
                        fullName = fullName.trim(),
                        mobileNumber = mobileNumber.trim(),
                        vehiclePlate = vehiclePlate.trim(),
                        vehicleModel = vehicleModel.trim(),
                        bloodGroup = bloodGroup.trim(),
                        medicalNotes = medicalNotes.trim(),
                        healthId = healthId.trim(),
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
                    showSyncSuccess = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryContainer,
                    contentColor = OnSurface
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CloudDone,
                    contentDescription = "Sync",
                    tint = OnSurface,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SAVE & SYNC PROFILE",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = OnSurface,
                        letterSpacing = 0.5.sp
                    )
                )
            }

            Text(
                text = "Instant over-the-air BLE encryption (AES-128)",
                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryColor)
            )
        }
    }
}
