package com.team2.handiwork.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.team2.handiwork.HomeActivity
import com.team2.handiwork.MainActivity
import com.team2.handiwork.R
import com.team2.handiwork.databinding.ActivityResetSentBinding
import com.team2.handiwork.viewModel.ForgotPasswordViewModel

class ResetSentActivity : AppCompatActivity() {
    lateinit var binding: ActivityResetSentBinding
    lateinit var viewModel: ForgotPasswordViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_sent)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reset_sent)
        viewModel = ViewModelProvider(this)[ForgotPasswordViewModel::class.java]
        binding.viewModel = viewModel

        viewModel.countDown.observe(this) { countValue ->
            binding.countdownInstruction.text = "You will redirected to Home page in $countValue seconds"
            if (countValue <= 0) {
                navigateToHomeScreen()
            }
        }

        viewModel.startCountDown(10)

        binding.navBtn.setOnClickListener {
            navigateToHomeScreen()
        }
    }

    // navigate to home screen and clear this activity
    private fun navigateToHomeScreen() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}