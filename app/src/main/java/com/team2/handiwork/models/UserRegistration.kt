package com.team2.handiwork.models

import java.io.Serializable

class UserRegistrationForm : Serializable {
    var email: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var phoneNumber: String = ""
    var phoneVerify: Boolean = false
    var imageURi: String = ""
    var isAgent: Boolean = false
    var isEmployer: Boolean = false
    var serviceTypeList: List<ServiceType> = arrayListOf()
    var locationLat: Double = 0.0
    var locationLng: Double = 0.0
    var locationStr: String = ""
    var distance: Int = 0
}