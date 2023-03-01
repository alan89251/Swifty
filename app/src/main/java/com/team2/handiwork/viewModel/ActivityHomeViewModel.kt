package com.team2.handiwork.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.firebase.Firestore
import com.team2.handiwork.models.Enrollment
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.User
import com.team2.handiwork.singleton.UserData
import io.reactivex.rxjava3.core.Observable

class ActivityHomeViewModel : ViewModel() {
    private val db = Firebase.firestore
    val missions = MutableLiveData<List<Mission>>()
    val currentUser = MutableLiveData<User>()


    fun getEmployerMission(email: String) {
        Firestore().subscribeMissionByEmail(email).subscribe { userMission ->
            missions.value = userMission
        }
    }

    fun getUserEnrollments(email: String) {
        Firestore().subscribeEnrolledMissionByEmail(email).subscribe { enrollments ->
            getMissionByEnrollments(enrollments)
        }
    }

    private fun getMissionByEnrollments(enrollments: List<Enrollment>) {
        enrollments.let {
            val tempList = mutableListOf<String>()
            for (enrollment in enrollments) {
                tempList.add(enrollment.missionId)
            }
            if (tempList.isNotEmpty()) {
                Firestore().getMissionByMissionId(tempList, getEnrolledMissionCallback)
            }
        }
    }

    fun getUserByEmail(email: String): Observable<User> {
        return Firestore().getUser(email)
    }

    private val getEnrolledMissionCallback: (missions: List<Mission>) -> Unit = { _missions ->
        missions.value = _missions
    }

    fun userLogout() {
        UserData.currentUserData = User()
    }
}