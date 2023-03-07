package com.team2.handiwork.firebase.firestore.repository

import android.util.Log
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.enums.FirebaseCollectionKey
import com.team2.handiwork.models.Enrollment
import com.team2.handiwork.models.Mission
import io.reactivex.rxjava3.core.Observable

class EnrollmentCollection {
    var instance = Firebase.firestore


    fun subscribeEnrolledMissionByEmail(userEmail: String): Observable<List<Mission>> {
        return Observable.create { observer ->
            instance.collection(FirebaseCollectionKey.MISSIONS.displayName)
                .whereArrayContains("enrollments", userEmail)
                .orderBy("endTime", Query.Direction.ASCENDING).addSnapshotListener { documents, e ->
                    e?.let { observer.onError(it) }

                    documents?.let {
                        val myMissionList = documents.map { document ->
                            val tempDocument = document.toObject<Mission>()
                            tempDocument.missionId = document.id
                            tempDocument
                        }
                        observer.onNext(myMissionList)
                    }
                }
        }
    }


    fun getEnrollmentsByMissionId(missionId: String): Observable<List<Enrollment>> {
        return Observable.create { observer ->
            val enrollments = mutableListOf<Enrollment>()
            instance
                .collection(FirebaseCollectionKey.ENROLLMENTS.displayName)
                .whereEqualTo("missionId", missionId)
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
                .document("${enrollment.missionId}-${enrollment.agent}")
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
            .document("${enrollment.missionId}-${enrollment.agent}")
            .set(enrollment)
            .addOnSuccessListener {
                Log.d("addEnrollment", "add enrollment successfully ")
            }.addOnFailureListener { e ->
                Log.w("addEnrollment", "Fail to add enrollment", e)
            }
    }

    fun deleteEnrollment(missionId: String, email: String) {
        instance
            .collection(FirebaseCollectionKey.ENROLLMENTS.displayName)
            .document("$missionId-$email")
            .delete()
            .addOnSuccessListener {
                Log.d("deleteEnrollment", "add enrollment successfully ")
            }.addOnFailureListener { e ->
                Log.w("deleteEnrollment", "Fail to add enrollment", e)
            }
    }
}