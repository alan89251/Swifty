package com.team2.handiwork.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.team2.handiwork.R
import com.team2.handiwork.databinding.FragmentRegistrationWorkerTNCBinding
import com.team2.handiwork.viewModel.FragmentRegistrationWorkerTNCViewModel
import java.io.BufferedReader
import java.io.InputStreamReader

class RegistrationWorkerTNCFragment : Fragment() {
    private lateinit var binding: FragmentRegistrationWorkerTNCBinding
    private lateinit var vm: FragmentRegistrationWorkerTNCViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrationWorkerTNCBinding.inflate(inflater, container, false)
        vm = FragmentRegistrationWorkerTNCViewModel()
        binding.vm = vm
        binding.lifecycleOwner = this

        // load the terms and conditions from resource
        val reader = BufferedReader(
            InputStreamReader(
                resources.openRawResource(R.raw.terms_and_conditions)
            )
        )
        val termsAndConditions = reader.readText()
        reader.close()
        //
        configStepper()

        binding.termsAndConditions.text = termsAndConditions
        binding.termsAndConditions.movementMethod = ScrollingMovementMethod()
        binding.userAgreementSwitch.setOnCheckedChangeListener { compoundButton, b ->
            vm.isEnableNextBtn.value = b
        }

        return binding.root
    }

    private fun configStepper() {
        val drawable: Drawable =
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_baseline_check_24,
                null
            )!!
        drawable.setTint(ContextCompat.getColor(requireContext(), R.color.white))
        binding.registrationStepper.ivStep1.background.setTint(
            ContextCompat.getColor(
                requireContext(),
                R.color.checked_color
            )
        )
        binding.registrationStepper.ivStep2.background.setTint(
            ContextCompat.getColor(
                requireContext(),
                R.color.checked_color
            )
        )
        binding.registrationStepper.ivStep1.setImageDrawable(drawable)
        binding.registrationStepper.ivStep2.setImageDrawable(drawable)
        binding.registrationStepper.ivStep3.setImageResource(R.drawable.stepper__active_3)
    }
}