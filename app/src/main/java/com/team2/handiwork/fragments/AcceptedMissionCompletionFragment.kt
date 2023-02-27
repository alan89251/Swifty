package com.team2.handiwork.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.team2.handiwork.R
import com.team2.handiwork.databinding.FragmentAcceptedMissionCompletionBinding
import com.team2.handiwork.viewModel.FragmentAcceptedMissionCompletionViewModel

private const val ARG_IS_ACCEPT_MISSION_SUCCESS = "is_accept_mission_success"

class AcceptedMissionCompletionFragment : Fragment() {
    private lateinit var binding: FragmentAcceptedMissionCompletionBinding
    private lateinit var vm: FragmentAcceptedMissionCompletionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = FragmentAcceptedMissionCompletionViewModel()

        arguments?.let {
            vm.isAcceptMissionSuccess.value = it.getBoolean(ARG_IS_ACCEPT_MISSION_SUCCESS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAcceptedMissionCompletionBinding.inflate(inflater, container, false)
        binding.vm = vm
        binding.lifecycleOwner = this

        // remove back button in navigation bar
        (requireActivity() as AppCompatActivity)
            .supportActionBar!!
            .setDisplayHomeAsUpEnabled(false)

        binding.btnNavToHome.setOnClickListener(btnNavToHomeOnClickListener)

        // Inflate the layout for this fragment
        return binding.root
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param isAcceptMissionSuccess Parameter 1.
         * @return A new instance of fragment AcceptedMissionCompletionFragment.
         */
        @JvmStatic
        fun newInstance(isAcceptMissionSuccess: Boolean) =
            AcceptedMissionCompletionFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_ACCEPT_MISSION_SUCCESS, isAcceptMissionSuccess)
                }
            }
    }
}