package com.team2.handiwork.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team2.handiwork.R
import com.team2.handiwork.databinding.FragmentSignUpCompletionBinding
import com.team2.handiwork.viewModel.FragmentSignUpCompletionViewModel

class SignUpCompletionFragment : Fragment() {
    private lateinit var binding: FragmentSignUpCompletionBinding
    private lateinit var vm: FragmentSignUpCompletionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpCompletionBinding.inflate(inflater, container, false)
        vm = FragmentSignUpCompletionViewModel()
        binding.vm = vm
        binding.lifecycleOwner = this

        // config UIs
        binding.navBtn.setOnClickListener(navBtnOnClickListener)

        vm.isMissionSuccess.observe(requireActivity(), ::updateUI)
        vm.isMissionSuccess.value = false

        return binding.root
    }

    private fun updateUI(isMissionSuccess: Boolean) {
        if (isMissionSuccess) {
            vm.missionResult.value = resources.getString(R.string.mission_result_success)
            vm.missionResultDescription.value = resources.getString(R.string.mission_result_description_success)
            vm.navBtnText.value = "Continue"
            binding.missionResult.setTextColor(Color.parseColor("#366FFF"))
        }
        else {
            vm.missionResult.value = resources.getString(R.string.mission_result_failed)
            vm.missionResultDescription.value = resources.getString(R.string.mission_result_description_failed)
            vm.navBtnText.value = "Back"
            binding.missionResult.setTextColor(Color.parseColor("#D52941"))
        }
    }

    private val navBtnOnClickListener = View.OnClickListener {
        if (vm.isMissionSuccess.value == null) {
            return@OnClickListener
        }
        if (vm.isMissionSuccess.value!!) {
            navigateToHomeScreen()
        }
        else {
            backToRegistrationWorkerTNCScreen()
        }
    }

    private fun navigateToHomeScreen() {
    }

    private fun backToRegistrationWorkerTNCScreen() {
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.user_profile_fragment, RegistrationWorkerTNCFragment())
            .commit()
    }
}