package com.team2.handiwork.viewModel.wallet

import androidx.lifecycle.ViewModel
import com.team2.handiwork.firebase.firestore.Firestore
import com.team2.handiwork.models.Transaction
import com.team2.handiwork.models.User
import io.reactivex.rxjava3.core.Observable

class FragmentWalletBalanceViewModel : ViewModel() {
    var fs = Firestore()

    fun getUser(email: String): Observable<User> {
        return fs.userCollection.getUser(email)
    }

    fun getUserTransaction(email: String): Observable<List<Transaction>> {
        return fs.transactionCollection.getUserTransaction(email)
    }
}