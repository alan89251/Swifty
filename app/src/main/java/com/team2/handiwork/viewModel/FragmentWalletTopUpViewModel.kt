package com.team2.handiwork.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FragmentWalletTopUpViewModel : ViewModel() {
    var creditCardNo = MutableLiveData<String>("")
    var cvv = MutableLiveData<String>("")
    var expiredDate = MutableLiveData<String>("")
}