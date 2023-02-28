package com.team2.handiwork.viewModel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.enum.MissionStatusEnum
import com.team2.handiwork.enum.TransactionEnum
import com.team2.handiwork.models.Enrollment
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.Transaction
import com.team2.handiwork.services.MissionService
import com.team2.handiwork.singleton.UserData
import io.reactivex.rxjava3.core.Observable

class FragmentAgentMissionDetailsViewModel : ViewModel() {
    var mission = MutableLiveData<Mission>()
    val enrolled = MutableLiveData<Boolean>(false)
    val withdrawWarn = MutableLiveData<Boolean>(false)
    val withdraw = MutableLiveData<Boolean>(false)
    val finished = MutableLiveData<Boolean>(false)

    var cancelledButtonVisibility = MutableLiveData<Int>(View.GONE)
    var enrolledButtonVisibility = MutableLiveData<Int>(View.GONE)
    var finishedButtonVisibility = MutableLiveData<Int>(View.GONE)

    // firebase
    val service = MissionService()

    fun updateMissionStatus(status: MissionStatusEnum) {
        val m = mission.value!!
        m.status = status.value
        mission.value = m
    }

    fun updateButtonVisibility() {
        when (mission.value!!.status) {
            MissionStatusEnum.CONFIRMED.value -> {
                if (mission.value!!.before48Hour) {
                    cancelledButtonVisibility.value = View.VISIBLE
                    finishedButtonVisibility.value = View.GONE
                } else {
                    finishedButtonVisibility.value = View.VISIBLE
                    cancelledButtonVisibility.value = View.GONE
                }
            }
            MissionStatusEnum.OPEN.value -> {
                enrolledButtonVisibility.value = View.VISIBLE
            }
            else -> {
                cancelledButtonVisibility.value = View.GONE
                enrolledButtonVisibility.value = View.GONE
                finishedButtonVisibility.value = View.GONE
            }
        }
    }

    fun enrollMission(enrollment: Enrollment): Observable<Boolean> {
        updateMissionStatus(MissionStatusEnum.ENROLLED)
        mission.value!!.enrollments.add(enrollment.agent)
        return service.submitEnrollmentToMission(enrollment, mission.value!!)
    }

    fun withdrawMission(enrollment: Enrollment): Observable<Boolean> {
        enrollment.enrolled = false
        updateMissionStatus(MissionStatusEnum.OPEN)
        mission.value!!.enrollments.remove(enrollment.agent)

        val deductedAmount = if (mission.value!!.before48Hour) {
            (mission.value!!.price / 2).toInt()
        } else {
            mission.value!!.price.toInt()
        }
        val balance = UserData.currentUserData.balance - deductedAmount

        val transaction = Transaction()
        transaction.amount = deductedAmount
        transaction.title =
            "${mission.value!!.serviceType} - ${mission.value!!.subServiceType} Withdraw"
        transaction.firstName = UserData.currentUserData.firstName
        transaction.lastName = UserData.currentUserData.lastName
        transaction.transType = TransactionEnum.WITHDRAW

        UserData.currentUserData.balance = balance
        return service.withdrawMission(enrollment, mission.value!!, balance, transaction)
    }

    fun finishedMission(): Observable<Boolean> {
        updateMissionStatus(MissionStatusEnum.PENDING_ACCEPTANCE)
        return service.finishedMission(mission.value!!)
    }

}