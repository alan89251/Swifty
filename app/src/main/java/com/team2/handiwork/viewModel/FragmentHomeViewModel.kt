package com.team2.handiwork.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.R
import com.team2.handiwork.enum.FirebaseCollectionKey
import com.team2.handiwork.models.Mission
import com.team2.handiwork.singleton.UserData

class FragmentHomeViewModel : ViewModel() {
    private val db = Firebase.firestore
    val missions = MutableLiveData<List<Mission>>()
    val mission: Mission = Mission()
    val serviceTypeListColumnNum = 2
    val serviceTypes = arrayListOf(
        "Assembling",
        "Cleaning",
        "Gardening",
        "Moving",
        "Renovation",
        "Repair",
        "Delivering",
        "Seasonal"
    )


    fun getMissionsByEmail(userEmail: String) {
        val myMissionList = mutableListOf<Mission>()
        db.collection(FirebaseCollectionKey.MISSIONS.displayName)
            .whereEqualTo("employer", userEmail)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val tempDocument = document.toObject<Mission>()
                    myMissionList.add(tempDocument)
                }
                missions.value = myMissionList
            }
            .addOnFailureListener {
                Log.d("hehehe", "getMissionsByEmail: $it")
            }



    }


    fun getSubServiceTypesResId(serviceType: String): Int {
        return when (serviceType) {
            "Assembling" -> R.array.sub_service_type_assembling
            "Cleaning" -> R.array.sub_service_type_cleaning
            "Gardening" -> R.array.sub_service_type_gardening
            "Moving" -> R.array.sub_service_type_moving
            "Renovation" -> R.array.sub_service_type_renovation
            "Repair" -> R.array.sub_service_type_repair
            "Delivering" -> R.array.sub_service_type_delivering
            else -> R.array.sub_service_type_seasonal
        }
    }


    fun checkEnoughBalance(): Boolean {
        return UserData.currentUserData.balance >= 0
    }

}