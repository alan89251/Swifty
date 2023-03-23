package com.team2.handiwork.utilities

import com.google.gson.Gson
import com.team2.handiwork.models.ChatUser
import com.team2.handiwork.models.User
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

class Ext {
    companion object {

        fun Disposable.disposedBy(disposeBag: CompositeDisposable) {
            disposeBag.add(this)
        }

        fun User.toChatUser(): ChatUser {
            val gson = Gson()
            val userJson = gson.toJson(this)
            val chatUser = gson.fromJson(userJson, ChatUser::class.java)
            chatUser.name = "${this.firstName} ${this.lastName}"
            return chatUser
        }
    }
}