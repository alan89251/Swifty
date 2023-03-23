package com.team2.handiwork.viewModel

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
            return missions.filter {
                it.status != convertStatusStringToEnum("Cancelled") && it.status != convertStatusStringToEnum(
                    "Completed"
                )
            }
        }

        return missions.filter { it.status == convertStatusStringToEnum(filter) }
    }


    fun checkEnoughBalance(): Boolean {
        return UserData.currentUserData.balance > 0
    }

}