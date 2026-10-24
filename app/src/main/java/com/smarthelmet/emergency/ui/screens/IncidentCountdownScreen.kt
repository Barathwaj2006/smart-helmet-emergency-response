package com.smarthelmet.emergency.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SportsMotorsports
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarthelmet.emergency.ui.theme.EmeraldDark
import com.smarthelmet.emergency.ui.theme.EmeraldLight
import com.smarthelmet.emergency.ui.theme.EmeraldLive
import com.smarthelmet.emergency.ui.theme.ErrorContainer
import com.smarthelmet.emergency.ui.theme.InverseOnSurface
import com.smarthelmet.emergency.ui.theme.InverseSurface
import com.smarthelmet.emergency.ui.theme.OnPrimaryContainer
import com.smarthelmet.emergency.ui.theme.OnSurface
import com.smarthelmet.emergency.ui.theme.OnTertiaryContainer
import com.smarthelmet.emergency.ui.theme.PrimaryContainer
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
    var showCancellationDialog by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
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
            .background(Surface)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top App Bar / Branding
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsMotorsports,
                        contentDescription = "Helmet",
                        tint = OnSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Suraksha Helmet",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    )
                    Text(
                        text = "Active Emergency Alert",
                        style = MaterialTheme.typography.bodySmall.copy(color = TertiaryRed, fontWeight = FontWeight.SemiBold)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceContainerHigh)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "#$incidentId",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                )
            }
        }

        // Simulation Mode Ribbon
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = InverseSurface),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = "Sim",
                        tint = PrimaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SIMULATION MODE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryContainer,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
                Text(
                    text = "No live dispatch",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = InverseOnSurface.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                )
            }
        }

        // High-G Impact Detected Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = ErrorContainer),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CrisisAlert,
                            contentDescription = "Impact",
                            tint = TertiaryRed,
                            modifier = Modifier
                                .size(24.dp)
                                .scale(pulseScale)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "HIGH-G IMPACT DETECTED",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = OnTertiaryContainer,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(TertiaryRed)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "5.8G",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Alarm",
                        tint = TertiaryRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Audible siren playing at maximum volume",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnTertiaryContainer.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }

        // Big Circular Countdown Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Circular Progress
                val progress = countdownRemaining.toFloat() / countdownTotal.toFloat()
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(175.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 10.dp.toPx()
                        // Background track
                        drawCircle(
                            color = SurfaceContainerHighest,
                            style = Stroke(width = strokeWidth)
                        )
                        // Active countdown sweep
                        drawArc(
                            color = TertiaryRed,
                            startAngle = -90f,
                            sweepAngle = 360f * progress,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val formattedSeconds = if (countdownRemaining < 10) "00:0$countdownRemaining" else "00:$countdownRemaining"
                        Text(
                            text = formattedSeconds,
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = OnSurface,
                                letterSpacing = (-1).sp
                            )
                        )
                        Text(
                            text = "SECONDS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextSecondaryColor,
                                letterSpacing = 2.sp
                            )
                        )
                    }
                }

                Text(
                    text = "Emergency SOS protocol will trigger in $countdownRemaining seconds if not cancelled.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = OnSurface,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Beacon capsule
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceContainerLow)
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(TertiaryRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Transmitting beacon broadcast",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = OnSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }

        // Giant Stop Action Button (I AM SAFE - STOP SOS)
        Button(
            onClick = {
                showCancellationDialog = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TertiaryRed,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Stop",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "I AM SAFE — STOP SOS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp,
                            color = Color.White
                        )
                    )
                }
                Text(
                    text = "Tap immediately to stop automatic alert dispatch",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.sp
                    )
                )
            }
        }

        // Telemetry Snapshot Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            shape = RoundedCornerShape(14.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TELEMETRY SNAPSHOT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextSecondaryColor
                        )
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(SurfaceContainerHigh)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "LOG #9410",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextSecondaryColor,
                                fontSize = 9.sp
                            )
                        )
                    }
                }

                // Location strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceContainerLow)
                        .padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = PrimaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Near Sony World Signal",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                        )
                        Text(
                            text = "Koramangala 80ft Road, Bengaluru",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryColor)
                        )
                        Text(
                            text = "12.9352° N, 77.6245° E",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMutedColor)
                        )
                    }
                }

                // Grid 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceContainerLow)
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(
                                text = "IMPACT VECTOR",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    color = TextSecondaryColor
                                )
                            )
                            Text(
                                text = "Torque Spike",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface,
                                    fontSize = 12.sp
                                )
                            )
                            Text(
                                text = formattedTime.takeLast(8),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = TextSecondaryColor
                                )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceContainerLow)
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(
                                text = "RESPONSE HUB",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    color = TextSecondaryColor
                                )
                            )
                            Text(
                                text = "Manipal Trauma",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface,
                                    fontSize = 12.sp
                                )
                            )
                            Text(
                                text = "Priority Queue",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = TextSecondaryColor
                                )
                            )
                        }
                    }
                }

                // Auto SMS notice
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryContainer.copy(alpha = 0.2f))
                        .padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Alert",
                        tint = OnPrimaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "At 00:00, automated SMS with live GPS link will be sent to 2 primary contacts and Trauma Network.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurface,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    )
                }
            }
        }

        // Accidental Trigger Note
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceContainerLow)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Note",
                tint = TextSecondaryColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Accidentally triggered? Press Cancel. Your emergency contacts will receive an 'All Safe' confirmation notice.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondaryColor,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            )
        }
    }

    // Cancellation Modal / Overlay
    if (showCancellationDialog) {
        AlertDialog(
            onDismissRequest = { /* Require explicit confirmation */ },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PrimaryContainer.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Safe",
                            tint = OnPrimaryContainer,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Emergency Cancelled",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            },
            text = {
                Text(
                    text = "Dispatcher notification aborted. Standby message sent to emergency contacts: \"Rider safe and responsive.\"",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondaryColor)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCancellationDialog = false
                        onCancelEmergency("Rider safe - false alarm / drop test")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = InverseSurface, contentColor = InverseOnSurface),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "RETURN TO DASHBOARD",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            },
            containerColor = SurfaceContainerLowest,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
