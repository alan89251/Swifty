package com.team2.handiwork.models

import com.google.firebase.firestore.Exclude

class Enrollment {
    var agent: String = ""
    var selected: Boolean = false

    @get:Exclude
    var enrollmentId: String = "" // Not save in field
}