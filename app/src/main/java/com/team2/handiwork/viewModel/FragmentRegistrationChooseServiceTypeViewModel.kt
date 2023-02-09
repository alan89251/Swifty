package com.team2.handiwork.viewModel

import androidx.lifecycle.ViewModel
import com.team2.handiwork.models.ServiceType
import com.team2.handiwork.models.SubServiceType

class FragmentRegistrationChooseServiceTypeViewModel : ViewModel() {
    var selectedServiceTypeList = arrayListOf<ServiceType>()
    var serviceTypeNameList = arrayListOf<String>(
        "Assembling",
        "Cleaning",
        "Gardening",
        "Moving",
        "Renovation",
        "Repair",
        "Delivering",
        "PlaceHolder1",
        "PlaceHolder2",
        "PlaceHolder3"
    )
    var subServiceTypeNameList = arrayListOf<String>("SubCat1", "SubCat2", "SubCat3")

    var serviceTypeList = serviceTypeNameList.map { it ->
        val serviceType = ServiceType()
        serviceType.name = it
        serviceType.subServiceTypeList = subServiceTypeNameList.map { name ->
            val subServiceType = SubServiceType()
            subServiceType.name = name
            subServiceType
        }.toList() as ArrayList<SubServiceType>
    }.toList() as ArrayList<ServiceType>
}