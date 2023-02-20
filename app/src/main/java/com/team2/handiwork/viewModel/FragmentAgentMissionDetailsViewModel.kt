package com.team2.handiwork.viewModel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.enum.MissionStatusEnum
import com.team2.handiwork.models.Mission

class FragmentAgentMissionDetailsViewModel : ViewModel() {
    var mission = MutableLiveData<Mission>()
    val enrolled = MutableLiveData<Boolean>(false)
    val withdrawWarn = MutableLiveData<Boolean>(false)
    val withdraw = MutableLiveData<Boolean>(false)
    val finished = MutableLiveData<Boolean>(false)

    fun cancelButtonVisibility(): Int {
        return if (mission.value!!.status == MissionStatusEnum.CONFIRMED.value) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    fun enrollButtonVisibility(): Int {
        return if (mission.value!!.status == MissionStatusEnum.OPEN.value) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    fun finishedButtonVisibility(): Int {
        return if (mission.value!!.status == MissionStatusEnum.CONFIRMED.value) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}