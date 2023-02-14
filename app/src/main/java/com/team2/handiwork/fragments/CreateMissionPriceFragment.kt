package com.team2.handiwork.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.team2.handiwork.R
import com.team2.handiwork.adapter.MissionPhotosViewRecyclerViewAdapter
import com.team2.handiwork.databinding.FragmentCreateMissionPriceBinding
import com.team2.handiwork.enum.MissionStatusEnum
import com.team2.handiwork.models.Mission
import com.team2.handiwork.uiComponents.CreateMissionStepper
import com.team2.handiwork.viewModel.FragmentCreateMissionPriceViewModel
import com.team2.handiwork.viewModel.LayoutCreateMissionStepperViewModel

private const val ARG_MISSION = "mission"

class CreateMissionPriceFragment : Fragment() {
    private lateinit var binding: FragmentCreateMissionPriceBinding
    private lateinit var vm: FragmentCreateMissionPriceViewModel
    private lateinit var createMissionStepper: CreateMissionStepper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = FragmentCreateMissionPriceViewModel()

        arguments?.let {
            vm.mission = it.getSerializable(ARG_MISSION) as Mission
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ):  View? {
        binding = FragmentCreateMissionPriceBinding.inflate(inflater, container, false)
        binding.vm = vm
        binding.lifecycleOwner = this

        binding.stepper.lifecycleOwner = this
        createMissionStepper = CreateMissionStepper(
            binding.stepper,
            LayoutCreateMissionStepperViewModel()
        )
        createMissionStepper.setCurrentStep(3)

        binding.tvCategoryContent.text = vm.mission.serviceType + " - " + vm.mission.subServiceType
        binding.tvDateTime.text = vm.missionDuration
        binding.tvAddress.text = vm.mission.location
        binding.tvDesc.text = vm.mission.description
        binding.rvPhotos.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false)
        binding.rvPhotos.adapter = MissionPhotosViewRecyclerViewAdapter(vm.mission.missionPhotos)
        binding.btnConfirm.setOnClickListener(btnConfirmOnClickListener)

        // Inflate the layout for this fragment
        return binding.root
    }

    private val btnConfirmOnClickListener = View.OnClickListener {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm to create Mission?")
            .setMessage("Credits will be deducted from your wallet. Agents will be able to see your mission.")
            .setPositiveButton("Confirm" , { dialogInterface, i ->
                addMissionToDB()
            })
            .setNegativeButton("Back", null)
            .show()
    }

    @SuppressLint("CheckResult")
    private fun addMissionToDB() {
        // save user input to model
        vm.mission.price = 100.0 //binding.amount.text.toString().toDouble()
        vm.mission.status = MissionStatusEnum.OPEN.value
        vm.mission.createdAt = System.currentTimeMillis()
        vm.mission.updatedAt = System.currentTimeMillis()

        vm.addMissionToDB(vm.mission)
            .subscribe {
                Log.d("addMissionToDB status: ", it.toString())

                //TODO change to fit with navigation
                val action =
                    CreateMissionPriceFragmentDirections
                        .actionCreateMissionPriceFragmentToCreateMissionCompletionFragment(
                            it
                        )
                findNavController().navigate(action)
            }
    }

    companion object {
        fun newInstance(mission: Mission) =
            CreateMissionPriceFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_MISSION, mission)
                }
            }
    }
}