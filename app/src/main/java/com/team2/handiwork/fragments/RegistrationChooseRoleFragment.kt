package com.team2.handiwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.team2.handiwork.AppConst
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
        activity.setCurrentStep(activity.binding.stepper, 2)
        binding.vm = vm
        binding.lifecycleOwner = this

        activity.setActionBarTitle("I'm here to...")
        val form = activity.getUserRegistrationForm()
        val sp = PreferenceManager.getDefaultSharedPreferences(this.requireContext())
        val email = sp.getString(AppConst.EMAIL, "abc@example.com")
        form.email = email!!

        // todo jump to next fragment
        binding.ibtnCard1.setOnClickListener {
            form.isAgent = true

            activity.updateUserRegistrationForm(form)
            val trans = activity
                .supportFragmentManager
                .beginTransaction()

            trans.replace(R.id.fm_registration, RegistrationChooseServiceTypeFragment())
            trans.addToBackStack("RegistrationChooseServiceTypeFragment")
            trans.commit()
        }

        binding.ibtnCard2.setOnClickListener {
            vm.isEmployer.value = true

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