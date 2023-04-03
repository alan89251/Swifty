package com.team2.handiwork.viewModel.profile

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.team2.handiwork.base.fragment.DisposeFragment
import com.team2.handiwork.base.viewModel.BaseMissionViewModel
import com.team2.handiwork.firebase.firestore.repository.CertificationCollection
import com.team2.handiwork.firebase.firestore.repository.CommentCollection
import com.team2.handiwork.firebase.firestore.repository.MissionCollection
import com.team2.handiwork.firebase.firestore.repository.UserCollection
import com.team2.handiwork.models.Certification
import com.team2.handiwork.models.Comment
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.User
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.launch

open class FragmentBaseProfileViewModel : BaseMissionViewModel() {
    var commentRepo = CommentCollection()
    var userRepo = UserCollection()
    var missionRepo = MissionCollection()
    var certRepo = CertificationCollection()

    var user = MutableLiveData<User>()
    var missions = MutableLiveData<List<Mission>>()
    var comments = MutableLiveData<List<Comment>>()
    var certifications = MutableLiveData<List<Certification>>()

    var categories: MutableLiveData<String> = MutableLiveData<String>("")
    var typeList: MutableLiveData<List<String>> = MutableLiveData<List<String>>()

    var cancelRate = MutableLiveData<String>()
    var rating = MutableLiveData<Float>()
    var ratingNumber = MutableLiveData<Int>()

    var showAddCertification = MutableLiveData<Int>()
    var showCertification = MutableLiveData<Int>()


    fun getComments(email: String): Observable<List<Comment>> {
        return commentRepo.getComments(email)
    }

    fun getUser(email: String): Observable<User> {
        return userRepo.getUser(email)
    }

    fun getMissions(email: String): Observable<List<Mission>> {
        return missionRepo.subscribeMissionByEmail(email)
    }

    fun getCertifications(email: String) {
        viewModelScope.launch {
            certifications.value = certRepo.getUserCertifications(email)
        }
    }

    fun calculateCancellationRate(user: User): String {
        user.let {
            val total = user.confirmedCancellationCount + user.finishMissionCount
            return if (total == 0) {
                "0%"
            } else {
                val rate = (user.confirmedCancellationCount.toDouble() / total) * 100
                if (rate == rate.toInt().toDouble()) {
                    "%.0f%%".format(rate)
                } else {
                    "%.1f%%".format(rate)
                }
            }
        }
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
}