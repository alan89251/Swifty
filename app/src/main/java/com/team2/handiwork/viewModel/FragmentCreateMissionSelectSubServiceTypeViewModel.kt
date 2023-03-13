package com.team2.handiwork.viewModel

import com.team2.handiwork.base.viewModel.BaseMissionViewModel
import com.team2.handiwork.models.Mission

class FragmentCreateMissionSelectSubServiceTypeViewModel : BaseMissionViewModel() {
    val subServiceTypeListColumnNum = 2
    var mission: Mission = Mission()
}