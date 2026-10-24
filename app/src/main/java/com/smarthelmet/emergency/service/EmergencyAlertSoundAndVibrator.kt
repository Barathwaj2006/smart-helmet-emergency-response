package com.smarthelmet.emergency.service

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class EmergencyAlertSoundAndVibrator(private val context: Context) {
    private var toneJob: Job? = null
    private var toneGenerator: ToneGenerator? = null

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    @Synchronized
    fun startAlarmAndVibration(scope: CoroutineScope) {
        stop() // Ensure clean state before starting

        // Start Vibration
        try {
            vibrator?.let { v ->
                if (v.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val pattern = longArrayOf(0, 400, 200, 400, 200, 600)
                        val effect = VibrationEffect.createWaveform(pattern, 0)
                        v.vibrate(effect)
                    } else {
                        @Suppress("DEPRECATION")
                        v.vibrate(longArrayOf(0, 400, 200, 400, 200, 600), 0)
                    }
                }
            }
        } catch (_: Exception) {}

        // Start Audio Tone Generator in Coroutine Loop
        toneJob = scope.launch(Dispatchers.IO) {
            try {
                toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
                while (isActive) {
                    toneGenerator?.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 450)
                    delay(550L)
                }
            } catch (_: Exception) {
                // Safe fallback: gracefully handle emulator or device audio stream restrictions
            } finally {
                releaseTone()
            }
        }
    }

    @Synchronized
    fun stop() {
        toneJob?.cancel()
        toneJob = null
        releaseTone()

        try {
            vibrator?.cancel()
        } catch (_: Exception) {}
    }

    private fun releaseTone() {
        try {
            toneGenerator?.stopTone()
            toneGenerator?.release()
        } catch (_: Exception) {}
        toneGenerator = null
    }
}
