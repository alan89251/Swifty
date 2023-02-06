package com.team2.handiwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.team2.handiwork.R
import com.team2.handiwork.activity.RegistrationPersonalInformationActivity
import com.team2.handiwork.databinding.FragmentRegistrationChooseRoleBinding
import com.team2.handiwork.viewModel.FragmentRegistrationChooseRoleViewModel

class RegistrationChooseRoleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        val binding = FragmentRegistrationChooseRoleBinding.inflate(
            inflater, container, false
        )
        val vm = FragmentRegistrationChooseRoleViewModel()
        val activity = requireActivity() as RegistrationPersonalInformationActivity
        activity.setCurrentStep(2)
        binding.vm = vm
        binding.lifecycleOwner = this

        // todo jump to next fragment
        binding.btnCard1.setOnClickListener {
            val button = it as Button
            vm.isAgent.value = button.currentTextColor == ContextCompat.getColor(
                this.requireContext(),
                R.color.buttonColor
            )
            activity
                .supportFragmentManager
                .beginTransaction()
                .replace(R.id.fm_registration, RegistrationChooseServiceTypeFragment())
                .commit()
        }
        binding.btnCard2.setOnClickListener {
            val button = it as Button
            vm.isEmployer.value = button.currentTextColor == ContextCompat.getColor(
                this.requireContext(),
                R.color.buttonColor
            )
            activity
                .supportFragmentManager
                .beginTransaction()
                .replace(R.id.fm_registration, RegistrationChooseServiceTypeFragment())
                .commit()
        }

        binding.btnDoBoth.setOnClickListener {
            vm.isEmployer.value = true
            vm.isAgent.value = true
            activity
                .supportFragmentManager
                .beginTransaction()
                .replace(R.id.fm_registration, RegistrationChooseServiceTypeFragment())
                .commit()
        }

//        binding.btnCard1.setOnClickListener {
//            val button = it as Button
//            // is selected
//            if (button.currentTextColor == ContextCompat.getColor(
//                    this.requireContext(),
//                    R.color.buttonColor
//                )
//            ) {
//                button.setBackgroundColor(
//                    ContextCompat.getColor(
//                        this.requireContext(),
//                        R.color.buttonColor
//                    )
//                )
//                button.setTextColor(
//                    ContextCompat.getColor(
//                        this.requireContext(),
//                        R.color.secondaryButtonColor
//                    )
//                )
//                vm.isAgent.value = true
//            } else {
//                button.setBackgroundColor(
//                    ContextCompat.getColor(
//                        this.requireContext(),
//                        R.color.secondaryButtonColor
//                    )
//                )
//                button.setTextColor(
//                    ContextCompat.getColor(
//                        this.requireContext(),
//                        R.color.buttonColor
//                    )
//                )
//                vm.isAgent.value = false
//            }
//        }
//
//        binding.btnCard2.setOnClickListener {
//            val button = it as Button
//            if (button.currentTextColor == ContextCompat.getColor(
//                    this.requireContext(),
//                    R.color.buttonColor
//                )
//            ) {
//                button.setBackgroundColor(
//                    ContextCompat.getColor(
//                        this.requireContext(),
//                        R.color.buttonColor
//                    )
//                )
//                button.setTextColor(
//                    ContextCompat.getColor(
//                        this.requireContext(),
//                        R.color.secondaryButtonColor
//                    )
//                )
//                vm.isEmployer.value = true
//            } else {
//                button.setBackgroundColor(
//                    ContextCompat.getColor(
//                        this.requireContext(),
//                        R.color.secondaryButtonColor
//                    )
//                )
//                button.setTextColor(
//                    ContextCompat.getColor(
//                        this.requireContext(),
//                        R.color.buttonColor
//                    )
//                )
//                vm.isEmployer.value = false
//            }
//        }
//
//        binding.btnDoBoth.setOnClickListener {
//            binding.ivCard1.setBackgroundColor(
//                ContextCompat.getColor(
//                    this.requireContext(),
//                    R.color.buttonColor
//                )
//            )
//            binding.ivCard2.setBackgroundColor(
//                ContextCompat.getColor(
//                    this.requireContext(),
//                    R.color.buttonColor
//                )
//            )
//            vm.isEmployer.value = true
//            vm.isAgent.value = true
//        }
        return binding.root
    }
}