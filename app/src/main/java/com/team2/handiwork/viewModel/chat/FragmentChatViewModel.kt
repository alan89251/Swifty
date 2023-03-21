package com.team2.handiwork.viewModel.chat

import android.util.Log
import android.view.View
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.base.viewModel.BaseMissionViewModel
import com.team2.handiwork.enums.FirebaseCollectionKey
import com.team2.handiwork.firebase.firestore.repository.ChatCollection
import com.team2.handiwork.firebase.firestore.repository.MissionCollection
import com.team2.handiwork.models.ChatUser
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.User
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.utilities.Ext.Companion.toChatUser
import com.team2.handiwork.utilities.Utility

class FragmentChatViewModel : BaseMissionViewModel() {
    val mission = MutableLiveData<Mission>()
    var mToken: String = ""

    // todo duplicate
    val period = MutableLiveData<String>("")
    val viewGone = MutableLiveData<Int>(View.GONE)
    val sendBtnEnabled = MutableLiveData<Boolean>(false)

    var misAgent = false
    val toEmail = MutableLiveData<String>("")
    var agent = MutableLiveData<ChatUser>()
    val employer = MutableLiveData<ChatUser>()

    val toUser = MediatorLiveData<ChatUser>()
    val fromUser = MediatorLiveData<ChatUser>()

    var repo = ChatCollection()
    var missionRepo = MissionCollection()

    init {
        toUser.addSource(agent) {
            if (misAgent) {
                fromUser.value = it
            }
            if (!misAgent) {
                toUser.value = it
            }
        }

        toUser.addSource(employer) {
            if (misAgent) {
                toUser.value = it
            }
            if (!misAgent) {
                fromUser.value = it
            }
        }
    }

    // todo duplicate
    fun updatePeriod() {
        val startDate = Utility.convertLongToDate(mission.value!!.startTime)
        val startTime = Utility.convertLongToHour(mission.value!!.startTime)
        val endDate = Utility.convertLongToDate(mission.value!!.endTime)
        val endTime = Utility.convertLongToHour(mission.value!!.endTime)
        period.value = "$startDate $startTime - $endDate $endTime"
    }

    fun getNotificationToken(toEmail: String) {
        Firebase.firestore.collection(FirebaseCollectionKey.USERS.displayName)
            .document(toEmail)
            .get()
            .addOnSuccessListener { document ->
                sendBtnEnabled.value = true
                if (document.data != null) {
                    val toUser = document.toObject<User>()!!
                    if (misAgent) {
                        employer.value = toUser.toChatUser()
                    } else {
                        employer.value = UserData.currentUserData.toChatUser()
                    }
                    mToken = toUser.fcmDeviceToken
                }
            }.addOnFailureListener { e ->
                sendBtnEnabled.value = true
                Log.e("ChatFragment", "Error reading document", e)
            }
    }
}