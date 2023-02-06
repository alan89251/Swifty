package com.team2.handiwork.models

data class ServiceType(
    var name: String,
    var subServiceType: List<SubServiceType>,
) {
    var selected: Boolean = false;
}
