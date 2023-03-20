package com.team2.handiwork.models

import java.io.Serializable

open class BaseUser : Serializable {
    var imageURi: String = ""
    var email = ""
    var uid = ""
}