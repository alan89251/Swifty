package com.team2.handiwork.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.enum.FirebaseCollectionKey
import com.team2.handiwork.firebase.Firestore
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.User
import com.team2.handiwork.singleton.UserData
import io.reactivex.rxjava3.core.Observable

class ActivityHomeViewModel : ViewModel() {
    private val db = Firebase.firestore
    val missions = MutableLiveData<List<Mission>>()
    val currentUser = MutableLiveData<User>()


    fun getUserMission(email: String) {
        Firestore().subscribeMissionByEmail(email).subscribe { userMission ->
            missions.value = userMission
        }
    }

    fun getUserByEmail(email: String): Observable<User> {
        return Firestore().getUser(email)
    }


    fun userLogout() {
        UserData.currentUserData = User()
    }
}