package com.team2.handiwork.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.team2.handiwork.R
import com.team2.handiwork.adapter.CreateMissionSubServiceTypeRecyclerViewAdapter
import com.team2.handiwork.databinding.FragmentCreateMissionSelectSubServiceTypeBinding
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.SubServiceType
import com.team2.handiwork.uiComponents.CreateMissionStepper
import com.team2.handiwork.viewModel.FragmentCreateMissionSelectSubServiceTypeViewModel
import com.team2.handiwork.viewModel.LayoutCreateMissionStepperViewModel

private const val ARG_MISSION = "mission"

class CreateMissionSelectSubServiceTypeFragment : Fragment() {
    private lateinit var binding: FragmentCreateMissionSelectSubServiceTypeBinding
    private lateinit var vm: FragmentCreateMissionSelectSubServiceTypeViewModel
    private lateinit var createMissionStepper: CreateMissionStepper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = FragmentCreateMissionSelectSubServiceTypeViewModel()

        val args: CreateMissionSelectSubServiceTypeFragmentArgs by navArgs()
        vm.mission = args.mission

        arguments?.let {
            vm.mission = it.getSerializable(ARG_MISSION) as Mission
        }
    }

    @SuppressLint("CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateMissionSelectSubServiceTypeBinding.inflate(inflater, container, false)
        binding.vm = vm
        binding.lifecycleOwner = this

        binding.stepper.lifecycleOwner = this
        createMissionStepper = CreateMissionStepper(
            binding.stepper,
            LayoutCreateMissionStepperViewModel()
        )
        createMissionStepper.setCurrentStep(1)

        binding.createMissionCategory.text = vm.mission.serviceType
        binding.subServiceTypeList.layoutManager = GridLayoutManager(context, vm.subServiceTypeListColumnNum)
        val adapter = CreateMissionSubServiceTypeRecyclerViewAdapter(getSubServiceTypes())
        binding.subServiceTypeList.adapter = adapter
        adapter.selectSubServiceType.subscribe {
            vm.mission.subServiceType = it.name

            val action =
                CreateMissionSelectSubServiceTypeFragmentDirections
                    .actionCreateMissionSelectSubServiceTypeFragmentToCreateMissionDetailsFragment(
                        vm.mission
                    )
            findNavController().navigate(action)
        }
        binding.btnAbort.setOnClickListener(btnAbortOnClickListener)

        // Inflate the layout for this fragment
        return binding.root
    }

    private val btnAbortOnClickListener = View.OnClickListener {
        AlertDialog.Builder(requireContext())
            .setTitle(resources.getString(R.string.abort_create_mission_alert_title))
            .setMessage(resources.getString(R.string.abort_create_mission_alert_msg))
            .setPositiveButton("Confirm") { _, _ ->
                navigateToHomeFragment()
            }
            .setNegativeButton("Back", null)
            .show()
    }

    private fun getSubServiceTypes(): ArrayList<SubServiceType> {
        return resources.getStringArray(
            vm.getSubServiceTypesResId(vm.mission.serviceType)
        )
            .map {
                val subServiceType = SubServiceType()
                subServiceType.name = it
                subServiceType
            }.toList() as ArrayList<SubServiceType>
    }

    private fun navigateToHomeFragment() {
        val action =
            CreateMissionSelectSubServiceTypeFragmentDirections
                .actionCreateMissionSelectSubServiceTypeFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param mission Parameter 1.
         * @return A new instance of fragment CreateMissionSelectSubServiceTypeFragment.
         */
        @JvmStatic
        fun newInstance(mission: Mission) =
            CreateMissionSelectSubServiceTypeFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_MISSION, mission)
                }
            }
    }
}