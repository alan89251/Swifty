package com.team2.handiwork.firebase

import android.util.Log
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.enums.FirebaseCollectionKey
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

    fun subscribeEnrolledMissionByEmail(userEmail: String): Observable<List<Enrollment>> {
        return Observable.create { observer ->
            instance
                .collection(FirebaseCollectionKey.ENROLLMENTS.displayName)
                .whereEqualTo("agent", userEmail)
                .addSnapshotListener { documents, e ->
                    val enrollmentList = documents!!.map { document ->
                        val tempDoc = document.toObject<Enrollment>()
                        Log.d("hehehe", "subscribeEnrolledMissionByEmail: ${tempDoc.missionId}")
                        tempDoc
                    }
                    observer.onNext(enrollmentList)
                    e?.let { observer.onError(it) }
                }
        }
    }

    fun getMissionByMissionId(missionIdList: List<String>, callback: (List<Mission>) -> Unit) {
        val missionDocRef = instance.collection(FirebaseCollectionKey.MISSIONS.displayName)
        missionDocRef.whereIn(FieldPath.documentId(), missionIdList)
            .addSnapshotListener { documents, error ->
                documents!!.let {
                    val missionList = mutableListOf<Mission>()
                    for (doc in documents) {
                        val mission = doc.toObject<Mission>()
                        mission.missionId = doc.id
                        missionList.add(mission)
                    }
                    callback(missionList)
                }
            }
    }


    fun getPoolMissionByEmail(userEmail: String, callback: (List<Mission>) -> Unit) {
        val missionList = mutableListOf<Mission>()
        instance.collection(FirebaseCollectionKey.MISSIONS.displayName)
            .orderBy("employer", Query.Direction.ASCENDING)
            .whereNotEqualTo("employer", userEmail)
            .whereEqualTo("status", 0)
            .orderBy("endTime", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    val tempDoc = doc.toObject<Mission>()
                    tempDoc.missionId = doc.id
                    if (!tempDoc.enrollments.contains(userEmail)){
                        missionList.add(tempDoc)
                    }
                }
                callback(missionList)
            }
            .addOnFailureListener {
                Log.d("hehehe", "getPoolMissionByEmail: $it")
            }
    }

    fun getUserTransaction(email: String): Observable<List<Transaction>> {
        instance.clearPersistence()
        return Observable.create<List<Transaction>> { observer ->
            instance
                .collection(FirebaseCollectionKey.USERS.displayName)
                .document(email)
                .collection(FirebaseCollectionKey.TRANSACTIONS.displayName)
                .addSnapshotListener { snapshot, error ->
                    val transactionList: List<Transaction> = snapshot!!.map {
                        Transaction.toObject(it.data)
                    }
                    observer.onNext(transactionList)
                    error?.let { observer.onError(it) }
                }
        }
    }

    fun updateUserBalance(email: String, balance: Int, transaction: Transaction) {
        val userDoc = instance
            .collection(FirebaseCollectionKey.USERS.displayName)
            .document(email)
        val transCollect =
            userDoc.collection(FirebaseCollectionKey.TRANSACTIONS.displayName).document()
        val batch = instance.batch()
        batch.update(userDoc, hashMapOf<String, Int>("balance" to balance) as Map<String, Any>)
        batch.set(transCollect, transaction.toHashMap())

        batch.commit()
            .addOnSuccessListener {
                Log.d("updateUserBalance: ", "Success")
            }
            .addOnCanceledListener {
                Log.d("updateUserBalance: ", "Success")
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
                    observer.onError(it)
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
                    if (documents.documents.isEmpty()) {
                        Log.d(
                            "Firestore.getSelectedEnrollmentByMissionId",
                            "Cannot find the selected enrollment"
                        )
                        observer.onNext(Enrollment())
                        return@addOnSuccessListener
                    }

                    // Should be only 1 result
                    val doc = documents.documents[0]
                    val tempDoc = doc.toObject<Enrollment>()
                    tempDoc!!.enrollmentId = doc.id
                    observer.onNext(tempDoc)
                }
                .addOnFailureListener {
                    Log.d("getSelectedEnrollmentByMissionId", "Fail: $it")
                    observer.onError(it)
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
