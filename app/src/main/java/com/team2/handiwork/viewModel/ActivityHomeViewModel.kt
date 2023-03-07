package com.team2.handiwork.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.firebase.firestore.Firestore
import com.team2.handiwork.models.Enrollment
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.User
import com.team2.handiwork.singleton.UserData
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable

class ActivityHomeViewModel : ViewModel() {
    private val db = Firebase.firestore
    val missions = MutableLiveData<List<Mission>>()
    val currentUser = MutableLiveData<User>()
    var fs = Firestore()
    var disposeBag = CompositeDisposable()

    fun getEmployerMission(email: String) {
        fs.missionCollection.subscribeMissionByEmail(email).subscribe { userMission ->
            missions.value = userMission
        }
    }

    fun getAgentEnrollments(email: String) {
        fs.enrollmentCollection.subscribeEnrolledMissionByEmail(email).subscribe { _missions ->
            _missions.let {
                missions.value = it
            }
        }
//        fs.enrollmentCollection.subscribeEnrolledMissionByEmail(email).subscribe { enrollments ->
//            getMissionByEnrollments(enrollments)
//        }
    }

    private fun getMissionByEnrollments(enrollments: List<Enrollment>) {
        enrollments.let {
            val tempList = mutableListOf<String>()
            for (enrollment in enrollments) {
                tempList.add(enrollment.missionId)
            }
            if (tempList.isNotEmpty()) {
                fs.missionCollection.getMissionByMissionId(tempList, getEnrolledMissionCallback)
            }
        }
    }

    fun getUserByEmail(email: String): Observable<User> {
        return fs.userCollection.getUser(email)
    }

    private val getEnrolledMissionCallback: (missions: List<Mission>) -> Unit = { _missions ->
        missions.value = _missions
    }

    fun userLogout() {
        UserData.currentUserData = User()
    }
}