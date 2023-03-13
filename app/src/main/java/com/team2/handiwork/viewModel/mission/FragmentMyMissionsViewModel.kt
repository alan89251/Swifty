package com.team2.handiwork.viewModel.mission

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.base.viewModel.BaseMissionViewModel
import com.team2.handiwork.models.Mission
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.viewModel.ActivityHomeViewModel

class FragmentMyMissionsViewModel : BaseMissionViewModel() {
    private val db = Firebase.firestore
    val serviceTypeListColumnNum = 2
    val filteredMissions = MutableLiveData<List<Mission>>()
    private val filterLiveData = MutableLiveData("All")
    private var _homeViewModel = ActivityHomeViewModel()

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

    fun updateFilter(filter: String) {
        filterLiveData.value = filter
        filteredMissions.value =
            _homeViewModel.missions.value?.let { filterMissions(it, filterLiveData.value!!) }
    }


    fun checkEnoughBalance(): Boolean {
        return UserData.currentUserData.balance > 0
    }
}