package com.smarthelmet.emergency

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.smarthelmet.emergency.ui.screens.DashboardScreen
import com.smarthelmet.emergency.ui.screens.HistoryScreen
import com.smarthelmet.emergency.ui.screens.IncidentCountdownScreen
import com.smarthelmet.emergency.ui.screens.IncidentResponseScreen
import com.smarthelmet.emergency.ui.screens.SetupScreen
import com.smarthelmet.emergency.ui.theme.AccentCyan
import com.smarthelmet.emergency.ui.theme.EmergencyRed
import com.smarthelmet.emergency.ui.theme.NavyBorder
import com.smarthelmet.emergency.ui.theme.NavyCard
import com.smarthelmet.emergency.ui.theme.NavyDeep
import com.smarthelmet.emergency.ui.theme.SmartHelmetTheme
import com.smarthelmet.emergency.ui.theme.TextMuted
import com.smarthelmet.emergency.ui.theme.TextPrimary
import com.smarthelmet.emergency.ui.theme.TextSecondary
import com.smarthelmet.emergency.viewmodel.EmergencyPhase
import com.smarthelmet.emergency.viewmodel.EmergencyViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: EmergencyViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle permissions response
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request runtime permissions if needed
        val perms = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        requestPermissionLauncher.launch(perms.toTypedArray())

        setContent {
            SmartHelmetTheme {
                MainContent(viewModel = viewModel)
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Physical key safety cancellation support: Volume keys or Enter cancels countdown
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ||
            keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
            keyCode == KeyEvent.KEYCODE_ENTER ||
            keyCode == KeyEvent.KEYCODE_HEADSETHOOK
        ) {
            if (viewModel.incidentState.value.phase == EmergencyPhase.COUNTDOWN_RUNNING) {
                viewModel.cancelEmergency("Physical hardware key pressed (Volume/Enter override)")
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}

enum class NavigationTab(val label: String) {
    DASHBOARD("Dashboard"),
    SETUP("Setup"),
    HISTORY("History")
}

@Composable
fun MainContent(viewModel: EmergencyViewModel) {
    val incidentState by viewModel.incidentState.collectAsState()
    val profile by viewModel.profileFlow.collectAsState()
    val hardwareState by viewModel.hardwareState.collectAsState()
    val incidents by viewModel.incidentHistoryFlow.collectAsState()

    var currentTab by remember { mutableStateOf(NavigationTab.DASHBOARD) }

    when (incidentState.phase) {
        EmergencyPhase.COUNTDOWN_RUNNING -> {
            IncidentCountdownScreen(
                incidentId = incidentState.incidentId,
                formattedTime = incidentState.formattedTime,
                countdownRemaining = incidentState.countdownRemaining,
                countdownTotal = incidentState.countdownTotal,
                isAlarmActive = incidentState.isAlarmActive,
                isDemoMode = incidentState.isDemoMode,
                onCancelEmergency = { reason ->
                    viewModel.cancelEmergency(reason)
                }
            )
        }

        EmergencyPhase.RESPONSE_DISPATCHED -> {
            IncidentResponseScreen(
                incidentId = incidentState.incidentId,
                timestampStr = incidentState.formattedTime,
                location = incidentState.location,
                alertMessage = incidentState.alertMessage,
                recipients = incidentState.recipients,
                timelineEvents = incidentState.timelineEvents,
                isDemoMode = incidentState.isDemoMode,
                onManualAdvanceStage = {
                    viewModel.manualAdvanceNextStage()
                },
                onResetToDashboard = {
                    viewModel.resetIncidentToIdle()
                    currentTab = NavigationTab.DASHBOARD
                },
                onDialNumber = { phone ->
                    viewModel.communicationManager.openDialerIntent(phone)
                },
                onSendSms = { phone, msg ->
                    viewModel.communicationManager.openSmsAppIntent(phone, msg)
                }
            )
        }

        else -> {
            Scaffold(
                containerColor = NavyDeep,
                bottomBar = {
                    NavigationBar(
                        containerColor = NavyCard,
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            selected = (currentTab == NavigationTab.DASHBOARD),
                            onClick = { currentTab = NavigationTab.DASHBOARD },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Dashboard,
                                    contentDescription = "Dashboard"
                                )
                            },
                            label = { Text("Dashboard") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EmergencyRed,
                                selectedTextColor = EmergencyRed,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted,
                                indicatorColor = EmergencyRed.copy(alpha = 0.15f)
                            )
                        )

                        NavigationBarItem(
                            selected = (currentTab == NavigationTab.SETUP),
                            onClick = { currentTab = NavigationTab.SETUP },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Setup"
                                )
                            },
                            label = { Text("Setup") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AccentCyan,
                                selectedTextColor = AccentCyan,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted,
                                indicatorColor = AccentCyan.copy(alpha = 0.15f)
                            )
                        )

                        NavigationBarItem(
                            selected = (currentTab == NavigationTab.HISTORY),
                            onClick = { currentTab = NavigationTab.HISTORY },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = "History"
                                )
                            },
                            label = { Text("History") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AccentCyan,
                                selectedTextColor = AccentCyan,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted,
                                indicatorColor = AccentCyan.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentTab) {
                        NavigationTab.DASHBOARD -> {
                            DashboardScreen(
                                profile = profile,
                                hardwareState = hardwareState,
                                hasLocationPermission = viewModel.locationHelper.hasLocationPermission(),
                                onSimulateAccident = {
                                    viewModel.triggerAccident("Manual Simulator Button")
                                },
                                onSimulateHardwareImpact = {
                                    viewModel.hardwareSimulator.triggerHardwareImpactEvent(5.8f, 310f)
                                },
                                onNavigateSetup = {
                                    currentTab = NavigationTab.SETUP
                                },
                                onToggleDemoMode = { isDemo ->
                                    viewModel.setDemoMode(isDemo)
                                }
                            )
                        }

                        NavigationTab.SETUP -> {
                            SetupScreen(
                                currentProfile = profile,
                                onSaveProfile = { newProfile ->
                                    viewModel.saveRiderProfile(newProfile)
                                },
                                onResetDefaults = {
                                    viewModel.resetProfileToSample()
                                },
                                onDone = {
                                    currentTab = NavigationTab.DASHBOARD
                                }
                            )
                        }

                        NavigationTab.HISTORY -> {
                            HistoryScreen(
                                incidents = incidents,
                                parseEvents = { raw -> viewModel.parseEvents(raw) },
                                onClearAllHistory = { viewModel.clearAllHistory() }
                            )
                        }
                    }
                }
            }
        }
    }
}
