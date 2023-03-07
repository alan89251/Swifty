package com.team2.handiwork.viewModel.mission

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.enums.MissionStatusEnum
import com.team2.handiwork.firebase.firestore.Firestore
import com.team2.handiwork.firebase.firestore.service.MissionService
import com.team2.handiwork.models.Mission
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.utilities.Ext.Companion.disposedBy
import com.team2.handiwork.utilities.Utility
import io.reactivex.rxjava3.disposables.CompositeDisposable

class FragmentAgentMissionDetailsViewModel : ViewModel() {
    val fs = Firestore()
    var mission = MutableLiveData<Mission>()
    val enrolled = MutableLiveData<Boolean>(false)
    val withdrawWarn = MutableLiveData<Boolean>(false)
    val withdraw = MutableLiveData<Boolean>(false)
    val revoke = MutableLiveData<Boolean>(false)
    val finished = MutableLiveData<Boolean>(false)
    val email = MutableLiveData<String>("")
    val period = MutableLiveData<String>("")
    var missionStatusDisplay = MutableLiveData<MissionStatusEnum>(MissionStatusEnum.COMPLETED)

    var cancelledButtonVisibility = MutableLiveData<Int>(View.GONE)
    var enrolledButtonVisibility = MutableLiveData<Int>(View.GONE)
    var finishedButtonVisibility = MutableLiveData<Int>(View.GONE)
    var revokeButtonVisibility = MutableLiveData<Int>(View.GONE)

    var disposeBag = CompositeDisposable()

    // firebase
    val service = MissionService(
        fs.userCollection, fs.missionCollection
    )

//    private fun updateMissionStatus(status: MissionStatusEnum) {
//        val m = mission.value!!
//        m.status = status.value
//        mission.value = m
//    }

    fun updateButtonVisibility() {
        val status = mission.value!!.status
        missionStatusDisplay.value!!.value = status

        // reset
        revokeButtonVisibility.value = View.GONE
        finishedButtonVisibility.value = View.GONE
        enrolledButtonVisibility.value = View.GONE
        cancelledButtonVisibility.value = View.GONE

        when (status) {
            MissionStatusEnum.CONFIRMED.value -> {
                cancelledButtonVisibility.value = View.VISIBLE
                if (mission.value!!.startTime < System.currentTimeMillis()) return
                finishedButtonVisibility.value = View.VISIBLE
            }

            MissionStatusEnum.OPEN.value -> {
                if (isEnrolled()) {
                    revokeButtonVisibility.value = View.VISIBLE
                    missionStatusDisplay.value = MissionStatusEnum.ENROLLED
                } else {
                    enrolledButtonVisibility.value = View.VISIBLE
                    missionStatusDisplay.value = MissionStatusEnum.OPEN
                }
            }

            else -> {
                cancelledButtonVisibility.value = View.GONE
                enrolledButtonVisibility.value = View.GONE
                finishedButtonVisibility.value = View.GONE
                revokeButtonVisibility.value = View.GONE
            }
        }
    }

    fun enrollMission() {
        service.enrolledMission(mission.value!!, UserData.currentUserData.email)
            .subscribe {
                mission.value = it
            }.disposedBy(disposeBag)
    }

    fun revokeMission() {
        val email = email.value.toString()
        service.revokeMission(mission.value!!, email).subscribe {
            mission.value = it
        }.disposedBy(disposeBag)
    }

    fun withdrawMission() {
//        val deductedAmount = if (mission.value!!.before48Hour) {
//            (mission.value!!.price / 2).toInt()
//        } else {
//            mission.value!!.price.toInt()
//        }
//        val balance = UserData.currentUserData.balance - deductedAmount
//
//        val transaction = Transaction()
//        transaction.amount = deductedAmount
//        transaction.title =
//            "${mission.value!!.serviceType} - ${mission.value!!.subServiceType} Withdraw"
//        transaction.firstName = UserData.currentUserData.firstName
//        transaction.lastName = UserData.currentUserData.lastName
//        transaction.transType = TransactionEnum.WITHDRAW

//        UserData.currentUserData.balance = balance
        if (mission.value!!.before48Hour) {
            service.cancelMissionBefore48HoursByAgent(mission.value!!, UserData.currentUserData)
                .subscribe {
                    UserData.currentUserData = it.user
                    mission.value = it.mission
                }.disposedBy(disposeBag)
        } else {
            service.cancelMissionWithin48HoursByAgent(mission.value!!, UserData.currentUserData)
                .subscribe {
                    UserData.currentUserData = it.user
                    mission.value = it.mission
                }.disposedBy(disposeBag)
        }

    }

    fun finishedMission() {
        service.finishedMission(mission.value!!).subscribe {
            mission.value = it
        }.disposedBy(disposeBag)
    }

    fun updatePeriod() {
        val startDate = Utility.convertLongToDate(mission.value!!.startTime)
        val startTime = Utility.convertLongToHour(mission.value!!.startTime)
        val endDate = Utility.convertLongToDate(mission.value!!.endTime)
        val endTime = Utility.convertLongToHour(mission.value!!.endTime)
        period.value = "$startDate $startTime - $endDate $endTime"
    }

    fun isEnrolled(): Boolean {
        return mission.value!!.enrollments.contains(email.value.toString())
    }

}