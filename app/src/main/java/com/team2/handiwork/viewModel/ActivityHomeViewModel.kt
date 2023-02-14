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
import com.team2.handiwork.models.User
import com.team2.handiwork.singleton.UserData
import io.reactivex.rxjava3.core.Observable

class ActivityHomeViewModel : ViewModel() {
    private val db = Firebase.firestore
    val currentUser = MutableLiveData<User>()


//    fun getUserByEmail(email: String) {
//        val docRef = db.collection(FirebaseCollectionKey.USERS.displayName).document(email)
//        docRef.get()
//            .addOnSuccessListener { document ->
//                if (document != null) {
//                    currentUser.value = document.toObject<User>()
//                    UserData.currentUserData = currentUser.value!!
//                } else {
//                    Log.d("hehehe", "getUserByEmail: no data")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.d("hehehe", "error: $exception")
//            }
//    }

    fun getUserByEmail(email: String): Observable<User> {
        return Firestore().getUser(email)
    }


    fun userLogout() {
        UserData.currentUserData = User()
    }
}