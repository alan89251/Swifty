package com.team2.handiwork.models

import java.io.Serializable

open class BaseUser : Serializable {
    var firstName = ""
    var lastName = ""
    var imageURi: String = ""
    var email = ""
}