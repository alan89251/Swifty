package com.team2.handiwork.firebase.firestore.repository

import android.util.Log
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.enums.FirebaseCollectionKey
import com.team2.handiwork.models.Mission
import io.reactivex.rxjava3.core.Observable

class MissionCollection {
    var instance = Firebase.firestore

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
                    if (!tempDoc.enrollments.contains(userEmail)) {
                        missionList.add(tempDoc)
                    }
                }
                callback(missionList)
            }
            .addOnFailureListener {
                Log.d("hehehe", "getPoolMissionByEmail: $it")
            }
    }


}