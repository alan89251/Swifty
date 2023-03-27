package com.team2.handiwork.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.team2.handiwork.R
import com.team2.handiwork.adapter.MissionPhotosViewRecyclerViewAdapter
import com.team2.handiwork.base.fragment.DisposeFragment
import com.team2.handiwork.databinding.FragmentCreateMissionPriceBinding
import com.team2.handiwork.enums.MissionStatusEnum
import com.team2.handiwork.models.Mission
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.uiComponents.CreateMissionStepper
import com.team2.handiwork.utilities.Ext.Companion.disposedBy
import com.team2.handiwork.viewModel.FragmentCreateMissionPriceViewModel
import com.team2.handiwork.viewModel.LayoutCreateMissionStepperViewModel

private const val ARG_MISSION = "mission"

class CreateMissionPriceFragment : DisposeFragment() {
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

    @SuppressLint("CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ):  View {
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
        binding.tvCredits.text = UserData.currentUserData.balance.toString()
        binding.rvPhotos.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false)
        binding.rvPhotos.adapter = MissionPhotosViewRecyclerViewAdapter(vm.mission.missionPhotoUris)
        binding.amount.addTextChangedListener {
            if (it == null) {
                return@addTextChangedListener
            }
            if (it.toString().trim() != "") {
                vm.price.value = it.toString().toDouble()
            }
            else {
                vm.price.value = 0.0
            }
        }
        binding.btnConfirm.setOnClickListener(btnConfirmOnClickListener)
        binding.btnAbort.setOnClickListener(btnAbortOnClickListener)

        // config photo uploading pipeline
        vm.photoUploadQueue.observe(requireActivity()) {
            vm.popPhotoFromQueueAndUploadToDB()
        }
        vm.photoUploadResult.observe(requireActivity()) {
            vm.popPhotoFromQueueAndUploadToDB()
        }
        vm.isPhotoUploadCompleted.observe(requireActivity()) {
            vm.updateMissionPhotoFireStorageUris()
                .subscribe {
                    if (it) {
                        Log.d("updateMissionPhotoFireStorageUris: ", "success")
                        navigateToCreateMissionCompletionFragment(true, vm.mission)
                    }
                    else {
                        Log.d("updateMissionPhotoFireStorageUris: ", "fail")
                        navigateToCreateMissionCompletionFragment(false, vm.mission)
                    }
                }.disposedBy(disposeBag)
        }

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

    private val btnConfirmOnClickListener = View.OnClickListener {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm to create Mission?")
            .setMessage("Credits will be deducted from your wallet. Agents will be able to see your mission.")
            .setPositiveButton("Confirm") { _, _ ->
                updateDB()
            }
            .setNegativeButton("Back", null)
            .show()
    }

    @SuppressLint("CheckResult")
    private fun updateDB() {
        // save user input to model
        vm.mission.price = vm.price.value!!
        vm.mission.status = MissionStatusEnum.OPEN
        vm.mission.employer = UserData.currentUserData.email
        vm.mission.createdAt = System.currentTimeMillis()
        vm.mission.updatedAt = System.currentTimeMillis()

        // Update user balance and suspend amount
        UserData.currentUserData.onHold += binding.amount.text.toString().toInt()
        UserData.currentUserData.balance -= binding.amount.text.toString().toInt()
        vm.updateSuspendAmount(
            UserData.currentUserData,
            { // updateSuspendAmount success
                addMissionToDB()
            },
            { // updateSuspendAmount fail
                navigateToCreateMissionCompletionFragment(false, vm.mission)
            }
        )
    }

    @SuppressLint("CheckResult")
    private fun addMissionToDB() {
        vm.addMissionToDB()
            .subscribe ({
                Log.d("addMissionToDB: ", "success")
                vm.uploadMissionPhotosToDB()
            }, {
                Log.d("addMissionToDB: ", "fail: $it")
                navigateToCreateMissionCompletionFragment(false, vm.mission)
            }).disposedBy(disposeBag)
    }

    private fun navigateToCreateMissionCompletionFragment(isCreateMissionSuccess: Boolean, mission: Mission) {
        val action =
            CreateMissionPriceFragmentDirections
                .actionCreateMissionPriceFragmentToCreateMissionCompletionFragment(
                    isCreateMissionSuccess,
                    mission
                )
        findNavController().navigate(action)
    }

    private fun navigateToHomeFragment() {
        val action = CreateMissionPriceFragmentDirections
            .actionCreateMissionPriceFragmentToHomeFragment()
        findNavController().navigate(action)
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