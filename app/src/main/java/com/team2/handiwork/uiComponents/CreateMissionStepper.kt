package com.team2.handiwork.uiComponents

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.team2.handiwork.R
import com.team2.handiwork.databinding.LayoutCreateMissionStepperBinding
import com.team2.handiwork.viewModel.LayoutCreateMissionStepperViewModel

class CreateMissionStepper(private val binding: LayoutCreateMissionStepperBinding) {
    private var vm: LayoutCreateMissionStepperViewModel = LayoutCreateMissionStepperViewModel()

    init {
        binding.vm = vm
    }

    fun setCurrentStep(step: Int) {
        if (step == 1) {
            binding.ivStep1.setImageResource(R.drawable.stepper_active_1)
            binding.ivStep2.setImageResource(R.drawable.stepper_inactive_2)
            binding.ivStep3.setImageResource(R.drawable.stepper_inactive_3)
        } else if (step == 2) {
            binding.ivStep1.setImageResource(R.drawable.stepper_completed)
            binding.ivStep2.setImageResource(R.drawable.stepper_active_2)
            binding.ivStep3.setImageResource(R.drawable.stepper_inactive_3)
        } else {
            binding.ivStep1.setImageResource(R.drawable.stepper_completed)
            binding.ivStep2.setImageResource(R.drawable.stepper_completed)
            binding.ivStep3.setImageResource(R.drawable.stepper_active_3)
        }
    }
}