package com.team2.handiwork.viewModel.mission

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.firebase.firestore.Firestore
import com.team2.handiwork.firebase.firestore.service.MissionService
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

    fun selectAgent(mission: Mission, selectedAgent: String, onSuccess: (Mission) -> Unit) {
        missionService.selectAgent(mission, selectedAgent, onSuccess)
    }

    fun completeMission(mission: Mission, onSuccess: (Mission) -> Unit) {
        missionService.completeMission(mission, onSuccess)
    }

    fun updateUser(user: User, onSuccess: (User) -> Unit) {
        fs.userCollection.updateUser(user, onSuccess)
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
            .getUserSingleTime(
                mission.selectedAgent,
                {
                    selectedAgent.value = it
                },
                {
                    Log.d("Employer mission detail",
                        "Fail to get selected agent from DB: $it")
                }
            )
    }

    fun cancelOpenMissionByEmployer(onSuccess: (MissionUser) -> Unit) {
        missionService.cancelOpenMissionByEmployer(
            mission,
            UserData.currentUserData,
            onSuccess
        )
    }

    fun cancelMissionBefore48HoursByEmployer(onSuccess: (MissionUser) -> Unit) {
        missionService.cancelMissionBefore48HoursByEmployer(
            mission,
            UserData.currentUserData,
            onSuccess
        )
    }

    fun cancelMissionWithin48HoursByEmployer(onSuccess: (MissionUser) -> Unit) {
        missionService.cancelMissionWithin48HoursByEmployer(
            mission,
            UserData.currentUserData,
            onSuccess
        )
    }

    fun disputeMission(onSuccess: (Mission) -> Unit) {
        missionService.disputeMission(mission, onSuccess)
    }

    fun rejectMission(onSuccess: (Mission) -> Unit) {
        missionService.rejectMission(mission, onSuccess)
    }
}