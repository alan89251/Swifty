package com.team2.handiwork

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.activity.SignUpActivity
import com.team2.handiwork.activity.UserProfileActivity
import com.team2.handiwork.databinding.ActivityMainBinding
import com.team2.handiwork.enum.FirebaseCollectionKey
import com.team2.handiwork.utilities.Utility

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var txtEmail: EditText
    private lateinit var txtPass: EditText

    private lateinit var fbEmail: String
    private lateinit var fbPassword: String

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        txtEmail = binding.email
        txtPass = binding.password

        binding.signup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.forgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPwActivity::class.java)
            startActivity(intent)
        }

        binding.loginBtn.setOnClickListener {
            performSignIn()
        }
    }

    private fun performSignIn() {
        var isPassValidation = true

        if (!Patterns.EMAIL_ADDRESS.matcher(txtEmail.text.toString()).matches()) {
            txtEmail.error = getString(R.string.email_error)
            txtEmail.requestFocus()
            isPassValidation = false
        } else {
            fbEmail = txtEmail.text.toString()
        }

        if (txtPass.text.toString().isEmpty()) {
            txtPass.error = getString(R.string.pw_error)
            txtPass.requestFocus()
            isPassValidation = false
        } else {
            fbPassword = txtPass.text.toString()
        }

        if (isPassValidation) {
            auth.signInWithEmailAndPassword(fbEmail, fbPassword)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.signing_in),
                            Toast.LENGTH_SHORT
                        ).show()

                        //save uid
                        val userUniqueID = auth.currentUser!!.uid
                        val pref = PreferenceManager.getDefaultSharedPreferences(this)
                        val editor: SharedPreferences.Editor = pref.edit()
                        editor.putString(AppConst.PREF_UID, userUniqueID)
                        editor.putString(AppConst.EMAIL, auth.currentUser!!.email)
                        editor.apply()

                        txtPass.setText("")

                        redirectToNextScreen(fbEmail)
                    } else {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.login_fail),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun redirectToNextScreen(uid: String) {
        var intent: Intent
        val fireStore = Firebase.firestore

        fireStore
            .collection(FirebaseCollectionKey.USERS.displayName).document(uid)
            .get()
            .addOnSuccessListener { document ->
                try {

                    if (document.data != null) {
                        val user = document.toObject<com.team2.handiwork.models.User>()
                        val pref = PreferenceManager.getDefaultSharedPreferences(this)
                        val editor: SharedPreferences.Editor = pref.edit()
                        if (user!!.isEmployer) {
                            Utility.setThemeToChange(Utility.THEME_EMPLOYER)
                            editor.putInt(AppConst.CURRENT_THEME, 1)
                        } else {
                            Utility.setThemeToChange(Utility.THEME_AGENT)
                            editor.putInt(AppConst.CURRENT_THEME, 0)
                        }
                        editor.apply()
                    }
                    intent = if (document.data != null) {
                        // User profile exists. Jump to home
                        Intent(this, HomeActivity::class.java)

                    } else {
                        // User profile not found.  Jump to input user profile
                        Intent(this, UserProfileActivity::class.java)
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                } catch (ex: Exception) {
                    ex.message?.let { Log.e("MainActivity", it) }
                }
            }.addOnFailureListener { e ->
                Log.e("MainActivity", "Error reading document", e)
            }
    }
}
