package com.team2.handiwork.viewModel

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.R
import com.team2.handiwork.firebase.firestore.repository.CommentCollection
import com.team2.handiwork.firebase.firestore.repository.MissionCollection
import com.team2.handiwork.firebase.firestore.repository.UserCollection
import com.team2.handiwork.models.Comment
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.User
import com.team2.handiwork.singleton.UserData

class FragmentLeaveReviewDialogViewModel : ViewModel() {
    companion object {
        const val DEFAULT_RATING = 3
    }

    private val missionCollection = MissionCollection()
    private val userCollection = UserCollection()

    var user: MutableLiveData<User> = MutableLiveData()
    var isReviewedForEmployer: Boolean = true
    var mission: Mission = Mission()
    var imgURL: String = ""
    var rating: MutableLiveData<Int> = MutableLiveData(DEFAULT_RATING)
    var star1Img: MediatorLiveData<Int> = MediatorLiveData()
    var star2Img: MediatorLiveData<Int> = MediatorLiveData()
    var star3Img: MediatorLiveData<Int> = MediatorLiveData()
    var star4Img: MediatorLiveData<Int> = MediatorLiveData()
    var star5Img: MediatorLiveData<Int> = MediatorLiveData()

    init {
        star1Img.addSource(rating) {
            star1Img.value = if (it >= 1) R.drawable.ic_star else R.drawable.ic_star_grey
        }
        star2Img.addSource(rating) {
            star2Img.value = if (it >= 2) R.drawable.ic_star else R.drawable.ic_star_grey
        }
        star3Img.addSource(rating) {
            star3Img.value = if (it >= 3) R.drawable.ic_star else R.drawable.ic_star_grey
        }
        star4Img.addSource(rating) {
            star4Img.value = if (it >= 4) R.drawable.ic_star else R.drawable.ic_star_grey
        }
        star5Img.addSource(rating) {
            star5Img.value = if (it >= 5) R.drawable.ic_star else R.drawable.ic_star_grey
        }
    }

    fun addComment(commentText: String, onSuccess: (() -> Unit)) {
        val comment = Comment()
        comment.content = commentText
        comment.rating = rating.value!!.toFloat()
        comment.firstname = UserData.currentUserData.firstName
        comment.lastname = UserData.currentUserData.lastName
        comment.missionId = mission.missionId
        comment.missionSubServiceType = mission.subServiceType
        comment.isFromAgent = isReviewedForEmployer
        CommentCollection().addComment(
            user.value!!.email,
            comment,
            {
                updateMissionToReviewed(onSuccess)
            }
        )
    }

    fun getUserFromDB(email: String) {
        userCollection.getUserSingleTime(email, {
            user.value = it
        }, {
            Log.d("Leave Review", "Cannot get user from DB: $it")
            user.value = User()
        })
    }

    private fun updateMissionToReviewed(onSuccess: (() -> Unit)) {
        if (isReviewedForEmployer) {
            mission.isAgentReviewed = true
        } else {
            mission.isReviewed = true
        }

        missionCollection.updateMission(mission, {
            onSuccess.invoke()
        })
    }
}