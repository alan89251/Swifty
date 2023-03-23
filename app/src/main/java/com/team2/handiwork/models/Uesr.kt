package com.team2.handiwork.models

class User : BaseUser() {
    var phoneNumber: String = ""
    var phoneVerify: Boolean = false
    var isAgent: Boolean = false
    var finishMissionCount: Int = 0
    var confirmedCancellationCount: Int = 0
    var firstName = ""
    var lastName = ""
    var cancellationRate: Double = 0.0
    var isEmployer: Boolean = false
    var serviceTypeList: List<ServiceType> = arrayListOf()
    var locationLat: Double = 0.0
    var locationLng: Double = 0.0
    var locationStr: String = ""
    var distance: Int = 0
    var balance: Int = 0
    var onHold: Int = 0
    var suspendAmount: Int = 0
    var fcmDeviceToken: String = ""
}