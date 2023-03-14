package com.team2.handiwork.base.viewModel

import androidx.lifecycle.ViewModel
import com.team2.handiwork.R
import com.team2.handiwork.enums.MissionStatusEnum

open class BaseMissionViewModel : ViewModel() {


    fun convertStatusColor(status: MissionStatusEnum): Int {
        return when (status) {
            MissionStatusEnum.OPEN, MissionStatusEnum.ENROLLED -> R.color.very_soft_blue_100
            MissionStatusEnum.PENDING_ACCEPTANCE, MissionStatusEnum.DISPUTED -> R.color.strong_red_100
            MissionStatusEnum.CONFIRMED -> R.color.dark_blue_100
            MissionStatusEnum.CANCELLED -> R.color.light_grey_100
            MissionStatusEnum.COMPLETED -> R.color.soft_orange_100
            else -> R.color.dark_blue_100
        }
    }

    fun getMissionStatusString(status: MissionStatusEnum): Int {
        return when (status) {
            // handle components show or hidden
            MissionStatusEnum.OPEN -> R.string.status_open
            MissionStatusEnum.PENDING_ACCEPTANCE -> R.string.status_pending
            MissionStatusEnum.CONFIRMED -> R.string.status_confirmed
            MissionStatusEnum.ENROLLED -> R.string.status_enrolled
            MissionStatusEnum.COMPLETED -> R.string.status_completed
            MissionStatusEnum.CANCELLED -> R.string.status_cancel
            MissionStatusEnum.DISPUTED -> R.string.status_disputed
            else -> throw java.lang.Exception("undefined mission status")
        }
    }

    fun convertStatusStringToEnum(status: String): MissionStatusEnum {
        return when (status) {
            "Open" -> MissionStatusEnum.OPEN
            "Pending Acceptance" -> MissionStatusEnum.PENDING_ACCEPTANCE
            "Confirmed" -> MissionStatusEnum.CONFIRMED
            "Disputed" -> MissionStatusEnum.DISPUTED
            "Cancelled" -> MissionStatusEnum.CANCELLED
            "Completed" -> MissionStatusEnum.COMPLETED
            "Enrolled" -> MissionStatusEnum.ENROLLED
            else -> throw java.lang.Exception("Error: convertStatusStringToEnum")
        }
    }

    fun getSubServiceTypesResId(serviceType: String): Int {
        return when (serviceType) {
            "Assembling" -> R.array.sub_service_type_assembling
            "Cleaning" -> R.array.sub_service_type_cleaning
            "Gardening" -> R.array.sub_service_type_gardening
            "Moving" -> R.array.sub_service_type_moving
            "Renovation" -> R.array.sub_service_type_renovation
            "Repair" -> R.array.sub_service_type_repair
            "Delivering" -> R.array.sub_service_type_delivering
            else -> R.array.sub_service_type_seasonal
        }
    }


}