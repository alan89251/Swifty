package com.team2.handiwork.viewModel.wallet

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class FragmentWalletTopUpViewModel : ViewModel() {
    var creditCardNo = MutableLiveData<String>("")
    var cvv = MutableLiveData<String>("")
    var expiredDate = MutableLiveData<String>("")
    var topUpAmount = MutableLiveData<Int>(50)

    var topUpBtnEnabled: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>()
    var errorMsg: MediatorLiveData<String> = MediatorLiveData<String>()

    init {
        topUpBtnEnabled.addSource(creditCardNo) { checkBtnEnable() }
        topUpBtnEnabled.addSource(cvv) { checkBtnEnable() }
        topUpBtnEnabled.addSource(expiredDate) { checkBtnEnable() }
    }

    private fun checkBtnEnable() {
        if (!isValidDate(expiredDate.value!!)) {
            errorMsg.value = "Invalid Expired Date"
            topUpBtnEnabled.value = false
            return
        } else if (creditCardNo.value!!.length < 16
            || cvv.value!!.length < 3
            || expiredDate.value!!.length < 4
        ) {
            topUpBtnEnabled.value = false
            errorMsg.value =
                "Invalid Input , " +
                        "Credit Card No should be 16 digit, " +
                        "CVC should be 3 digit, " +
                        "expired Date should be 4 digit"
            return
        }
        topUpBtnEnabled.value = true
        errorMsg.value = ""
    }

    private fun isValidDate(dateStr: String): Boolean {
        val pattern = "^(0[1-9]|1[0-2])[0-9]{2}$"
        val regex: Pattern = Pattern.compile(pattern)
        val matcher: Matcher = regex.matcher(dateStr)
        if (!matcher.matches()) return false

        val dateFormat = SimpleDateFormat("MMyy", Locale.getDefault())
        val expiredDate = dateFormat.parse(dateStr)

        return expiredDate.after(Date())
    }

}