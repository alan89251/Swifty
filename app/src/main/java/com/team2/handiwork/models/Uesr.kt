package com.team2.handiwork.models

import java.io.Serializable

class User : Serializable {
    var email: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var phoneNumber: String = ""
    var phoneVerify: Boolean = false
    var imageURi: String = ""
    var isAgent: Boolean = false
    var finishMissionCount: Int = 0
    var confirmedCancellationCount: Int = 0
    var cancellationRate: Double = 0.0
    var isEmployer: Boolean = false
    var serviceTypeList: List<ServiceType> = arrayListOf()
    var uId: String = ""
    var locationLat: Double = 0.0
    var locationLng: Double = 0.0
    var locationStr: String = ""
    var distance: Int = 0
    var balance: Int = 0
    var onHold: Int = 0
    var suspendAmount: Int = 0
}