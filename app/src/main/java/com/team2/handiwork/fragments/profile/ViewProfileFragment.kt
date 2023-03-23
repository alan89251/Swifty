package com.team2.handiwork.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team2.handiwork.base.fragment.DisposalFragment
import com.team2.handiwork.databinding.FragmentViewProfileBinding
import com.team2.handiwork.viewModel.profile.FragmentViewProfileViewModel

class ViewProfileFragment : DisposalFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentViewProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        val vm = FragmentViewProfileViewModel()
        binding.vm = vm

        val targetEmail = requireArguments().getSerializable("targetEmail") as String

        // set view invisibility
        binding.layoutBasicInfo.lblEmail.text = "Phone number verified"
        binding.layoutBasicInfo.tvEmail.visibility = View.GONE
        binding.layoutBasicInfo.tvPhone.visibility = View.GONE

        vm.fs.getUser(targetEmail).subscribe {
            vm.user.value = it
        }





        return binding.root
    }
}