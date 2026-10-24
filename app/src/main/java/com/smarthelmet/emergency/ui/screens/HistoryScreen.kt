package com.smarthelmet.emergency.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarthelmet.emergency.data.model.IncidentEntity
import com.smarthelmet.emergency.data.model.TimelineEvent
import com.smarthelmet.emergency.ui.theme.EmeraldDark
import com.smarthelmet.emergency.ui.theme.EmeraldLight
import com.smarthelmet.emergency.ui.theme.InverseOnSurface
import com.smarthelmet.emergency.ui.theme.InverseSurface
import com.smarthelmet.emergency.ui.theme.OnPrimaryContainer
import com.smarthelmet.emergency.ui.theme.OnSurface
import com.smarthelmet.emergency.ui.theme.OnTertiaryContainer
import com.smarthelmet.emergency.ui.theme.PrimaryContainer
import com.smarthelmet.emergency.ui.theme.Surface
import com.smarthelmet.emergency.ui.theme.SurfaceContainer
import com.smarthelmet.emergency.ui.theme.SurfaceContainerHigh
import com.smarthelmet.emergency.ui.theme.SurfaceContainerLow
import com.smarthelmet.emergency.ui.theme.SurfaceContainerLowest
import com.smarthelmet.emergency.ui.theme.TertiaryContainer
import com.smarthelmet.emergency.ui.theme.TertiaryRed
import com.smarthelmet.emergency.ui.theme.TextMutedColor
import com.smarthelmet.emergency.ui.theme.TextSecondaryColor
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
            .background(Surface)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Incident Audit Logs",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        letterSpacing = (-0.5).sp
                    )
                )
                Text(
                    text = "Persistent accident history, cancellation records & telemetries",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryColor)
                )
            }

            if (incidents.isNotEmpty()) {
                IconButton(onClick = { showConfirmClearDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear History",
                        tint = TextSecondaryColor
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
                        tint = TextSecondaryColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Incident Records Found",
                        style = MaterialTheme.typography.titleMedium.copy(color = OnSurface, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Simulated accidents, countdown aborts, and emergency dispatches will appear here persistently.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryColor),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(incidents) { incident ->
                    SurakshaHistoryCard(
                        incident = incident,
                        onClick = { selectedIncident = incident }
                    )
                }
            }
        }
    }

    // Detail Dialog
    selectedIncident?.let { inc ->
        SurakshaHistoryDetailDialog(
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
                    colors = ButtonDefaults.buttonColors(containerColor = TertiaryRed)
                ) {
                    Text("Delete All Records")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showConfirmClearDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = SurfaceContainerLowest,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun SurakshaHistoryCard(
    incident: IncidentEntity,
    onClick: () -> Unit
) {
    val timeFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
    val dateStr = timeFormat.format(Date(incident.timestampMillis))

    val isCancelled = incident.outcome == "CANCELLED"
    val badgeBg = if (isCancelled) PrimaryContainer.copy(alpha = 0.35f) else TertiaryContainer.copy(alpha = 0.6f)
    val badgeText = if (isCancelled) OnPrimaryContainer else OnTertiaryContainer
    val outcomeLabel = if (isCancelled) "ABORTED (SAFE)" else "DISPATCHED"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${incident.incidentId}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeBg)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = outcomeLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = badgeText
                        )
                    )
                }
            }

            Text(
                text = "$dateStr · Rider: ${incident.riderName}",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryColor)
            )

            Text(
                text = if (isCancelled) "Reason: ${incident.cancellationReason ?: "Rider safe"}" else "Location: ${incident.locationLabel.ifBlank { "GPS Active" }}",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = if (isCancelled) OnPrimaryContainer else EmeraldDark
                )
            )
        }
    }
}

@Composable
fun SurakshaHistoryDetailDialog(
    incident: IncidentEntity,
    events: List<TimelineEvent>,
    onDismiss: () -> Unit
) {
    val timeFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault())
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
                    text = "AUDIT: #${incident.incidentId}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryColor)
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(text = "Timestamp: $dateStr", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryColor))
                    Text(text = "Rider: ${incident.riderName} (${incident.riderPhone})", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryColor))
                    Text(text = "Outcome: ${incident.outcome}", style = MaterialTheme.typography.bodySmall.copy(color = OnSurface, fontWeight = FontWeight.Bold))
                    if (incident.cancellationReason != null) {
                        Text(text = "Reason: ${incident.cancellationReason}", style = MaterialTheme.typography.bodySmall.copy(color = OnPrimaryContainer))
                    }
                    if (incident.latitude != null && incident.latitude != 0.0) {
                        Text(text = "Location: ${String.format("%.5f", incident.latitude)}, ${String.format("%.5f", incident.longitude)}", style = MaterialTheme.typography.bodySmall.copy(color = OnSurface))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "TIMELINE MILESTONES:", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryColor, fontWeight = FontWeight.Bold))
                }

                items(events) { ev ->
                    SurakshaTimelineRow(event = ev, isLast = ev == events.lastOrNull())
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = InverseSurface, contentColor = InverseOnSurface),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Close Audit Log")
            }
        },
        containerColor = SurfaceContainerLowest,
        shape = RoundedCornerShape(16.dp)
    )
}
