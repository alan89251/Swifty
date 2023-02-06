package com.team2.handiwork.viewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.models.UserRegistrationForm

class FragmentRegistrationPersonalInformationViewModel : ViewModel() {
    var firstName = MutableLiveData("")
    var lastName = MutableLiveData("")
    var phoneNumber = MutableLiveData("")
    var verifyMsg = MutableLiveData("")

    lateinit var form: UserRegistrationForm;

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
        form.firstName = firstName.value!!
        form.lastName = lastName.value!!
        form.phoneNumber = phoneNumber.value!!
        form.phoneVerify = verifyMsg.value!!.isNotEmpty()
        nextBtnEnabled.value = true
    }

}