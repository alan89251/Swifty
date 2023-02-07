package com.team2.handiwork.activity

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.team2.handiwork.R
import com.team2.handiwork.databinding.ActivityRegistrationPersonalInformationBinding
import com.team2.handiwork.enum.EditorKey
import com.team2.handiwork.enum.SharePreferenceKey
import com.team2.handiwork.models.UserRegistrationForm
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

        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        val backArrow = ContextCompat.getDrawable(applicationContext, R.drawable.ic_back_arrow)
        supportActionBar!!.setHomeAsUpIndicator(backArrow)
        supportActionBar!!.title = "Personal Information"
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

    fun getUserRegistrationForm(): UserRegistrationForm {
        val sp = this.getSharedPreferences(
            SharePreferenceKey.USER_FORM.toString(),
            Context.MODE_PRIVATE,
        )
        val json = sp.getString(EditorKey.USER_FORM.toString(), "")
        return if (json == "") {
            Log.e("getUserRegistrationForm: ", "form does not exist")
            UserRegistrationForm()
        } else {
            Gson().fromJson(json, UserRegistrationForm::class.java)
        }
    }

    fun updateUserRegistrationForm(form: UserRegistrationForm) {
        val sp = this.getSharedPreferences(
            SharePreferenceKey.USER_FORM.toString(),
            Context.MODE_PRIVATE,
        )
        val editor = sp.edit()
        val json: String = Gson().toJson(form)
        editor.putString(EditorKey.USER_FORM.toString(), json)
        editor.apply()
    }


//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            android.R.id.home -> {
//                // API 5+ solution
//                onBackPressed()
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        supportFragmentManager.popBackStack()
        return true
    }
}