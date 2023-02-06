package com.team2.handiwork.models

import android.view.View


data class ServiceType(
    var name: String,
    var subServiceType: List<SubServiceType>,
) {
    var selected: Boolean = true;
    var visibility = if (selected) View.VISIBLE else View.INVISIBLE;
}
