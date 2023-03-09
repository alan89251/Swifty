package com.team2.handiwork.fragments.mission

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.team2.handiwork.AppConst
import com.team2.handiwork.R
import com.team2.handiwork.adapter.MissionPhotosViewRecyclerViewAdapter
import com.team2.handiwork.databinding.DialogConfrimBinding
import com.team2.handiwork.databinding.FragmentAgentMissionDetailsBinding
import com.team2.handiwork.enums.MissionStatusEnum
import com.team2.handiwork.models.ConfirmDialog
import com.team2.handiwork.models.Mission
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.utilities.Ext.Companion.disposedBy
import com.team2.handiwork.utilities.Utility
import com.team2.handiwork.viewModel.mission.FragmentAgentMissionDetailsViewModel


class AgentMissionDetailsFragment : Fragment() {
    val vm = FragmentAgentMissionDetailsViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAgentMissionDetailsBinding.inflate(inflater, container, false)
        binding.vm = vm
        binding.lifecycleOwner = this

        val mission = requireArguments().getSerializable("mission")
        vm.mission.value = mission as Mission
        binding.missionContent.mission = mission
        val sp = PreferenceManager.getDefaultSharedPreferences(this.requireContext())
        vm.email.value = sp.getString(AppConst.EMAIL, "").toString()

        vm.mission.observe(viewLifecycleOwner) {
            // update button visibility
            vm.updateButtonVisibility()

            // todo update once
            vm.updatePeriod()

            val backgroundDrawable = GradientDrawable()
            backgroundDrawable.shape = GradientDrawable.RECTANGLE
            val cornerRadius = 20.0f
            backgroundDrawable.cornerRadius = cornerRadius
            backgroundDrawable.setColor(
                ContextCompat.getColor(
                    requireContext(), Utility.convertStatusColor(mission.status)
                )
            )

            binding.missionStatus.cvBackground.background = backgroundDrawable

            // set photo adapter
            if (binding.missionContent.rvPhotos.adapter == null) {

                binding.missionContent.rvPhotos.adapter =
                    MissionPhotosViewRecyclerViewAdapter(it.missionPhotoUris)
                binding.missionContent.rvPhotos.layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            }
            binding.missionStatus.tvStatus.text = when (vm.missionStatusDisplay.value!!.value) {
                // handle components show or hidden
                MissionStatusEnum.OPEN.value -> getString(R.string.status_open)
                MissionStatusEnum.PENDING_ACCEPTANCE.value -> getString(R.string.status_pending)
                MissionStatusEnum.CONFIRMED.value -> getString(R.string.status_confirmed)
                MissionStatusEnum.ENROLLED.value -> getString(R.string.status_enrolled)
                MissionStatusEnum.COMPLETED.value -> getString(R.string.status_completed)
                MissionStatusEnum.CANCELLED.value -> getString(R.string.status_cancel)
                else -> ""
            }
        }

        binding.btnEnroll.setOnClickListener {
            createEnrollMissionDialog()
        }

        vm.enrolled.observe(viewLifecycleOwner) {
            binding.missionStatus.btnCancelOpen.visibility = View.GONE
            if (!it) return@observe
            vm.service.enrolledMission(vm.mission.value!!, UserData.currentUserData.email)
                .subscribe { m ->
                    vm.mission.value = m
                }.disposedBy(vm.disposeBag)
        }

        val bundle: Bundle = Bundle()

        bundle.putSerializable("mission", vm.mission.value)
        bundle.putBoolean("isAgent", true)

        binding.btnChat.setOnClickListener {
            findNavController().navigate(
                R.id.action_agentMissionDetailFragment_to_chatFragment,
                bundle
            )
        }

        binding.missionStatus.btnCancelOpen.setOnClickListener {
            if (vm.mission.value!!.before48Hour) {
                createWithdrawBefore48HoursMissionDialog()
            } else {
                createWithdrawWithin48HoursMissionDialog()
            }
        }

        vm.withdrawBefore48Hours.observe(viewLifecycleOwner) {
            if (!it) return@observe
            vm.service.cancelMissionBefore48HoursByAgent(
                vm.mission.value!!,
                UserData.currentUserData,
            )
                .subscribe { missionUser ->
                    UserData.currentUserData = missionUser.user
                    vm.mission.value = missionUser.mission
                }.disposedBy(vm.disposeBag)
        }

        vm.withdrawWithin48Hours.observe(viewLifecycleOwner) {
            if (!it) return@observe
            vm.service.cancelMissionWithin48HoursByAgent(
                vm.mission.value!!,
                UserData.currentUserData
            )
                .subscribe { missionUser ->
                    UserData.currentUserData = missionUser.user
                    vm.mission.value = missionUser.mission
                }.disposedBy(vm.disposeBag)
        }

        binding.btnRevoke.setOnClickListener {
            revokeMissionDialog()
        }

        vm.revoke.observe(viewLifecycleOwner) {
            if (!it) return@observe
            vm.service.revokeMission(
                vm.mission.value!!,
                UserData.currentUserData.email,
            ).subscribe { m ->
                vm.mission.value = m
            }.disposedBy(vm.disposeBag)
        }

        binding.btnCompleted.setOnClickListener {
            createCompleteMissionDialog()
        }

        vm.finished.observe(viewLifecycleOwner) {
            if (!it) return@observe
            vm.service.finishedMission(vm.mission.value!!).subscribe { m ->
                vm.mission.value = m
            }.disposedBy(vm.disposeBag)
        }

        return binding.root
    }

    override fun onDestroy() {
        vm.disposeBag.dispose()
        super.onDestroy()
    }

    private fun createDialogBuilder(
        title: String, content: String, confirmButtonText: String
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

    private fun createWithdrawBefore48HoursMissionDialog() {
        val dialog = createDialogBuilder(
            getString(R.string.withdraw_mission_header),
            getString(R.string.withdraw_mission_warning_content),
            getString(R.string.confirm_withdraw)
        )

        dialog.binding.btnConfirm.setOnClickListener {
            dialog.builder.dismiss()
            vm.withdrawBefore48Hours.value = true
        }

        dialog.binding.btnBack.setOnClickListener {
            dialog.builder.dismiss()
            vm.withdrawBefore48Hours.value = false
        }
        dialog.builder.show()
    }

    private fun createWithdrawWithin48HoursMissionDialog() {
        val dialog = createDialogBuilder(
            getString(R.string.withdraw_mission_header),
            getString(R.string.withdraw_mission_content),
            getString(R.string.confirm_withdraw)
        )

        dialog.binding.btnConfirm.setOnClickListener {
            dialog.builder.dismiss()
            vm.withdrawWithin48Hours.value = true
        }

        dialog.binding.btnBack.setOnClickListener {
            dialog.builder.dismiss()
            vm.withdrawWithin48Hours.value = false
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
            vm.finished.value = true
            dialog.builder.dismiss()
        }

        dialog.binding.btnBack.setOnClickListener {
            dialog.builder.dismiss()
            vm.finished.value = false
        }
        dialog.builder.show()
    }

    private fun revokeMissionDialog() {
        val dialog = createDialogBuilder(
            getString(R.string.revoke_mission_header),
            getString(R.string.revoke_mission_content),
            getString(R.string.confirm_revoke)
        )

        dialog.binding.btnConfirm.setOnClickListener {
            vm.revoke.value = true
            dialog.builder.dismiss()
        }

        dialog.binding.btnBack.setOnClickListener {
            vm.revoke.value = false
            dialog.builder.dismiss()
        }
        dialog.builder.show()
    }


}