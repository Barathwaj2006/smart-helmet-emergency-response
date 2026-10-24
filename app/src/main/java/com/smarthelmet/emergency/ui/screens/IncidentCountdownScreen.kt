package com.smarthelmet.emergency.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarthelmet.emergency.ui.theme.EmergencyRed
import com.smarthelmet.emergency.ui.theme.EmergencyRedDark
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
fun IncidentCountdownScreen(
    incidentId: String,
    formattedTime: String,
    countdownRemaining: Int,
    countdownTotal: Int,
    isAlarmActive: Boolean,
    isDemoMode: Boolean,
    onCancelEmergency: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    var selectedReason by remember {
        mutableStateOf("False alarm / Helmet dropped or tested")
    }

    val reasons = listOf(
        "False alarm / Helmet dropped or tested",
        "Minor bump / Rider OK, no assistance required",
        "Accidental simulator trigger",
        "Hardware sensor calibration test"
    )

    // Pulse animation for the emergency badge
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeep)
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // High visibility Emergency Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(EmergencyRed.copy(alpha = 0.2f))
                .border(2.dp, EmergencyRed, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CrisisAlert,
                        contentDescription = "Alert",
                        tint = EmergencyRed,
                        modifier = Modifier
                            .size(24.dp)
                            .scale(pulseScale)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ACCIDENT IMPACT DETECTED!",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = EmergencyRed,
                            letterSpacing = 1.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ID: $incidentId  •  $formattedTime",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, fontSize = 12.sp)
                )
            }
        }

        // Circular Countdown Display
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(170.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(EmergencyRedDark.copy(alpha = 0.35f))
                .border(4.dp, EmergencyRed, CircleShape)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$countdownRemaining",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                )
                Text(
                    text = "SECONDS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 2.sp
                    )
                )
            }
        }

        // Alarm status indicator
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(NavyCard)
                .border(1.dp, NavyBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = "Audio Alarm",
                tint = if (isAlarmActive) WarningAmber else TextMuted,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isAlarmActive) "ALARM SOUND & VIBRATION ACTIVE" else "ALARM MUTED",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isAlarmActive) WarningAmber else TextMuted
                )
            )
        }

        // Prominent Cancellation Button (CRITICAL REQUIREMENT)
        Button(
            onClick = { onCancelEmergency(selectedReason) },
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SafeGreen,
                contentColor = NavyDeep
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = "Cancel Emergency",
                    modifier = Modifier.size(28.dp),
                    tint = NavyDeep
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "STOP EMERGENCY PROCEDURE\nFALSE ALARM / NOT SEVERE",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.5.sp,
                        color = NavyDeep
                    )
                )
            }
        }

        Text(
            text = "Hardware Key Support: Pressing Volume Down or Enter also safely halts countdown.",
            style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted, fontSize = 11.sp),
            textAlign = TextAlign.Center
        )

        // Cancellation Reason Selector
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "REASON FOR CANCELLATION (LOGGED FOR AUDIT):",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))

                reasons.forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReason = reason }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (reason == selectedReason),
                            onClick = { selectedReason = reason },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = SafeGreen,
                                unselectedColor = TextMuted
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = reason,
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
                        )
                    }
                }
            }
        }

        // Consequence Notice
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = NavyCard),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Security Note",
                    tint = WarningAmber,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "If no cancellation occurs before $countdownTotal seconds elapse, live GPS coordinates and trauma alert telemetry will be transmitted automatically.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, fontSize = 12.sp)
                )
            }
        }
    }
}
