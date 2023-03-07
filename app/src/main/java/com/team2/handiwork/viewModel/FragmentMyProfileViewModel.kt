package com.team2.handiwork.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.firebase.firestore.Firestore
import com.team2.handiwork.models.Comment
import com.team2.handiwork.singleton.UserData
import io.reactivex.rxjava3.core.Observable

class FragmentMyProfileViewModel : ViewModel() {

    var fs = Firestore()
    var userData: MutableLiveData<UserData> = MutableLiveData(UserData)
    var categories: MutableLiveData<String> = MutableLiveData<String>("")

    fun getComments(): Observable<List<Comment>> {
        return fs.commentCollection.getComments(userData.value!!.currentUserData.email)
    }
}