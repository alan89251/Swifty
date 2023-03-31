package com.team2.handiwork

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.AppConst.PREF_DEVICE_TOKEN
import com.team2.handiwork.activity.SignUpActivity
import com.team2.handiwork.activity.UserProfileActivity
import com.team2.handiwork.databinding.ActivityMainBinding
import com.team2.handiwork.enums.FirebaseCollectionKey
import com.team2.handiwork.enums.SignInMethodEnum
import com.team2.handiwork.utilities.Utility

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var txtEmail: EditText
    private lateinit var txtPass: EditText

    private lateinit var fbEmail: String
    private lateinit var fbPassword: String

    private lateinit var auth: FirebaseAuth

    //Google sign-in attributes
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var googleAuth: FirebaseAuth

    //Facebook sign-in attributes
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso)
        googleAuth= FirebaseAuth.getInstance()

        // Config Facebook sign-in
        //FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(application)
        callbackManager = CallbackManager.Factory.create()

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

        binding.GoogleSignInBtn.setOnClickListener {
            performGoogleSignIn()
        }

        binding.loginBtn.setOnClickListener {
            performSignIn()
        }

        binding.FacebookSignInBtn.setPermissions(listOf("email", "public_profile").toString())
        // Callback registration
        val callback = object: FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Log.d(getString(R.string.app_name), "Facebook sign-in success")
                GraphRequest.newMeRequest(
                    result.accessToken
                ) { me, response ->
                    if (response!!.error != null) {
                        // handle error
                        Toast.makeText(applicationContext, getString(R.string.facebook_sign_in_fail), Toast.LENGTH_LONG).show()
                        Log.e(getString(R.string.app_name), "Facebook get profile error: ${response.error}")
                    } else {
                        // get email and id of the user
                        val profileEmail = me!!.optString("email")
                        val profileId = me.optString("id")
                        handleLoginSuccess(SignInMethodEnum.FACEBOOK, profileId, profileEmail)
                    }
                }.executeAsync()
            }
            override fun onCancel() {
                Log.d(getString(R.string.app_name), "Facebook sign-in canceled")
            }
            override fun onError(error: FacebookException) {
                Toast.makeText(applicationContext, getString(R.string.facebook_sign_in_fail), Toast.LENGTH_LONG).show()
                Log.e(getString(R.string.app_name), "Facebook sign-in error: $error")
            }
        }
        binding.FacebookSignInBtn.registerCallback(callbackManager, callback)
    }

    private fun performGoogleSignIn(){
        Log.d("sch", "performGoogleSignIn() running...")
        val signInIntent:Intent=mGoogleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            //handleResult(task)
            try {
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    googleAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            handleLoginSuccess(
                                SignInMethodEnum.GOOGLE,
                                account.id!!,
                                account.email!!
                            )
                        }
                    }
                }
            } catch (e: ApiException) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
                Log.e(getString(R.string.app_name), "Google sign-in Error: ${e.toString()}")
            }
        } else {
            Toast.makeText(this, getString(R.string.google_sign_in_fail), Toast.LENGTH_LONG).show()
            Log.e(getString(R.string.app_name), "Google sign-in Error. Result Code: ${result.resultCode}")
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
                        handleLoginSuccess(SignInMethodEnum.EMAIL, auth.currentUser!!.uid, fbEmail)
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

    private fun handleLoginSuccess(signedInBy: SignInMethodEnum, userUniqueID: String, email: String) {
        // Sign in success, update UI with the signed-in user's information
        Toast.makeText(
            applicationContext,
            getString(R.string.signing_in),
            Toast.LENGTH_SHORT
        ).show()

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor: SharedPreferences.Editor = pref.edit()
        editor.putString(AppConst.PREF_UID, userUniqueID)
        editor.putString(AppConst.EMAIL, email)
        editor.putString(AppConst.PREF_SIGNED_IN_BY, signedInBy.displayName)
        editor.apply()

        txtPass.setText("")

        redirectToNextScreen(email)
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
                        // save device fcm token to the user profile only if the user exists
                        val deviceToken = getDeviceToken()
                        if (deviceToken != null) {
                            if (deviceToken.isNotEmpty() && deviceToken != user.fcmDeviceToken) {
                                fireStore
                                    .collection(FirebaseCollectionKey.USERS.displayName)
                                    .document(uid)
                                    .update("fcmDeviceToken", deviceToken)
                                    .addOnSuccessListener {
                                        Log.d("MainActivity", "FCM device token updated to the user profile")
                                    }.addOnFailureListener { e ->
                                        Log.w("MainActivity", "Error updating FCM device token", e)
                                    }
                            }
                        }
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

    private fun getDeviceToken() : String? {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        return pref.getString(PREF_DEVICE_TOKEN, null)
    }
}
