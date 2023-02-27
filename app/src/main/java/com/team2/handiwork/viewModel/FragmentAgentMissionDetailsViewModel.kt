package com.team2.handiwork.viewModel

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.enum.MissionStatusEnum
import com.team2.handiwork.models.Mission

class FragmentAgentMissionDetailsViewModel : ViewModel() {
    var mission = MutableLiveData<Mission>()
    val enrolled = MutableLiveData<Boolean>(mission.value?.status == MissionStatusEnum.OPEN.value)
    val withdrawWarn = MutableLiveData<Boolean>(mission.value?.status == MissionStatusEnum.CANCELLED.value)
    val withdraw = MutableLiveData<Boolean>(mission.value?.status == MissionStatusEnum.CANCELLED.value)
    val finished = MutableLiveData<Boolean>(mission.value?.status == MissionStatusEnum.COMPLETED.value)

    fun cancelButtonVisibility(): Int {
        return if (mission.value!!.status == MissionStatusEnum.CONFIRMED.value) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    fun enrollButtonVisibility(): Int {
        Log.d("???????", mission.value!!.status.toString())
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