package com.team2.handiwork.firebase.firestore.repository

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.enums.FirebaseCollectionKey
import com.team2.handiwork.models.ChatMessage
import io.reactivex.rxjava3.core.Observable

class ChatCollection {
    var instance = Firebase.firestore
    var collection = instance.collection(FirebaseCollectionKey.CHATS.displayName)

    fun addMessage(
        agentEmail: String, missionId: String, message: ChatMessage
    ) {
        collection
            .document(missionId)
            .collection(agentEmail)
            .document()
            .set(message)
    }

    fun fetchMessage(agentEmail: String, missionId: String): Observable<ArrayList<ChatMessage>> {
        return Observable.create<ArrayList<ChatMessage>> { observer ->
            collection
                .document(missionId)
                .collection(agentEmail)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshots, error ->
                    error?.let {
                        observer.onError(error)
                    }
                    val list = snapshots!!.map {
                        it.toObject<ChatMessage>()
                    }
                    observer.onNext(ArrayList(list))
                }
        }
    }

    fun addMessages(
        agentEmail: String, missionId: String, messages: List<ChatMessage>
    ) {
        val batch = instance.batch()
        messages.forEach {
            val doc = collection
                .document(missionId)
                .collection(agentEmail)
                .document()

            batch.set(doc, it)
        }
        batch.commit()
    }


}