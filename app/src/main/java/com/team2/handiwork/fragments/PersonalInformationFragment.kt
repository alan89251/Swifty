package com.team2.handiwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.team2.handiwork.databinding.FragmentPersonalInformationBinding
import com.team2.handiwork.viewModel.FragmentPersonalInformationViewModel

class PersonalInformationFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPersonalInformationBinding.inflate(
            inflater,
            container,
            false
        )
        binding.vm = FragmentPersonalInformationViewModel()
        binding.lifecycleOwner = this

        binding.btnSendMsg.setOnClickListener {
            // do nth
        }

        binding.btnNext.setOnClickListener {
            // todo verify input
        }

        binding.ibtnPersonalInfoCamera.setOnClickListener {
            // todo upload or take photo
        }


        return binding.root
    }
}