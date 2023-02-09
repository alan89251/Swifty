package com.team2.handiwork.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.team2.handiwork.AppConst
import com.team2.handiwork.models.UserRegistrationForm

class ActivityUserProfileViewModel(context: Context) : ViewModel() {
    val registrationForm = MutableLiveData<UserRegistrationForm>(UserRegistrationForm())

    fun initRegistrationForm(context: Context) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val email = sp.getString(AppConst.EMAIL, "")
        val uId = sp.getString(AppConst.PREF_UID, "")
        registrationForm.value!!.email = email!!
        registrationForm.value!!.uId = uId!!
    }

}