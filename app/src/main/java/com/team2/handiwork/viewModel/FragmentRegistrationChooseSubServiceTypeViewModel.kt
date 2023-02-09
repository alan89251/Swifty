package com.team2.handiwork.viewModel

import androidx.lifecycle.ViewModel
import com.team2.handiwork.models.ServiceType

class FragmentRegistrationChooseSubServiceTypeViewModel : ViewModel() {
    var selectedServiceTypeMap = hashMapOf<String, ServiceType>()
}