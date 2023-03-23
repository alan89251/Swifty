package com.team2.handiwork.viewModel

import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.team2.handiwork.ScreenMsg
import com.team2.handiwork.base.viewModel.BaseMissionViewModel
import com.team2.handiwork.firebase.firestore.Firestore
import com.team2.handiwork.models.Mission
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.utilities.Event
import com.team2.handiwork.utilities.GetDeviceLocationLogic
import com.team2.handiwork.utilities.MissionSuggestionWorker
import com.team2.handiwork.utilities.Utility.Companion.calculateDistance
import java.util.Timer
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask

class FragmentAgentHomeViewModel : BaseMissionViewModel() {
    val filteredMissions = MutableLiveData<List<Mission>>()
    val poolMissions = MutableLiveData<List<Mission>>()
    val suggestedMissions = MutableLiveData<List<Mission>>()
    val mission: Mission = Mission()
    val serviceTypeListColumnNum = 2
    private val filterLiveData = MutableLiveData("All")
    val filterText: LiveData<String>
        get() = filterLiveData
    private val timer = Timer()
    private var userEmail = ""
    val suggestedMissionCount = MutableLiveData<Int>()

    init {
        startHourlyCountdown()
    }

    private fun startHourlyCountdown() {
        timer.schedule(timerTask {
            if (userEmail != "") {
                getMissionFromMissionPool(userEmail)
            }
        }, 0, 60 * 60 * 1000) // Schedule the timer to run every hour
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }


    private var _homeViewModel = ActivityHomeViewModel()
    var fs = Firestore()
    private var userLocation: Location? = null

    fun observeMissionList(homeViewModel: ActivityHomeViewModel) {
        _homeViewModel = homeViewModel
        homeViewModel.missions.observeForever { missionList ->
            filteredMissions.value = filterOwnMissions(missionList, filterLiveData.value!!)
        }
    }

    fun getMissionFromMissionPool(email: String) {
        userEmail = email
        fs.missionCollection.getPoolMissionByEmail(email, getPoolMissionCallback)
    }


    fun updateFilter(filter: String) {
        filterLiveData.value = filter

        filteredMissions.value =
            _homeViewModel.missions.value?.let { filterOwnMissions(it, filterLiveData.value!!) }
    }

    private fun filterOwnMissions(missions: List<Mission>, filter: String): List<Mission> {
        if (filter == "All") {
            return missions.filter {
                it.status != convertStatusStringToEnum("Cancelled") && it.status != convertStatusStringToEnum(
                    "Completed"
                )
            }
        }
        return missions.filter { it.status == convertStatusStringToEnum(filter) }
    }

    private fun filterSuggestedMission(missions: List<Mission>) {
        // filter if the mission fulfill the user preference sub category > category
        // then filter the mission within the distance if(0 || null), means no distance preference
        val user = UserData.currentUserData
        val tempMissionList = mutableListOf<Mission>()
        val poolMissionList = mutableListOf<Mission>()
        val subServiceTypeNames = mutableListOf<String>()
        val userServiceTypeNames = user.serviceTypeList.map { it.name }

        if (userServiceTypeNames.isEmpty() && user.distance == 0) {
            poolMissions.value = missions
            return
        }

        for (serviceType in user.serviceTypeList) {
            for (subServiceType in serviceType.subServiceTypeList) {
                subServiceTypeNames.add(subServiceType.name)
            }
        }

        for (mission in missions) {

            var isValidSuggestion = true

            // check distance
            if (user.distance != 0 && userLocation?.latitude != null && userLocation?.longitude != null) {
                val distance = calculateDistance(
                    userLocation?.latitude!!,
                    userLocation?.longitude!!, mission.latitude, mission.longitude
                )

                if (distance > user.distance) {
                    isValidSuggestion = false
                }
            }

            if (isValidSuggestion && userServiceTypeNames.isNotEmpty()) {
                // check service type filter
                if (subServiceTypeNames.isNotEmpty() && mission.subServiceType in subServiceTypeNames ||
                    subServiceTypeNames.isEmpty() && mission.serviceType in userServiceTypeNames
                ) {

                } else {
                    isValidSuggestion = false
                }
            }

            if (isValidSuggestion) {
                tempMissionList.add(mission)
            } else {
                poolMissionList.add(mission)
            }
        }
        suggestedMissionCount.value = tempMissionList.size
        suggestedMissions.value = tempMissionList
        poolMissions.value = poolMissionList
    }

    fun getUserLocation(locationManager: LocationManager) {
        GetDeviceLocationLogic(locationManager).requestLocation(getLocationCallback)
    }

    private val getPoolMissionCallback: (missions: List<Mission>) -> Unit = { missions ->
        // mission filter, one to suggested mission, one to pool mission
        filterSuggestedMission(missions)
//        poolMissions.value = missions.filter { !filterSuggestedMission(missions) }
    }

    private val getLocationCallback: (location: Location) -> Unit = { location ->
        Log.d("hehehe", ": alt: ${location.latitude}, long :${location.longitude}")
        userLocation = location
    }
}