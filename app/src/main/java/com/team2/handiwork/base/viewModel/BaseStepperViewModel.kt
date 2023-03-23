package com.team2.handiwork.base.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BaseStepperViewModel : ViewModel() {
    var currentStep = MutableLiveData<Int>(1)
}