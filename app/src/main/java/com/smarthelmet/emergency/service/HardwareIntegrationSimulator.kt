package com.smarthelmet.emergency.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class HardwareConnectionState(val displayText: String) {
    HARDWARE_STANDBY("ESP32 Sensor: Standby (Emulated Interface)"),
    NOT_CONNECTED("ESP32 Bluetooth/Serial: Not Connected (Software Simulation Active)"),
    IMPACT_SIGNAL_RECEIVED("ESP32 Trigger: Impact Threshold Exceeded (>4.5G)")
}

interface HelmetHardwareListener {
    fun onAccidentImpactDetected(
        source: String,
        impactGForce: Float,
        angularVelocityDegSec: Float,
        sensorTimestampMillis: Long
    )
}

class HardwareIntegrationSimulator {
    private val _connectionState = MutableStateFlow(HardwareConnectionState.NOT_CONNECTED)
    val connectionState: StateFlow<HardwareConnectionState> = _connectionState.asStateFlow()

    private var hardwareListener: HelmetHardwareListener? = null

    fun registerListener(listener: HelmetHardwareListener) {
        this.hardwareListener = listener
    }

    fun unregisterListener() {
        this.hardwareListener = null
    }

    /**
     * Entrypoint for simulated or incoming ESP32 BLE/UART/TCP payload packets.
     * Example packet format from ESP32 MPU6050/ADXL345: {"gForce": 5.8, "gyroDelta": 240.2}
     */
    fun triggerHardwareImpactEvent(
        gForce: Float = 5.6f,
        gyroDelta: Float = 280.0f
    ) {
        _connectionState.value = HardwareConnectionState.IMPACT_SIGNAL_RECEIVED
        hardwareListener?.onAccidentImpactDetected(
            source = "ESP32_IMU_BROADCAST",
            impactGForce = gForce,
            angularVelocityDegSec = gyroDelta,
            sensorTimestampMillis = System.currentTimeMillis()
        )
    }

    fun resetHardwareState() {
        _connectionState.value = HardwareConnectionState.NOT_CONNECTED
    }
}
