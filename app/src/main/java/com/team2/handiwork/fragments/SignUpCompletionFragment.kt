package com.team2.handiwork.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.team2.handiwork.R
import com.team2.handiwork.activity.UserProfileActivity
import com.team2.handiwork.databinding.FragmentSignUpCompletionBinding
import com.team2.handiwork.viewModel.FragmentSignUpCompletionViewModel

class SignUpCompletionFragment : Fragment() {
    private lateinit var binding: FragmentSignUpCompletionBinding
    private lateinit var vm: FragmentSignUpCompletionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpCompletionBinding.inflate(inflater, container, false)
        vm = FragmentSignUpCompletionViewModel()
        binding.vm = vm
        binding.lifecycleOwner = this

        // config UIs
        val activity = requireActivity() as UserProfileActivity
        activity.setCurrentStep(activity.binding.stepper, 4)
        binding.navBtn.setOnClickListener(navBtnOnClickListener)

        vm.isMissionSuccess.value = true

        return binding.root
    }

    private val navBtnOnClickListener = View.OnClickListener {
        if (vm.isMissionSuccess.value == null) {
            return@OnClickListener
        }
        if (vm.isMissionSuccess.value!!) {
            navigateToHomeScreen()

            val activity = requireActivity() as UserProfileActivity
            vm.register(activity.getUserRegistrationForm()).subscribe {
                Log.d("registration status: ", it.toString())
            }
            val p = PreferenceManager.getDefaultSharedPreferences(this.requireContext())
            p.edit().clear().commit()
        } else {
            backToRegistrationWorkerTNCScreen()
        }
    }

    private fun navigateToHomeScreen() {

    }

    private fun backToRegistrationWorkerTNCScreen() {
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.fm_registration, RegistrationWorkerTNCFragment())
            .commit()
    }
}