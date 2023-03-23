package com.team2.handiwork.viewModel.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.firebase.firestore.repository.UserCollection
import com.team2.handiwork.models.User

class FragmentViewProfileViewModel : ViewModel() {
    var fs = UserCollection()
    var user = MutableLiveData<User>()
}