package com.team2.handiwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.team2.handiwork.R
import com.team2.handiwork.activity.UserProfileActivity
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
        val activity = requireActivity() as UserProfileActivity
        activity.binding.vm!!.currentStep.value = 2

        binding.vm = vm
        binding.lifecycleOwner = this

        activity.setActionBarTitle("I'm here to...")

        // todo jump to next fragment
        binding.ibtnCard1.setOnClickListener {
            activity.vm.registrationForm.value!!.isAgent = true

            val trans = activity
                .supportFragmentManager
                .beginTransaction()

            trans.replace(R.id.fm_registration, RegistrationChooseServiceTypeFragment())
            trans.addToBackStack("RegistrationChooseServiceTypeFragment")
            trans.commit()
        }

        binding.ibtnCard2.setOnClickListener {
            activity.vm.registrationForm.value!!.isEmployer = true

            activity
                .supportFragmentManager
                .beginTransaction()
                .replace(R.id.fm_registration, RegistrationChooseServiceTypeFragment())
                .commit()
        }

        binding.btnDoBoth.setOnClickListener {
            activity.vm.registrationForm.value!!.isEmployer = true
            activity.vm.registrationForm.value!!.isAgent = true

            activity
                .supportFragmentManager
                .beginTransaction()
                .replace(R.id.fm_registration, RegistrationChooseServiceTypeFragment())
                .commit()
        }
        return binding.root
    }
}