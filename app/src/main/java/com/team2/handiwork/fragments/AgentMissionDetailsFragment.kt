package com.team2.handiwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.team2.handiwork.R
import com.team2.handiwork.databinding.DialogConfrimBinding
import com.team2.handiwork.databinding.FragmentAgentMissionDetailsBinding
import com.team2.handiwork.enum.MissionStatusEnum
import com.team2.handiwork.models.ConfirmDialog
import com.team2.handiwork.viewModel.FragmentAgentMissionDetailsViewModel


class AgentMissionDetailsFragment : Fragment() {
    val vm = FragmentAgentMissionDetailsViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAgentMissionDetailsBinding.inflate(inflater, container, false)
        binding.vm = vm
        binding.lifecycleOwner = this

        binding.btnEnroll.setOnClickListener {
            createEnrollMissionDialog()
        }

        vm.enrolled.observe(viewLifecycleOwner) {
            binding.missionStatus.btnCancelOpen.visibility = View.GONE
            if (!it) return@observe
            // todo confirm enrolled status
            vm.mission.value!!.status = MissionStatusEnum.PENDING_ACCEPTANCE.value
        }

        binding.missionStatus.btnCancelOpen.setOnClickListener {
            createWithdrawWarnMissionDialog()
        }

        vm.withdrawWarn.observe(viewLifecycleOwner) {
            if (!it) return@observe
            createWithdrawMissionDialog()
        }

        vm.withdraw.observe(viewLifecycleOwner) {
            if (!it) return@observe
            vm.mission.value!!.status = MissionStatusEnum.CANCELLED.value
        }

        binding.btnCompleted.setOnClickListener {
            createCompleteMissionDialog()
        }

        vm.finished.observe(viewLifecycleOwner) {
            if (!it) return@observe
            vm.mission.value!!.status = MissionStatusEnum.COMPLETED.value
        }

        binding.btnComm.setOnClickListener {
            // todo communicate with employer
        }

        return binding.root
    }

    private fun createDialogBuilder(
        title: String,
        content: String,
        confirmButtonText: String
    ): ConfirmDialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this.requireContext())
        val binding = DialogConfrimBinding.inflate(layoutInflater)
        builder.setView(binding.root)
        builder.setTitle(title)
        builder.setMessage(content)
        binding.btnConfirm.text = confirmButtonText
        return ConfirmDialog(builder.create(), binding)
    }


    private fun createEnrollMissionDialog() {
        val dialog = createDialogBuilder(
            getString(R.string.enroll_mission_header),
            getString(R.string.enroll_mission_content),
            getString(R.string.confirm_enrollment)
        )

        dialog.binding.btnConfirm.setOnClickListener {
            dialog.builder.dismiss()
            vm.enrolled.value = true
        }

        dialog.binding.btnBack.setOnClickListener {
            dialog.builder.dismiss()
            vm.enrolled.value = false
        }
        dialog.builder.show()
    }

    private fun createWithdrawWarnMissionDialog() {
        val dialog = createDialogBuilder(
            getString(R.string.withdraw_mission_header),
            getString(R.string.withdraw_mission_warning_content),
            getString(R.string.confirm_withdraw)
        )

        dialog.binding.btnConfirm.setOnClickListener {
            dialog.builder.dismiss()
            vm.withdrawWarn.value = true
        }

        dialog.binding.btnBack.setOnClickListener {
            dialog.builder.dismiss()
            vm.withdrawWarn.value = false
        }
        dialog.builder.show()
    }

    private fun createWithdrawMissionDialog() {
        val dialog = createDialogBuilder(
            getString(R.string.withdraw_mission_header),
            getString(R.string.withdraw_mission_content),
            getString(R.string.confirm_withdraw)
        )

        dialog.binding.btnConfirm.setOnClickListener {
            dialog.builder.dismiss()
            vm.withdraw.value = true
        }

        dialog.binding.btnBack.setOnClickListener {
            dialog.builder.dismiss()
            vm.withdraw.value = false
        }
        dialog.builder.show()
    }


    private fun createCompleteMissionDialog() {
        val dialog = createDialogBuilder(
            getString(R.string.confirm_mission_header),
            getString(R.string.confirm_mission_content),
            getString(R.string.confirm_complete)
        )

        dialog.binding.btnConfirm.setOnClickListener {
            // todo implement in logic control
            dialog.builder.dismiss()
        }

        dialog.binding.btnBack.setOnClickListener {
            dialog.builder.dismiss()
        }
        dialog.builder.show()
    }


}