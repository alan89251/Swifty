package com.team2.handiwork.uiComponents

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.team2.handiwork.R
import com.team2.handiwork.databinding.LayoutCreateMissionStepperBinding
import com.team2.handiwork.viewModel.LayoutCreateMissionStepperViewModel

class CreateMissionStepper(
    private val binding: LayoutCreateMissionStepperBinding,
    private val vm: LayoutCreateMissionStepperViewModel) {

    init {
        binding.vm = vm
    }

    fun setCurrentStep(step: Int) {
        vm.step.value = step
    }
}