package com.team2.handiwork.fragments.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team2.handiwork.R
import com.team2.handiwork.activity.UserProfileActivity
import com.team2.handiwork.base.fragment.BaseFragmentActivity
import com.team2.handiwork.databinding.FragmentRegistrationChooseRoleBinding

class RegistrationChooseRoleFragment : BaseFragmentActivity() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegistrationChooseRoleBinding.inflate(
            inflater,
            container,
            false,
        )
        val fragmentActivity = requireActivity() as UserProfileActivity

        fragmentActivity.binding.vm!!.currentStep.value = 2
        binding.vm = fragmentActivity.vm
        binding.lifecycleOwner = this

        fragmentActivity.setActionBarTitle("I'm here to...")

        binding.ibtnCard1.setOnClickListener {
            fragmentActivity.vm.registrationForm.value!!.isEmployer = true
            this.navigate(
                R.id.fm_registration,
                RegistrationWorkerTNCFragment(),
                "RegistrationWorkerTNCFragment"
            )
        }

        binding.ibtnCard2.setOnClickListener {
            fragmentActivity.vm.registrationForm.value!!.isAgent = true
            this.navigate(
                R.id.fm_registration,
                RegistrationChooseServiceTypeFragment(),
                "RegistrationChooseServiceTypeFragment"
            )
        }

        binding.btnDoBoth.setOnClickListener {
            fragmentActivity.vm.registrationForm.value!!.isEmployer = true
            fragmentActivity.vm.registrationForm.value!!.isAgent = true
            this.navigate(
                R.id.fm_registration,
                RegistrationChooseServiceTypeFragment(),
                "RegistrationChooseServiceTypeFragment"
            )
        }
        return binding.root
    }
}