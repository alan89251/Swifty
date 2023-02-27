package com.team2.handiwork.viewModel

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.firebase.Firestore
import com.team2.handiwork.models.Enrollment
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.User
import io.reactivex.rxjava3.core.Observable

class FragmentEmployerMissionDetailsViewModel: ViewModel() {
    var mission: Mission = Mission()
    var enrollments: MutableLiveData<List<Enrollment>> = MutableLiveData()
    var selectedEnrollment: MutableLiveData<Enrollment> = MutableLiveData()

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
                selectedEnrollment.value = it
            }
    }

    fun updateMission(mission: Mission): Observable<Boolean> {
        return Firestore().updateMission(mission)
    }

    fun updateEmployerSuspendAmount(user: User): Observable<Boolean> {
        return Firestore().updateUser(user)
    }


}