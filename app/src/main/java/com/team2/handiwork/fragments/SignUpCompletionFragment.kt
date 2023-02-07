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

        // config UIs
        binding.navBtn.setOnClickListener(navBtnOnClickListener)

        vm.isMissionSuccess.value = false

        return binding.root
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
            .replace(R.id.fm_registration, RegistrationWorkerTNCFragment())
            .commit()
    }
}