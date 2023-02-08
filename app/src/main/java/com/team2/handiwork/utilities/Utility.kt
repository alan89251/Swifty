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

        fun getUserRegistrationForm(sp: SharedPreferences): UserRegistrationForm {
            val json = sp.getString(EditorKey.USER_FORM.toString(), "")
            return if (json == "") {
                Log.e("getUserRegistrationForm: ", "form does not exist")
                UserRegistrationForm()
            } else {
                Gson().fromJson(json, UserRegistrationForm::class.java)
            }
        }

        fun updateUserRegistrationForm(sp: SharedPreferences, form: UserRegistrationForm) {
            val editor = sp.edit()
            val json: String = Gson().toJson(form)
            editor.putString(EditorKey.USER_FORM.toString(), json)
            editor.apply()
        }
    }
}