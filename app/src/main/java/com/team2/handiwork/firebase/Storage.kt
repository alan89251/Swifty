package com.team2.handiwork.firebase

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.rxjava3.core.Observable

class Storage {
    private var root = FirebaseStorage.getInstance().reference

    fun uploadImg(bucket: String, name: String, uri: Uri): Observable<Boolean> {
        return Observable.create<Boolean> { observer ->
            root.child("$bucket/$name").putFile(uri).addOnSuccessListener {
                    observer.onNext(true)
                    Log.d("Firebase Storage uploadImg", "success")
                }.addOnFailureListener {
                    observer.onNext(false)
                    Log.e("Firebase Storage uploadImg", "fail")
                }
        }
    }
}