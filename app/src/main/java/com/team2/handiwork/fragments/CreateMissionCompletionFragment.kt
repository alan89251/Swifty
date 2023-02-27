package com.team2.handiwork.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.team2.handiwork.R
import com.team2.handiwork.databinding.FragmentCreateMissionCompletionBinding
import com.team2.handiwork.models.Mission
import com.team2.handiwork.viewModel.FragmentCreateMissionCompletionViewModel

private const val ARG_IS_CREATE_MISSION_SUCCESS = "is_create_mission_success"
private const val ARG_MISSION = "mission"

class CreateMissionCompletionFragment : Fragment() {
    private lateinit var binding: FragmentCreateMissionCompletionBinding
    private lateinit var vm: FragmentCreateMissionCompletionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = FragmentCreateMissionCompletionViewModel()

        arguments?.let {
            vm.isCreateMissionSuccess.value = it.getBoolean(ARG_IS_CREATE_MISSION_SUCCESS)
            vm.mission = it.getSerializable(ARG_MISSION) as Mission
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateMissionCompletionBinding.inflate(inflater, container, false)
        binding.vm = vm
        binding.lifecycleOwner = this

        // remove back button in navigation bar
        (requireActivity() as AppCompatActivity)
            .supportActionBar!!
            .setDisplayHomeAsUpEnabled(false)

        binding.btnViewMission.setOnClickListener(btnViewMissionOnClickListener)
        binding.btnNavToHome.setOnClickListener(btnNavToHomeOnClickListener)

        // Inflate the layout for this fragment
        return binding.root
    }

    private val btnViewMissionOnClickListener = View.OnClickListener {
        // display back button in navigation bar
        (requireActivity() as AppCompatActivity)
            .supportActionBar!!
            .setDisplayHomeAsUpEnabled(false)
        navigateToEmployerMissionDetailsFragment()
    }

    private val btnNavToHomeOnClickListener = View.OnClickListener {
        // display back button in navigation bar
        (requireActivity() as AppCompatActivity)
            .supportActionBar!!
            .setDisplayHomeAsUpEnabled(false)
        navigateToHomeFragment()
    }

    private fun navigateToHomeFragment() {
        val action =
            CreateMissionCompletionFragmentDirections
                .actionCreateMissionCompletionFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    private fun navigateToEmployerMissionDetailsFragment() {
        val action =
            CreateMissionCompletionFragmentDirections
                .actionCreateMissionCompletionFragmentToEmployerMissionDetailsFragment(
                    vm.mission
                )
        findNavController().navigate(action)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param isCreateMissionSuccess Parameter 1.
         * @param mission mission
         * @return A new instance of fragment CreateMissionCompletionFragment.
         */
        @JvmStatic
        fun newInstance(isCreateMissionSuccess: Boolean, mission: Mission) =
            CreateMissionCompletionFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_CREATE_MISSION_SUCCESS, isCreateMissionSuccess)
                    putSerializable(ARG_MISSION, mission)
                }
            }
    }
}