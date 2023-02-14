package com.team2.handiwork.viewModel

import androidx.lifecycle.ViewModel
import com.team2.handiwork.firebase.Firestore
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.User
import io.reactivex.rxjava3.core.Observable
import java.text.SimpleDateFormat
import java.util.*

class FragmentCreateMissionPriceViewModel: ViewModel() {
    var mission: Mission = Mission()
    var missionDuration: String = ""
        get() {
            val dateFormatter = SimpleDateFormat("MM/dd/yyyy HH:mm")
            return dateFormatter.format(Date(mission.startTime)) +
                    " - " +
                    dateFormatter.format(Date(mission.endTime))
        }

    fun updateSuspendAmount(user: User): Observable<Boolean> {
        return Firestore().updateUser(user)
    }

    fun addMissionToDB(mission: Mission): Observable<Boolean> {
        return Firestore().addMission("Missions", mission)
    }

}