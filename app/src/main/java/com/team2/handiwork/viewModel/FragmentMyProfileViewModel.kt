package com.team2.handiwork.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.firebase.firestore.Firestore
import com.team2.handiwork.models.Comment
import com.team2.handiwork.models.User
import com.team2.handiwork.singleton.UserData
import io.reactivex.rxjava3.core.Observable

class FragmentMyProfileViewModel : ViewModel() {

    var fs = Firestore()
    var userData = MutableLiveData<User>()
    var categories: MutableLiveData<String> = MutableLiveData<String>("")

    fun getComments(homeViewModel: ActivityHomeViewModel): Observable<List<Comment>> {
        return fs.commentCollection.getComments(homeViewModel.currentUser.value!!.email)
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
}