package com.team2.handiwork

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.team2.handiwork.databinding.ActivityHomeBinding
import com.team2.handiwork.enums.SignInMethodEnum
import com.team2.handiwork.firebase.Storage
import com.team2.handiwork.firebase.firestore.Firestore
import com.team2.handiwork.models.Mission
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.utilities.MissionSuggestionWorker
import com.team2.handiwork.utilities.Utility
import com.team2.handiwork.viewModel.ActivityHomeViewModel
import java.util.*
import java.util.concurrent.TimeUnit

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private val viewModel by viewModels<ActivityHomeViewModel>()
    private var isEmployer = false
    private lateinit var iconImageView: ImageView

    // For GoogleSignIn
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val googleAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        // For GoogleSignIn
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val displayTheme = pref.getInt(AppConst.CURRENT_THEME, 0)
        isEmployer = displayTheme == 1
        Utility.onActivityCreateSetTheme(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        val headerView = binding.navView.getHeaderView(0)

        if (Utility.currentDisplayingTheme != displayTheme) {
            switchTheme()
        }

        val userEmail = pref.getString(AppConst.EMAIL, "")!!
        viewModel.getUserByEmail(userEmail).subscribe { user ->
            val emailTextView = headerView.findViewById<TextView>(R.id.header_email)
            val nameTextView = headerView.findViewById<TextView>(R.id.header_name)
            iconImageView = headerView.findViewById(R.id.imageView)

            Glide.with(this)
                .load(user.imageURi)
                .into(iconImageView)


            emailTextView.text = user.email
            nameTextView.text = "${user.firstName} ${user.lastName}"
            viewModel.currentUser.value = user
            UserData.currentUserData = user
        }

        if (isEmployer) {
            viewModel.getEmployerMission(userEmail)
        } else {
            viewModel.getAgentEnrollments(userEmail)
        }

        setHomeScreen()

        viewModel.message.observe(this) { event ->
            event.getContentIfNotHandled()?.let { msg ->
                when (msg) {
                    "UpdateIcon" -> updateIcon(headerView.findViewById(R.id.imageView))
                }
            }
        }

        binding.switchButton.text = if (isEmployer) {
            "Switch To Agent Portal"
        } else {
            "Switch To Client Portal"
        }
        binding.navView.menu.findItem(R.id.portal_name).title = if (isEmployer) {
            "Client Portal"
        } else {
            "Agent Portal"
        }

        //switch theme button
        binding.switchButton.setOnClickListener {
            switchTheme()
        }

        // logout button
        binding.logoutBtn.setOnClickListener {
            viewModel.userLogout()
            val sp = PreferenceManager.getDefaultSharedPreferences(this)
            when (sp.getString(AppConst.PREF_SIGNED_IN_BY, "")) {
                SignInMethodEnum.GOOGLE.displayName -> {
                    mGoogleSignInClient.signOut().addOnCompleteListener {
                        doLogoutActions()
                    }
                }
                SignInMethodEnum.FACEBOOK.displayName -> {
                    LoginManager.getInstance().logOut()
                    doLogoutActions()
                }
                else -> {
                    // signed in by email
                    doLogoutActions()
                }
            }
        }
    }

    private fun doLogoutActions() {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val editor: SharedPreferences.Editor = sp.edit()
        editor.putString(AppConst.EMAIL, "")
        editor.apply()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    private fun switchTheme() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor: SharedPreferences.Editor = pref.edit()
        if (!isEmployer) {
            editor.putInt(AppConst.CURRENT_THEME, 1)
            editor.apply()
            Utility.changeToTheme(this, Utility.THEME_EMPLOYER)
        } else {
            editor.putInt(AppConst.CURRENT_THEME, 0)
            editor.apply()
            Utility.changeToTheme(this, Utility.THEME_AGENT)
        }
    }

    // switch the nav host fragment start destination & navigation view menu
    private fun setHomeScreen() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph)
        binding.navView.menu.clear()
        val menuInflater = MenuInflater(this)
        if (isEmployer) {
            graph.setStartDestination(R.id.homeFragment)
            menuInflater.inflate(R.menu.nav_menu, binding.navView.menu)
        } else {
            graph.setStartDestination(R.id.agentHomeFragment)
            menuInflater.inflate(R.menu.agent_nav_menu, binding.navView.menu)
        }
        val navController = navHostFragment.navController
        navController.setGraph(graph, intent.extras)

        val homeID = if (isEmployer) R.id.homeFragment else R.id.agentHomeFragment

        appBarConfiguration = AppBarConfiguration(
            setOf(
                homeID,
                R.id.walletBalanceFragment,
                R.id.myMissionsFragment,
                R.id.myProfileFragment,
                R.id.chatRoomFragment
            ),
            binding.drawerLayout
        )
        binding.apply {
            navView.setupWithNavController(navController)
            setupActionBarWithNavController(navController, appBarConfiguration)
        }
    }

    private fun updateIcon(imgView: ImageView) {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val imgUrl = pref.getString(AppConst.PREF_USER_ICON_URL, "")
        if (imgUrl != "") {
            Glide.with(this)
                .load(imgUrl)
                .into(imgView)
        }
    }

    private val onIconLoaded: (mission: String) -> Unit = { imgUrl ->
        Glide.with(this)
            .load(imgUrl)
            .into(iconImageView)
    }

    private val onIconLoadFailed: () -> Unit = {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor: SharedPreferences.Editor = pref.edit()
        editor.putString(AppConst.PREF_USER_ICON_URL, "")
        editor.apply()
    }

    override fun onStop() {
        super.onStop()
        scheduleCheckMissionWork()
    }

    override fun onResume() {
        super.onResume()
        cancelCheckMissionWork()
    }

    private fun scheduleCheckMissionWork() {
        if (viewModel.currentUser.value?.distance != 0 || viewModel.currentUser.value?.serviceTypeList!!.isNotEmpty()) {
            val workRequest = PeriodicWorkRequestBuilder<MissionSuggestionWorker>(1, TimeUnit.HOURS)
                .setInitialDelay(1, TimeUnit.HOURS)
                .build()

            WorkManager.getInstance(this).enqueue(workRequest)
            val sp = PreferenceManager.getDefaultSharedPreferences(this)
            val editor: SharedPreferences.Editor = sp.edit()
            editor.putString(AppConst.PREF_SCHEDULE_SUGGESTION_WORK, workRequest.id.toString())
            editor.apply()
            Log.d("hehehe", "scheduleCheckMissionWork: ${workRequest.id}")
        }
    }

    private fun cancelCheckMissionWork() {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val workID = sp.getString(AppConst.PREF_SCHEDULE_SUGGESTION_WORK, "")
        if (workID != "") {
            Log.d("hehehe", "cancelCheckMissionWork:$workID")
            WorkManager.getInstance(this).cancelWorkById(UUID.fromString(workID))
        } else {
            WorkManager.getInstance(this).cancelAllWork()
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(binding.navHostFragment.id)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}