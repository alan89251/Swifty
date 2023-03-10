package com.team2.handiwork.viewModel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.enums.MissionStatusEnum
import com.team2.handiwork.firebase.firestore.repository.ChatCollection
import com.team2.handiwork.models.ChatMessage
import com.team2.handiwork.models.Mission
import com.team2.handiwork.utilities.Utility

class FragmentChatViewModel : ViewModel() {
    val mission = MutableLiveData<Mission>()

    // todo duplicate
    val period = MutableLiveData<String>("")
    val viewGone = MutableLiveData<Int>(View.GONE)
    var missionStatusDisplay = MutableLiveData<MissionStatusEnum>(MissionStatusEnum.COMPLETED)
    var repo = ChatCollection()
    var initMsg = MutableLiveData<Boolean>(false)

    // todo duplicate
    fun updatePeriod() {
        val startDate = Utility.convertLongToDate(mission.value!!.startTime)
        val startTime = Utility.convertLongToHour(mission.value!!.startTime)
        val endDate = Utility.convertLongToDate(mission.value!!.endTime)
        val endTime = Utility.convertLongToHour(mission.value!!.endTime)
        period.value = "$startDate $startTime - $endDate $endTime"
    }

    fun getInitDefaultMessages(): ArrayList<ChatMessage> {
        return arrayListOf<ChatMessage>(
            ChatMessage(text = "Hello! I'm interested!"),
            ChatMessage(text = "Do you have the tools?"),
        )
    }
}