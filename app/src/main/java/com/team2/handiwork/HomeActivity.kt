package com.team2.handiwork

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuInflater
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.team2.handiwork.databinding.ActivityHomeBinding
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.utilities.Utility
import com.team2.handiwork.viewModel.ActivityHomeViewModel

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private val viewModel by viewModels<ActivityHomeViewModel>()
    private var isEmployer = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
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
            emailTextView.text = user.email
            nameTextView.text = "${user.firstName} ${user.lastName}"
            viewModel.currentUser.value = user
            UserData.currentUserData = user
        }

        if (isEmployer) {
            viewModel.getEmployerMission(userEmail)
        } else {
            viewModel.getUserEnrollments(userEmail)
        }

        setHomeScreen()

        binding.switchButton.text = if (isEmployer) {
            "Switch To Agent Portal"
        } else {
            "Switch To Employer Portal"
        }
        binding.navView.menu.findItem(R.id.portal_name).title = if (isEmployer) {
            "Employer Portal"
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
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
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
                R.id.myProfileFragment
            ),
            binding.drawerLayout
        )
        binding.apply {
            navView.setupWithNavController(navController)
            setupActionBarWithNavController(navController, appBarConfiguration)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(binding.navHostFragment.id)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}