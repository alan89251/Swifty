package com.team2.handiwork.firebase.firestore.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.team2.handiwork.enums.FirebaseCollectionKey
import com.team2.handiwork.models.Certification
import com.team2.handiwork.models.Comment
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.math.log

class CertificationCollection {
    var instance = Firebase.firestore
    var collection = instance.collection(FirebaseCollectionKey.CERTIFICATIONS.displayName)

    suspend fun uploadCert(uri: Uri, name: String): String? {
        return withContext(Dispatchers.IO) {
            val storageRef = FirebaseStorage.getInstance().getReference("Certifications/$name")
            try {
                val uploadTask = storageRef.putFile(uri)
                uploadTask.await()
                val downloadUrlTask = storageRef.downloadUrl.await()
                downloadUrlTask.toString()
            } catch (e: Exception) {
                null
            }
        }
    }


    suspend fun deleteCertification(email: String, certification: Certification) {
        try {
            instance.collection(FirebaseCollectionKey.USERS.displayName).document(email)
                .collection(FirebaseCollectionKey.CERTIFICATIONS.displayName)
                .document("$email-${certification.createdAt}")
                .delete().await()

            val storageRef = FirebaseStorage.getInstance().getReference("Certifications/$email-${certification.name}")
            storageRef.delete().await()

        } catch (e: Exception) {
            Log.d("hehehe", "deleteCertification: ${e.message}")
        }
    }

    suspend fun addCertification(agentEmail: String, certification: Certification) {
        try {
            instance.collection(FirebaseCollectionKey.USERS.displayName).document(agentEmail)
                .collection(FirebaseCollectionKey.CERTIFICATIONS.displayName)
                .document("$agentEmail-${certification.createdAt}")
                .set(certification).await()
        } catch (e: Exception) {
            Log.d("hehehe", "getUserCertifications: ${e.message}")
        }
    }


    suspend fun getUserCertifications(email: String): List<Certification> {
        try {
            val query = instance.collection(FirebaseCollectionKey.USERS.displayName).document(email)
                .collection(FirebaseCollectionKey.CERTIFICATIONS.displayName)
                .get().await()

            val certs = ArrayList<Certification>()
            for (doc in query.documents) {
                certs.add(doc.toObject()!!)
            }
            return certs
        } catch (e: Exception) {
            Log.d("hehehe", "getUserCertifications: ${e.message}")
        }

        return ArrayList()
    }
}