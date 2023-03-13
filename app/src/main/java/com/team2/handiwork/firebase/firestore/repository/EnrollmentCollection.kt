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
}