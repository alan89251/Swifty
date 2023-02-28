package com.team2.handiwork.viewModel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.firebase.Firestore
import com.team2.handiwork.models.Enrollment
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.User
import io.reactivex.rxjava3.core.Observable
import java.text.SimpleDateFormat
import java.util.*

class FragmentEmployerMissionDetailsViewModel: ViewModel() {
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

    @SuppressLint("CheckResult")
    fun getEnrollmentsFromDB() {
        Firestore()
            .getEnrollmentsByMissionId(mission.missionId)
            .subscribe {
                enrollments.value = it
            }
    }

    fun updateSelectedEnrollment(enrollment: Enrollment): Observable<Boolean> {
        return Firestore().updateEnrollment(enrollment)
    }

    @SuppressLint("CheckResult")
    fun getSelectedEnrollmentFromDB() {
        Firestore()
            .getSelectedEnrollmentByMissionId(mission.missionId)
            .subscribe {
                if (it.enrollmentId == "") {
                    Log.d("getSelectedEnrollmentFromDB",
                        "Cannot find the selected enrollment"
                    )
                }

                selectedEnrollment.value = it
            }
    }

    fun updateMission(mission: Mission): Observable<Boolean> {
        return Firestore().updateMission(mission)
    }

    fun updateUser(user: User): Observable<Boolean> {
        return Firestore().updateUser(user)
    }

    fun isMissionStartIn48Hours(): Boolean {
        val date48HoursBefore = Calendar.getInstance()
            .add(Calendar.HOUR_OF_DAY, -48)
        var startTime = Calendar.getInstance()
        startTime.timeInMillis = mission.startTime

        return startTime.after(date48HoursBefore)
    }

    fun getAgentsByEmails(emails: List<String>): Observable<List<User>> {
        return Firestore().getUsers(emails)
    }

    @SuppressLint("CheckResult")
    fun getSelectedAgentFromDB() {
        Firestore()
            .getUser(selectedEnrollment.value!!.agent)
            .subscribe {
                selectedAgent.value = it
            }
    }
}