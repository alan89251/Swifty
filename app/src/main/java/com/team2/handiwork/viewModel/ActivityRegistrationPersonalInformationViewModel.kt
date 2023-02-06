package com.team2.handiwork.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ActivityRegistrationPersonalInformationViewModel : ViewModel() {
    var verificationCode: MutableLiveData<String> = MutableLiveData()
}