package com.team2.handiwork.firebase.firestore.collection

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.enums.FirebaseCollectionKey
import com.team2.handiwork.models.Transaction
import io.reactivex.rxjava3.core.Observable

class Transaction {
    var instance = Firebase.firestore


    fun getUserTransaction(email: String): Observable<List<Transaction>> {
        instance.clearPersistence()
        return Observable.create<List<Transaction>> { observer ->
            instance
                .collection(FirebaseCollectionKey.USERS.displayName)
                .document(email)
                .collection(FirebaseCollectionKey.TRANSACTIONS.displayName)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    val transactionList: List<Transaction> = snapshot!!.map {
                        Transaction.toObject(it.data)
                    }
                    observer.onNext(transactionList)
                    error?.let { observer.onError(it) }
                }
        }
    }
}