package com.team2.handiwork.viewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FragmentRegistrationPersonalInformationViewModel : ViewModel() {
    var firstName = MutableLiveData("")
    var lastName = MutableLiveData("")
    var phoneNumber = MutableLiveData("")
    var verifyMsg = MutableLiveData("")

    var nextBtnEnabled: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>()

    init {
        nextBtnEnabled.addSource(firstName) { checkBtnEnable() }
        nextBtnEnabled.addSource(lastName) { checkBtnEnable() }
        nextBtnEnabled.addSource(phoneNumber) { checkBtnEnable() }

    }

    private fun checkBtnEnable() {
        if (firstName.value!!.isEmpty()
            || lastName.value!!.isEmpty()
            || phoneNumber.value!!.isEmpty()
        ) {
            nextBtnEnabled.value = false
            return
        }
        nextBtnEnabled.value = true
    }

}