package com.team2.handiwork.firebase

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.enum.FirebaseCollectionKey
import com.team2.handiwork.models.Transaction
import com.team2.handiwork.models.User
import io.reactivex.rxjava3.core.Observable

class Firestore {

    var instance = Firebase.firestore

    fun register(
        collection: String,
        user: User
    ): Observable<Boolean> {
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

    fun getUserTransaction(email: String): Observable<List<Transaction>> {
        return Observable.create<List<Transaction>> { observer ->
            instance
                .collection(FirebaseCollectionKey.USERS.displayName)
                .document(email)
                .collection(FirebaseCollectionKey.TRANSACTIONS.displayName)
                .addSnapshotListener { snapshot, error ->
                    val transactionList: List<Transaction> = snapshot!!.map {
                        val transaction = Transaction()
                        transaction.amount = (it["amount"] as Long).toInt()
                        transaction.missionId = it["missionId"] as String
                        transaction.title = it["title"] as String
                        transaction.firstName = it["firstName"] as String
                        transaction.lastName = it["lastName"] as String
                        transaction.updatedAt =
                            (it["updatedAt"] as com.google.firebase.Timestamp).seconds
                        transaction.createdAt =
                            (it["createdAt"] as com.google.firebase.Timestamp).seconds
                        transaction
                    }
                    observer.onNext(transactionList)
                    error?.let { observer.onError(it) }
                }
        }
    }

//    fun addOrder(order: Order) {
//        instance.collection(collectionKey).document(order.orderId).set(order).addOnSuccessListener {
//            Log.d("Order FB", "DocumentSnapshot added with ID ")
//        }.addOnFailureListener { e ->
//            Log.w("Order FB", "Error adding document", e)
//        }
//    }
//
//    fun updateOrder(orderId: String, data: Map<String, Any>) {
//        instance.collection(collectionKey).document(orderId).update(data).addOnSuccessListener {
//
//        }.addOnFailureListener {
//
//        }
//    }
//    fun getOrdersByCustId(custId: Int): Observable<List<Order>> {
//        return Observable.create { observer ->
//            instance.collection(collectionKey).whereEqualTo("custId", custId)
//                .get()
//                .addOnSuccessListener { result ->
//                    val orders: List<Order> = result.toObjects()
//                    observer.onNext(orders)
//                }
//        }
//    }
//
//    fun getOrders(): Observable<List<Order>> {
//        return Observable.create { observer ->
//            instance.collection(collectionKey).addSnapshotListener { snapshot, error ->
//                val orders: List<Order> = snapshot!!.documents.map {
//                    it.toObject<Order>()!!
//                }
//                observer.onNext(orders)
//                error?.let { observer.onError(it) }
//            }
//        }
//    }
}
