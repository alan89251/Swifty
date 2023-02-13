package com.team2.handiwork.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.team2.handiwork.HomeActivity
import com.team2.handiwork.MainActivity
import com.team2.handiwork.R
import com.team2.handiwork.databinding.ActivityUserProfileBinding
import com.team2.handiwork.viewModel.ActivityUserProfileViewModel

class UserProfileActivity : BaseStepperActivity() {
    val vm = ActivityUserProfileViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityUserProfileBinding>(
            this,
            R.layout.activity_user_profile
        )
        stepper = binding.stepper
        binding.vm = vm
        vm.initRegistrationForm(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var back = true

        if (step != 1) {
            supportFragmentManager.popBackStack()
            return false
        }

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