package com.team2.handiwork.firebase.firestore.repository

import android.util.Log
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.enums.FirebaseCollectionKey
import com.team2.handiwork.models.Transaction
import io.reactivex.rxjava3.core.Observable

class TransactionCollection {
    var instance = Firebase.firestore
    var collection = instance.collection(FirebaseCollectionKey.TRANSACTIONS.displayName)


    fun getUserTransaction(email: String): Observable<List<Transaction>> {
        instance.clearPersistence()
        return Observable.create<List<Transaction>> { observer ->
            instance
                .collection(FirebaseCollectionKey.USERS.displayName)
                .document(email)
                .collection(FirebaseCollectionKey.TRANSACTIONS.displayName)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(2)
                .addSnapshotListener { snapshot, error ->
                    error?.let {
                        Log.d("getUserTransaction : ", error.message.toString())
                        observer.onError(it)
                    }

                    val transactionList: List<Transaction> = snapshot!!.map {
                        Transaction.toObject(it.data)
                    }
                    observer.onNext(transactionList)
                }
        }
    }
}