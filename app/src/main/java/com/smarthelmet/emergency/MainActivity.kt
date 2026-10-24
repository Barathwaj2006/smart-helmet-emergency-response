package com.smarthelmet.emergency

import android.Manifest
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
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarthelmet.emergency.ui.screens.DashboardScreen
import com.smarthelmet.emergency.ui.screens.HistoryScreen
import com.smarthelmet.emergency.ui.screens.IncidentCountdownScreen
import com.smarthelmet.emergency.ui.screens.IncidentResponseScreen
import com.smarthelmet.emergency.ui.screens.SetupScreen
import com.smarthelmet.emergency.ui.theme.OnPrimaryContainer
import com.smarthelmet.emergency.ui.theme.PrimaryContainer
import com.smarthelmet.emergency.ui.theme.PrimaryGolden
import com.smarthelmet.emergency.ui.theme.SmartHelmetTheme
import com.smarthelmet.emergency.ui.theme.Surface
import com.smarthelmet.emergency.ui.theme.SurfaceContainerLowest
import com.smarthelmet.emergency.ui.theme.TextSecondaryColor
import com.smarthelmet.emergency.viewmodel.EmergencyPhase
import com.smarthelmet.emergency.viewmodel.EmergencyViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: EmergencyViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
    SOS_SIM("SOS Sim"),
    EMERGENCY("Emergency"),
    LOG("Log")
}

@Composable
fun MainContent(viewModel: EmergencyViewModel) {
    val incidentState by viewModel.incidentState.collectAsState()
    val profile by viewModel.profileFlow.collectAsState()
    val hardwareState by viewModel.hardwareState.collectAsState()
    val incidents by viewModel.incidentHistoryFlow.collectAsState()

    var currentTab by remember { mutableStateOf(NavigationTab.DASHBOARD) }

    // If countdown is active, show countdown full-screen for safety
    if (incidentState.phase == EmergencyPhase.COUNTDOWN_RUNNING) {
        IncidentCountdownScreen(
            incidentId = incidentState.incidentId,
            formattedTime = incidentState.formattedTime,
            countdownRemaining = incidentState.countdownRemaining,
            countdownTotal = incidentState.countdownTotal,
            isAlarmActive = incidentState.isAlarmActive,
            isDemoMode = incidentState.isDemoMode,
            onCancelEmergency = { reason ->
                viewModel.cancelEmergency(reason)
                currentTab = NavigationTab.DASHBOARD
            }
        )
    } else {
        Scaffold(
            containerColor = Surface,
            bottomBar = {
                NavigationBar(
                    containerColor = SurfaceContainerLowest,
                    tonalElevation = 6.dp
                ) {
                    // Tab 1: Dashboard
                    NavigationBarItem(
                        selected = (currentTab == NavigationTab.DASHBOARD),
                        onClick = { currentTab = NavigationTab.DASHBOARD },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Dashboard"
                            )
                        },
                        label = { Text("Dashboard", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = OnPrimaryContainer,
                            selectedTextColor = OnPrimaryContainer,
                            unselectedIconColor = TextSecondaryColor,
                            unselectedTextColor = TextSecondaryColor,
                            indicatorColor = PrimaryContainer.copy(alpha = 0.45f)
                        )
                    )

                    // Tab 2: SOS Sim
                    NavigationBarItem(
                        selected = (currentTab == NavigationTab.SOS_SIM),
                        onClick = {
                            currentTab = NavigationTab.SOS_SIM
                            if (incidentState.phase == EmergencyPhase.IDLE || incidentState.phase == EmergencyPhase.CANCELLED) {
                                viewModel.triggerAccident("Bottom Nav SOS Simulation Trigger")
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.CrisisAlert,
                                contentDescription = "SOS Sim"
                            )
                        },
                        label = { Text("SOS Sim", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = OnPrimaryContainer,
                            selectedTextColor = OnPrimaryContainer,
                            unselectedIconColor = TextSecondaryColor,
                            unselectedTextColor = TextSecondaryColor,
                            indicatorColor = PrimaryContainer.copy(alpha = 0.45f)
                        )
                    )

                    // Tab 3: Emergency Info (Setup)
                    NavigationBarItem(
                        selected = (currentTab == NavigationTab.EMERGENCY),
                        onClick = { currentTab = NavigationTab.EMERGENCY },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.LocalHospital,
                                contentDescription = "Emergency"
                            )
                        },
                        label = { Text("Emergency", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = OnPrimaryContainer,
                            selectedTextColor = OnPrimaryContainer,
                            unselectedIconColor = TextSecondaryColor,
                            unselectedTextColor = TextSecondaryColor,
                            indicatorColor = PrimaryContainer.copy(alpha = 0.45f)
                        )
                    )

                    // Tab 4: Log (History)
                    NavigationBarItem(
                        selected = (currentTab == NavigationTab.LOG),
                        onClick = { currentTab = NavigationTab.LOG },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Log"
                            )
                        },
                        label = { Text("Log", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = OnPrimaryContainer,
                            selectedTextColor = OnPrimaryContainer,
                            unselectedIconColor = TextSecondaryColor,
                            unselectedTextColor = TextSecondaryColor,
                            indicatorColor = PrimaryContainer.copy(alpha = 0.45f)
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
                                viewModel.triggerAccident("Dashboard Hero Simulation Button")
                            },
                            onSimulateHardwareImpact = {
                                viewModel.hardwareSimulator.triggerHardwareImpactEvent(5.8f, 320f)
                            },
                            onNavigateSetup = {
                                currentTab = NavigationTab.EMERGENCY
                            },
                            onToggleDemoMode = { isDemo ->
                                viewModel.setDemoMode(isDemo)
                            }
                        )
                    }

                    NavigationTab.SOS_SIM -> {
                        if (incidentState.phase == EmergencyPhase.RESPONSE_DISPATCHED) {
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
                        } else {
                            // If idle on this tab, auto trigger or return to dashboard
                            DashboardScreen(
                                profile = profile,
                                hardwareState = hardwareState,
                                hasLocationPermission = viewModel.locationHelper.hasLocationPermission(),
                                onSimulateAccident = {
                                    viewModel.triggerAccident("Dashboard Hero Simulation Button")
                                },
                                onSimulateHardwareImpact = {
                                    viewModel.hardwareSimulator.triggerHardwareImpactEvent(5.8f, 320f)
                                },
                                onNavigateSetup = {
                                    currentTab = NavigationTab.EMERGENCY
                                },
                                onToggleDemoMode = { isDemo ->
                                    viewModel.setDemoMode(isDemo)
                                }
                            )
                        }
                    }

                    NavigationTab.EMERGENCY -> {
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

                    NavigationTab.LOG -> {
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
