package com.team2.handiwork.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.R

class LayoutCreateMissionStepperViewModel: ViewModel() {
    var step1Desc: MutableLiveData<Int> = MutableLiveData(R.string.create_mission_step_1)
    var step2Desc: MutableLiveData<Int> = MutableLiveData(R.string.create_mission_step_2)
    var step3Desc: MutableLiveData<Int> = MutableLiveData(R.string.create_mission_step_3)
}