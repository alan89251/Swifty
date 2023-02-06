package com.team2.handiwork.fragments

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

        vm.isMissionSuccess.observe(requireActivity(), ::configureUI)
        vm.isMissionSuccess.value = true

        return binding.root
    }

    private fun configureUI(isMissionSuccess: Boolean) {
        if (isMissionSuccess) {
            vm.missionResult.value = "Mission Completed!"
            vm.missionResultDescription.value = "You are now a member of Swifty Daily Service Agency."
            vm.navBtnText.value = "Continue"
            binding.missionResult.setTextColor(resources.getColor(R.color.missionSuccessTextColor))
        }
        else {
            vm.missionResult.value = "Mission Failed!"
            vm.missionResultDescription.value = "Something went wrong.\nPlease try again."
            vm.navBtnText.value = "Back"
            binding.missionResult.setTextColor(resources.getColor(R.color.missionFailedTextColor))
        }
    }
}