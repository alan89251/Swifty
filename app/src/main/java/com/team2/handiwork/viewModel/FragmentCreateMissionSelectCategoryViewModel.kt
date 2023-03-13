package com.team2.handiwork.viewModel

import com.team2.handiwork.base.viewModel.BaseMissionViewModel
import com.team2.handiwork.models.Mission

class FragmentCreateMissionSelectCategoryViewModel : BaseMissionViewModel() {
    val serviceTypeListColumnNum = 2
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

    val mission: Mission = Mission()
}