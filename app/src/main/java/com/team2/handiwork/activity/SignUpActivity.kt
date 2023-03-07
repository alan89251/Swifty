package com.team2.handiwork.activity

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.AppConst
import com.team2.handiwork.MainActivity
import com.team2.handiwork.R
import com.team2.handiwork.databinding.ActivitySignUpBinding
import com.team2.handiwork.utilities.Utility


class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    private var screenId = ScreenId.SignupEmail.id
    private lateinit var fbEmail: String
    private lateinit var fbPassword: String
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        // Set action bar color
        val actionBarColor = ResourcesCompat.getColor(resources, R.color.white_100, null)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(actionBarColor))
        // Set action bar title and text color
        val htmlTitle = "<font color='" + R.color.black_100 + "'>" + getString(R.string.create_account) + "</font>"
        supportActionBar!!.title = (HtmlCompat.fromHtml(htmlTitle, HtmlCompat.FROM_HTML_MODE_LEGACY))
        // Enable the back arrow on the action bar
        val backArrow = ContextCompat.getDrawable(applicationContext, R.drawable.ic_back_arrow)
        supportActionBar!!.setHomeAsUpIndicator(backArrow)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // Navigate to verify code screen
        binding.buttonEmailNext.setOnClickListener {
            if (Utility.isValidEmail(binding.email.text.toString())) {
                setScreen(ScreenId.VerifyCode.id)
            } else {
                binding.email.error = getString(R.string.invalid_email)
                binding.email.requestFocus()
            }
        }

        // Navigate to signup password screen
        binding.buttonVerifyNext.setOnClickListener {
            if (binding.verificationCode.text.toString().isNotEmpty() ) {
                setScreen(ScreenId.SignupPassword.id)
            } else {
                binding.verificationCode.error = getString(R.string.invalid_verification_code)
                binding.verificationCode.requestFocus()
            }
        }

        // Disable the Signup email input field
        binding.signupEmail.keyListener = null

        // Submit to firebase auth open account
        binding.registerBtn.setOnClickListener {
            performSignUp()
        }
    }

    override fun onResume() {
        super.onResume()
        setScreen(screenId)
    }

    // Back arrow navigation
    override fun onSupportNavigateUp(): Boolean {
        when (screenId) {
            ScreenId.SignupEmail.id -> onBackPressed()
            ScreenId.VerifyCode.id -> enableSignUpEmailScreen()
            ScreenId.SignupPassword.id -> enableVerifyCodeScreen()
            else -> onBackPressed()
        }
        return true
    }

    private fun setScreen(id: Int) {
        when (id) {
            ScreenId.SignupEmail.id -> enableSignUpEmailScreen()
            ScreenId.VerifyCode.id -> enableVerifyCodeScreen()
            ScreenId.SignupPassword.id -> enableSigneUpPasswordScreen()
            else -> enableSignUpEmailScreen()
        }
    }

    private fun enableSignUpEmailScreen() {
        binding.email.requestFocus()
        binding.screen0Layout.visibility = View.VISIBLE
        binding.screen1Layout.visibility = View.GONE
        binding.screen2Layout.visibility = View.GONE
        binding.registerBtn.isEnabled = false
        screenId = ScreenId.SignupEmail.id
    }

    private fun enableVerifyCodeScreen() {
        binding.sendToEmail.text = binding.email.text
        binding.verificationCode.requestFocus()
        binding.screen0Layout.visibility = View.GONE
        binding.screen1Layout.visibility = View.VISIBLE
        binding.screen2Layout.visibility = View.GONE
        binding.registerBtn.isEnabled = false
        screenId = ScreenId.VerifyCode.id
    }

    private fun enableSigneUpPasswordScreen() {
        binding.signupEmail.text = binding.email.text
        binding.signUpPassword.requestFocus()
        binding.screen0Layout.visibility = View.GONE
        binding.screen1Layout.visibility = View.GONE
        binding.screen2Layout.visibility = View.VISIBLE
        binding.registerBtn.isEnabled = true
        screenId = ScreenId.SignupPassword.id
    }

    private fun performSignUp() {
        var isPassValidation = true
        fbEmail = binding.signupEmail.text.toString()

        if(binding.signUpPassword.text.toString().isEmpty()){
            binding.signUpPassword.error = getString(R.string.pw_error)
            binding.signUpPassword.requestFocus()
            isPassValidation = false
        }
        if(binding.verifyPassword.text.toString().isEmpty()){
            binding.verifyPassword.error = getString(R.string.pw_error)
            binding.verifyPassword.requestFocus()
            isPassValidation = false
        }else{
            fbPassword = binding.signUpPassword.text.toString()
        }

        if(binding.signUpPassword.text.toString() != binding.verifyPassword.text.toString()){
            binding.signUpPassword.error = getString(R.string.passwords_not_match)
            binding.verifyPassword.error = getString(R.string.passwords_not_match)
            binding.signUpPassword.requestFocus()
            isPassValidation = false
        }

        if (isPassValidation) {
            auth.createUserWithEmailAndPassword(fbEmail, fbPassword)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(ContentValues.TAG, "createUserWithEmail:success")

                        val userUniqueID = auth.currentUser!!.uid

                        //save uid
                        val pref = PreferenceManager.getDefaultSharedPreferences(this)
                        val editor: SharedPreferences.Editor = pref.edit()
                        editor.putString(AppConst.PREF_UID, userUniqueID)
                        editor.putString(AppConst.EMAIL, auth.currentUser!!.email)
                        editor.apply()

                        binding.signUpPassword.setText("")
                        binding.verifyPassword.setText("")

                        showSuccessDialog()
                    } else {
                        showFailDialog()
                    }
                }
        }
    }

    private fun showSuccessDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        val mImage = ResourcesCompat.getDrawable(resources, R.mipmap.ic_reg_success, null)
        builder.setIcon(mImage)
        builder.setTitle(getString(R.string.account_created))
        builder.setMessage(getString(R.string.login_now))

        builder.setPositiveButton(getString(R.string.go_login)
        ) { dialog, _ ->
            run {
                dialog.dismiss()
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }
        builder.create().show()
    }

    private fun showFailDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        val mImage = ResourcesCompat.getDrawable(resources, R.mipmap.ic_reg_error, null)
        builder.setIcon(mImage)
        builder.setTitle(getString(R.string.account_not_created))
        builder.setMessage(getString(R.string.retry_registration))

        builder.setPositiveButton(getString(R.string.go_registration)
        ) { dialog, _ ->
            run {
                dialog.dismiss()
                screenId = ScreenId.SignupEmail.id
                setScreen(screenId)
            }
        }
        builder.create().show()
    }
}

enum class ScreenId(val id: Int) {
    SignupEmail(0),
    VerifyCode(1),
    SignupPassword(2),
}