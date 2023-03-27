package com.team2.handiwork.fragments.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.team2.handiwork.R
import com.team2.handiwork.databinding.FragmentUpdateProfileBinding
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.User
import com.team2.handiwork.viewModel.profile.FragmentUpdateProfileViewModel


class UpdateProfileFragment : Fragment() {

    val args: UpdateProfileFragmentArgs by navArgs()
    private lateinit var binding: FragmentUpdateProfileBinding
    lateinit var vm: FragmentUpdateProfileViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUpdateProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        vm = FragmentUpdateProfileViewModel()
        binding.vm = vm
        vm.setUser(args.user)

        binding.btnSave.setOnClickListener {
            vm.updateUser(updateFinishCallback)
        }

        return binding.root
    }

    private val updateFinishCallback: (user: User) -> Unit = { missions ->
        findNavController().navigate(R.id.action_updateProfileFragment_to_myProfileFragment)
    }

}