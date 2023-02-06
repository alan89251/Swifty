package com.team2.handiwork.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FragmentRegistrationWorkerTNCViewModel : ViewModel() {
    var isEnableNextBtn: MutableLiveData<Boolean> = MutableLiveData(false)
}