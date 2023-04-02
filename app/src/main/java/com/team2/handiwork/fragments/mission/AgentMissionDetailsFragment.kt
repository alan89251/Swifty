package com.team2.handiwork.fragments.mission

import android.content.SharedPreferences
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.team2.handiwork.AppConst
import com.team2.handiwork.R
import com.team2.handiwork.adapter.MissionPhotosViewRecyclerViewAdapter
import com.team2.handiwork.base.fragment.DisposeFragment
import com.team2.handiwork.databinding.DialogConfrimBinding
import com.team2.handiwork.databinding.FragmentAgentMissionDetailsBinding
import com.team2.handiwork.enums.MissionStatusEnum
import com.team2.handiwork.firebase.Storage
import com.team2.handiwork.fragments.LeaveReviewDialogFragment
import com.team2.handiwork.models.ConfirmDialog
import com.team2.handiwork.models.Mission
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.utilities.Ext.Companion.disposedBy
import com.team2.handiwork.viewModel.mission.FragmentAgentMissionDetailsViewModel


class AgentMissionDetailsFragment : DisposeFragment() {
    val vm = FragmentAgentMissionDetailsViewModel()
    private lateinit var binding: FragmentAgentMissionDetailsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAgentMissionDetailsBinding.inflate(inflater, container, false)
        binding.vm = vm
        binding.lifecycleOwner = this

        val mission = requireArguments().getSerializable("mission")
        vm.mission.value = mission as Mission
        vm.updatePeriod()

        val sp = PreferenceManager.getDefaultSharedPreferences(this.requireContext())
        vm.email.value = sp.getString(AppConst.EMAIL, "").toString()

        binding.ibtnEmployer.setOnClickListener {
            val bundle: Bundle = Bundle()
            bundle.putString("targetEmail", vm.mission.value!!.employer)
            bundle.putString("targetIconURL", vm.targetImgUrl.value)
            findNavController().navigate(
                R.id.action_agentMissionDetailFragment_to_viewProfileFragment,
                bundle
            )
        }

        loadEmployerIcon(mission.employer)
        vm.getComments(mission.employer).subscribe {
            vm.rating.value = vm.calculateRating(it)
            binding.tvRating.text = it.count().toString()
        }.disposedBy(disposeBag)

        vm.mission.observe(viewLifecycleOwner) {
            // update button visibility
            vm.updateButtonVisibility()


            val backgroundDrawable = GradientDrawable()
            backgroundDrawable.shape = GradientDrawable.RECTANGLE
            val cornerRadius = 20.0f
            backgroundDrawable.cornerRadius = cornerRadius
            backgroundDrawable.setColor(
                ContextCompat.getColor(
                    requireContext(),
                    vm.convertStatusColor(mission.status)
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
            // todo should handle by xml
            val statusStrId = vm.getMissionStatusString(vm.missionStatusDisplay.value!!)
            binding.missionStatus.tvStatus.text = getString(statusStrId)

            if (vm.missionStatusDisplay.value!! == MissionStatusEnum.DISPUTED) {
                vm.disputedReasonsVisibility.value = View.VISIBLE
                val listView = binding.disputedReasonSection.reasonList
                val bulletPoint = "\u2022"
                val adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.disputed_reason_list_item,
                    vm.mission.value!!.disputeReasons.map {
                       "$bulletPoint  $it"
                    }
                )
                listView.adapter = adapter
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
                }.disposedBy(disposeBag)
        }

        val bundle: Bundle = Bundle()
        bundle.putSerializable("mission", vm.mission.value)
        bundle.putSerializable("agent", UserData.currentUserData)
        bundle.putSerializable("toEmail", vm.mission.value!!.employer)

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
                }.disposedBy(disposeBag)
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
                }.disposedBy(disposeBag)
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
            }.disposedBy(disposeBag)
        }

        binding.btnCompleted.setOnClickListener {
            createCompleteMissionDialog()
        }

        vm.finished.observe(viewLifecycleOwner) {
            if (!it) return@observe
            vm.service.finishedMission(vm.mission.value!!).subscribe { m ->
                vm.mission.value = m
            }.disposedBy(disposeBag)
        }

        binding.btnLeaveReview.setOnClickListener {
            createLeaveReviewDialog()
        }

        return binding.root
    }

    private val onIconLoaded: (mission: String) -> Unit = { imgUrl ->
        Glide.with(this)
            .load(imgUrl)
            .into(binding.ibtnEmployer)
        vm.setTargetImgURL(imgUrl)
    }

    private val onIconLoadFailed: () -> Unit = {
        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val editor: SharedPreferences.Editor = pref.edit()
        editor.putString(AppConst.PREF_TARGET_ICON_URL, "")
        editor.apply()
    }

    private fun loadEmployerIcon(employer: String) {
        Storage().getImgUrl("User/$employer", onIconLoaded, onIconLoadFailed)
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

    private fun createLeaveReviewDialog() {
        // set a listener to receive result sending back from the leave review dialog fragment
        childFragmentManager.setFragmentResultListener(
            LeaveReviewDialogFragment.RESULT_LISTENER_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            vm.leaveReviewButtonVisibility.value =
                if (bundle.getBoolean(LeaveReviewDialogFragment.RESULT_ARG_IS_USER_REVIEWED))
                    View.GONE
                else View.VISIBLE
        }

        val bundle = Bundle()
        bundle.putBoolean(LeaveReviewDialogFragment.ARG_IS_REVIEWED_FOR_EMPLOYER, true)
        bundle.putSerializable(LeaveReviewDialogFragment.ARG_MISSION, vm.mission.value!!)
        val leaveReviewDialogFragment = LeaveReviewDialogFragment()
        leaveReviewDialogFragment.arguments = bundle
        leaveReviewDialogFragment.show(childFragmentManager, LeaveReviewDialogFragment.TAG)
    }


}