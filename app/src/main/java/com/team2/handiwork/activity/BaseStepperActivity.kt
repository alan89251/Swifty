package com.team2.handiwork.activity

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import com.team2.handiwork.R
import com.team2.handiwork.databinding.ActivityStepperBinding
import com.team2.handiwork.databinding.LayoutStepperBinding


open class BaseStepperActivity : AppCompatActivity() {
    private var currentStep = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityStepperBinding>(
            this, R.layout.activity_stepper
        )

        // Set action bar color
        val backArrow = ContextCompat.getDrawable(applicationContext, R.drawable.ic_back_arrow)
        val actionBarColor = ResourcesCompat.getColor(resources, R.color.white, null)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(actionBarColor))
        supportActionBar!!.setHomeAsUpIndicator(backArrow)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    fun setActionBarTitle(title: String) {
        val htmlTitle = "<font color='" + R.color.black + "'>" + title + "</font>"
        supportActionBar!!.title = (HtmlCompat.fromHtml(
            htmlTitle,
            HtmlCompat.FROM_HTML_MODE_LEGACY,
        ))
    }

    fun setCurrentStep(stepper: LayoutStepperBinding, step: Int) {
        currentStep = step
        if (step == 1) {
            stepper.stepperWidget.visibility = View.VISIBLE
            stepper.ivStep1.setImageResource(R.drawable.stepper__active)
            stepper.ivStep2.setImageResource(R.drawable.stepper__next)
            stepper.ivStep3.setImageResource(R.drawable.stepper__next_2)
        } else if (step == 2) {
            stepper.stepperWidget.visibility = View.VISIBLE
            val drawable: Drawable = ResourcesCompat.getDrawable(
                resources, R.drawable.ic_baseline_check_24, null
            )!!
            drawable.setTint(ContextCompat.getColor(this, R.color.white))
            stepper.ivStep1.background.setTint(
                ContextCompat.getColor(
                    this, R.color.checked_color
                )
            )
            stepper.ivStep1.setImageDrawable(drawable)
            stepper.ivStep2.setImageResource(R.drawable.stepper__active_2)
            stepper.ivStep3.setImageResource(R.drawable.stepper__next_2)
        } else if (step == 3) {
            stepper.stepperWidget.visibility = View.VISIBLE
            val drawable: Drawable = ResourcesCompat.getDrawable(
                resources, R.drawable.ic_baseline_check_24, null
            )!!
            drawable.setTint(ContextCompat.getColor(this, R.color.white))
            stepper.ivStep1.background.setTint(
                ContextCompat.getColor(
                    this, R.color.checked_color
                )
            )
            stepper.ivStep2.background.setTint(
                ContextCompat.getColor(
                    this, R.color.checked_color
                )
            )
            stepper.ivStep1.setImageDrawable(drawable)
            stepper.ivStep2.setImageDrawable(drawable)
            stepper.ivStep3.setImageResource(R.drawable.stepper__active_3)
        } else {
            stepper.stepperWidget.visibility = View.INVISIBLE
        }
    }
}