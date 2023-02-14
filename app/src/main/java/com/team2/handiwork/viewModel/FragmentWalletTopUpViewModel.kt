package com.team2.handiwork.viewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FragmentWalletTopUpViewModel : ViewModel() {
    var creditCardNo = MutableLiveData<String>("")
    var cvv = MutableLiveData<String>("")
    var expiredDate = MutableLiveData<String>("")
    var topUpAmount = MutableLiveData<Int>(50)

    var topUpBtnEnabled: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>()

    init {
        topUpBtnEnabled.addSource(creditCardNo) { checkBtnEnable() }
        topUpBtnEnabled.addSource(cvv) { checkBtnEnable() }
        topUpBtnEnabled.addSource(expiredDate) { checkBtnEnable() }
    }

    private fun checkBtnEnable() {
        if (creditCardNo.value!!.length < 16
            || cvv.value!!.length < 3
            || expiredDate.value!!.length < 4
        ) {
            topUpBtnEnabled.value = false
            return
        }
        topUpBtnEnabled.value = true
    }

}