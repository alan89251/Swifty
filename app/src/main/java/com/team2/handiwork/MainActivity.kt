package com.team2.handiwork

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.activity.RegistrationPersonalInformationActivity
import com.team2.handiwork.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var txtEmail: EditText
    private lateinit var txtPass: EditText
    private lateinit var txtSignupEmail: EditText
    private lateinit var txtSignupPassword: EditText
    private lateinit var txtVerifyPassword: EditText

    private lateinit var userEmail: String
    private lateinit var userPassword: String

    private lateinit var loginEmail: String
    private lateinit var loginPassword: String

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // todo charlene test
        binding = ActivityMainBinding.inflate(layoutInflater)
        startActivity(Intent(this, RegistrationPersonalInformationActivity::class.java))


        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        database = Firebase.database.getReference("Users")

        txtEmail = findViewById(R.id.email)
        txtPass = findViewById(R.id.password)

        txtSignupEmail = findViewById(R.id.signupEmail)
        txtSignupPassword = findViewById(R.id.SignUpPassword)
        txtVerifyPassword = findViewById(R.id.VerifyPassword)

        binding.mainBtn.tag =
            ButtonActions.LOGIN   //default action of the main button as defined in the xml

        binding.forgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPwActivity::class.java)
            startActivity(intent)
        }

        binding.signUpToggle.setOnClickListener {
            binding.signUpToggle.background =
                ResourcesCompat.getDrawable(resources, R.drawable.switch_highlighted, null)
            binding.signUpToggle.setTextColor(resources.getColor(R.color.textColor, null))
            binding.logInToggle.background = null
            binding.SignUpLayout.visibility = View.VISIBLE
            binding.LogInLayout.visibility = View.GONE
            binding.logInToggle.setTextColor(resources.getColor(R.color.switchOnColor, null))
            binding.mainBtn.text = getString(R.string.signup)
            binding.mainBtn.tag = ButtonActions.SIGNUP
        }

        binding.logInToggle.setOnClickListener {
            binding.logInToggle.background =
                ResourcesCompat.getDrawable(resources, R.drawable.switch_highlighted, null)
            binding.logInToggle.setTextColor(resources.getColor(R.color.textColor, null))
            binding.signUpToggle.background = null
            binding.LogInLayout.visibility = View.VISIBLE
            binding.SignUpLayout.visibility = View.GONE
            binding.signUpToggle.setTextColor(resources.getColor(R.color.switchOnColor, null))
            binding.mainBtn.text = getString(R.string.login)
            binding.mainBtn.tag = ButtonActions.LOGIN
        }

        binding.mainBtn.setOnClickListener {
            if (binding.mainBtn.tag == ButtonActions.LOGIN)
                performSignIn()
            if (binding.mainBtn.tag == ButtonActions.SIGNUP)
                performSignUp()
        }
    }

    private fun performSignIn() {
        var isPassValidation = true

        if (!Patterns.EMAIL_ADDRESS.matcher(txtEmail.text.toString()).matches()) {
            txtEmail.error = getString(R.string.email_error)
            txtEmail.requestFocus()
            isPassValidation = false
        } else {
            loginEmail = txtEmail.text.toString()
        }

        if (txtPass.text.toString().isEmpty()) {
            txtPass.error = getString(R.string.pw_error)
            txtPass.requestFocus()
            isPassValidation = false
        } else {
            loginPassword = txtPass.text.toString()
        }

        if (isPassValidation) {
            auth.signInWithEmailAndPassword(loginEmail, loginPassword)
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
                        editor.commit()

                        txtPass.setText("")
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
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


    private fun performSignUp() {
        var isPassValidation = true
        if (!Patterns.EMAIL_ADDRESS.matcher(txtSignupEmail.text.toString()).matches()) {
            txtSignupEmail.error = getString(R.string.email_error)
            txtSignupEmail.requestFocus()
            isPassValidation = false
        } else {
            userEmail = txtSignupEmail.text.toString()
        }

        if (txtSignupPassword.text.toString().isEmpty()) {
            txtSignupPassword.error = getString(R.string.pw_error)
            txtSignupPassword.requestFocus()
            isPassValidation = false
        }
        if (txtVerifyPassword.text.toString().isEmpty()) {
            txtVerifyPassword.error = getString(R.string.pw_error)
            txtVerifyPassword.requestFocus()
            isPassValidation = false
        } else {
            userPassword = txtVerifyPassword.text.toString()
        }

        if (txtSignupPassword.text.toString() != txtVerifyPassword.text.toString()) {
            txtSignupPassword.error = getString(R.string.passwords_not_match)
            txtVerifyPassword.error = getString(R.string.passwords_not_match)
            txtSignupPassword.requestFocus()
            txtVerifyPassword.requestFocus()
            isPassValidation = false
        }

        if (isPassValidation) {
            auth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(ContentValues.TAG, "createUserWithEmail:success")

                        val userUniqueID = auth.currentUser!!.uid
                        database.child(userUniqueID)

                        //save uid
                        val pref = PreferenceManager.getDefaultSharedPreferences(this)
                        val editor: SharedPreferences.Editor = pref.edit()
                        editor.putString(AppConst.PREF_UID, userUniqueID)
                        editor.commit()

                        txtSignupPassword.setText("")
                        txtVerifyPassword.setText("")

                        val intent = Intent(this, UserProfileActivity::class.java)
                        intent.putExtra(AppConst.PREF_CALL_FROM_SIGNUP, true)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, getString(R.string.signup_fail),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}

enum class ButtonActions {
    LOGIN, SIGNUP
}