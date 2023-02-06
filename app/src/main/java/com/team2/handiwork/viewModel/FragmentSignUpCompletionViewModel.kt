package com.team2.handiwork.viewModel

import androidx.lifecycle.MutableLiveData

class FragmentSignUpCompletionViewModel {
    var isMissionSuccess: MutableLiveData<Boolean> = MutableLiveData()
    var missionResult: MutableLiveData<String> = MutableLiveData()
    var missionResultDescription: MutableLiveData<String> = MutableLiveData()
    var navBtnText: MutableLiveData<String> = MutableLiveData()
}