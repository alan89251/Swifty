package com.team2.handiwork.viewModel

import android.view.View
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.R
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.User

class FragmentAcceptedMissionCompletionViewModel : ViewModel() {
    var isAcceptMissionSuccess: MutableLiveData<Boolean> = MutableLiveData(false)
    var missionResult: MediatorLiveData<Int> = MediatorLiveData()
    var missionResultTextColor: MediatorLiveData<Int> = MediatorLiveData()
    var missionResultDescription: MediatorLiveData<Int> = MediatorLiveData()
    var btnLeaveReviewVisibility: MediatorLiveData<Int> = MediatorLiveData()
    var isAgentReviewed: MutableLiveData<Boolean> = MutableLiveData(false)
    var isBtnLeaveReviewClicked: MutableLiveData<Boolean> = MutableLiveData(false)
    var missionResultDescriptionVisibility: MediatorLiveData<Int> = MediatorLiveData()
    var agent: User = User()
    var mission: Mission = Mission()

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
                if (isShowBtnLeaveReview()) View.VISIBLE else View.INVISIBLE
        }

        btnLeaveReviewVisibility.addSource(isAgentReviewed) {
            btnLeaveReviewVisibility.value =
                if (isShowBtnLeaveReview()) View.VISIBLE else View.INVISIBLE
        }

        missionResultDescriptionVisibility.addSource(isBtnLeaveReviewClicked) {
            missionResultDescriptionVisibility.value =
                if (it) View.INVISIBLE else View.VISIBLE
        }
    }

    private fun isShowBtnLeaveReview(): Boolean {
        return isAcceptMissionSuccess.value!!
                && !isAgentReviewed.value!!
    }
}