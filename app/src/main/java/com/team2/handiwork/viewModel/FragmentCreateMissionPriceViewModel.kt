package com.team2.handiwork.viewModel

import android.net.Uri
import android.view.View
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.R
import com.team2.handiwork.firebase.firestore.Firestore
import com.team2.handiwork.firebase.Storage
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
    val photoUploadQueue = MutableLiveData<Queue<Uri>>()
    val photoUploadResult = MutableLiveData<Boolean>()
    val isPhotoUploadCompleted = MutableLiveData<Boolean>()
    val price: MutableLiveData<Double> = MutableLiveData(0.0)
    val isShowCreditErrorMsg: MediatorLiveData<Int> = MediatorLiveData()
    val creditErrorMsg: MediatorLiveData<Int> = MediatorLiveData()
    val isEnableBtnConfirm: MediatorLiveData<Boolean> = MediatorLiveData()
    var fs = Firestore()

    private var photoSerialNo = 1

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

    fun updateSuspendAmount(
        user: User,
        onSuccess: (User) -> Unit,
        onError: (Exception) -> Unit
    ) {
        fs.userCollection.updateUser(user, onSuccess, onError)
    }

    fun addMissionToDB(): Observable<Mission> {
        return fs.missionCollection.addMission("Missions", mission)
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

    fun popPhotoFromQueueAndUploadToDB() {
        if (photoUploadQueue.value != null
            && !photoUploadQueue.value!!.isEmpty()) {
            val uri = photoUploadQueue.value!!.poll()!!
            // retrieve the sub file name
            val s = uri.toString().split(".")
            val subFileName = s.last()
            // file name format:
            // <mission_id>_<photo_serial_no>.<sub_file_name>
            Storage().uploadImg(
                "Mission",
                "${mission.missionId}_${photoSerialNo}.${subFileName}",
                uri,
                photoUploadResult)

            photoSerialNo += 1
        }
        else {
            isPhotoUploadCompleted.value = true
        }
    }

    fun uploadMissionPhotosToDB() {
        // set the value of photoUploadQueue trigger the upload process
        photoUploadQueue.value = LinkedList<Uri>(mission.missionPhotoUris)
    }

    fun updateMissionPhotoFireStorageUris(): Observable<Boolean> {
        // generate the photo uris
        for (i in 1..mission.missionPhotoUris.size) {
            // retrieve the sub file name
            val uri = mission.missionPhotoUris[i - 1]
            val s = uri.toString().split(".")
            val subFileName = s.last()

            mission.missionPhotos.add(
                "Mission/${mission.missionId}_${i}.${subFileName}"
            )
        }

        return fs.missionCollection.updateMissionObservable(mission)
    }

}