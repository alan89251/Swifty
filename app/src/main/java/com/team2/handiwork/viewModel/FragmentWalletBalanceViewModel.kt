package com.team2.handiwork.viewModel

import androidx.lifecycle.ViewModel
import com.team2.handiwork.firebase.Firestore
import com.team2.handiwork.models.Transaction
import com.team2.handiwork.models.User
import io.reactivex.rxjava3.core.Observable

class FragmentWalletBalanceViewModel : ViewModel() {

    fun getUser(email: String): Observable<User> {
        return Firestore().getUser(email)
    }

    fun getUserTransaction(email: String): Observable<List<Transaction>> {
        return Firestore().getUserTransaction(email)
    }
}