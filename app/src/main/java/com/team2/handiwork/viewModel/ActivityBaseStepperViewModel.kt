package com.team2.handiwork.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ActivityBaseStepperViewModel : ViewModel() {
    var currentStep = MutableLiveData<Int>(1)
}