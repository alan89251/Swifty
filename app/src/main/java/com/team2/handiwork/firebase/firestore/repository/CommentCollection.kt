package com.team2.handiwork.firebase.firestore.repository

import android.util.Log
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.enums.FirebaseCollectionKey
import com.team2.handiwork.models.Comment
import io.reactivex.rxjava3.core.Observable

class CommentCollection {

    var instance = Firebase.firestore

    fun getComments(email: String): Observable<List<Comment>> {
        return Observable.create<List<Comment>> { observer ->
            instance.collection(FirebaseCollectionKey.USERS.displayName).document(email)
                .collection(FirebaseCollectionKey.COMMENTS.displayName)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    error?.let {
                        observer.onError(it)
                    }
                    val comments = snapshot!!.documents.map { doc ->
                        doc.toObject<Comment>()!!
                    }.sortedBy {
                        it.createdAt
                    }
                    observer.onNext(comments)
                }
        }
    }

    fun getCommentsSingleTime(
        email: String,
        onSuccess: (List<Comment>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        instance.collection(FirebaseCollectionKey.USERS.displayName).document(email)
            .collection(FirebaseCollectionKey.COMMENTS.displayName)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { docs ->
                val comments = ArrayList<Comment>()
                for (doc in docs) {
                    comments.add(doc.toObject())
                }
                onSuccess(comments)
            }
            .addOnFailureListener {
                onError(it)
            }
    }

    fun addComment(
        agentEmail: String,
        comment: Comment,
        onSuccess: ((Comment) -> Unit)? = null,
        onError: ((Exception) -> Unit)? = null
    ) {
        instance.collection(FirebaseCollectionKey.USERS.displayName).document(agentEmail)
            .collection(FirebaseCollectionKey.COMMENTS.displayName)
            .add(comment)
            .addOnSuccessListener {
                Log.d("addComment", "DocumentSnapshot added without ID")
                onSuccess?.invoke(comment)
            }
            .addOnFailureListener {
                Log.w("addComment", "Error adding comment", it)
                onError?.invoke(it)
            }
    }
}