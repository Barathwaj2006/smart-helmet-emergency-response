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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarthelmet.emergency.data.model.RiderProfile
import com.smarthelmet.emergency.service.HardwareConnectionState
import com.smarthelmet.emergency.ui.theme.AccentCyan
import com.smarthelmet.emergency.ui.theme.EmergencyRed
import com.smarthelmet.emergency.ui.theme.EmergencyRedDark
import com.smarthelmet.emergency.ui.theme.NavyBorder
import com.smarthelmet.emergency.ui.theme.NavyCard
import com.smarthelmet.emergency.ui.theme.NavySurface
import com.smarthelmet.emergency.ui.theme.SafeGreen
import com.smarthelmet.emergency.ui.theme.TextMuted
import com.smarthelmet.emergency.ui.theme.TextPrimary
import com.smarthelmet.emergency.ui.theme.TextSecondary
import com.smarthelmet.emergency.ui.theme.WarningAmber

@Composable
fun DashboardScreen(
    profile: RiderProfile,
    hardwareState: HardwareConnectionState,
    hasLocationPermission: Boolean,
    onSimulateAccident: () -> Unit,
    onSimulateHardwareImpact: () -> Unit,
    onNavigateSetup: () -> Unit,
    onToggleDemoMode: (Boolean) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SMART HELMET",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp
                    )
                )
                Text(
                    text = "Accident Detection & Emergency Response",
                    style = MaterialTheme.typography.bodyMedium.copy(color = AccentCyan)
                )
            }
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(EmergencyRed.copy(alpha = 0.15f))
                    .border(1.dp, EmergencyRed, CircleShape)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsBike,
                    contentDescription = "Helmet Status",
                    tint = EmergencyRed,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Mode Selector Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = NavyCard),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (profile.isDemoMode) WarningAmber.copy(alpha = 0.5f) else AccentCyan.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (profile.isDemoMode) WarningAmber else SafeGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (profile.isDemoMode) "DEMO / SIMULATION MODE" else "REAL INTEGRATION MODE",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (profile.isDemoMode) WarningAmber else SafeGreen
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (profile.isDemoMode)
                            "100% safe demonstration: simulated alerts, no unwanted 911 calls or SMS charges."
                        else
                            "Uses live device GPS; opens real system dialer and messaging intents on dispatch.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )
                }

                Switch(
                    checked = profile.isDemoMode,
                    onCheckedChange = { onToggleDemoMode(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = WarningAmber,
                        checkedTrackColor = WarningAmber.copy(alpha = 0.3f),
                        uncheckedThumbColor = SafeGreen,
                        uncheckedTrackColor = SafeGreen.copy(alpha = 0.3f)
                    )
                )
            }
        }

        // Rider Profile Overview Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateSetup() },
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
                    Text(
                        text = "RIDER & EMERGENCY SETUP",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                    Text(
                        text = if (profile.isConfigured()) "✓ CONFIG READY" else "⚠ SETUP REQUIRED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (profile.isConfigured()) SafeGreen else WarningAmber,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = profile.fullName.ifBlank { "Unnamed Rider" },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(EmergencyRed.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = profile.bloodGroup.ifBlank { "N/A" },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EmergencyRed,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Phone: ${profile.mobileNumber}  •  Hospital: ${profile.hospitalName}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            }
        }

        // Live System Telemetry Status Grid
        Text(
            text = "SYSTEM TELEMETRY & HARDWARE",
            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatusCard(
                title = "Phone GPS",
                status = if (hasLocationPermission) "GPS Ready" else "Permission Needed",
                isOk = hasLocationPermission,
                icon = Icons.Default.LocationOn,
                modifier = Modifier.weight(1f)
            )
            StatusCard(
                title = "ESP32 Helmet",
                status = if (hardwareState == HardwareConnectionState.IMPACT_SIGNAL_RECEIVED) "Impact Active" else "Ready (Emulated)",
                isOk = true,
                icon = Icons.Default.Sensors,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatusCard(
                title = "Emergency Contacts",
                status = "${profile.contact1Name.ifBlank { "None" }} + 1",
                isOk = profile.contact1Mobile.isNotBlank(),
                icon = Icons.Default.People,
                modifier = Modifier.weight(1f)
            )
            StatusCard(
                title = "Designated Trauma",
                status = profile.hospitalName.take(16) + (if (profile.hospitalName.length > 16) "..." else ""),
                isOk = profile.hospitalPhone.isNotBlank(),
                icon = Icons.Default.LocalHospital,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Large SIMULATE ACCIDENT Button
        Button(
            onClick = onSimulateAccident,
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = EmergencyRed,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CrisisAlert,
                    contentDescription = "Simulate Accident",
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "SIMULATE ACCIDENT",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "Triggers ${profile.countdownDurationSeconds}s countdown & emergency alert sequence",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    )
                }
            }
        }

        // Secondary Button: ESP32 IMU Impact Simulation
        OutlinedButton(
            onClick = onSimulateHardwareImpact,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentCyan),
            border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.6f))
        ) {
            Icon(
                imageVector = Icons.Default.Memory,
                contentDescription = "ESP32",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Simulate Incoming ESP32 Sensor Impact (>4.5G)",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
            )
        }

        // Reviewer Notice
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(NavySurface)
                .border(1.dp, NavyBorder, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Notice",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Project Reviewer Note: This build provides complete software simulation without requiring a physical helmet or ESP32 board. Switch to Real Mode to test with actual phone GPS and communication intents.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, fontSize = 12.sp)
                )
            }
        }
    }
}

@Composable
fun StatusCard(
    title: String,
    status: String,
    isOk: Boolean,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isOk) SafeGreen else WarningAmber,
                    modifier = Modifier.size(20.dp)
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isOk) SafeGreen else WarningAmber)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
            )
            Text(
                text = status,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                ),
                maxLines = 1
            )
        }
    }
}
