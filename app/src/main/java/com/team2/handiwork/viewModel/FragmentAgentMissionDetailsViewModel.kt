package com.team2.handiwork.viewModel

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.enums.MissionStatusEnum
import com.team2.handiwork.enums.TransactionEnum
import com.team2.handiwork.models.Enrollment
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.Transaction
import com.team2.handiwork.services.MissionService
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.utilities.Utility
import io.reactivex.rxjava3.core.Observable

class FragmentAgentMissionDetailsViewModel : ViewModel() {
    var mission = MutableLiveData<Mission>()
    val enrolled = MutableLiveData<Boolean>(false)
    val withdrawWarn = MutableLiveData<Boolean>(false)
    val withdraw = MutableLiveData<Boolean>(false)
    val finished = MutableLiveData<Boolean>(false)
    val email = MutableLiveData<String>("")
    val period = MutableLiveData<String>("")
    var missionStatusDisplay = MutableLiveData<MissionStatusEnum>(MissionStatusEnum.COMPLETED)

    var cancelledButtonVisibility = MutableLiveData<Int>(View.GONE)
    var enrolledButtonVisibility = MutableLiveData<Int>(View.GONE)
    var finishedButtonVisibility = MutableLiveData<Int>(View.GONE)

    // firebase
    val service = MissionService()

    private fun updateMissionStatus(status: MissionStatusEnum) {
        val m = mission.value!!
        m.status = status.value
        mission.value = m
    }

    fun updateButtonVisibility() {
        val status = mission.value!!.status
        missionStatusDisplay.value!!.value = status
        when (status) {
            MissionStatusEnum.CONFIRMED.value -> {
                cancelledButtonVisibility.value = View.VISIBLE
                if (mission.value!!.startTime >= System.currentTimeMillis()) {
                    finishedButtonVisibility.value = View.VISIBLE
                } else {
                    finishedButtonVisibility.value = View.GONE
                }
            }

            MissionStatusEnum.OPEN.value -> {
                if (isEnrolled()) {
                    enrolledButtonVisibility.value = View.GONE
                    cancelledButtonVisibility.value = View.VISIBLE
                    missionStatusDisplay.value = MissionStatusEnum.ENROLLED
                } else {
                    enrolledButtonVisibility.value = View.VISIBLE
                    cancelledButtonVisibility.value = View.GONE
                    missionStatusDisplay.value = MissionStatusEnum.OPEN
                }
            }

            else -> {
                cancelledButtonVisibility.value = View.GONE
                enrolledButtonVisibility.value = View.GONE
                finishedButtonVisibility.value = View.GONE
            }
        }
    }

    fun enrollMission(enrollment: Enrollment): Observable<Boolean> {
        mission.value!!.enrollments.add(enrollment.agent)
        updateMissionStatus(MissionStatusEnum.OPEN)
        return service.submitEnrollmentToMission(enrollment, mission.value!!)
    }

    fun withdrawMission(enrollment: Enrollment): Observable<Boolean> {
        enrollment.enrolled = false
        mission.value!!.enrollments.remove(enrollment.agent)
        updateMissionStatus(MissionStatusEnum.OPEN)

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

    fun updatePeriod() {
        val startDate = Utility.convertLongToDate(mission.value!!.startTime)
        val startTime = Utility.convertLongToHour(mission.value!!.endTime)
        val endDate = Utility.convertLongToDate(mission.value!!.endTime)
        val endTime = Utility.convertLongToHour(mission.value!!.endTime)
        period.value = "$startDate $startTime - $endDate $endTime"
    }

    fun isEnrolled(): Boolean {
        return mission.value!!.enrollments.contains(email.value.toString())
    }

}