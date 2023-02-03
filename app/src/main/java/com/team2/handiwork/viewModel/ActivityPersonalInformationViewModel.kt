package com.team2.handiwork.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.models.PersonalInformationForm

class ActivityPersonalInformationViewModel : ViewModel() {
    var form: MutableLiveData<PersonalInformationForm> = MutableLiveData()
    var verificationCode: MutableLiveData<String> = MutableLiveData()
}