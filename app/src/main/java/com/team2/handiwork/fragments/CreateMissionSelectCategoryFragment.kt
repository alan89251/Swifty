package com.team2.handiwork.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.team2.handiwork.R
import com.team2.handiwork.adapter.CreateMissionServiceTypeRecyclerViewAdapter
import com.team2.handiwork.databinding.FragmentCreateMissionSelectCategoryBinding
import com.team2.handiwork.models.ServiceType
import com.team2.handiwork.models.SubServiceType
import com.team2.handiwork.uiComponents.CreateMissionStepper
import com.team2.handiwork.viewModel.FragmentCreateMissionSelectCategoryViewModel
import com.team2.handiwork.viewModel.LayoutCreateMissionStepperViewModel

class CreateMissionSelectCategoryFragment : Fragment() {
    private lateinit var binding: FragmentCreateMissionSelectCategoryBinding
    private lateinit var vm: FragmentCreateMissionSelectCategoryViewModel
    private lateinit var createMissionStepper: CreateMissionStepper

    @SuppressLint("CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateMissionSelectCategoryBinding.inflate(inflater, container, false)
        vm = FragmentCreateMissionSelectCategoryViewModel()
        binding.vm = vm
        binding.lifecycleOwner = this

        binding.stepper.lifecycleOwner = this
        createMissionStepper = CreateMissionStepper(
            binding.stepper,
            LayoutCreateMissionStepperViewModel()
        )
        createMissionStepper.setCurrentStep(1)

        binding.serviceTypeList.layoutManager = GridLayoutManager(context, vm.serviceTypeListColumnNum)
        val adapter = CreateMissionServiceTypeRecyclerViewAdapter(getServiceTypes())
        binding.serviceTypeList.adapter = adapter
        adapter.selectServiceType.subscribe {
            vm.mission.serviceType = it.name

            val action = CreateMissionSelectCategoryFragmentDirections
                .actionCreateMissionSelectCategoryFragmentToCreateMissionSelectSubServiceTypeFragment(
                    vm.mission
                )
            findNavController().navigate(action)
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun getServiceTypes(): ArrayList<ServiceType> {
        return vm.serviceTypes.map {
            val serviceType = ServiceType()
            serviceType.name = it
            serviceType.subServiceTypeList = resources.getStringArray(
                vm.getSubServiceTypesResId(it)
            ).map {
                val subServiceType = SubServiceType()
                subServiceType.name = it
                subServiceType
            }.toList() as ArrayList<SubServiceType>
            serviceType
        }.toList() as ArrayList<ServiceType>
    }
}