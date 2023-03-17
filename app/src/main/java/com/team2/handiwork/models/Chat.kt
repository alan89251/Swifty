package com.team2.handiwork.models

import android.view.View
import androidx.core.text.HtmlCompat
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.Exclude

class Chat(var name: String = "", var icon: String = "") {
    @get:Exclude
    var missionId: String = ""
    var uid: String = ""
    var missionName: String = ""
        set(value) {
            field =
                HtmlCompat.fromHtml("<u>${value}</u>", HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
        }
    var isRead: Boolean = false
        set(value) {
            if (value) {
                isReadVisibility.value = View.GONE
            } else {
                isReadVisibility.value = View.VISIBLE
            }
            field = value
        }
    val createdAt: Long = System.currentTimeMillis()

    var isReadVisibility = MutableLiveData<Int>(View.GONE)
}