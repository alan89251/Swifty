package com.team2.handiwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        activity.supportActionBar!!.title = "I'm here to..."

        val form = activity.getUserRegistrationForm()

        // todo jump to next fragment
        binding.ibtnCard1.setOnClickListener {
            vm.isAgent.value = binding.tvCard2.currentTextColor == ContextCompat.getColor(
                this.requireContext(),
                R.color.buttonColor
            )
            form.isAgent = vm.isAgent.value!!

            activity.updateUserRegistrationForm(form)

            val trans = activity
                .supportFragmentManager
                .beginTransaction()

            trans.replace(R.id.fm_registration, RegistrationChooseServiceTypeFragment())
            trans.addToBackStack("RegistrationChooseServiceTypeFragment")
            trans.commit()
        }

        binding.ibtnCard2.setOnClickListener {
            vm.isEmployer.value = binding.tvCard2.currentTextColor == ContextCompat.getColor(
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