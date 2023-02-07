package com.team2.handiwork.fragments

import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.team2.handiwork.R
import com.team2.handiwork.databinding.FragmentRegistrationWorkerTNCBinding
import com.team2.handiwork.enum.SharePreferenceKey
import com.team2.handiwork.utilities.Utility
import com.team2.handiwork.viewModel.ActivityRegistrationPersonalInformationSharedViewModel
import com.team2.handiwork.viewModel.FragmentRegistrationWorkerTNCViewModel
import java.io.BufferedReader
import java.io.InputStreamReader

class RegistrationWorkerTNCFragment : Fragment() {
    private lateinit var binding: FragmentRegistrationWorkerTNCBinding
    private lateinit var vm: FragmentRegistrationWorkerTNCViewModel
    private val sharedViewModel: ActivityRegistrationPersonalInformationSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrationWorkerTNCBinding.inflate(inflater, container, false)
        vm = FragmentRegistrationWorkerTNCViewModel()
        binding.vm = vm
        binding.lifecycleOwner = this

        // config UIs
        sharedViewModel.step.value = 3
        binding.nextBtn.setOnClickListener(nextBtnOnClickListener)
        binding.backBtn.setOnClickListener(backBtnOnClickListener)

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

    private val nextBtnOnClickListener = View.OnClickListener {
        // update UserRegistrationForm
        val sp = requireActivity()
            .getSharedPreferences(
                SharePreferenceKey.USER_FORM.toString(),
                Context.MODE_PRIVATE,
            )
        val form = Utility.getUserRegistrationForm(sp)

        // navigate to SignUpCompletionFragment
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.fm_registration, SignUpCompletionFragment())
            .commit()
    }

    private val backBtnOnClickListener = View.OnClickListener {
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.fm_registration, RegistrationWorkerProfileFragment())
            .commit()
    }
}