# SURAKSHA HELMET: Smart Accident Detection & Emergency Response System
**Android Native Application (Kotlin + Jetpack Compose + Material 3 + Room DB)**

[![Build Status](https://img.shields.io/badge/Build-SUCCESS-brightgreen.svg)](file:///c:/Users/barat/OneDrive/Desktop/SMART%20HELMET/SmartHelmet-Emergency-debug.apk)
[![Platform](https://img.shields.io/badge/Platform-Android%208.0%2B%20(API%2026--35)-blue.svg)](file:///c:/Users/barat/OneDrive/Desktop/SMART%20HELMET/app/build.gradle.kts)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple.svg)](file:///c:/Users/barat/OneDrive/Desktop/SMART%20HELMET/build.gradle.kts)
[![Design System](https://img.shields.io/badge/UI%2FUX-Suraksha%20Tactile%20Safety-FFC700.svg)](file:///c:/Users/barat/OneDrive/Desktop/SMART%20HELMET/app/src/main/java/com/smarthelmet/emergency/ui/theme/Color.kt)
[![Specification](https://img.shields.io/badge/Specification-Full%20Whitepaper-blue.svg)](file:///c:/Users/barat/OneDrive/Desktop/SMART%20HELMET/SYSTEM_DOCUMENTATION.md)

> **"Protecting riders through faster accident recognition, location sharing, and coordinated emergency response."**  
> For the complete architectural whitepaper, see [SYSTEM_DOCUMENTATION.md](file:///c:/Users/barat/OneDrive/Desktop/SMART%20HELMET/SYSTEM_DOCUMENTATION.md).

---

## 1. Verified Deliverables & Artifact Paths

- **Ready-to-Install Android Debug APK**:
  - `c:\Users\barat\OneDrive\Desktop\SMART HELMET\SmartHelmet-Emergency-debug.apk` (16.5 MB, recompiled with modern Suraksha UI/UX)
  - Also located at standard Gradle path: `c:\Users\barat\OneDrive\Desktop\SMART HELMET\app\build\outputs\apk\debug\app-debug.apk`
- **Complete Kotlin & Compose Source Code**:
  - Located in `c:\Users\barat\OneDrive\Desktop\SMART HELMET\app\src\main\`
- **Comprehensive System Whitepaper & Specification**:
  - Located in [SYSTEM_DOCUMENTATION.md](file:///c:/Users/barat/OneDrive/Desktop/SMART%20HELMET/SYSTEM_DOCUMENTATION.md)

---

## 2. Modern Suraksha UI/UX Design System

The application features a modern mobile safety UI/UX design:
- **Tactile Safety Palette**:
  - Tactile Safety Yellow (`#FFC700` / `#6E5400`) for primary accident triggers and action trays.
  - High-G Emergency Crimson (`#BA1A20` / `#FFDAD6`) for active alerts, impact badges, and trauma priority.
  - Dark Charcoal (`#2F3132` / `#F0F1F2`) for tactical controls and resolution buttons.
  - Active BLE Emerald (`#059669`) for live Bluetooth 5.2 link status and verification badges.
  - Clean Surface Light Scheme (`#F9F9FA` / `#FFFFFF`) for crisp readability under outdoor daylight conditions.
- **4-Tab Intuitive Navigation**:
  1. **Dashboard (`shield`)**: Rider greeting ("Namaste, Rahul Sharma!"), Royal Enfield bike anchor, Smart Helmet v2 Pro status (88% battery, IMU sensors, live GPS strip), prominent **SIMULATE ACCIDENT** action tray, safe sandbox toggle, and 2-column quick SOS & diagnostics cards.
  2. **SOS Sim (`e911_emergency`)**: Emergency Response Tracker with `#SIM-894` telemetry capsule, incident location map preview with Google Maps link, 3-column peak impact stamp (5.8G, 34 km/h, Attached), 5-milestone connected timeline chronology, and quick-call actions for Dad and resolution.
  3. **Emergency (`medical_information`)**: Emergency Vault with government-verified identity, vehicle plate, blood group pills (`O+`, `A+`, `B+`, etc.), allergies & asthma notes, ABHA Health ID, emergency contacts with SMS/WhatsApp badges, and Manipal Hospital trauma facility with direct geofence tag.
  4. **Log (`history`)**: Incident history audit trail with full timeline inspection, telemetry metrics, and outcome tracking.
- **Active Emergency Countdown Modal**: High-G Impact overlay with SVG-style circular timer ring, siren indicator, and prominent **"I Am Safe — Stop SOS"** cancellation button.

---

## 3. Core Architecture & Workflow

This project implements an end-to-end accident detection and emergency response system engineered for high-consequence safety scenarios. The workflow does **not** require a physical ESP32 or helmet sensor to demonstrate full production fidelity.

```
+-------------------------------------------------------------+
|                     SMART HELMET APP                        |
|                                                             |
|  [SIMULATE ACCIDENT]             [ESP32 Hardware Trigger]   |
|         |                                   |               |
|         +-----------------+-----------------+               |
|                           |                                 |
|                           v                                 |
|            ACCIDENT IMPACT DETECTED                         |
|            - Unique Incident ID generated                   |
|            - Alarm tone & vibration activated               |
|            - Status bar ongoing notification                |
|                           |                                 |
|                           v                                 |
|          5s COUNTDOWN (Configurable up to 10s)             |
|          +--------------------------------------+           |
|          | STOP EMERGENCY PROCEDURE             |           |
|          | FALSE ALARM / NOT SEVERE             |           |
|          | (On-screen button or Volume key)     |           |
|          +--------------------------------------+           |
|                  /                      \                   |
|         [If Cancelled]           [If Countdown Expires]     |
|                v                                 v          |
|    - Audio alarm muted immediately    - Assistance Confirmed|
|    - Incident recorded as CANCELLED   - Acquire GPS (or demo)|
|    - Reason logged for audit          - Compile SOS payload |
|    - Clean coroutine cancellation     - Dispatch alerts     |
|                                       - Auto response track |
+-------------------------------------------------------------+
```

---

## 3. Two Distinct Operating Modes

### A. DEMO MODE (Default — 100% Safe Simulation)
- **Zero Physical Hardware Required**: Operates completely stand-alone without ESP32, physical gyro, or GPS module.
- **Accident Simulation**: Prominent **SIMULATE ACCIDENT** button and **Simulate ESP32 Impact (>4.5G)** button.
- **Isolated Telemetry**: Generates realistic coordinates (`37.774929, -122.419416`) clearly labelled with **[DEMO GPS]**.
- **Simulated Communications**: Prepares exact SOS SMS and hospital telemetry payloads. Clearly marked with **[SIMULATED]** badges. **Never makes accidental 911 calls or incurs carrier SMS charges.**
- **Chronological Response Timeline**: Auto-advances through post-accident stages (Ambulance Dispatched -> Paramedic Pickup -> Hospital Arrival -> Trauma Assessment -> Family Update) at configurable 3.5s intervals with manual "Next Stage" fast-forward.

### B. REAL MODE (Architecture-Ready Live Integration)
- **Live Phone GPS**: Reads high-accuracy device GPS (`ACCESS_FINE_LOCATION`) with fallback to last known location.
- **Live Google Maps Link**: Formats direct clickable map link: `https://maps.google.com/?q=lat,lng`.
- **System Intents**: Direct buttons to launch Android's native phone dialer (`Intent.ACTION_DIAL`) and SMS app (`Intent.ACTION_SENDTO`) pre-populated with emergency text.
- **Honest Statuses**: Never falsely claims calls were completed or SMS was delivered without confirmed platform results.

---

## 4. Key Screens & Features

1. **Emergency Dashboard**:
   - Rider profile quick summary card with blood group badge (`O+`).
   - Mode switcher: Toggle between Demo Mode and Real Mode.
   - Live hardware status grid: Phone GPS status, ESP32 connection state, Emergency Contacts status, Hospital status.
   - Large emergency-red **SIMULATE ACCIDENT** button.
   - Secondary button for incoming ESP32 accelerometer impact (>4.5G).

2. **Incident Simulation & Countdown**:
   - Distinct pulsing red accident indicator with unique Incident ID (e.g. `INC-241026`).
   - Circular countdown display (5 seconds by default, configurable up to 10 seconds).
   - High-pitched audible alarm tone and vibrating pulse waveform.
   - Massive **STOP EMERGENCY PROCEDURE — FALSE ALARM / NOT SEVERE** button.
   - Hardware key interception: Pressing Volume Down, Volume Up, or Enter key instantly stops the emergency.
   - Immediate cancellation recording with selectable reason (e.g. "False alarm / Drop test", "Minor bump / Rider OK").

3. **Emergency Response & Live Timeline**:
   - Live GPS Coordinates, accuracy radius, and "View on Live Google Maps" button.
   - SOS message preview with complete medical notes and designated trauma center.
   - Separate status card for each recipient:
     - Emergency Contact 1 (Spouse / Sarah Doe)
     - Emergency Contact 2 (Brother / Robert Doe)
     - Metro General Trauma Center
     - EMS Dispatch Center
   - Chronological vertical timeline:
     1. Accident Impact Detected
     2. Emergency Countdown Commenced
     3. Countdown Expired - Assistance Confirmed
     4. GPS Coordinates Acquired
     5. Emergency Contacts Alerted
     6. Trauma Center Alerted
     7. Ambulance Unit Dispatched (ETA: 7 mins)
     8. Paramedics on Scene & Patient Secured
     9. Arrival at Emergency Ward Bay 3
     10. Triage & Medical Assessment Underway
     11. Family Notification Update Sent

4. **Rider & Emergency Setup**:
   - Editable fields: Rider name, phone, blood group, medical notes/allergies.
   - Primary & Secondary emergency contacts (name, relationship, phone).
   - Preferred hospital (name, phone, address).
   - Emergency service phone number (911 / 112 / 108).
   - Cancellation countdown duration slider (5s to 10s).
   - Full persistence across app restarts via `ProfileRepository`.
   - "Reset to Sample Demonstration Data" button for quick review resets.

5. **Incident Audit Logs (History)**:
   - Persistent Room database (`AppDatabase`, `IncidentDao`).
   - Logs all past incidents, dates, outcomes (`CANCELLED` vs `CONFIRMED_ASSISTANCE`), locations, and timestamps.
   - Tap any past incident to view full modal audit trail and event timeline.
   - Clear history capability.

---

## 5. Build and Test Instructions

### Prerequisites
- JDK 17 (installed)
- Android SDK (API 34/35/36 installed)
- Android Gradle Plugin 8.7.2
- Gradle 8.11.1

### Run Unit Tests
```powershell
.\gradlew.bat testDebugUnitTest
```
*(All unit tests in `EmergencyUnitTest.kt` verify profile validation, countdown transitions, cancellation stops, hardware simulation, location format, and event parsing).*

### Build Debug APK
```powershell
.\gradlew.bat assembleDebug
```
The output APK is generated at:
`c:\Users\barat\OneDrive\Desktop\SMART HELMET\app\build\outputs\apk\debug\app-debug.apk`

### Install onto Connected Android Phone
1. Enable **Developer Options** and **USB Debugging** on your Android phone.
2. Connect your phone via USB cable.
3. Run:
```powershell
& "C:\Users\barat\AppData\Local\Android\Sdk\platform-tools\adb.exe" install -r "c:\Users\barat\OneDrive\Desktop\SMART HELMET\SmartHelmet-Emergency-debug.apk"
```
4. Launch the app:
```powershell
& "C:\Users\barat\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell am start -n com.smarthelmet.emergency/.MainActivity
```

---

## 6. Future ESP32 Hardware Integration Contract

The app architecture includes `HardwareIntegrationSimulator` and `HelmetHardwareListener` ready for physical hardware connection:
- When an ESP32 equipped with MPU6050 / ADXL345 detects an impact acceleration ($G > 4.5\text{G}$) and angular rotation:
```kotlin
// ESP32 BLE / Serial callback entrypoint:
viewModel.hardwareSimulator.triggerHardwareImpactEvent(
    gForce = 5.8f,
    gyroDelta = 290.0f
)
```
- In this build, this event can be demonstrated live by clicking the **"Simulate Incoming ESP32 Sensor Impact (>4.5G)"** button on the dashboard.

---

## 7. Safety, Prototype Limitations & Honest Reporting

- **Demo vs Production**: In Demo Mode, no real SMS or phone calls are placed to emergency services or personal numbers.
- **Real Mode Restrictions**: Modern Android security models require default SMS handler permissions or carrier subscriptions for silent background SMS dispatch. Therefore, in Real Mode, standard Android Intent dispatch (`Intent.ACTION_DIAL` and `Intent.ACTION_SENDTO`) is utilized for user-confirmed, secure emergency communications.
- **Prototype Status**: This application is built as an academic prototype for college project evaluation and demonstrates the complete software simulation workflow without requiring certified emergency hardware.
