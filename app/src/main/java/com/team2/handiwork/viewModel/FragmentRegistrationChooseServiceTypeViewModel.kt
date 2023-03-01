package com.team2.handiwork.viewModel

import androidx.lifecycle.ViewModel
import com.team2.handiwork.R
import com.team2.handiwork.models.ServiceType

class FragmentRegistrationChooseServiceTypeViewModel : ViewModel() {
    var serviceTypeMap = hashMapOf<String, ServiceType>()

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