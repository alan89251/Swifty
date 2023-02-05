package com.team2.handiwork.viewModel

import androidx.lifecycle.ViewModel
import com.team2.handiwork.models.ServiceType

class FragmentRegistrationChooseServiceTypeViewModel : ViewModel() {
    var serviceTypeList =
        arrayListOf<ServiceType>(
            ServiceType("Assembling", arrayListOf()),
            ServiceType("Cleaning", arrayListOf()),
            ServiceType("Gardening", arrayListOf()),
            ServiceType("Moving", arrayListOf()),
            ServiceType("Renovation", arrayListOf()),
            ServiceType("Repair", arrayListOf()),
            ServiceType("Delivering", arrayListOf()),
        )
}