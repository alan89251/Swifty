package com.team2.handiwork.viewModel.mission

import android.net.Uri
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.team2.handiwork.adapter.ImageRecyclerViewAdapter
import com.team2.handiwork.base.viewModel.BaseMissionViewModel
import com.team2.handiwork.enums.MissionStatusEnum
import com.team2.handiwork.firebase.Storage
import com.team2.handiwork.firebase.firestore.Firestore
import com.team2.handiwork.firebase.firestore.service.MissionService
import com.team2.handiwork.models.Comment
import com.team2.handiwork.models.Mission
import com.team2.handiwork.utilities.Utility
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentAgentMissionDetailsViewModel : BaseMissionViewModel() {
    val fs = Firestore()
    var mission = MutableLiveData<Mission>()
    val enrolled = MutableLiveData<Boolean>(false)
    val withdrawBefore48Hours = MutableLiveData<Boolean>(false)
    val withdrawWithin48Hours = MutableLiveData<Boolean>(false)
    val revoke = MutableLiveData<Boolean>(false)
    val finished = MutableLiveData<Boolean>(false)
    val email = MutableLiveData<String>("")
    val period = MutableLiveData<String>("")
    val rating = MutableLiveData<Float>(0.0F)
    val targetImgUrl = MutableLiveData<String>()
    var missionStatusDisplay = MutableLiveData<MissionStatusEnum>(MissionStatusEnum.COMPLETED)
    var btnSelectResultPhotoOnClick: (() -> Unit)? = null
    val imageUriList = MutableLiveData<ArrayList<Uri>>()
    val missionPhotosRvAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
        get() = ImageRecyclerViewAdapter(mission.value!!.missionPhotos)
    val resultPhotoRvAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
        get() = ImageRecyclerViewAdapter(mission.value!!.resultPhotos)

    var cancelledButtonVisibility = MutableLiveData<Int>(View.GONE)
    var enrolledButtonVisibility = MutableLiveData<Int>(View.GONE)
    var finishedButtonVisibility = MutableLiveData<Int>(View.GONE)
    var revokeButtonVisibility = MutableLiveData<Int>(View.GONE)
    var leaveReviewButtonVisibility = MutableLiveData<Int>(View.GONE)

    var disputedReasonsVisibility = MutableLiveData<Int>(View.GONE)

    // firebase
    val service = MissionService(
        fs.userCollection,
        fs.missionCollection,
        fs.transactionCollection,
    )

    fun updateButtonVisibility() {
        val status = mission.value!!.status
        missionStatusDisplay.value = status

        // reset
        revokeButtonVisibility.value = View.GONE
        finishedButtonVisibility.value = View.GONE
        enrolledButtonVisibility.value = View.GONE
        cancelledButtonVisibility.value = View.GONE
        leaveReviewButtonVisibility.value = View.GONE

        when (status) {
            MissionStatusEnum.CONFIRMED -> {
                cancelledButtonVisibility.value = View.VISIBLE
                if (mission.value!!.startTime > System.currentTimeMillis()) return
                finishedButtonVisibility.value = View.VISIBLE
            }

            MissionStatusEnum.OPEN -> {
                if (isEnrolled()) {
                    revokeButtonVisibility.value = View.VISIBLE
                    missionStatusDisplay.value = MissionStatusEnum.ENROLLED
                } else {
                    enrolledButtonVisibility.value = View.VISIBLE
                    missionStatusDisplay.value = MissionStatusEnum.OPEN
                }
            }

            MissionStatusEnum.COMPLETED -> {
                leaveReviewButtonVisibility.value =
                    if (mission.value!!.isAgentReviewed) View.GONE else View.VISIBLE
            }

            else -> {
                cancelledButtonVisibility.value = View.GONE
                enrolledButtonVisibility.value = View.GONE
                finishedButtonVisibility.value = View.GONE
                revokeButtonVisibility.value = View.GONE
                leaveReviewButtonVisibility.value = View.GONE
            }
        }
    }

    fun setTargetImgURL(value: String) {
        targetImgUrl.value = value
    }

    fun updatePeriod() {
        val startDate = Utility.convertLongToDate(mission.value!!.startTime)
        val startTime = Utility.convertLongToHour(mission.value!!.startTime)
        val endDate = Utility.convertLongToDate(mission.value!!.endTime)
        val endTime = Utility.convertLongToHour(mission.value!!.endTime)
        period.value = "$startDate $startTime - $endDate $endTime"
    }

    fun isEnrolled(): Boolean {
        return mission.value!!.enrollments.contains(email.value.toString())
    }


    fun getComments(email: String): Observable<List<Comment>> {
        return fs.commentCollection.getComments(email)
    }


    fun calculateRating(comments: List<Comment>): Float {
        if (comments.isEmpty()) {
            return 0F
        }
        var ratingSum: Double = 0.0
        comments.forEach {
            ratingSum += it.rating
        }
        return (ratingSum / comments.size).toFloat()
    }

    val btnSelectResultPhotoOnClickListener = View.OnClickListener { btnSelectResultPhotoOnClick?.invoke() }

    fun uploadResultPhotos(
        onResult: (List<String>) -> Unit // arg: paths of uploaded images
    ) {
        val uploadedImagesPaths = ArrayList<String>()
        CoroutineScope(Dispatchers.IO).launch {
            var photoSerialNo = 1
            imageUriList.value?.forEach {
                val subFileName = it.toString().split(".").last()
                // file name format:
                // <mission_id>_result_<photo_serial_no>.<sub_file_name>
                val fileName = "${mission.value!!.missionId}_result_${photoSerialNo}.${subFileName}"
                val path = "Mission/$fileName"
                uploadedImagesPaths.add(path)
                Storage().uploadImgSync(
                    "Mission",
                    fileName,
                    it)
                photoSerialNo += 1
            }

            withContext(Dispatchers.Main) {
                onResult(uploadedImagesPaths)
            }
        }
    }

    val layoutMissionResultVisibility: Int
        get() {
            return when (mission.value!!.status) {
                MissionStatusEnum.PENDING_ACCEPTANCE,
                MissionStatusEnum.COMPLETED,
                MissionStatusEnum.DISPUTED -> View.VISIBLE
                else -> View.INVISIBLE
            }
        }
}