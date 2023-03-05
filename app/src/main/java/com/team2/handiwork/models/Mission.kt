package com.team2.handiwork.models

import android.net.Uri
import com.google.firebase.firestore.Exclude
import java.io.Serializable

class Mission : Serializable {
    var serviceType: String = ""
    var subServiceType: String = ""
    var startTime: Long = 0L
    var endTime: Long = 0L
    var location: String = ""
    var missionPhotos: ArrayList<String> = ArrayList()
    var description: String = ""
    var price: Double = 0.0
    var employer: String = ""
    var status: Int = 0
    var rating: Float = 0F
    var selectedAgent = ""

    // store username / email  instead of enrollment id
    var enrollments: ArrayList<String> = ArrayList()
    var totalRatingPeople: Int = 0
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    // check mission is before of starting time
    @get:Exclude
    var before48Hour: Boolean = 86400000 < startTime - System.currentTimeMillis()

    @get:Exclude
    var missionPhotoUris: ArrayList<Uri> = ArrayList()

    @get:Exclude
    var missionId: String = "" // Not save in field
}