package com.team2.handiwork.viewModel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.firebase.firestore.Firestore
import com.team2.handiwork.models.Enrollment
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.User
import io.reactivex.rxjava3.core.Observable
import java.text.SimpleDateFormat
import java.util.*

class FragmentEmployerMissionDetailsViewModel : ViewModel() {
    lateinit var mission: Mission
    var missionDuration: String = ""
        get() {
            val dateFormatter = SimpleDateFormat("MM/dd/yyyy HH:mm")
            return dateFormatter.format(Date(mission.startTime)) +
                    " - " +
                    dateFormatter.format(Date(mission.endTime))
        }
    var enrollments: MutableLiveData<List<Enrollment>> = MutableLiveData()
    var selectedEnrollment: MutableLiveData<Enrollment> = MutableLiveData()
    var selectedAgent: MutableLiveData<User> = MutableLiveData()

    var fs = Firestore()

    @SuppressLint("CheckResult")
    fun getEnrollmentsFromDB() {
        fs.enrollmentCollection.getEnrollmentsByMissionId(mission.missionId)
            .subscribe {
                enrollments.value = it
            }
    }

    fun updateSelectedEnrollment(enrollment: Enrollment): Observable<Boolean> {
        return fs.enrollmentCollection.updateEnrollment(enrollment)
    }

    @SuppressLint("CheckResult")
    fun getSelectedEnrollmentFromDB() {
        fs.enrollmentCollection
            .getSelectedEnrollmentByMissionId(mission.missionId)
            .subscribe {
                if (it.enrollmentId == "") {
                    Log.d(
                        "getSelectedEnrollmentFromDB",
                        "Cannot find the selected enrollment"
                    )
                }

                selectedEnrollment.value = it
            }
    }

    fun updateMission(mission: Mission): Observable<Boolean> {
        return fs.missionCollection.updateMissionObservable(mission)
    }

    fun updateUser(user: User): Observable<Boolean> {
        return fs.userCollection.updateUser(user)
    }

    fun isMissionStartIn48Hours(): Boolean {
        var startTime = Calendar.getInstance()
        startTime.timeInMillis = mission.startTime
        var date48HoursBefore = Calendar.getInstance()
        date48HoursBefore.timeInMillis = startTime.timeInMillis - 172800000L // 48 hours before
        var curDate = Calendar.getInstance()

        return curDate.after(date48HoursBefore)
    }

    fun isCurrentDateAfterMissionEndDate(): Boolean {
        var curDate = Calendar.getInstance()
        var endTime = Calendar.getInstance()
        endTime.timeInMillis = mission.endTime
        return curDate.after(endTime)
    }

    fun getAgentsByEmails(emails: List<String>): Observable<List<User>> {
        return fs.userCollection.getUsers(emails)
    }

    @SuppressLint("CheckResult")
    fun getSelectedAgentFromDB() {
        fs.userCollection
            .getUser(selectedEnrollment.value!!.agent)
            .subscribe {
                selectedAgent.value = it
            }
    }
}