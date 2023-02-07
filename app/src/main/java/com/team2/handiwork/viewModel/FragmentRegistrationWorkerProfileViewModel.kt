package com.team2.handiwork.viewModel

import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.team2.handiwork.R
import com.team2.handiwork.utilities.GetDeviceLocationLogic
import kotlin.math.log10

class FragmentRegistrationWorkerProfileViewModel : ViewModel() {
    var workerLocationMap: MutableLiveData<GoogleMap> = MutableLiveData()
    var deviceLocation: MutableLiveData<Location> = MutableLiveData()
    var workerPreferredMissionDistance: MutableLiveData<Int> = MutableLiveData()
    var workerPreferredMissionCircle: MutableLiveData<Circle> = MutableLiveData()
    var locationIndicator: MutableLiveData<GroundOverlay> = MutableLiveData()

    fun configMapContentByDeviceLocation(location: Location) {
        if (workerPreferredMissionDistance.value == null) {
            return
        }
        updateMapContent(workerPreferredMissionDistance.value!!)
    }

    fun updateMapContent(selectedDistance: Int) {
        updateMapZoomingScale(getMapScaleByDistance(selectedDistance))
        updateCircleOfUserPreferredDistance(selectedDistance)
        updateLocationIndicator(selectedDistance)
    }

    fun updateMapZoomingScale(scale: Float) {
        if (workerLocationMap.value == null) {
            return
        }
        if (deviceLocation.value == null) {
            return
        }
        workerLocationMap.value!!.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
            LatLng(deviceLocation.value!!.latitude, deviceLocation.value!!.longitude),
            scale))
    }

    fun updateCircleOfUserPreferredDistance(selectedDistance: Int) {
        if (workerLocationMap.value == null) {
            return
        }
        if (deviceLocation.value == null) {
            return
        }
        // clear previous circle
        workerPreferredMissionCircle.value?.remove()
        // add new circle
        val deviceLatLng = LatLng(deviceLocation.value!!.latitude, deviceLocation.value!!.longitude)
        workerPreferredMissionCircle.value = workerLocationMap.value!!.addCircle(
            CircleOptions()
                .center(deviceLatLng)
                .radius(selectedDistance * 1000.0) // change km to meter
                .fillColor(Color.parseColor("#80E5B769"))
                .strokeColor(Color.parseColor("#80E5B769"))
        )
    }

    fun updateLocationIndicator(selectedDistance: Int) {
        if (workerLocationMap.value == null) {
            return
        }
        if (deviceLocation.value == null) {
            return
        }
        locationIndicator.value?.remove()
        val imageDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.location_indicator)
        locationIndicator.value = workerLocationMap.value!!.addGroundOverlay(
            GroundOverlayOptions()
                .image(imageDescriptor)
                .position(
                    LatLng(deviceLocation.value!!.latitude, deviceLocation.value!!.longitude),
                    selectedDistance.toFloat() * 200F,
                    selectedDistance.toFloat() * 200F
                )
        )
    }

    // get google map scale of the distance
    fun getMapScaleByDistance(distance: Int): Float {
        return BASE_ZOOM_LEVEL - (log10(distance.toDouble() / BASE_DISTANCE) / log10(2.0)).toFloat()
    }

    fun requireDeviceLocation(locationManager: LocationManager) {
        GetDeviceLocationLogic(locationManager)
            .requestLocation {
                deviceLocation.value = it
            }
    }

    companion object {
        private const val BASE_DISTANCE = 5.0
        private const val BASE_ZOOM_LEVEL = 12f
    }

}