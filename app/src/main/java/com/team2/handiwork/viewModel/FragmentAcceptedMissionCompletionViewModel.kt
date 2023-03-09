package com.team2.handiwork.viewModel

import android.view.View
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.R

class FragmentAcceptedMissionCompletionViewModel : ViewModel() {
    var isAcceptMissionSuccess: MutableLiveData<Boolean> = MutableLiveData()
    var missionResult: MediatorLiveData<Int> = MediatorLiveData()
    var missionResultTextColor: MediatorLiveData<Int> = MediatorLiveData()
    var missionResultDescription: MediatorLiveData<Int> = MediatorLiveData()
    var btnLeaveReviewVisibility: MediatorLiveData<Int> = MediatorLiveData()

    init {
        missionResult.addSource(isAcceptMissionSuccess) {
            missionResult.value =
                if (it) R.string.accept_mission_result_success else R.string.accept_mission_result_failed
        }

        missionResultTextColor.addSource(isAcceptMissionSuccess) {
            missionResultTextColor.value =
                if (it) R.color.bright_blue_90 else R.color.strong_red_100
        }

        missionResultDescription.addSource(isAcceptMissionSuccess) {
            missionResultDescription.value =
                if (it) R.string.accept_mission_result_description_success else R.string.accept_mission_result_description_failed
        }

        btnLeaveReviewVisibility.addSource(isAcceptMissionSuccess) {
            btnLeaveReviewVisibility.value =
                if (it) View.VISIBLE else View.INVISIBLE
        }
    }
}