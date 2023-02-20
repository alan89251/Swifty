package com.team2.handiwork

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import androidx.preference.PreferenceManager
import com.team2.handiwork.activity.UserProfileActivity
import com.team2.handiwork.databinding.ActivityHomeBinding
import com.team2.handiwork.databinding.NavHeaderBinding
import com.team2.handiwork.models.User
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.utilities.Utility
import com.team2.handiwork.viewModel.ActivityHomeViewModel

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private val viewModel by viewModels<ActivityHomeViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val currentTheme = pref.getInt(AppConst.CURRENT_THEME, 0)
        Utility.onActivityCreateSetTheme(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        val headerView = binding.navView.getHeaderView(0)


        // set the header & navigation view UI information
        val userEmail = pref.getString(AppConst.EMAIL, "")!!
        viewModel.getUserByEmail(userEmail).subscribe { user ->
            val emailTextView = headerView.findViewById<TextView>(R.id.header_email)
            val nameTextView = headerView.findViewById<TextView>(R.id.header_name)
            emailTextView.text = user.email
            nameTextView.text = "${user.firstName} ${user.lastName}"
            viewModel.currentUser.value = user
            UserData.currentUserData = user
        }

        viewModel.getUserMission(userEmail)

        //set up navigation drawer & action bar
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment,R.id.walletBalanceFragment,R.id.myMissionsFragment), binding.drawerLayout)

        binding.apply {
            navView.setupWithNavController(navController)
            setupActionBarWithNavController(navController, appBarConfiguration)
        }

        //switch theme button
        binding.switchButton.setOnClickListener {
            val pref = PreferenceManager.getDefaultSharedPreferences(this)
            val editor: SharedPreferences.Editor = pref.edit()
            if (currentTheme == 0) {
                Utility.changeToTheme(this, Utility.THEME_EMPLOYER)
                editor.putInt(AppConst.CURRENT_THEME, 1)
            } else {
                Utility.changeToTheme(this, Utility.THEME_AGENT)
                editor.putInt(AppConst.CURRENT_THEME, 0)
            }
            editor.apply()
        }

        binding.switchButton.text = if (currentTheme == 0) {
            "Switch To Employer Portal"
        } else {
            "Switch To Agent Portal"
        }
        binding.navView.menu.findItem(R.id.portal_name).title = if (currentTheme == 0) {
            "Agent Portal"
        } else {
            "Employer Portal"
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


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(binding.navHostFragment.id)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}