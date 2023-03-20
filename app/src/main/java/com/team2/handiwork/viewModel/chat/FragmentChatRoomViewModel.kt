package com.team2.handiwork.viewModel.chat

import androidx.lifecycle.ViewModel
import com.team2.handiwork.firebase.firestore.repository.ChatCollection
import com.team2.handiwork.models.ChatInfo
import com.team2.handiwork.models.ChatUser

class FragmentChatRoomViewModel : ViewModel() {
    var repo = ChatCollection()
    var chatUserMap = hashMapOf<String, ChatUser>()
    var chatInfo: ChatInfo = ChatInfo()
}