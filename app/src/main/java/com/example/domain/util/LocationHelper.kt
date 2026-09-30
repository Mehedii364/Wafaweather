package com.example.domain.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

class LocationHelper(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocation || coarseLocation
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        if (!hasLocationPermission()) return null

        return withContext(Dispatchers.IO) {
            try {
                val cts = CancellationTokenSource()
                suspendCancellableCoroutine { continuation ->
                    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                        .addOnSuccessListener { location: Location? ->
                            if (location != null) {
                                continuation.resume(location)
                            } else {
                                // Fallback to last known location
                                fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                                    continuation.resume(lastLoc)
                                }.addOnFailureListener {
                                    continuation.resume(null)
                                }
                            }
                        }
                        .addOnFailureListener {
                            fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                                continuation.resume(lastLoc)
                            }.addOnFailureListener {
                                continuation.resume(null)
                            }
                        }

                    continuation.invokeOnCancellation {
                        cts.cancel()
                    }
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun reverseGeocode(latitude: Double, longitude: Double): Pair<String, String> {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    suspendCancellableCoroutine { continuation ->
                        geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                            val address = addresses.firstOrNull()
                            val result = extractCityCountry(address, latitude, longitude)
                            continuation.resume(result)
                        }
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    val address = addresses?.firstOrNull()
                    extractCityCountry(address, latitude, longitude)
                }
            } catch (e: Exception) {
                Pair("Location (${String.format(Locale.US, "%.2f", latitude)}, ${String.format(Locale.US, "%.2f", longitude)})", "")
            }
        }
    }

    private fun extractCityCountry(address: Address?, latitude: Double, longitude: Double): Pair<String, String> {
        if (address == null) {
            return Pair("Location (${String.format(Locale.US, "%.2f", latitude)}, ${String.format(Locale.US, "%.2f", longitude)})", "")
        }
        val city = address.locality
            ?: address.subAdminArea
            ?: address.adminArea
            ?: address.subLocality
            ?: "Detected Location"
        val country = address.countryName ?: ""
        return Pair(city, country)
    }
}
