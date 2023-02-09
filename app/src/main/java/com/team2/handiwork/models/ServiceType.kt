package com.team2.handiwork.models


class ServiceType(
    var name: String,
    var subServiceTypeList: ArrayList<SubServiceType>,
) {
    var selected: Boolean = false
    var selectedSubServiceTypeList = arrayListOf<SubServiceType>()

}
