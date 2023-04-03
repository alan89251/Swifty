package com.team2.handiwork.firebase

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Tasks
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.rxjava3.core.Observable

class Storage {
    private var root = FirebaseStorage.getInstance().reference

    fun uploadImg(bucket: String, path: String, uri: Uri): Observable<Boolean> {
        return Observable.create<Boolean> { observer ->
            root.child("$bucket/$path").putFile(uri).addOnSuccessListener {
                observer.onNext(true)
                Log.d("Firebase Storage uploadImg", "success")
            }.addOnFailureListener {
                observer.onNext(false)
                Log.e("Firebase Storage uploadImg", "fail")
            }
        }
    }

    fun uploadImg(bucket: String, path: String, uri: Uri, result: MutableLiveData<Boolean>) {
        root.child("$bucket/$path")
            .putFile(uri)
            .addOnSuccessListener {
                result.value = true
                Log.d("Firebase Storage uploadImg", "success")
            }.addOnFailureListener {
                result.value = false
                Log.e("Firebase Storage uploadImg", "fail")
            }
    }

    fun uploadImgSync(bucket: String, path: String, uri: Uri): Boolean {
        val task = root.child("$bucket/$path")
            .putFile(uri)
        val result = Tasks.await(task)
        if (result.error != null) {
            Log.e("Firebase Storage uploadImg", "fail")
            return false
        }
        else {
            Log.d("Firebase Storage uploadImg", "success")
            return true
        }
    }

    fun getImgUrl(imageString: String, callback: (String) -> Unit, errorCallback: () -> Unit) {
        root.child("$imageString").downloadUrl
            .addOnSuccessListener { uri ->
                callback(uri.toString())
            }
            .addOnFailureListener {
                errorCallback()
            }
    }
}