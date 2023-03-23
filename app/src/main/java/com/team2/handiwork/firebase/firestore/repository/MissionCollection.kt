package com.team2.handiwork.firebase.firestore.repository

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
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
    var collection = instance.collection(FirebaseCollectionKey.MISSIONS.displayName)

    fun addMission(
        collection: String,
        mission: Mission
    ): Observable<Mission> {
        return Observable.create<Mission> { observer ->
            instance
                .collection(collection)
                .add(mission.serialize())
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
    fun updateMissionObservable(mission: Mission): Observable<Boolean> {
        return Observable.create<Boolean> { observer ->
            instance
                .collection(FirebaseCollectionKey.MISSIONS.displayName)
                .document(mission.missionId)
                .set(mission.serialize()) // todo createdAt overwrite
                .addOnSuccessListener {
                    observer.onNext(true)
                    Log.d("updateMission", "updated mission successfully")
                }.addOnFailureListener { e ->
                    observer.onNext(false)
                    Log.w("updateMission", "Fail to updated mission", e)
                }
        }
    }

    fun updateMission(
        mission: Mission,
        onSuccess: ((Mission) -> Unit)? = null,
        onError: ((Exception) -> Unit)? = null
    ) {
        mission.updatedAt = System.currentTimeMillis()
        instance
            .collection(FirebaseCollectionKey.MISSIONS.displayName)
            .document(mission.missionId)
            .set(mission.serialize()) // todo createdAt overwrite
            .addOnSuccessListener {
                Log.d("updateMission", "updated mission successfully")
                onSuccess?.invoke(mission)
            }.addOnFailureListener { e ->
                Log.w("updateMission", "Fail to updated mission", e)
                onError?.invoke(e)
            }
    }

    fun subscribeEnrolledMissionByEmail(userEmail: String): Observable<List<Mission>> {
        return Observable.create { observer ->
            instance.collection(FirebaseCollectionKey.MISSIONS.displayName)
                .whereArrayContains("enrollments", userEmail)
                .orderBy("endTime", Query.Direction.ASCENDING).addSnapshotListener { documents, e ->
                    e?.let { observer.onError(it) }

                    documents?.let {
                        val myMissionList = documents.map { document ->
                            val tempDocument = Mission.deserialize(document.data)
                            tempDocument.missionId = document.id
                            tempDocument
                        }
                        observer.onNext(myMissionList)
                    }
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
                        val tempDocument = Mission.deserialize(document.data)
                        tempDocument.missionId = document.id
                        tempDocument
                    }
                    observer.onNext(myMissionList)
                    e?.let { observer.onError(it) }
                }
        }
    }


    fun getMissionsByMissionId(missionIdList: List<String>, callback: (List<Mission>) -> Unit) {
        val missionDocRef = instance.collection(FirebaseCollectionKey.MISSIONS.displayName)
        missionDocRef.whereIn(FieldPath.documentId(), missionIdList)
            .addSnapshotListener { documents, error ->
                documents!!.let {
                    val missionList = mutableListOf<Mission>()
                    for (doc in documents) {
                        val mission = Mission.deserialize(doc.data)
                        mission.missionId = doc.id
                        missionList.add(mission)
                    }
                    callback(missionList)
                }
            }
    }

    fun getMissionById(missionId: String): Observable<Mission> {
        return Observable.create<Mission> { observer ->
            collection
                .document(missionId)
                .get()
                .addOnSuccessListener {
                    val mission = Mission.deserialize(it.data!!)
                    mission.missionId = it.id
                    observer.onNext(mission)
                }
                .addOnFailureListener {

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
                    val tempDoc = Mission.deserialize(doc.data)
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