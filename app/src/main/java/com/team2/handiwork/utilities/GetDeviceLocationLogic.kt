package com.team2.handiwork.utilities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat

class GetDeviceLocationLogic(
    private val locationManager: LocationManager,
    private val context: Context
) {
    //private var isUpdateLocation = true // control whether to update the location
    private lateinit var onReceievedDeviceLocation: (Location) -> Unit

    /**
     * Send a request for location
     */
    fun requestLocation(onReceievedDeviceLocation: (Location) -> Unit) {
        this.onReceievedDeviceLocation = onReceievedDeviceLocation

        val ONE_HOUR_IN_MS = 3600000L // interval for location update, set to 1 hour to prevent frequent update
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, ONE_HOUR_IN_MS, 0F, locationListener)
    }

    // listener to receive location
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(p0: Location) {
            locationManager.removeUpdates(this)
            onReceievedDeviceLocation(p0)

            /*if (isUpdateLocation) {
                //isUpdateLocation = false // only update the location once
                onReceievedDeviceLocation(p0)
            }*/
        }
    }
}