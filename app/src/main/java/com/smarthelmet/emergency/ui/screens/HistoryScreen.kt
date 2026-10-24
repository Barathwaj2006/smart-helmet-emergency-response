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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarthelmet.emergency.data.model.IncidentEntity
import com.smarthelmet.emergency.data.model.TimelineEvent
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
fun HistoryScreen(
    incidents: List<IncidentEntity>,
    parseEvents: (String) -> List<TimelineEvent>,
    onClearAllHistory: () -> Unit
) {
    var selectedIncident by remember { mutableStateOf<IncidentEntity?>(null) }
    var showConfirmClearDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeep)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "INCIDENT AUDIT LOGS",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "Persistent accident history, cancellation records & telemetries",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, fontSize = 12.sp)
                )
            }

            if (incidents.isNotEmpty()) {
                IconButton(onClick = { showConfirmClearDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear History",
                        tint = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (incidents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Empty History",
                        tint = TextMuted,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Incident Records Found",
                        style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Simulated accidents, countdown aborts, and emergency dispatches will appear here persistently.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, fontSize = 12.sp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(incidents) { incident ->
                    IncidentHistoryItem(
                        incident = incident,
                        onClick = { selectedIncident = incident }
                    )
                }
            }
        }
    }

    // Detail Dialog
    selectedIncident?.let { inc ->
        IncidentDetailDialog(
            incident = inc,
            events = parseEvents(inc.rawEventsJson),
            onDismiss = { selectedIncident = null }
        )
    }

    // Confirm Clear Dialog
    if (showConfirmClearDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmClearDialog = false },
            title = { Text("Clear All Incident History?") },
            text = { Text("This will permanently remove all stored local accident and cancellation audit records from the device.") },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllHistory()
                        showConfirmClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                ) {
                    Text("Delete All Records")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showConfirmClearDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = NavySurface,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary
        )
    }
}

@Composable
fun IncidentHistoryItem(
    incident: IncidentEntity,
    onClick: () -> Unit
) {
    val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val dateStr = timeFormat.format(Date(incident.timestampMillis))

    val isCancelled = incident.outcome == "CANCELLED"
    val outcomeColor = if (isCancelled) WarningAmber else EmergencyRed
    val outcomeLabel = if (isCancelled) "ABORTED (FALSE ALARM)" else "ASSISTANCE DISPATCHED"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = incident.incidentId,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(outcomeColor.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = outcomeLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = outcomeColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$dateStr  •  Rider: ${incident.riderName}",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, fontSize = 12.sp)
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isCancelled) "Reason: ${incident.cancellationReason ?: "User cancelled"}" else "Location: ${incident.locationLabel.ifBlank { "GPS Active" }}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isCancelled) WarningAmber else SafeGreen,
                    fontSize = 12.sp
                )
            )
        }
    }
}

@Composable
fun IncidentDetailDialog(
    incident: IncidentEntity,
    events: List<TimelineEvent>,
    onDismiss: () -> Unit
) {
    val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val dateStr = timeFormat.format(Date(incident.timestampMillis))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "INCIDENT AUDIT: ${incident.incidentId}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(text = "Timestamp: $dateStr", style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
                    Text(text = "Rider: ${incident.riderName} (${incident.riderPhone})", style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
                    Text(text = "Outcome: ${incident.outcome}", style = MaterialTheme.typography.bodyMedium.copy(color = if (incident.outcome == "CANCELLED") WarningAmber else SafeGreen, fontWeight = FontWeight.Bold))
                    if (incident.cancellationReason != null) {
                        Text(text = "Cancellation Reason: ${incident.cancellationReason}", style = MaterialTheme.typography.bodyMedium.copy(color = WarningAmber))
                    }
                    if (incident.latitude != null && incident.latitude != 0.0) {
                        Text(text = "Location: ${String.format("%.5f", incident.latitude)}, ${String.format("%.5f", incident.longitude)}", style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary))
                        Text(text = "Label: ${incident.locationLabel}", style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, fontSize = 11.sp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "TIMELINE EVENTS LOGGED:", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontWeight = FontWeight.Bold))
                }

                items(events) { ev ->
                    TimelineItemRow(event = ev, isLast = ev == events.lastOrNull())
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = NavyCard)) {
                Text("Close Audit Log")
            }
        },
        containerColor = NavySurface,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary
    )
}
