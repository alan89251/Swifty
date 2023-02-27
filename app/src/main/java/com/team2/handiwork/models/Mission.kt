package com.team2.handiwork.models

import android.net.Uri
import com.google.firebase.firestore.Exclude
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

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
    var createdAt: Long = 0L
    var updatedAt: Long = 0L

    @get:Exclude
    var missionPhotoUris: ArrayList<Uri> = ArrayList()
    @get:Exclude
    var missionId: String = "" // Not save in field

    @get:Exclude
    var period: String = run {
        val dateFormatter = SimpleDateFormat("MM/dd/yyyy\nHH:mm", Locale.getDefault())
        val dateStr =
            "${dateFormatter.format(Date(startTime))} - ${dateFormatter.format(Date(endTime))}"
        dateStr
    }
}