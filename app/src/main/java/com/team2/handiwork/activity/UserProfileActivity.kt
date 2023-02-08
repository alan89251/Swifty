package com.team2.handiwork.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.team2.handiwork.MainActivity
import com.team2.handiwork.R
import com.team2.handiwork.databinding.ActivityUserProfileBinding
import com.team2.handiwork.enum.EditorKey
import com.team2.handiwork.models.UserRegistrationForm

class UserProfileActivity : BaseStepperActivity() {
    lateinit var binding: ActivityUserProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityUserProfileBinding>(
            this,
            R.layout.activity_user_profile
        )
        this.binding = binding
    }

    fun getUserRegistrationForm(): UserRegistrationForm {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val json = sp.getString(EditorKey.USER_FORM.toString(), "")
        return if (json == "") {
            Log.e("getUserRegistrationForm: ", "form does not exist")
            UserRegistrationForm()
        } else {
            Gson().fromJson(json, UserRegistrationForm::class.java)
        }
    }

    fun updateUserRegistrationForm(form: UserRegistrationForm) {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sp.edit()
        val json: String = Gson().toJson(form)
        editor.putString(EditorKey.USER_FORM.toString(), json)
        editor.apply()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var back = true

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val view: View = layoutInflater.inflate(R.layout.dialog_confrim, null)
        builder.setView(view)
        builder.setTitle(getString(R.string.registration_alert))
        builder.setMessage(getString(R.string.registration_alert_msg))
        val quitButton: Button = view.findViewById<Button>(R.id.btn_quit)
        val backButton: Button = view.findViewById<Button>(R.id.btn_back)
        val dialog = builder.create()
        quitButton.setOnClickListener {
            dialog.dismiss()
//            supportFragmentManager.popBackStack()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            back = false
        }
        backButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        return back
    }
}