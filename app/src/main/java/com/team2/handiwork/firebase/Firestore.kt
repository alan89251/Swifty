package com.team2.handiwork.firebase

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.models.UserRegistrationForm
import io.reactivex.rxjava3.core.Observable

class Order {
    val orderId: String = ""
}

class Firestore() {

    var instance = Firebase.firestore

    fun register(collection: String, userRegistrationForm: UserRegistrationForm){
        instance
            .collection(collection)
            .document(userRegistrationForm.email)
            .set(userRegistrationForm)
            .addOnSuccessListener {
                Log.d("userRegistration", "DocumentSnapshot added with ID ")
            }.addOnFailureListener { e ->
                Log.w("userRegistration", "Error adding document", e)
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
//
//    fun getOrder(custId: String): Observable<Order> {
//        return Observable.create { observer ->
//            instance.collection(collectionKey).document(custId)
//                .addSnapshotListener { snapshot, error ->
//                    val order: Order = snapshot!!.toObject<Order>()!!
//                    observer.onNext(order)
//                    error?.let { observer.onError(it) }
//                }
//        }
//    }
//
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
