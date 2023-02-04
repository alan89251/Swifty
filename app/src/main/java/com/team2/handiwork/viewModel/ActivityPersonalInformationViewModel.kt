package com.team2.handiwork.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ActivityPersonalInformationViewModel : ViewModel() {
    var verificationCode: MutableLiveData<String> = MutableLiveData()
}