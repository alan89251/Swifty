package com.team2.handiwork.viewModel

import android.view.View
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.R
import com.team2.handiwork.firebase.Firestore
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.User
import com.team2.handiwork.singleton.UserData
import io.reactivex.rxjava3.core.Observable
import java.text.SimpleDateFormat
import java.util.*

class FragmentCreateMissionPriceViewModel: ViewModel() {
    var mission: Mission = Mission()
    var missionDuration: String = ""
        get() {
            val dateFormatter = SimpleDateFormat("MM/dd/yyyy HH:mm")
            return dateFormatter.format(Date(mission.startTime)) +
                    " - " +
                    dateFormatter.format(Date(mission.endTime))
        }
    val price: MutableLiveData<Double> = MutableLiveData(0.0)
    val isShowCreditErrorMsg: MediatorLiveData<Int> = MediatorLiveData()
    val creditErrorMsg: MediatorLiveData<Int> = MediatorLiveData()
    val isEnableBtnConfirm: MediatorLiveData<Boolean> = MediatorLiveData()

    init {
        isShowCreditErrorMsg.addSource(price) {
            isShowCreditErrorMsg.value =
                if (isMissionCreditValid()) View.INVISIBLE else View.VISIBLE
        }
        creditErrorMsg.addSource(price) {
            creditErrorMsg.value = createCreditErrorMsg()
        }
        isEnableBtnConfirm.addSource(price) {
            isEnableBtnConfirm.value = isMissionCreditValid()
        }
    }

    fun updateSuspendAmount(user: User): Observable<Boolean> {
        return Firestore().updateUser(user)
    }

    fun addMissionToDB(mission: Mission): Observable<Boolean> {
        return Firestore().addMission("Missions", mission)
    }

    fun isMissionCreditValid(): Boolean {
        return price.value != null
                && price.value!! > 0.0
                && price.value!! <= UserData.currentUserData.balance
    }

    fun createCreditErrorMsg(): Int {
        return if (price.value == null) {
            R.string.empty_credit
        } else if (price.value!! <= 0.0) {
            R.string.credit_less_than_or_equal_to_zero
        } else if (price.value!! > UserData.currentUserData.balance) {
            R.string.not_enough_credit
        } else {
            R.string.empty_string
        }
    }

}