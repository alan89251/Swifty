package com.team2.handiwork.viewModel.profile

import android.app.Dialog
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.team2.handiwork.firebase.firestore.Firestore
import com.team2.handiwork.firebase.firestore.repository.UserCollection
import com.team2.handiwork.models.Certification
import com.team2.handiwork.models.ServiceType
import com.team2.handiwork.singleton.UserData
import kotlinx.coroutines.launch

class FragmentMyProfileViewModel : FragmentBaseProfileViewModel() {

    val imageURL = MutableLiveData("")
    val newImageUrl = MutableLiveData<Uri>()
    val newCertName = MutableLiveData("")

    fun cancelDistanceSubscription() {
        UserData.currentUserData.distance = 0

        // update DB
        Firestore()
            .userCollection
            .updateUser(UserData.currentUserData, {})
    }

    fun cancelServiceTypeSubscription() {
        UserData.currentUserData.serviceTypeList = ArrayList()

        // update DB
        Firestore()
            .userCollection
            .updateUser(UserData.currentUserData, {})
    }

    fun uploadCertificate(email: String, callback: () -> Unit) {

        viewModelScope.launch {
            imageURL.value = certRepo.uploadCert(newImageUrl.value!!, "$email-${newCertName.value!!}")
            try {
                if (imageURL.value!!.isNotEmpty()) {
                    val cert = Certification()
                    cert.name = newCertName.value!!
                    cert.imgUrl = imageURL.value!!
                    cert.email = email
                    certRepo.addCertification(email, cert)
                    newCertName.value = ""
                    callback()
                }
            } catch (e: Exception) {

            }
        }
    }

    fun deleteCertificate(email: String, certification: Certification, callback: () -> Unit) {
        viewModelScope.launch {
            certRepo.deleteCertification(email, certification)
            callback()
        }
    }
}