package com.team2.handiwork.fragments

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        binding.termsAndConditions.text = termsAndConditions
        binding.termsAndConditions.movementMethod = ScrollingMovementMethod()
        binding.userAgreementSwitch.setOnCheckedChangeListener { compoundButton, b ->
            vm.isEnableNextBtn.value = b
        }

        return binding.root
    }
}