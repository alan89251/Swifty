package com.team2.handiwork

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.team2.handiwork.enum.EditorKey
import com.team2.handiwork.enum.SharePreferenceKey
import com.team2.handiwork.models.UserRegistrationForm

class UserProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
    }

    fun getUserRegistrationForm(): UserRegistrationForm {
        val sp = this.getSharedPreferences(
            SharePreferenceKey.USER_FORM.toString(),
            Context.MODE_PRIVATE,
        )
        val json = sp.getString(EditorKey.USER_FORM.toString(), "")
        return if (json == "") {
            Log.e("getUserRegistrationForm: ", "form does not exist")
            UserRegistrationForm()
        } else {
            Gson().fromJson(json, UserRegistrationForm::class.java)
        }
    }

    fun updateUserRegistrationForm(form: UserRegistrationForm) {
        val sp = this.getSharedPreferences(
            SharePreferenceKey.USER_FORM.toString(),
            Context.MODE_PRIVATE,
        )
        val editor = sp.edit()
        val json: String = Gson().toJson(form)
        editor.putString(EditorKey.USER_FORM.toString(), json)
        editor.apply()
    }
}