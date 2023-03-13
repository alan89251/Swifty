package com.team2.handiwork.viewModel

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.R
import com.team2.handiwork.base.viewModel.BaseMissionViewModel
import com.team2.handiwork.models.Mission
import com.team2.handiwork.singleton.UserData

class FragmentHomeViewModel : BaseMissionViewModel() {
    private val db = Firebase.firestore
    val filteredMissions = MutableLiveData<List<Mission>>()
    val mission: Mission = Mission()
    val serviceTypeListColumnNum = 2
    private val filterLiveData = MutableLiveData("All")
    private var _homeViewModel = ActivityHomeViewModel()
    val serviceTypes = arrayListOf(
        "Assembling",
        "Cleaning",
        "Gardening",
        "Moving",
        "Renovation",
        "Repair",
        "Delivering",
        "Seasonal"
    )

    fun observeMissionList(homeViewModel: ActivityHomeViewModel) {
        _homeViewModel = homeViewModel
        homeViewModel.missions.observeForever { missionList ->
            filteredMissions.value = filterMissions(missionList, filterLiveData.value!!)
        }
    }


    fun updateFilter(filter: String) {
        filterLiveData.value = filter

        filteredMissions.value =
            _homeViewModel.missions.value?.let { filterMissions(it, filterLiveData.value!!) }
    }

    private fun filterMissions(missions: List<Mission>, filter: String): List<Mission> {
        if (filter == "All") {
            return missions
        }

        return missions.filter { it.status == convertStatusStringToEnum(filter) }
    }

    private fun convertStatusStringToEnum(status: String): Int {
        return when (status) {
            "Open" -> 0
            "Pending Acceptance" -> 1
            "Confirmed" -> 2
            "Disputed" -> 3
            "Cancelled" -> 4
            "Completed" -> 5
            "Enrolled" -> 6
            else -> -1
        }
    }

    fun getSubServiceTypesResId(serviceType: String): Int {
        return when (serviceType) {
            "Assembling" -> R.array.sub_service_type_assembling
            "Cleaning" -> R.array.sub_service_type_cleaning
            "Gardening" -> R.array.sub_service_type_gardening
            "Moving" -> R.array.sub_service_type_moving
            "Renovation" -> R.array.sub_service_type_renovation
            "Repair" -> R.array.sub_service_type_repair
            "Delivering" -> R.array.sub_service_type_delivering
            else -> R.array.sub_service_type_seasonal
        }
    }


    fun checkEnoughBalance(): Boolean {
        return UserData.currentUserData.balance > 0
    }

}