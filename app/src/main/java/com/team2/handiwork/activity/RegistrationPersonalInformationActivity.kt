package com.team2.handiwork.activity

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.team2.handiwork.R
import com.team2.handiwork.databinding.ActivityRegistrationPersonalInformationBinding
import com.team2.handiwork.viewModel.ActivityRegistrationPersonalInformationViewModel

class RegistrationPersonalInformationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityRegistrationPersonalInformationBinding>(
                this,
                R.layout.activity_registration_personal_information
            )
        binding.vm = ActivityRegistrationPersonalInformationViewModel()
        // Set action bar color
        val actionBarColor = ResourcesCompat.getColor(resources, R.color.white, null)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(actionBarColor))
        // Set action bar title and text color
//        val htmlTitle = "<font color='" + R.color.black + "'>" + getString(R.string.forgot_password_title) + "</font>"
//        supportActionBar!!.title = (HtmlCompat.fromHtml(htmlTitle, HtmlCompat.FROM_HTML_MODE_LEGACY))
//        // Enable the back arrow on the action bar
//        val backArrow = ContextCompat.getDrawable(applicationContext, R.drawable.ic_back_arrow)
////        supportActionBar!!.setHomeAsUpIndicator(backArrow)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    }
}