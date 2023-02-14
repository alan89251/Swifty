package com.team2.handiwork.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.team2.handiwork.R
import com.team2.handiwork.databinding.FragmentCreateMissionCompletionBinding
import com.team2.handiwork.viewModel.FragmentCreateMissionCompletionViewModel

private const val ARG_IS_CREATE_MISSION_SUCCESS = "is_create_mission_success"

class CreateMissionCompletionFragment : Fragment() {
    private lateinit var binding: FragmentCreateMissionCompletionBinding
    private lateinit var vm: FragmentCreateMissionCompletionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = FragmentCreateMissionCompletionViewModel()

        arguments?.let {
            vm.isCreateMissionSuccess.value = it.getBoolean(ARG_IS_CREATE_MISSION_SUCCESS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateMissionCompletionBinding.inflate(inflater, container, false)
        binding.vm = vm
        binding.lifecycleOwner = this

        binding.btnNavToHome.setOnClickListener {
            val action =
                CreateMissionCompletionFragmentDirections
                    .actionCreateMissionCompletionFragmentToHomeFragment()
            findNavController().navigate(action)
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param isCreateMissionSuccess Parameter 1.
         * @return A new instance of fragment CreateMissionCompletionFragment.
         */
        @JvmStatic
        fun newInstance(isCreateMissionSuccess: Boolean) =
            CreateMissionCompletionFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_CREATE_MISSION_SUCCESS, isCreateMissionSuccess)
                }
            }
    }
}