package com.team2.handiwork.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.team2.handiwork.R
import com.team2.handiwork.activity.RegistrationPersonalInformationActivity
import com.team2.handiwork.databinding.FragmentRegistrationChooseRoleBinding
import com.team2.handiwork.enum.EditorKey
import com.team2.handiwork.enum.SharePreferenceKey
import com.team2.handiwork.models.UserRegistrationForm
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


        val form = activity.getUserRegistrationForm()

        // todo jump to next fragment
        binding.btnCard1.setOnClickListener {
            val button = it as Button
            vm.isAgent.value = button.currentTextColor == ContextCompat.getColor(
                this.requireContext(),
                R.color.buttonColor
            )
            form.isAgent = vm.isAgent.value!!

            activity.updateUserRegistrationForm(form)

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

            form.isEmployer = vm.isEmployer.value!!
            activity.updateUserRegistrationForm(form)

            activity
                .supportFragmentManager
                .beginTransaction()
                .replace(R.id.fm_registration, RegistrationChooseServiceTypeFragment())
                .commit()
        }

        binding.btnDoBoth.setOnClickListener {
            form.isAgent = true
            form.isEmployer = true

            activity.updateUserRegistrationForm(form)

            activity
                .supportFragmentManager
                .beginTransaction()
                .replace(R.id.fm_registration, RegistrationChooseServiceTypeFragment())
                .commit()
        }
        return binding.root
    }
}