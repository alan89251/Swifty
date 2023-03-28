package com.team2.handiwork.firebase

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
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