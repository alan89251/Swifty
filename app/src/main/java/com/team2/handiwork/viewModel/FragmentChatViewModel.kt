package com.team2.handiwork.viewModel

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
import com.team2.handiwork.models.ChatMessage
import com.team2.handiwork.models.ChatUser
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.User
import com.team2.handiwork.singleton.UserData
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
    var agent = MutableLiveData<User>(User())
    val employer = MutableLiveData<User>(User())

    val toUser = MediatorLiveData<User>()
    val fromUser = MediatorLiveData<User>()


    val missionUserIsLoading: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>()
    val loaded: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>()
    var isFinished = MutableLiveData<Boolean>(false)
    var repo = ChatCollection()
    var initMsg = MutableLiveData<Boolean>(false)

    init {
        Log.d("????1111111", "")
//        missionUserIsLoading.addSource(mission) {
//            Log.d("???111", it.toString())
//            if (it == null) loaded.value = false
//
//        }
//
//        missionUserIsLoading.addSource(isFinished) {
//            Log.d("???11224441", it.toString())
//            if (!it) loaded.value = false
//        }

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

    fun getChatUserOfAgent(): ChatUser {
        val user = ChatUser()
        user.firstName = agent.value!!.firstName
        user.lastName = agent.value!!.lastName
        user.email = agent.value!!.email
        user.imageURi = agent.value!!.imageURi
        user.uid = agent.value!!.uId
        return user
    }

    fun getNotificationToken() {
        Firebase.firestore.collection(FirebaseCollectionKey.USERS.displayName)
            .document(toEmail.value!!)
            .get()
            .addOnSuccessListener { document ->
                sendBtnEnabled.value = true
                if (document.data != null) {
                    val toUser = document.toObject<User>()!!
                    Log.d("????????? toEmail", "success")
                    if (misAgent) {
                        employer.value = toUser
                    } else {
                        employer.value = UserData.currentUserData
                    }
                    mToken = toUser.fcmDeviceToken
                }
            }.addOnFailureListener { e ->
                sendBtnEnabled.value = true
                Log.e("ChatFragment", "Error reading document", e)
            }
    }
}