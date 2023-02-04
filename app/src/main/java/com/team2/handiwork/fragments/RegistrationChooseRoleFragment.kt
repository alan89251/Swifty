package com.team2.handiwork.fragments

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.team2.handiwork.R
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
        binding.vm = vm
        binding.lifecycleOwner = this

        // todo jump to next fragment
        binding.btnCard1.setOnClickListener {
            if ((it.background as ColorDrawable).color == R.color.blue_1200) {
                it.setBackgroundResource(R.color.blue_200)
                vm.isAgent.value = true
            } else {
                it.setBackgroundResource(R.color.blue_1200)
                vm.isAgent.value = false
            }
        }

        binding.btnCard2.setOnClickListener {
            if ((it.background as ColorDrawable).color == R.color.blue_1200) {
                it.setBackgroundResource(R.color.blue_200)
                vm.isEmployer.value = true
            } else {
                it.setBackgroundResource(R.color.blue_1200)
                vm.isEmployer.value = false
            }
        }

        binding.btnDoBoth.setOnClickListener {
            binding.ivCard1.setBackgroundResource(R.color.blue_1200)
            binding.ivCard2.setBackgroundResource(R.color.blue_1200)
            vm.isEmployer.value = true
            vm.isAgent.value = true
        }



        return binding.root
    }
}