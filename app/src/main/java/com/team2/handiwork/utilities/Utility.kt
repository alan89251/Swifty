package com.team2.handiwork.utilities

import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.team2.handiwork.enum.EditorKey
import com.team2.handiwork.models.UserRegistrationForm

class Utility {
    companion object {
        fun isValidEmail(target: CharSequence): Boolean {
            return if (TextUtils.isEmpty(target)) {
                false
            } else {
                android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
            }
        }
    }
}