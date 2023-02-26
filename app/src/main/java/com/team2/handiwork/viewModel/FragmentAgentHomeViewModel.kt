package com.team2.handiwork.viewModel

import android.graphics.drawable.GradientDrawable
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.firebase.Firestore
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.User
import com.team2.handiwork.utilities.Utility

class FragmentAgentHomeViewModel : ViewModel() {
    val filteredMissions = MutableLiveData<List<Mission>>()
    val poolMissions = MutableLiveData<List<Mission>>()
    val mission: Mission = Mission()
    val serviceTypeListColumnNum = 2
    var userEmail = ""
    private val filterLiveData = MutableLiveData("All")
    val filterText: LiveData<String>
        get() = filterLiveData

    private var _homeViewModel = ActivityHomeViewModel()

    fun observeMissionList(homeViewModel: ActivityHomeViewModel) {
        _homeViewModel = homeViewModel
        homeViewModel.missions.observeForever { missionList ->
            filteredMissions.value = filterMissions(missionList, filterLiveData.value!!)
        }
    }

    fun getMissionFromMissionPool() {
        Firestore().getPoolMissionByEmail(userEmail, getPoolMissionCallback)
    }


    fun updateFilter(filter: String) {
        filterLiveData.value = filter

        filteredMissions.value = _homeViewModel.missions.value?.let { filterMissions(it, filterLiveData.value!!) }
    }

    private fun filterMissions(missions: List<Mission>, filter: String): List<Mission> {
        if (filter == "All") {
            return missions
        }

        return missions.filter { it.status == convertStatusStringToEnum(filter) }
    }

    fun setEmail(user : User){
        userEmail = user.email
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

    private val getPoolMissionCallback: (missions: List<Mission>) -> Unit = { missions ->
        poolMissions.value = missions
    }
}