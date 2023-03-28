package com.team2.handiwork.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.ScreenMsg
import com.team2.handiwork.firebase.firestore.Firestore
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.User
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.utilities.Event
import com.team2.handiwork.utilities.Ext.Companion.disposedBy
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable

class ActivityHomeViewModel : ViewModel() {
    private val db = Firebase.firestore
    val missions = MutableLiveData<List<Mission>>()
    val currentUser = MutableLiveData<User>()
    var fs = Firestore()
    var disposeBag = CompositeDisposable()
    private val statusMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage

    fun getEmployerMission(email: String) {
        fs.missionCollection.subscribeMissionByEmail(email).subscribe { userMission ->
            missions.value = userMission
        }.disposedBy(disposeBag)
    }

    fun getAgentEnrollments(email: String) {
        fs.missionCollection.subscribeEnrolledMissionByEmail(email).subscribe { _missions ->
            _missions.let {
                missions.value = it
            }
        }.disposedBy(disposeBag)
//        fs.enrollmentCollection.subscribeEnrolledMissionByEmail(email).subscribe { enrollments ->
//            getMissionByEnrollments(enrollments)
//        }
    }

    fun passMessage(string: String) {
        statusMessage.value = Event(string)
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