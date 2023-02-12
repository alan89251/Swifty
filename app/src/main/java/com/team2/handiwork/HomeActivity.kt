package com.team2.handiwork

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.team2.handiwork.databinding.ActivityHomeBinding
import com.team2.handiwork.utilities.Utility

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var listener: NavController.OnDestinationChangedListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO use share Pref to save current selected theme
        // TODO get the current user is agent or employer to determine the theme
        Utility.onActivityCreateSetTheme(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)

        binding.apply {

            navView.setupWithNavController(navController)
            setupActionBarWithNavController(navController, appBarConfiguration)

        }

        binding.switchButton.setOnClickListener {
            Utility.changeToTheme(this, Utility.THEME_EMPLOYER)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(binding.navHostFragment.id)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}