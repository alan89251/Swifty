package com.team2.handiwork.viewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.models.User

class FragmentRegistrationPersonalInformationViewModel : ViewModel() {
    // temp form
    var form = MutableLiveData<User>(User())

    var firstName = MutableLiveData(form.value!!.firstName)
    var lastName = MutableLiveData(form.value!!.lastName)
    var phoneNumber = MutableLiveData(form.value!!.phoneNumber)
    var verifyMsg = MutableLiveData("")
    var email = MutableLiveData(form.value!!.email)


    var nextBtnEnabled: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>()

    init {
        nextBtnEnabled.addSource(firstName) { checkBtnEnable() }
        nextBtnEnabled.addSource(lastName) { checkBtnEnable() }

    }

    private fun checkBtnEnable() {
        if (firstName.value!!.isEmpty()
            || lastName.value!!.isEmpty()
        ) {
            nextBtnEnabled.value = false
            return
        }
        nextBtnEnabled.value = true
    }

}