package com.team2.handiwork.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.team2.handiwork.MainActivity
import com.team2.handiwork.R
import com.team2.handiwork.databinding.ActivityUserProfileBinding
import com.team2.handiwork.databinding.DialogConfrimBinding
import com.team2.handiwork.viewModel.ActivityRegistrationViewModel

class UserProfileActivity : BaseStepperActivity() {
    val vm = ActivityRegistrationViewModel()

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
        val binding: DialogConfrimBinding = DialogConfrimBinding.inflate(layoutInflater)
        builder.setView(binding.root)
        builder.setTitle(getString(R.string.registration_alert))
        builder.setMessage(getString(R.string.registration_alert_msg))
        binding.btnConfirm.text = getString(R.string.quit)
        val dialog = builder.create()
        binding.btnConfirm.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            back = false
        }
        binding.btnBack.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        return back
    }
}