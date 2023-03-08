package com.team2.handiwork.viewModel.mission

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.firebase.firestore.Firestore
import com.team2.handiwork.firebase.firestore.service.MissionService
import com.team2.handiwork.models.Enrollment
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.MissionUser
import com.team2.handiwork.models.User
import com.team2.handiwork.singleton.UserData
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
    var selectedAgent: MutableLiveData<User> = MutableLiveData()

    var fs = Firestore()
    var missionService = MissionService(fs.userCollection, fs.missionCollection)

    fun updateSelectedEnrollment(enrollment: Enrollment): Observable<Boolean> {
        return fs.enrollmentCollection.updateEnrollment(enrollment)
    }

    fun selectAgent(mission: Mission, selectedAgent: String): Observable<Mission> {
        return missionService.selectAgent(mission, selectedAgent)
    }

    fun completeMission(mission: Mission): Observable<Mission> {
        return missionService.completeMission(mission)
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
            .getUser(mission.selectedAgent)
            .subscribe {
                selectedAgent.value = it
            }
    }

    fun cancelOpenMissionByEmployer(): Observable<MissionUser> {
        return missionService.cancelOpenMissionByEmployer(
            mission,
            UserData.currentUserData
        )
    }

    fun cancelMissionBefore48HoursByEmployer(): Observable<MissionUser> {
        return missionService.cancelMissionBefore48HoursByEmployer(
            mission,
            UserData.currentUserData
        )
    }

    fun cancelMissionWithin48HoursByEmployer(): Observable<MissionUser> {
        return missionService.cancelMissionWithin48HoursByEmployer(
            mission,
            UserData.currentUserData
        )
    }

    fun disputeMission(): Observable<Mission> {
        return missionService.disputeMission(mission)
    }

    fun rejectMission(): Observable<Mission> {
        return missionService.rejectMission(mission)
    }
}