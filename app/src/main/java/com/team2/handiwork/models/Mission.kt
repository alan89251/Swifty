package com.team2.handiwork.models

import android.net.Uri
import com.google.firebase.firestore.Exclude
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.team2.handiwork.enums.MissionStatusEnum
import com.team2.handiwork.enums.MissionStatusEnumDeserializeAdapter
import com.team2.handiwork.enums.MissionStatusEnumSerializeAdapter
import java.io.Serializable

class Mission : Serializable {
    var serviceType: String = ""
    var subServiceType: String = ""
    var startTime: Long = 0L
    var endTime: Long = 0L
    var location: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var missionPhotos: ArrayList<String> = ArrayList()
    var resultPhotos: ArrayList<String> = ArrayList()
    var description: String = ""
    var price: Double = 0.0
    var employer: String = ""
    var disputeReasons: ArrayList<String> = ArrayList()
    var status: MissionStatusEnum = MissionStatusEnum.COMPLETED
    var rating: Float = 0F
    var selectedAgent = ""
    var resultComments = ""

    // store username / email  instead of enrollment id
    var enrollments: ArrayList<String> = ArrayList()
    var totalRatingPeople: Int = 0
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    // For ratings / comments
    var isReviewed: Boolean = false // is the employer have left review for this mission
    var isAgentReviewed: Boolean = false // is the agent have left review for this mission

    // check mission is before of starting time
    @get:Exclude
    var before48Hour: Boolean = 86400000 < startTime - System.currentTimeMillis()

    @get:Exclude
    var missionPhotoUris: ArrayList<Uri> = ArrayList()

    @get:Exclude
    var missionId: String = "" // Not save in field


    fun serialize(): Map<String, Any> {
        val gson = GsonBuilder()
            .registerTypeAdapter(
                MissionStatusEnum::class.java,
                MissionStatusEnumSerializeAdapter(),
            )
            .addSerializationExclusionStrategy(exclusionStrategy)
            .create()
        val string = gson.toJson(this)
        val map: Map<String, Any> = HashMap()
        return gson.fromJson(string, map.javaClass)
    }

    companion object {
        fun deserialize(mission: Map<String, Any>): Mission {
            val json = Gson().toJson(mission)
            val gson = GsonBuilder()
                .registerTypeAdapter(
                    MissionStatusEnum::class.java,
                    MissionStatusEnumDeserializeAdapter(),
                ).create()
            return gson.fromJson(json, Mission::class.java)
        }

        val exclusionStrategy = object: ExclusionStrategy {
            override fun shouldSkipField(f: FieldAttributes?): Boolean {
                f?.let {
                    if (it.name == "missionPhotoUris") {
                        return true
                    }
                }
                return false
            }

            override fun shouldSkipClass(clazz: Class<*>?): Boolean {
                return false;
            }
        }
    }


}