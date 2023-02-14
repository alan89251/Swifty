package com.team2.handiwork.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.enum.FirebaseCollectionKey
import com.team2.handiwork.models.Mission
import com.team2.handiwork.singleton.UserData

class FragmentMyMissionsViewModel : ViewModel() {
    private val db = Firebase.firestore
    val serviceTypeListColumnNum = 2
    val missions = MutableLiveData<List<Mission>>()




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

    fun checkEnoughBalance(): Boolean {
        return UserData.currentUserData.balance > 0
    }
}