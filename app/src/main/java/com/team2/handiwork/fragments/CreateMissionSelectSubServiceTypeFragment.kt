package com.team2.handiwork.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.team2.handiwork.R
import com.team2.handiwork.adapter.CreateMissionSubServiceTypeRecyclerViewAdapter
import com.team2.handiwork.databinding.FragmentCreateMissionSelectSubServiceTypeBinding
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.SubServiceType
import com.team2.handiwork.uiComponents.CreateMissionStepper
import com.team2.handiwork.viewModel.FragmentCreateMissionSelectSubServiceTypeViewModel

private const val ARG_MISSION = "mission"

class CreateMissionSelectSubServiceTypeFragment : Fragment() {
    private lateinit var binding: FragmentCreateMissionSelectSubServiceTypeBinding
    private lateinit var vm: FragmentCreateMissionSelectSubServiceTypeViewModel
    private lateinit var createMissionStepper: CreateMissionStepper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = FragmentCreateMissionSelectSubServiceTypeViewModel()

        val args: CreateMissionSelectSubServiceTypeFragmentArgs by navArgs()
        vm.mission = args.selectedMission

        arguments?.let {
            vm.mission = it.getSerializable(ARG_MISSION) as Mission
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateMissionSelectSubServiceTypeBinding.inflate(inflater, container, false)
        binding.vm = vm
        binding.lifecycleOwner = this

        createMissionStepper = CreateMissionStepper(binding.stepper)
        createMissionStepper.setCurrentStep(1)

        binding.createMissionCategory.text = vm.mission.serviceType
        binding.subServiceTypeList.layoutManager = GridLayoutManager(context, vm.subServiceTypeListColumnNum)
        val adapter = CreateMissionSubServiceTypeRecyclerViewAdapter(getSubServiceTypes())
        binding.subServiceTypeList.adapter = adapter
        adapter.selectSubServiceType.subscribe {
            vm.mission.subServiceType = it.name

            //TODO Change to fit with navigator
            requireActivity()
                .supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fm_registration,
                    CreateMissionDetailsFragment
                        .newInstance(vm.mission))
                .commit()
        }

        // Inflate the layout for this fragment
        return binding.root
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