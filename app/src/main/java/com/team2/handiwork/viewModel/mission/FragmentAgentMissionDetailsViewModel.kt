package com.team2.handiwork.viewModel.mission

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.base.viewModel.BaseMissionViewModel
import com.team2.handiwork.enums.MissionStatusEnum
import com.team2.handiwork.firebase.firestore.Firestore
import com.team2.handiwork.firebase.firestore.service.MissionService
import com.team2.handiwork.models.Mission
import com.team2.handiwork.utilities.Utility
import io.reactivex.rxjava3.disposables.CompositeDisposable

class FragmentAgentMissionDetailsViewModel : BaseMissionViewModel() {
    val fs = Firestore()
    var mission = MutableLiveData<Mission>()
    val enrolled = MutableLiveData<Boolean>(false)
    val withdrawBefore48Hours = MutableLiveData<Boolean>(false)
    val withdrawWithin48Hours = MutableLiveData<Boolean>(false)
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
        fs.userCollection,
        fs.missionCollection,
        fs.transactionCollection,
    )

    fun updateButtonVisibility() {
        val status = mission.value!!.status
        missionStatusDisplay.value = status

        // reset
        revokeButtonVisibility.value = View.GONE
        finishedButtonVisibility.value = View.GONE
        enrolledButtonVisibility.value = View.GONE
        cancelledButtonVisibility.value = View.GONE

        when (status) {
            MissionStatusEnum.CONFIRMED -> {
                cancelledButtonVisibility.value = View.VISIBLE
                if (mission.value!!.startTime > System.currentTimeMillis()) return
                finishedButtonVisibility.value = View.VISIBLE
            }

            MissionStatusEnum.OPEN -> {
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