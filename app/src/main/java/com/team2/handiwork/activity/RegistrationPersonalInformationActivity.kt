package com.team2.handiwork.activity

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.team2.handiwork.R
import com.team2.handiwork.databinding.ActivityRegistrationPersonalInformationBinding
import com.team2.handiwork.viewModel.ActivityRegistrationPersonalInformationViewModel

class RegistrationPersonalInformationActivity : AppCompatActivity() {
    private var currentStep = 1;
    private lateinit var binding: ActivityRegistrationPersonalInformationBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityRegistrationPersonalInformationBinding>(
                this,
                R.layout.activity_registration_personal_information
            )
        binding.vm = ActivityRegistrationPersonalInformationViewModel()
        this.binding = binding
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

    fun setCurrentStep(step: Int) {
        currentStep = step
        if (step == 1) {
            binding.stepper.ivStep1.setImageResource(R.drawable.stepper__active)
            binding.stepper.ivStep2.setImageResource(R.drawable.stepper__next)
            binding.stepper.ivStep3.setImageResource(R.drawable.stepper__next_2)
        } else if (step == 2) {

            val drawable: Drawable =
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_baseline_check_24,
                    null
                )!!
            drawable.setTint(ContextCompat.getColor(this, R.color.white))
            binding.stepper.ivStep1.background.setTint(
                ContextCompat.getColor(
                    this,
                    R.color.checked_color
                )
            )
            binding.stepper.ivStep1.setImageDrawable(drawable)
            binding.stepper.ivStep2.setImageResource(R.drawable.stepper__active_2)
            binding.stepper.ivStep3.setImageResource(R.drawable.stepper__next_2)
        } else {
            val drawable: Drawable =
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_baseline_check_24,
                    null
                )!!
            drawable.setTint(ContextCompat.getColor(this, R.color.white))
            binding.stepper.ivStep1.background.setTint(
                ContextCompat.getColor(
                    this,
                    R.color.checked_color
                )
            )
            binding.stepper.ivStep1.setImageDrawable(drawable)
            binding.stepper.ivStep2.setImageDrawable(drawable)
            binding.stepper.ivStep3.setImageResource(R.drawable.stepper__active_2)
        }
    }
}