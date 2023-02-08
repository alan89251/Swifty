package com.team2.handiwork.activity

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
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
import com.team2.handiwork.viewModel.ActivityRegistrationPersonalInformationSharedViewModel
import com.team2.handiwork.viewModel.ActivityRegistrationPersonalInformationViewModel


class RegistrationPersonalInformationActivity : AppCompatActivity() {
    private var currentStep = 1;
    private lateinit var binding: ActivityRegistrationPersonalInformationBinding;
    private val sharedViewModel: ActivityRegistrationPersonalInformationSharedViewModel by viewModels()

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

        sharedViewModel.step.observe(this) {
            setCurrentStep(it)
        }
    }

    fun setCurrentStep(step: Int) {
        currentStep = step
        if (step == 1) {
            binding.stepper.stepperWidget.visibility = View.VISIBLE
            binding.stepper.ivStep1.setImageResource(R.drawable.stepper__active)
            binding.stepper.ivStep2.setImageResource(R.drawable.stepper__next)
            binding.stepper.ivStep3.setImageResource(R.drawable.stepper__next_2)
        } else if (step == 2) {
            binding.stepper.stepperWidget.visibility = View.VISIBLE
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
        } else if (step == 3) {
            binding.stepper.stepperWidget.visibility = View.VISIBLE
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
            binding.stepper.ivStep2.background.setTint(
                ContextCompat.getColor(
                    this,
                    R.color.checked_color
                )
            )
            binding.stepper.ivStep1.setImageDrawable(drawable)
            binding.stepper.ivStep2.setImageDrawable(drawable)
            binding.stepper.ivStep3.setImageResource(R.drawable.stepper__active_3)
        } else {
            binding.stepper.stepperWidget.visibility = View.INVISIBLE
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var back = true;

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val view: View = layoutInflater.inflate(com.team2.handiwork.R.layout.dialog_confrim, null)
        builder.setView(view)
        builder.setTitle("Confirm to quit the registration process?")
        builder.setMessage("This will logout your account and the data you entered will not be saved.")
        val quitButton: Button = view.findViewById<Button>(R.id.btn_quit)
        val backButton: Button = view.findViewById<Button>(R.id.btn_back)
        val dialog = builder.create()
        quitButton.setOnClickListener {
            dialog.dismiss()
            supportFragmentManager.popBackStack()
            back = false
        }
        backButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        return back
    }
}