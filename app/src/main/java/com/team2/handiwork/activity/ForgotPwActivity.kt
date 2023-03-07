package com.team2.handiwork

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.team2.handiwork.activity.ResetSentActivity
import com.team2.handiwork.databinding.ActivityForgotPwBinding
import com.team2.handiwork.viewModel.ForgotPasswordViewModel

class ForgotPwActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPwBinding
    private lateinit var viewModel: ForgotPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_pw)
        viewModel = ViewModelProvider(this)[ForgotPasswordViewModel::class.java]
        binding.viewModel = viewModel

        // Set action bar color
        val actionBarColor = ResourcesCompat.getColor(resources, R.color.white_100, null)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(actionBarColor))
        // Set action bar title and text color
        val htmlTitle = "<font color='" + R.color.black_100 + "'>" + getString(R.string.forgot_password_title) + "</font>"
        supportActionBar!!.title = (HtmlCompat.fromHtml(htmlTitle, HtmlCompat.FROM_HTML_MODE_LEGACY))
        // Enable the back arrow on the action bar
        val backArrow = ContextCompat.getDrawable(applicationContext, R.drawable.ic_back_arrow)
        supportActionBar!!.setHomeAsUpIndicator(backArrow)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        // observe the status msg from view model and shows dialog accordingly
        viewModel.message.observe(this) { event ->
            event.getContentIfNotHandled()?.let { msg ->

                when (msg) {
                    ScreenMsg.SEND_RESET_EMAIL_SUCCESSFUL -> navigateToCompleteScreen()
                    ScreenMsg.SEND_RESET_EMAIL_FAILED -> showFailDialog()
                    ScreenMsg.INVALID_EMAIL_ADDRESS -> indicateInvalidEmail()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun indicateInvalidEmail() {
        binding.email.error = getString(R.string.invalid_email)
        binding.email.requestFocus()
    }

    private fun navigateToCompleteScreen() {
        val intent = Intent(this, ResetSentActivity::class.java)
        startActivity(intent)
    }

    private fun showSuccessDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        val mImage = ResourcesCompat.getDrawable(resources, R.mipmap.ic_reg_success, null)
        builder.setIcon(mImage)
        builder.setTitle(getString(R.string.send_reset_link_success))
        builder.setMessage(getString(R.string.login_now))

        builder.setPositiveButton(
            getString(R.string.login_now_after_reset)
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
        builder.setTitle(getString(R.string.email_not_sent))
        builder.setMessage(getString(R.string.resend_email))
        builder.setPositiveButton(
            getString(R.string.go_registration)
        ) { dialog, _ ->
            run {
                dialog.dismiss()
            }
        }
        builder.create().show()
    }

}

enum class ScreenMsg {
    SEND_RESET_EMAIL_SUCCESSFUL,
    SEND_RESET_EMAIL_FAILED,
    INVALID_EMAIL_ADDRESS,
}
