package com.team2.handiwork.viewModel.profile

import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.team2.handiwork.AppConst
import com.team2.handiwork.R
import com.team2.handiwork.firebase.Storage
import com.team2.handiwork.firebase.firestore.Firestore
import com.team2.handiwork.models.User
import com.team2.handiwork.singleton.UserData
import kotlinx.coroutines.launch


class FragmentUpdateProfileViewModel : ViewModel() {
    private val user = MutableLiveData<User>()
    val firstName = MutableLiveData<String>()
    val lastName = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()
    val newImageUrl = MutableLiveData<Uri>()
    val fsImgUrl = MutableLiveData<String>()

    fun setUser(_user: User) {
        user.value = _user
        firstName.value = _user.firstName
        lastName.value = _user.lastName
        phoneNumber.value = _user.phoneNumber
        fsImgUrl.value = _user.imageURi
    }

    fun updateUser(callback: (User) -> Unit) {
        user.value!!.firstName = firstName.value!!
        user.value!!.lastName = lastName.value!!
        user.value!!.phoneNumber = phoneNumber.value!!
        user.value!!.imageURi = fsImgUrl.value!!
        Firestore().userCollection.updateUser(user.value!!, callback)
    }

    fun uploadUserNew(
        finishUpdateUserCallback: (User) -> Unit,
        onLoadCB: (String) -> Unit,
        onFailCB: () -> Unit
    ) {
        if (newImageUrl.value != null) {
            Storage().uploadImg("User", user.value!!.email, newImageUrl.value!!).subscribe {
                if (it) {
                    Storage().getImgUrl("User/${UserData.currentUserData.email}", onLoadCB, onFailCB)
                }
            }
        } else {
            updateUser(finishUpdateUserCallback)
        }
    }

}