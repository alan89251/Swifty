package com.team2.handiwork.fragments.mission

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.team2.handiwork.R
import com.team2.handiwork.databinding.FragmentEmployerMissionDetailsBinding
import com.team2.handiwork.enums.MissionStatusEnum
import com.team2.handiwork.fragments.LeaveReviewDialogFragment
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.User
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.viewModel.mission.FragmentEmployerMissionDetailsViewModel

private const val ARG_MISSION = "mission"

class EmployerMissionDetailsFragment : Fragment() {
    private lateinit var binding: FragmentEmployerMissionDetailsBinding
    private lateinit var vm: FragmentEmployerMissionDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = FragmentEmployerMissionDetailsViewModel()
        vm.alertBuilder = AlertDialog.Builder(requireContext())

        arguments?.let {
            vm.mission.value = it.getSerializable(ARG_MISSION) as Mission
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmployerMissionDetailsBinding.inflate(inflater, container, false)
        binding.vm = vm
        binding.lifecycleOwner = this
        vm.onOpenChat = ::navigateToChatFragment
        vm.onViewProfile = ::navigateToViewProfileFragment
        vm.onAcceptedMission = ::navigateToAcceptedMissionCompletionFragment
        vm.onLeaveReview = ::leaveReview
        vm.refreshScreen()

        vm.isAgentReviewed.value = vm.mission.value!!.isReviewed

        // set a listener to receive result sending back from the leave review dialog fragment
        childFragmentManager.setFragmentResultListener(
            LeaveReviewDialogFragment.RESULT_LISTENER_KEY,
            this
        ) { _, bundle ->
            vm.isAgentReviewed.value =
                bundle.getBoolean(LeaveReviewDialogFragment.RESULT_ARG_IS_USER_REVIEWED)
        }

        vm.agentListAdapter.observe(viewLifecycleOwner) { binding.missionAgentOpen.rvAgents.adapter = it }
        vm.selectedAgent.observe(viewLifecycleOwner, vm::getCommentsForSelectedAgentFromDB)

        vm.iconImageUrl.observe(viewLifecycleOwner) { url ->
            var imageView: ShapeableImageView?
            when (vm.mission.value!!.status) {
                MissionStatusEnum.COMPLETED -> {
                    imageView = binding.missionAgentCompleted.layoutAgentCompleted.ibtnUser
                    loadImage(url, imageView)
                }
                MissionStatusEnum.PENDING_ACCEPTANCE -> {
                    imageView = binding.missionAgentPending.layoutAgentPending.ibtnUser
                    loadImage(url, imageView)
                }
                MissionStatusEnum.CONFIRMED -> {
                    imageView = binding.missionAgentConfirmed.layoutAgentConfirmed.ibtnUser
                    loadImage(url, imageView)
                }
                MissionStatusEnum.DISPUTED -> {
                    imageView = binding.missionAgentDisputed.layoutAgentDisputed.ibtnUser
                    loadImage(url, imageView)
                }
                else -> {

                }
            }
        }


        binding.missionAgentPending.btnReject.setOnClickListener {
            showCustomDialog()
        }

        binding.missionAgentConfirmed.btnDispute.setOnClickListener {
            showCustomDialog()
        }

        return binding.root
    }

    private fun showCustomDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_dispute_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        var state1 = false
        var state2 = false
        var state3 = false
        var state4 = false
        val attendanceBtn = dialog.findViewById<Button>(R.id.btn_attendance)
        attendanceBtn.setOnClickListener {
            state1 = !state1
            switchButtonStyle(attendanceBtn, state1)
            modifyReason(attendanceBtn.text.toString(), state1)
        }

        val proBtn = dialog.findViewById<Button>(R.id.btn_professionalism)
        proBtn.setOnClickListener {
            state2 = !state2
            switchButtonStyle(proBtn, state2)
            modifyReason(proBtn.text.toString(), state2)
        }

        val perBtn = dialog.findViewById<Button>(R.id.btn_performance)
        perBtn.setOnClickListener {
            state3 = !state3
            switchButtonStyle(perBtn, state3)
            modifyReason(perBtn.text.toString(), state3)
        }

        val otherBtn = dialog.findViewById<Button>(R.id.btn_others)
        otherBtn.setOnClickListener {
            state4 = !state4
            switchButtonStyle(otherBtn, state4)
            modifyReason(otherBtn.text.toString(), state4)
        }


        val raiseBtn = dialog.findViewById<TextView>(R.id.dialog_raise_dispute)
        raiseBtn.setOnClickListener {
            if (vm.disputeReasons.isNotEmpty()) {
                vm.setDisputeReasons()
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Please select at least one reason", Toast.LENGTH_SHORT).show()
            }
        }

        val backBtn = dialog.findViewById<TextView>(R.id.dialog_dispute_back)
        backBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun switchButtonStyle(btn: Button, state: Boolean) {
        if (state) {
            btn.background.setTint(ContextCompat.getColor(requireContext(), R.color.very_dark_blue_100))
            btn.setTextColor(resources.getColor(R.color.white_100))
        } else {
            btn.background.setTint(ContextCompat.getColor(requireContext(), R.color.light_grayish_blue_100))
            btn.setTextColor(resources.getColor(R.color.very_dark_blue_100))
        }
    }

    private fun modifyReason(value: String, isAdd: Boolean) {
        if (isAdd) {
            vm.addReason(value)
        } else {
            vm.removeReason(value)
        }
    }

    private fun loadImage(string: String, shapeableImageView: ShapeableImageView) {
        Glide.with(this)
            .load(string)
            .into(shapeableImageView)
    }

    private fun leaveReview() {
        val bundle = Bundle()
        bundle.putBoolean(LeaveReviewDialogFragment.ARG_IS_REVIEWED_FOR_EMPLOYER, false)
        bundle.putSerializable(LeaveReviewDialogFragment.ARG_USER, vm.selectedAgent.value!!)
        bundle.putSerializable(LeaveReviewDialogFragment.ARG_MISSION, vm.mission.value!!)
        val leaveReviewDialogFragment = LeaveReviewDialogFragment()
        leaveReviewDialogFragment.arguments = bundle
        leaveReviewDialogFragment.show(
            childFragmentManager,
            LeaveReviewDialogFragment.TAG
        )
    }

    private fun navigateToAcceptedMissionCompletionFragment(result: Boolean) {
        val action =
            EmployerMissionDetailsFragmentDirections.actionEmployerMissionDetailsFragmentToAcceptedMissionCompletionFragment(
                result,
                vm.selectedAgent.value!!,
                vm.mission.value!!
            )
        findNavController().navigate(action)
    }

    private fun navigateToChatFragment(agent: User) {
        val bundle: Bundle = Bundle()

        bundle.putSerializable("mission", vm.mission.value!!)
        bundle.putSerializable("agent", agent)
        bundle.putSerializable("toEmail", agent.email)
        bundle.putSerializable("clientImgUrl", UserData.currentUserData.imageURi)

        findNavController().navigate(
            R.id.action_employerMissionDetailsFragment_to_chatFragment,
            bundle
        )
    }

    private fun navigateToViewProfileFragment(agent: User) {
        val bundle: Bundle = Bundle()
        bundle.putSerializable("targetEmail", agent.email)
        findNavController().navigate(
            R.id.action_employerMissionDetailsFragment_to_viewProfileFragment,
            bundle
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(mission: Mission) =
            EmployerMissionDetailsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_MISSION, mission)
                }
            }
    }
}