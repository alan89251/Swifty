package com.team2.handiwork.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.models.Mission
import com.team2.handiwork.singleton.UserData

class FragmentMyMissionsViewModel : ViewModel() {
    private val db = Firebase.firestore
    val serviceTypeListColumnNum = 2
    val filteredMissions = MutableLiveData<List<Mission>>()
    private val filterLiveData = MutableLiveData("All")
    private var _homeViewModel =  ActivityHomeViewModel()

    fun observeMissionList(homeViewModel: ActivityHomeViewModel) {
        _homeViewModel = homeViewModel
        homeViewModel.missions.observeForever { missionList ->
            filteredMissions.value = filterMissions(missionList, filterLiveData.value!!)
        }
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

    fun updateFilter(filter: String) {
        filterLiveData.value = filter
        filteredMissions.value = _homeViewModel.missions.value?.let { filterMissions(it, filterLiveData.value!!) }
    }


    fun checkEnoughBalance(): Boolean {
        return UserData.currentUserData.balance > 0
    }
}