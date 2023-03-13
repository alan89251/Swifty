package com.team2.handiwork.activity

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import com.team2.handiwork.R
import com.team2.handiwork.databinding.ActivityStepperBinding
import com.team2.handiwork.databinding.LayoutStepperBinding
import com.team2.handiwork.base.viewModel.BaseStepperViewModel


open class BaseStepperActivity : AppCompatActivity() {
    lateinit var binding: ActivityStepperBinding
    lateinit var stepper: LayoutStepperBinding // override
    var step = 1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityStepperBinding>(
            this, R.layout.activity_stepper
        )
        val vm = BaseStepperViewModel()
        binding.vm = vm
        this.binding = binding

        vm.currentStep.observe(this) {
            setCurrentStep(it)
            Log.d("BaseStepperActivity stepper change: ", it.toString())
        }

        // Set action bar color
        val backArrow = ContextCompat.getDrawable(applicationContext, R.drawable.ic_back_arrow)
        val actionBarColor = ResourcesCompat.getColor(resources, R.color.white_100, null)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(actionBarColor))
        supportActionBar!!.setHomeAsUpIndicator(backArrow)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    fun setActionBarTitle(title: String) {
        val htmlTitle = "<font color='" + R.color.black_100 + "'>" + title + "</font>"
        supportActionBar!!.title = (HtmlCompat.fromHtml(
            htmlTitle,
            HtmlCompat.FROM_HTML_MODE_LEGACY,
        ))
    }

    private fun setCurrentStep(step: Int) {
        this.step = step;
        if (step == 1) {
            stepper.stepperWidget.visibility = View.VISIBLE
            stepper.ivStep1.setImageResource(R.drawable.stepper_active_1)
            stepper.ivStep2.setImageResource(R.drawable.stepper_inactive_2)
            stepper.ivStep3.setImageResource(R.drawable.stepper_inactive_3)
        } else if (step == 2) {
            stepper.stepperWidget.visibility = View.VISIBLE
            val drawable: Drawable = ResourcesCompat.getDrawable(
                resources, R.drawable.ic_baseline_check_24, null
            )!!
            drawable.setTint(ContextCompat.getColor(this, R.color.white_100))
            stepper.ivStep1.background.setTint(
                ContextCompat.getColor(
                    this, R.color.soft_orange_100
                )
            )
            stepper.ivStep1.setImageDrawable(drawable)
            stepper.ivStep2.setImageResource(R.drawable.stepper_active_2)
            stepper.ivStep3.setImageResource(R.drawable.stepper_inactive_3)
        } else if (step == 3) {
            stepper.stepperWidget.visibility = View.VISIBLE
            val drawable: Drawable = ResourcesCompat.getDrawable(
                resources, R.drawable.ic_baseline_check_24, null
            )!!
            drawable.setTint(ContextCompat.getColor(this, R.color.white_100))
            stepper.ivStep1.background.setTint(
                ContextCompat.getColor(
                    this, R.color.soft_orange_100
                )
            )
            stepper.ivStep2.background.setTint(
                ContextCompat.getColor(
                    this, R.color.soft_orange_100
                )
            )
            stepper.ivStep1.setImageDrawable(drawable)
            stepper.ivStep2.setImageDrawable(drawable)
            stepper.ivStep3.setImageResource(R.drawable.stepper_active_3)
        } else {
            stepper.stepperWidget.visibility = View.INVISIBLE
        }
    }
}