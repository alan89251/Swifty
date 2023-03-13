package com.team2.handiwork.firebase.firestore.repository

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.enums.FirebaseCollectionKey
import com.team2.handiwork.models.Transaction
import com.team2.handiwork.models.User
import io.reactivex.rxjava3.core.Observable

class UserCollection {
    var instance = Firebase.firestore
    var collection = instance.collection(FirebaseCollectionKey.USERS.displayName)

    fun register(
        collection: String,
        user: User
    ): Observable<Boolean> {

        // remove unselect subServiceType
        user.serviceTypeList = user.serviceTypeList.map { serviceType ->
            serviceType.subServiceTypeList.removeIf { !it.selected }
            serviceType
        }

        return Observable.create<Boolean> { observer ->
            instance
                .collection(collection)
                .document(user.email)
                .set(user)
                .addOnSuccessListener {
                    observer.onNext(true)
                    Log.d("userRegistration", "DocumentSnapshot added with ID ")
                }.addOnFailureListener { e ->
                    observer.onNext(false)
                    Log.w("userRegistration", "Error adding document", e)
                }
        }
    }

    fun getUser(email: String): Observable<User> {
        return Observable.create<User> { observer ->
            instance
                .collection(FirebaseCollectionKey.USERS.displayName)
                .document(email)
                .addSnapshotListener { snapshot, error ->
                    val user: User = snapshot!!.toObject<User>()!!
                    observer.onNext(user)
                    error?.let { observer.onError(it) }
                }
        }
    }

    fun getUserSingleTime(
        email: String,
        onSuccess: (User) -> Unit,
        onError: (Exception) -> Unit
    ) {
        instance
            .collection(FirebaseCollectionKey.USERS.displayName)
            .document(email)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                val user: User = snapshot!!.toObject<User>()!!
                onSuccess(user)
            }
    }

    fun getUsers(emails: List<String>): Observable<List<User>> {
        return Observable.create { observer ->
            val users = mutableListOf<User>()
            instance
                .collection(FirebaseCollectionKey.USERS.displayName)
                .whereIn("email", emails)
                .get()
                .addOnSuccessListener { documents ->
                    for (doc in documents) {
                        val tempDoc = doc.toObject<User>()
                        users.add(tempDoc)
                    }
                    observer.onNext(users)
                }
                .addOnFailureListener {
                    Log.d("getUsers", "Fail: $it")
                }
        }
    }

    fun updateUser(
        user: User,
        onSuccess: ((User) -> Unit)? = null,
        onError: ((Exception) -> Unit)? = null
    ) {
        instance
            .collection(FirebaseCollectionKey.USERS.displayName)
            .document(user.email)
            .set(user)
            .addOnSuccessListener {
                Log.d("updateUser", "updated user successfully ")
                onSuccess?.invoke(user)
            }.addOnFailureListener { e ->
                Log.w("updateUser", "Fail to updated user", e)
                onError?.invoke(e)
            }
    }


    fun updateUserBalance(email: String, balance: Int, transaction: Transaction) {
        val userDoc = instance
            .collection(FirebaseCollectionKey.USERS.displayName)
            .document(email)
        val id = System.currentTimeMillis().toString()
        val transCollect = userDoc
            .collection(FirebaseCollectionKey.TRANSACTIONS.displayName)
            .document(id)
        val batch = instance.batch()
        batch.update(userDoc, hashMapOf<String, Int>("balance" to balance) as Map<String, Any>)
        batch.set(transCollect, transaction.serialize())

        batch.commit()
            .addOnSuccessListener {
                Log.d("updateUserBalance: ", "Success")
            }
            .addOnCanceledListener {
                Log.d("updateUserBalance: ", "Success")
            }
    }


}
