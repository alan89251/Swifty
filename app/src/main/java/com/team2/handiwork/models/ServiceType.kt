package com.team2.handiwork.models

data class ServiceType(
    var name: String,
    var subServiceType: List<String>,
) {
    var selected = false;
}
