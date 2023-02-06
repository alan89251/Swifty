package com.team2.handiwork.viewModel

import androidx.lifecycle.ViewModel
import com.team2.handiwork.models.ServiceType
import com.team2.handiwork.models.SubServiceType

class FragmentRegistrationChooseServiceTypeViewModel : ViewModel() {
    var serviceTypeList =
        arrayListOf<ServiceType>(
            ServiceType(
                "Assembling",
                arrayListOf(
                    SubServiceType("SubCat1"),
                    SubServiceType("SubCat2"),
                    SubServiceType("SubCat3")
                )
            ),
            ServiceType(
                "Cleaning",
                arrayListOf(
                    SubServiceType("SubCat1"),
                    SubServiceType("SubCat2"),
                    SubServiceType("SubCat3")
                )
            ),
            ServiceType(
                "Gardening",
                arrayListOf(
                    SubServiceType("SubCat1"),
                    SubServiceType("SubCat2"),
                    SubServiceType("SubCat3")
                )
            ),
            ServiceType(
                "Moving",
                arrayListOf(
                    SubServiceType("SubCat1"),
                    SubServiceType("SubCat2"),
                    SubServiceType("SubCat3")
                )
            ),
            ServiceType(
                "Renovation",
                arrayListOf(
                    SubServiceType("SubCat1"),
                    SubServiceType("SubCat2"),
                    SubServiceType("SubCat3")
                )
            ),
            ServiceType(
                "Repair",
                arrayListOf(
                    SubServiceType("SubCat1"),
                    SubServiceType("SubCat2"),
                    SubServiceType("SubCat3")
                )
            ),
            ServiceType(
                "Delivering",
                arrayListOf(
                    SubServiceType("SubCat1"),
                    SubServiceType("SubCat2"),
                    SubServiceType("SubCat3")
                )
            ),
        )
}