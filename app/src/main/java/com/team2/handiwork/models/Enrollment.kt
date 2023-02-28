package com.team2.handiwork.models

import com.google.firebase.firestore.Exclude

class Enrollment {
    // todo confirm email or username
    var agent: String = ""
    var selected: Boolean = false
    var missionId: String = ""
    var enrolled: Boolean = true

    @get:Exclude
    var enrollmentId: String = "" // Not save in field
}