package com.team2.handiwork.viewModel

import android.view.View
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.R
import com.team2.handiwork.models.Mission

class FragmentCreateMissionCompletionViewModel: ViewModel() {
    var isCreateMissionSuccess: MutableLiveData<Boolean> = MutableLiveData()
    var missionResult: MediatorLiveData<Int> = MediatorLiveData()
    var missionResultTextColor: MediatorLiveData<String> = MediatorLiveData()
    var missionResultDescription: MediatorLiveData<Int> = MediatorLiveData()
    var btnViewMissionVisibility: MediatorLiveData<Int> = MediatorLiveData()
    var mission: Mission = Mission()

    init {
        missionResult.addSource(isCreateMissionSuccess) {
            missionResult.value =
                if (it) R.string.create_mission_result_success else R.string.create_mission_result_failed
        }

        missionResultTextColor.addSource(isCreateMissionSuccess) {
            missionResultTextColor.value = if (it) "#366FFF" else "#D52941"
        }

        missionResultDescription.addSource(isCreateMissionSuccess) {
            missionResultDescription.value =
                if (it) R.string.create_mission_result_description_success else R.string.create_mission_result_description_failed
        }

        btnViewMissionVisibility.addSource(isCreateMissionSuccess) {
            btnViewMissionVisibility.value =
                if (it) View.VISIBLE else View.INVISIBLE
        }
    }
}