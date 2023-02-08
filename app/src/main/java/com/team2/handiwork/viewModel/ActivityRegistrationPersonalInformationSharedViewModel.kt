package com.team2.handiwork.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.models.UserRegistrationForm

class ActivityRegistrationPersonalInformationSharedViewModel: ViewModel() {
    var form: MutableLiveData<UserRegistrationForm> = MutableLiveData()
}