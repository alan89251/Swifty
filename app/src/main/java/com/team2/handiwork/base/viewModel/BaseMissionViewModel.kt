package com.team2.handiwork.base.viewModel

import androidx.lifecycle.ViewModel
import com.team2.handiwork.R

open class BaseMissionViewModel : ViewModel() {


    fun convertStatusColor(int: Int): Int {
        return when (int) {
            0, 6 -> R.color.very_soft_blue_100
            1, 3 -> R.color.strong_red_100
            2 -> R.color.dark_blue_100
            4 -> R.color.light_grey_100
            5 -> R.color.soft_orange_100
            else -> R.color.dark_blue_100
        }
    }
}