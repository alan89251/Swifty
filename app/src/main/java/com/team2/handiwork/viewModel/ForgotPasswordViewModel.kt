package com.team2.handiwork.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.team2.handiwork.ScreenMsg
import com.team2.handiwork.utilities.Event
import com.team2.handiwork.utilities.Utility
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    val userEmail = MutableLiveData<String>()
    private val statusMessage = MutableLiveData<Event<ScreenMsg>>()
    val message: LiveData<Event<ScreenMsg>>
        get() = statusMessage
    val countDown = MutableLiveData(10)


    fun startCountDown(startValue: Int) {
        viewModelScope.launch {
            for (i in startValue downTo 0) {
                countDown.postValue(i)
                delay(1000)
            }
        }
    }


    private fun passMessage(_message: ScreenMsg) {
        statusMessage.value = Event(_message)
    }

    private fun isValidEmailAddress(): Boolean {
        return if (userEmail.value != null) {
            Utility.isValidEmail(userEmail.value!!)
        } else {
            false
        }
    }

    fun sendResetPasswordEmail() {
        if (isValidEmailAddress()) {
            userEmail.value.let { email ->
                if (email != null) {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            passMessage(ScreenMsg.SEND_RESET_EMAIL_SUCCESSFUL)
                        } else {
                            passMessage(ScreenMsg.SEND_RESET_EMAIL_FAILED)
                        }
                    }
                }
            }
        } else {
            passMessage(ScreenMsg.INVALID_EMAIL_ADDRESS)
        }
    }


}