package com.team2.handiwork.viewModel.profile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.firebase.firestore.Firestore
import com.team2.handiwork.models.User


class FragmentUpdateProfileViewModel : ViewModel() {
    private val user = MutableLiveData<User>()
    val firstName = MutableLiveData<String>()
    val lastName = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()

    fun setUser(_user: User) {
        Log.d("hehehe", "setUser: ${_user.firstName}")
        user.value = _user
        firstName.value = _user.firstName
        lastName.value = _user.lastName
        phoneNumber.value = _user.phoneNumber
    }

    fun updateUser(callback: (User) -> Unit) {
        user.value!!.firstName = firstName.value!!
        user.value!!.lastName = lastName.value!!
        user.value!!.phoneNumber = phoneNumber.value!!
        Firestore().userCollection.updateUser(user.value!!, callback)
    }
}