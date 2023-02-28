package com.team2.handiwork.models

import java.io.Serializable


class ServiceType : Serializable {
    var name: String = ""
    var subServiceTypeList: ArrayList<SubServiceType> = arrayListOf()
    var selected: Boolean = false
}
