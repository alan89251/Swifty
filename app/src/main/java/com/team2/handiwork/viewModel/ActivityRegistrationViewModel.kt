package com.team2.handiwork.viewModel

import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.team2.handiwork.AppConst
import com.team2.handiwork.R
import com.team2.handiwork.base.viewModel.BaseMissionViewModel
import com.team2.handiwork.firebase.firestore.Firestore
import com.team2.handiwork.models.ServiceType
import com.team2.handiwork.models.SubServiceType
import com.team2.handiwork.models.User
import com.team2.handiwork.utilities.GetDeviceLocationLogic
import io.reactivex.rxjava3.core.Observable
import kotlin.math.log10

class ActivityRegistrationViewModel : BaseMissionViewModel() {
    val registrationForm = MutableLiveData<User>(User())
    var serviceTypeMap = hashMapOf<String, ServiceType>()
    var selectedServiceTypeMap = hashMapOf<String, ServiceType>()

    var workerLocationMap: MutableLiveData<GoogleMap> = MutableLiveData()
    var deviceLocation: MutableLiveData<Location> = MutableLiveData()
    var workerPreferredMissionDistance: MutableLiveData<Int> = MutableLiveData()
    var workerPreferredMissionCircle: MutableLiveData<Circle> = MutableLiveData()
    var locationIndicator: MutableLiveData<GroundOverlay> = MutableLiveData()

    // temp form
    var form = MutableLiveData<User>(User())

    var firstName = MutableLiveData(form.value!!.firstName)
    var lastName = MutableLiveData(form.value!!.lastName)
    var phoneNumber = MutableLiveData(form.value!!.phoneNumber)
    var verifyMsg = MutableLiveData("")
    var email = MutableLiveData(form.value!!.email)

    // color
    var primaryButtonColor = MutableLiveData<Int>(R.color.dark_blue_100)

    var isFirstTimeRun = true


    var nextBtnEnabled: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>()
    var isEnableNextBtn: MutableLiveData<Boolean> = MutableLiveData(false)
    var fs = Firestore()


    companion object {
        private const val BASE_DISTANCE = 5.0
        private const val BASE_ZOOM_LEVEL = 12f
    }

    init {
        nextBtnEnabled.addSource(firstName) { checkBtnEnable() }
        nextBtnEnabled.addSource(lastName) { checkBtnEnable() }

    }


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
                scale
            )
        )
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
                .fillColor(R.color.soft_orange_100)
                .strokeColor(R.color.soft_orange_100)
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

    fun register(form: User): Observable<Boolean> {
        return fs.userCollection.register("Users", form)
    }


    private fun checkBtnEnable() {
        if (firstName.value!!.isEmpty()
            || lastName.value!!.isEmpty()
        ) {
            nextBtnEnabled.value = false
            return
        }
        nextBtnEnabled.value = true
    }

    fun initRegistrationForm(context: Context) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val email = sp.getString(AppConst.EMAIL, "")
        val uId = sp.getString(AppConst.PREF_UID, "")
        registrationForm.value!!.email = email!!
        registrationForm.value!!.uid = uId!!
    }

    fun markCurrentSelectedSubServiceTypes() {
        for (serviceType in registrationForm.value!!.serviceTypeList) {
            if (!selectedServiceTypeMap.contains(serviceType.name)) {
                val tempServiceType = ServiceType()
                tempServiceType.name = serviceType.name
                tempServiceType.selected = serviceType.selected
                for (subServiceType in serviceType.subServiceTypeList) {
                    val tempSubServiceType = SubServiceType()
                    tempSubServiceType.name = subServiceType.name
                    tempSubServiceType.selected = subServiceType.selected
                    tempServiceType.subServiceTypeList.add(tempSubServiceType)
                }
                selectedServiceTypeMap[serviceType.name] = tempServiceType
            }
        }
    }
}