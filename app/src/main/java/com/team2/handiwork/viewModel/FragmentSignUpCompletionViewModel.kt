package com.team2.handiwork.viewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.team2.handiwork.R

class FragmentSignUpCompletionViewModel {
    var isMissionSuccess: MutableLiveData<Boolean> = MutableLiveData()
    var missionResult: MediatorLiveData<Int> = MediatorLiveData()
    var missionResultTextColor: MediatorLiveData<Int> = MediatorLiveData()
    var missionResultDescription: MediatorLiveData<Int> = MediatorLiveData()
    var navBtnText: MediatorLiveData<String> = MediatorLiveData()

    init {
        missionResult.addSource(isMissionSuccess) {
            missionResult.value =
                if (it) R.string.mission_result_success else R.string.mission_result_failed
        }

        missionResultTextColor.addSource(isMissionSuccess) {
            missionResultTextColor.value = if (it) R.color.bright_blue_90 else R.color.strong_red_100
        }

        missionResultDescription.addSource(isMissionSuccess) {
            missionResultDescription.value =
                if (it) R.string.mission_result_description_success else R.string.mission_result_description_failed
        }

        navBtnText.addSource(isMissionSuccess) {
            navBtnText.value = if (it) "Continue" else "Back"
        }
    }
}