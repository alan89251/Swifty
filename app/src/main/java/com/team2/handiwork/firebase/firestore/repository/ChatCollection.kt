package com.team2.handiwork.firebase.firestore.repository

import android.util.Log
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.enums.FirebaseCollectionKey
import com.team2.handiwork.models.ChatInfo
import com.team2.handiwork.models.ChatMessage
import io.reactivex.rxjava3.core.Observable

class ChatCollection {
    var instance = Firebase.firestore
    var collection = instance.collection(FirebaseCollectionKey.CHATS.displayName)

    fun addMessage(
        agentEmail: String, missionId: String, message: ChatMessage, chatInfo: ChatInfo
    ) {

        val batch = instance.batch()
        val chatDoc = collection.document(missionId).collection(agentEmail).document()
        batch.set(chatDoc, message)

        chatInfo.let {
            val infoDoc = collection.document(missionId)
            batch.set(infoDoc, it, SetOptions.merge())
        }

        batch.commit()
    }

    fun fetchMessage(agentEmail: String, missionId: String): Observable<ArrayList<ChatMessage>> {
        return Observable.create<ArrayList<ChatMessage>> { observer ->
            collection.document(missionId).collection(agentEmail)
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

    fun fetchChatInfoByEmployer(employEmail: String): Observable<List<ChatInfo>> {
        return Observable.create { observer ->
            collection
                .whereEqualTo("employer", employEmail)
                .addSnapshotListener { snapshot, error ->
                    val list = snapshot!!.documents.map {
                        val chat = it.toObject<ChatInfo>()!!
                        chat.missionId = it.id.toString()
                        chat
                    }
                    observer.onNext(list)

                }
        }
    }

    fun fetchChatInfoByAgent(agentUID: String, agentEmail: String): Observable<List<ChatInfo>> {
        return Observable.create { observer ->
            collection
                .whereEqualTo("users.${agentUID}.email", agentEmail)
                .addSnapshotListener { snapshot, error ->
                    val list = snapshot!!.documents.map {
                        val chat = it.toObject<ChatInfo>()!!
                        chat.missionId = it.id.toString()
                        chat
                    }
                    observer.onNext(list)

                }
        }
    }

    fun updateChatIsReadByEmployer(missionId: String, agentUID: String) {
        collection
            .document(missionId)
            .update("users.${agentUID}.employerIsRead", true)
            .addOnSuccessListener {
                Log.d("Success to update Chat as read", "")
            }
            .addOnFailureListener {
                Log.d("Fail to update Chat as read", it.message.toString())
            }
    }

    fun updateChatIsReadByAgent(missionId: String, agentUID: String) {
        collection
            .document(missionId)
            .update("users.${agentUID}.agentIsRead", true)
            .addOnSuccessListener {
                Log.d("Success to update Chat as read", "")
            }
            .addOnFailureListener {
                Log.d("Fail to update Chat as read", it.message.toString())
            }
    }


}