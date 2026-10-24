package com.smarthelmet.emergency.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarthelmet.emergency.data.model.EventStatus
import com.smarthelmet.emergency.data.model.TimelineEvent
import com.smarthelmet.emergency.service.EmergencyAlertMessage
import com.smarthelmet.emergency.service.LocationResult
import com.smarthelmet.emergency.service.RecipientStatus
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun IncidentResponseScreen(
    incidentId: String,
    timestampStr: String,
    location: LocationResult?,
    alertMessage: EmergencyAlertMessage?,
    recipients: List<RecipientStatus>,
    timelineEvents: List<TimelineEvent>,
    isDemoMode: Boolean,
    onManualAdvanceStage: () -> Unit,
    onResetToDashboard: () -> Unit,
    onDialNumber: (String) -> Unit,
    onSendSms: (String, String) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeep)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Incident Header Badge
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = EmergencyRed.copy(alpha = 0.15f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, EmergencyRed),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(EmergencyRed)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "EMERGENCY ASSISTANCE ACTIVATED",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = EmergencyRed
                            )
                        )
                    }
                    if (isDemoMode) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(WarningAmber.copy(alpha = 0.25f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "DEMO SIMULATION",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = WarningAmber
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Incident ID: $incidentId  •  $timestampStr",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, fontSize = 12.sp)
                )
            }
        }

        // GPS Telemetry Card
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
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = AccentCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ACCIDENT LOCATION",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }

                    val badgeColor = if (location?.isSimulated == true) WarningAmber else SafeGreen
                    val badgeLabel = if (location?.isSimulated == true) "DEMO GPS" else "LIVE GPS FIX"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(badgeColor.copy(alpha = 0.2f))
                            .border(1.dp, badgeColor, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = badgeColor,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (location != null && location.latitude != 0.0) {
                    Text(
                        text = "Lat: ${String.format("%.6f", location.latitude)}, Lon: ${String.format("%.6f", location.longitude)}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "${location.statusDescription} (Accuracy ±${location.accuracyMeters.toInt()}m)",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(location.toGoogleMapsUrl())).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            try {
                                context.startActivity(mapIntent)
                            } catch (_: Exception) {}
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = NavyDeep),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Map, contentDescription = "Map")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "View on Live Google Maps", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text(
                        text = location?.statusDescription ?: "Location unavailable",
                        style = MaterialTheme.typography.bodyLarge.copy(color = WarningAmber)
                    )
                }
            }
        }

        // Recipient Status Cards (Honest Real vs Simulated)
        Text(
            text = "EMERGENCY NOTIFICATION DISPATCH",
            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
        )

        recipients.forEach { recipient ->
            RecipientItemCard(
                recipient = recipient,
                alertMessage = alertMessage?.fullText ?: "",
                isDemoMode = isDemoMode,
                onDialNumber = onDialNumber,
                onSendSms = onSendSms
            )
        }

        // Chronological Incident Timeline
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
                    Text(
                        text = "INCIDENT TIMELINE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )

                    if (isDemoMode) {
                        OutlinedButton(
                            onClick = onManualAdvanceStage,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentCyan),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.FastForward, contentDescription = "Advance", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Next Stage", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                timelineEvents.forEachIndexed { index, event ->
                    TimelineItemRow(
                        event = event,
                        isLast = index == timelineEvents.lastIndex
                    )
                }
            }
        }

        // Action Buttons: Share Alert and Return/Reset
        if (alertMessage != null) {
            OutlinedButton(
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "EMERGENCY: Smart Helmet Accident Alert")
                        putExtra(Intent.EXTRA_TEXT, alertMessage.fullText)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Emergency SOS Telemetry"))
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Export / Share SOS Telemetry Payload", fontWeight = FontWeight.SemiBold)
            }
        }

        Button(
            onClick = onResetToDashboard,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NavyCard, contentColor = TextPrimary),
            border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Return")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "End Incident / Return to Dashboard", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun RecipientItemCard(
    recipient: RecipientStatus,
    alertMessage: String,
    isDemoMode: Boolean,
    onDialNumber: (String) -> Unit,
    onSendSms: (String, String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                Column {
                    Text(
                        text = recipient.recipientName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "${recipient.role} • ${recipient.phoneNumber}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, fontSize = 12.sp)
                    )
                }

                val statusColor = if (recipient.isSimulated) WarningAmber else SafeGreen
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(statusColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = recipient.status,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = recipient.statusDetail,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (recipient.isSimulated) WarningAmber else TextSecondary,
                    fontSize = 12.sp
                )
            )

            // Direct Call / SMS intent buttons if in Real Mode or for operator demonstration
            if (!isDemoMode && recipient.phoneNumber.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onDialNumber(recipient.phoneNumber) },
                        colors = ButtonDefaults.buttonColors(containerColor = SafeGreen, contentColor = NavyDeep),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Call", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onSendSms(recipient.phoneNumber, alertMessage) },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = NavyDeep),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Message, contentDescription = "SMS", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Send SMS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineItemRow(
    event: TimelineEvent,
    isLast: Boolean
) {
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(event.timestampMillis))

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(
                        when (event.status) {
                            EventStatus.COMPLETED -> SafeGreen
                            EventStatus.ACTIVE -> WarningAmber
                            EventStatus.CANCELLED -> EmergencyRed
                            EventStatus.FAILED -> EmergencyRed
                            else -> TextMuted
                        }
                    )
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(38.dp)
                        .background(NavyBorder)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                )
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, fontSize = 12.sp)
            )
        }
    }
}
