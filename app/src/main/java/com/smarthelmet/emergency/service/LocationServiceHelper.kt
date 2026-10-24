package com.smarthelmet.emergency.service

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

data class LocationResult(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float,
    val isSimulated: Boolean,
    val provider: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val statusDescription: String
) {
    fun toGoogleMapsUrl(): String = "https://maps.google.com/?q=$latitude,$longitude"
}

class LocationServiceHelper(private val context: Context) {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentOrDemoLocation(isDemoMode: Boolean): LocationResult {
        if (!hasLocationPermission()) {
            return if (isDemoMode) {
                getDemoLocation("DEMO MODE: Location simulated (Permission not granted)")
            } else {
                LocationResult(
                    latitude = 0.0,
                    longitude = 0.0,
                    accuracyMeters = 0f,
                    isSimulated = false,
                    provider = "NONE",
                    statusDescription = "Location permission not granted. Real GPS coordinates unavailable."
                )
            }
        }

        // Try getting real location with 4 second timeout
        val realLocation = withTimeoutOrNull(4000L) {
            fetchRealLocation()
        }

        if (realLocation != null) {
            return LocationResult(
                latitude = realLocation.latitude,
                longitude = realLocation.longitude,
                accuracyMeters = realLocation.accuracy,
                isSimulated = false,
                provider = realLocation.provider ?: "GPS",
                statusDescription = "Real GPS fix acquired (Accuracy ±${realLocation.accuracy.toInt()}m)"
            )
        }

        // Try last known location
        val lastKnown = getLastKnownLocation()
        if (lastKnown != null) {
            val ageMinutes = (System.currentTimeMillis() - lastKnown.time) / (1000 * 60)
            return LocationResult(
                latitude = lastKnown.latitude,
                longitude = lastKnown.longitude,
                accuracyMeters = lastKnown.accuracy,
                isSimulated = false,
                provider = "LAST_KNOWN_${lastKnown.provider}",
                statusDescription = "Last known GPS fix (${ageMinutes}m old, Accuracy ±${lastKnown.accuracy.toInt()}m)"
            )
        }

        // Fallback
        return if (isDemoMode) {
            getDemoLocation("DEMO MODE: Simulated GPS (Real fix timed out)")
        } else {
            LocationResult(
                latitude = 0.0,
                longitude = 0.0,
                accuracyMeters = 0f,
                isSimulated = false,
                provider = "UNAVAILABLE",
                statusDescription = "GPS signal unavailable (No satellite lock)"
            )
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun fetchRealLocation(): Location? = suspendCancellableCoroutine { continuation ->
        if (locationManager == null) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                try {
                    locationManager.removeUpdates(this)
                } catch (_: Exception) {}
                if (continuation.isActive) {
                    continuation.resume(location)
                }
            }
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }

        continuation.invokeOnCancellation {
            try {
                locationManager.removeUpdates(listener)
            } catch (_: Exception) {}
        }

        try {
            val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (hasGps) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    500L,
                    0f,
                    listener,
                    Looper.getMainLooper()
                )
            } else if (hasNetwork) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    500L,
                    0f,
                    listener,
                    Looper.getMainLooper()
                )
            } else {
                continuation.resume(null)
            }
        } catch (e: Exception) {
            continuation.resume(null)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(): Location? {
        if (locationManager == null) return null
        return try {
            val gpsLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val netLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            when {
                gpsLoc != null && netLoc != null -> if (gpsLoc.time > netLoc.time) gpsLoc else netLoc
                gpsLoc != null -> gpsLoc
                else -> netLoc
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getDemoLocation(label: String = "DEMO LOCATION: Springfield Highway Mile 42"): LocationResult {
        // High-precision demo coordinates near Springfield / City Trauma Corridor
        return LocationResult(
            latitude = 37.774929,
            longitude = -122.419416,
            accuracyMeters = 4.2f,
            isSimulated = true,
            provider = "SIMULATED_DEMO",
            statusDescription = label
        )
    }
}
