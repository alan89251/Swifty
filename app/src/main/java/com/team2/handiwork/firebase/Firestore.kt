package com.team2.handiwork.firebase

import android.util.Log
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.enum.FirebaseCollectionKey
import com.team2.handiwork.models.Enrollment
import com.team2.handiwork.models.Mission
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

    fun addMission(
        collection: String,
        mission: Mission
    ): Observable<Mission> {
        return Observable.create<Mission> { observer ->
            instance
                .collection(collection)
                .add(mission)
                .addOnSuccessListener {
                    mission.missionId = it.id
                    observer.onNext(mission)
                    Log.d("addMission", "DocumentSnapshot added without ID ")
                }.addOnFailureListener { e ->
                    observer.onError(e)
                    Log.w("addMission", "Error adding document", e)
                }
        }
    }


    // todo dont need to return subscription
    fun updateMission(mission: Mission): Observable<Boolean> {
        return Observable.create<Boolean> { observer ->
            instance
                .collection(FirebaseCollectionKey.MISSIONS.displayName)
                .document(mission.missionId)
                .set(mission)
                .addOnSuccessListener {
                    observer.onNext(true)
                    Log.d("updateMission", "updated mission successfully")
                }.addOnFailureListener { e ->
                    observer.onNext(false)
                    Log.w("updateMission", "Fail to updated mission", e)
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

    fun updateUser(user: User): Observable<Boolean> {
        return Observable.create<Boolean> { observer ->
            instance
                .collection(FirebaseCollectionKey.USERS.displayName)
                .document(user.email)
                .set(user)
                .addOnSuccessListener {
                    observer.onNext(true)
                    Log.d("updateUser", "updated user successfully ")
                }.addOnFailureListener { e ->
                    observer.onNext(false)
                    Log.w("updateUser", "Fail to updated user", e)
                }
        }
    }

    fun subscribeMissionByEmail(userEmail: String): Observable<List<Mission>> {
        return Observable.create { observer ->
            instance
                .collection(FirebaseCollectionKey.MISSIONS.displayName)
                .whereEqualTo("employer", userEmail)
                .orderBy("endTime", Query.Direction.ASCENDING)
                .addSnapshotListener { documents, e ->

                    val myMissionList = documents!!.map { document ->
                        val tempDocument = document.toObject<Mission>()
                        tempDocument.missionId = document.id
                        tempDocument
                    }
                    observer.onNext(myMissionList)
                    e?.let { observer.onError(it) }
                }
        }
    }


    fun getPoolMissionByEmail(userEmail: String, callback: (List<Mission>) -> Unit) {
        val missionList = mutableListOf<Mission>()
        instance.collection(FirebaseCollectionKey.MISSIONS.displayName)
            .orderBy("employer", Query.Direction.ASCENDING)
            .whereNotEqualTo("employer", userEmail)
            .orderBy("endTime", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    val tempDoc = doc.toObject<Mission>()
                    tempDoc.missionId = doc.id
                    missionList.add(tempDoc)
                }
                callback(missionList)
            }
            .addOnFailureListener {
                Log.d("hehehe", "getPoolMissionByEmail: $it")
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
                        transaction.transType =
                            transaction.getTransType((it["transType"] as Long).toInt())
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

    fun updateUserBalance(email: String, balance: Int) {
        instance
            .collection(FirebaseCollectionKey.USERS.displayName)
            .document(email)
            .update(hashMapOf<String, Int>("balance" to balance) as Map<String, Any>)
            .addOnSuccessListener {
                Log.d("updateUserBalance: ", "Success")
            }.addOnFailureListener {
                Log.d("updateUserBalance: ", "Fail")
            }
    }

    fun getEnrollmentsByMissionId(missionId: String): Observable<List<Enrollment>> {
        return Observable.create { observer ->
            val enrollments = mutableListOf<Enrollment>()
            instance
                .collection(FirebaseCollectionKey.ENROLLMENTS.displayName)
                .whereEqualTo("missionId", missionId)
                .whereEqualTo("enrolled", true)
                .get()
                .addOnSuccessListener { documents ->
                    for (doc in documents) {
                        val tempDoc = doc.toObject<Enrollment>()
                        tempDoc.enrollmentId = doc.id
                        enrollments.add(tempDoc)
                    }
                    observer.onNext(enrollments)
                }
                .addOnFailureListener {
                    Log.d("getEnrollmentsByMissionId", "Fail: $it")
                }
        }
    }

    fun getSelectedEnrollmentByMissionId(missionId: String): Observable<Enrollment> {
        return Observable.create { observer ->
            instance
                .collection(FirebaseCollectionKey.ENROLLMENTS.displayName)
                .whereEqualTo("missionId", missionId)
                .whereEqualTo("selected", true)
                .get()
                .addOnSuccessListener { documents ->
                    // Should be only 1 result
                    val doc = documents.documents.get(0)
                    val tempDoc = doc.toObject<Enrollment>()
                    tempDoc!!.enrollmentId = doc.id
                    observer.onNext(tempDoc)
                }
                .addOnFailureListener {
                    Log.d("getSelectedEnrollmentByMissionId", "Fail: $it")
                }
        }
    }

    fun updateEnrollment(enrollment: Enrollment): Observable<Boolean> {
        return Observable.create<Boolean> { observer ->
            instance
                .collection(FirebaseCollectionKey.ENROLLMENTS.displayName)
                .document(enrollment.enrollmentId)
                .set(enrollment)
                .addOnSuccessListener {
                    observer.onNext(true)
                    Log.d("updateEnrollment", "updated enrollment successfully ")
                }.addOnFailureListener { e ->
                    observer.onNext(false)
                    Log.w("updateEnrollment", "Fail to update enrollment", e)
                }
        }
    }

    fun addEnrollment(enrollment: Enrollment) {
        instance
            .collection(FirebaseCollectionKey.ENROLLMENTS.displayName)
            .add(enrollment)
            .addOnSuccessListener {
                Log.d("addEnrollment", "add enrollment successfully ")
            }.addOnFailureListener { e ->
                Log.w("addEnrollment", "Fail to add enrollment", e)
            }
    }
}
