package com.team2.handiwork.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.firebase.Firestore
import com.team2.handiwork.models.User
import io.reactivex.rxjava3.core.Observable

class FragmentRegistrationWorkerTNCViewModel : ViewModel() {
    var isEnableNextBtn: MutableLiveData<Boolean> = MutableLiveData(false)

    fun register(form: User): Observable<Boolean> {
        return Firestore().register("Users", form)
    }
}