package com.team2.handiwork.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.firebase.firestore.repository.ChatCollection
import com.team2.handiwork.firebase.firestore.repository.UserCollection
import com.team2.handiwork.models.Chat

class FragmentChatRoomViewModel : ViewModel() {
    var repo = ChatCollection()
    var user = UserCollection()
}