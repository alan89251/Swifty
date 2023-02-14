package com.team2.handiwork.models

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class Mission: Serializable {

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

    fun getMissionPhotosInBitmaps(): List<Bitmap> {
        val list = ArrayList<Bitmap>()
        for (photoStr in missionPhotos) {
            val byteArray = Base64.getDecoder().decode(photoStr)
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            list.add(bitmap)
        }
        return list
    }
}