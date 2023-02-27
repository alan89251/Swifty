package com.team2.handiwork.models

import com.google.firebase.firestore.Exclude

class Enrollment {
    var agent: String = ""
    var selected: Boolean = false
    var missionId: String = ""

    @get:Exclude
    var enrollmentId: String = "" // Not save in field
}