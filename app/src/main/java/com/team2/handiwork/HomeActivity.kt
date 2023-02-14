package com.team2.handiwork

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

        // TODO use share Pref to save current selected theme
        // TODO get the current user is agent or employer to determine the theme
        Utility.onActivityCreateSetTheme(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        val headerView = binding.navView.getHeaderView(0)
        viewModel.getUserByEmail(pref.getString(AppConst.EMAIL, "")!!)

        // set the header UI information
        viewModel.currentUser.observe(this) { user ->
            val emailTextView = headerView.findViewById<TextView>(R.id.header_email)
            val nameTextView = headerView.findViewById<TextView>(R.id.header_name)
            emailTextView.text = user.email
            nameTextView.text = user.firstName + " " + user.lastName
        }

//        set up navigation drawer & action bar
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)

        binding.apply {

            navView.setupWithNavController(navController)
            setupActionBarWithNavController(navController, appBarConfiguration)

        }

        binding.switchButton.setOnClickListener {
            if (viewModel.currentUser.value!!.isEmployer) {
                Utility.changeToTheme(this, Utility.THEME_AGENT)
            } else {
                Utility.changeToTheme(this, Utility.THEME_EMPLOYER)
            }
        }
    }

    private fun setDefaultTheme(user: User) {
        if (user.isEmployer) {
            Utility.changeToTheme(this, Utility.THEME_EMPLOYER)
        } else {
            Utility.changeToTheme(this, Utility.THEME_AGENT)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(binding.navHostFragment.id)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}