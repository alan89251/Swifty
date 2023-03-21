package com.team2.handiwork.fragments.mission

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.team2.handiwork.R
import com.team2.handiwork.adapter.Agent1RecyclerViewAdapter
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

        arguments?.let {
            vm.mission = it.getSerializable(ARG_MISSION) as Mission
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmployerMissionDetailsBinding.inflate(inflater, container, false)
        binding.vm = vm
        binding.lifecycleOwner = this

        updateMissionContent()
        refreshScreen()

        binding.missionAgentCompleted.btnLeaveReview.setOnClickListener(btnLeaveReviewOnClickListener)
        vm.isAgentReviewed.value = vm.mission.isReviewed
        vm.isAgentReviewed.observe(requireActivity()) {
            binding.missionAgentCompleted.btnLeaveReview.visibility =
                if (vm.mission.isReviewed) View.INVISIBLE else View.VISIBLE
        }

        // set a listener to receive result sending back from the leave review dialog fragment
        childFragmentManager.setFragmentResultListener(
            LeaveReviewDialogFragment.RESULT_LISTENER_KEY,
            this) { _, bundle ->
            vm.isAgentReviewed.value = bundle.getBoolean(LeaveReviewDialogFragment.RESULT_ARG_IS_USER_REVIEWED)
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun refreshScreen() {
        configLayout()
        updateUIContents()
    }

    private fun configLayout() {
        binding.missionContent.root.visibility = View.VISIBLE
        binding.layoutHeaderOpen.root.visibility = View.INVISIBLE
        binding.layoutHeaderConfirmed.root.visibility = View.INVISIBLE
        binding.layoutHeaderPending.root.visibility = View.INVISIBLE
        binding.layoutHeaderCancelled.root.visibility = View.INVISIBLE
        binding.layoutHeaderDisputed.root.visibility = View.INVISIBLE
        binding.layoutHeaderCompleted.root.visibility = View.INVISIBLE
        binding.missionAgentOpen.root.visibility = View.INVISIBLE
        binding.missionAgentConfirmed.root.visibility = View.INVISIBLE
        binding.missionAgentPending.root.visibility = View.INVISIBLE
        binding.missionAgentCancelled.root.visibility = View.INVISIBLE
        binding.missionAgentDisputed.root.visibility = View.INVISIBLE
        binding.missionAgentCompleted.root.visibility = View.INVISIBLE

        if (vm.mission.status == MissionStatusEnum.OPEN) {
            configLayoutToOpen()
        } else if (vm.mission.status == MissionStatusEnum.CONFIRMED) {
            configLayoutToConfirmed()
        } else if (vm.mission.status == MissionStatusEnum.PENDING_ACCEPTANCE) {
            configLayoutToPendingAcceptance()
        } else if (vm.mission.status == MissionStatusEnum.CANCELLED) {
            configLayoutToCancelled()
        } else if (vm.mission.status == MissionStatusEnum.DISPUTED) {
            configLayoutToDisputed()
        } else { // COMPLETED
            configLayoutToCompleted()
        }
    }

    private fun configLayoutToOpen() {
        // set the layout visibility for mission status OPEN
        binding.layoutHeaderOpen.root.visibility = View.VISIBLE
        binding.missionAgentOpen.root.visibility = View.VISIBLE
    }

    private fun configLayoutToConfirmed() {
        // set the layout visibility for mission status CONFIRMED
        binding.layoutHeaderConfirmed.root.visibility = View.VISIBLE
        binding.missionAgentConfirmed.root.visibility = View.VISIBLE
    }

    private fun configLayoutToPendingAcceptance() {
        // set the layout visibility for mission status PENDING ACCEPTANCE
        binding.layoutHeaderPending.root.visibility = View.VISIBLE
        binding.missionAgentPending.root.visibility = View.VISIBLE
    }

    private fun configLayoutToCancelled() {
        // set the layout visibility for mission status CANCELLED
        binding.layoutHeaderCancelled.root.visibility = View.VISIBLE
        binding.missionAgentCancelled.root.visibility = View.VISIBLE
    }

    private fun configLayoutToDisputed() {
        // set the layout visibility for mission status DISPUTED
        binding.layoutHeaderDisputed.root.visibility = View.VISIBLE
        binding.missionAgentDisputed.root.visibility = View.VISIBLE
    }

    private fun configLayoutToCompleted() {
        // set the layout visibility for mission status COMPLETED
        binding.layoutHeaderCompleted.root.visibility = View.VISIBLE
        binding.missionAgentCompleted.root.visibility = View.VISIBLE
    }

    private fun updateUIContents() {
        if (vm.mission.status == MissionStatusEnum.OPEN) {
            updateUIContentsToOpen()
        } else if (vm.mission.status == MissionStatusEnum.CONFIRMED) {
            updateUIContentsToConfirmed()
        } else if (vm.mission.status == MissionStatusEnum.PENDING_ACCEPTANCE) {
            updateUIContentsToPendingAcceptance()
        } else if (vm.mission.status == MissionStatusEnum.CANCELLED) {
            updateUIContentsToCancelled()
        } else if (vm.mission.status == MissionStatusEnum.DISPUTED){
            updateUIContentsToDisputed()
        } else { // COMPLETED
            updateUIContentsToCompleted()
        }
    }

    private fun updateUIContentsToOpen() {
        binding.layoutHeaderOpen.tvCreditsOpen.text = vm.mission.price.toString()
        binding.layoutHeaderOpen.btnCancelOpen.setOnClickListener(btnCancelOpenOnClickListener)
        getAgentsFromDB()
    }

    @SuppressLint("CheckResult")
    private fun getAgentsFromDB() {
        if (vm.mission.enrollments.isNotEmpty()) {
            vm.getAgentsByEmails(vm.mission.enrollments)
                .subscribe {
                    updateAgentList(it)
                }
        }
    }

    @SuppressLint("CheckResult")
    private fun updateAgentList(agents: List<User>) {
        val adapter = Agent1RecyclerViewAdapter(agents)
        binding.missionAgentOpen.rvAgents.adapter = adapter

        adapter.chatAgent.subscribe {
            navigateToChatFragment(it)
        }

        adapter.selectedAgent.subscribe {
            AlertDialog.Builder(requireContext())
                .setTitle("Select ${it.firstName} ${it.lastName} as your agent?")
                .setMessage(resources.getString(R.string.select_agent_alert_msg))
                .setPositiveButton("Confirm Mission") { _, _ ->
                    vm.selectAgent(
                        vm.mission,
                        it.email
                    ) { updateMissionResult ->
                        vm.mission = updateMissionResult
                        refreshScreen()
                    }
                }
                .setNegativeButton("Back", null)
                .show()
        }
    }

    private fun updateUIContentsToConfirmed() {
        binding.layoutHeaderConfirmed.tvCreditsConfirmed.text = vm.mission.price.toString()
        binding.layoutHeaderConfirmed.btnCancelConfirmed.visibility =
            if (vm.isCurrentDateAfterMissionStartDate()) View.INVISIBLE else View.VISIBLE
        binding.layoutHeaderConfirmed.btnCancelConfirmed.setOnClickListener(
            btnCancelConfirmedOnClickListener
        )

        if (vm.mission.selectedAgent != "") {
            vm.selectedAgent.observe(requireActivity(), ::updateSelectedAgentForMissionConfirmed)
            // result assign to selectedAgent and trigger updateSelectedAgentForMissionConfirmed
            vm.getSelectedAgentFromDB()
        }

        // set the accept mission button
        binding.missionAgentConfirmed.btnAccept.visibility =
            if (vm.isCurrentDateAfterMissionStartDate()) View.VISIBLE else View.INVISIBLE
        binding.missionAgentConfirmed.btnAccept.setOnClickListener(btnAcceptOnClickListener)

        // set the dispute mission button
        binding.missionAgentConfirmed.btnDispute.visibility =
            if (vm.isCurrentDateAfterMissionEndDate()) View.VISIBLE else View.INVISIBLE
        binding.missionAgentConfirmed.btnDispute.setOnClickListener(btnDisputeOnClickListener)
    }

    private fun updateUIContentsToCancelled() {
        binding.layoutHeaderCancelled.tvCreditsCancelled.text = vm.mission.price.toString()

        if (vm.mission.selectedAgent != "") {
            vm.selectedAgent.observe(requireActivity(), ::updateSelectedAgentForMissionCancelled)
            // result assign to selectedAgent and trigger updateSelectedAgentForMissionCancelled
            vm.getSelectedAgentFromDB()
        }
    }

    private fun updateUIContentsToDisputed() {
        binding.layoutHeaderDisputed.tvCreditsDisputed.text = vm.mission.price.toString()

        if (vm.mission.selectedAgent != "") {
            vm.selectedAgent.observe(requireActivity(), ::updateSelectedAgentForMissionDisputed)
            // result assign to selectedAgent and trigger updateSelectedAgentForMissionDisputed
            vm.getSelectedAgentFromDB()
        }
    }

    private fun updateUIContentsToCompleted() {
        binding.layoutHeaderCompleted.tvCreditsCompleted.text = vm.mission.price.toString()

        if (vm.mission.selectedAgent != "") {
            vm.selectedAgent.observe(requireActivity(), ::updateSelectedAgentForMissionCompleted)
            // result assign to selectedAgent and trigger updateSelectedAgentForMissionCompleted
            vm.getSelectedAgentFromDB()
        }
        binding.missionAgentCompleted.btnLeaveReview.isEnabled = true
    }

    private val btnLeaveReviewOnClickListener = View.OnClickListener {
        val bundle = Bundle()
        bundle.putBoolean(LeaveReviewDialogFragment.ARG_IS_REVIEWED_FOR_EMPLOYER, false)
        bundle.putSerializable(LeaveReviewDialogFragment.ARG_USER, vm.selectedAgent.value!!)
        bundle.putSerializable(LeaveReviewDialogFragment.ARG_MISSION, vm.mission)
        val leaveReviewDialogFragment = LeaveReviewDialogFragment()
        leaveReviewDialogFragment.arguments = bundle
        leaveReviewDialogFragment.show(
            childFragmentManager,
            LeaveReviewDialogFragment.TAG
        )
    }

    private val btnAcceptOnClickListener = View.OnClickListener {
        AlertDialog.Builder(requireContext())
            .setTitle(resources.getString(R.string.accept_mission_result_alert_title))
            .setMessage(resources.getString(R.string.accept_mission_result_alert_msg))
            .setPositiveButton("Confirm Completion") { _, _ ->
                updateMissionToCompleted()
            }
            .setNegativeButton("Back", null)
            .show()
    }

    private val btnDisputeOnClickListener = View.OnClickListener {
        AlertDialog.Builder(requireContext())
            .setTitle(resources.getString(R.string.dispute_mission_alert_title))
            .setMessage(resources.getString(R.string.dispute_mission_alert_msg))
            .setPositiveButton("Raise Dispute") { _, _ ->
                disputeMission()
            }
            .setNegativeButton("Back", null)
            .show()
    }

    @SuppressLint("CheckResult")
    private fun disputeMission() {
        vm.disputeMission {
            vm.mission = it
            refreshScreen()
        }
    }

    private fun updateSelectedAgentForMissionConfirmed(agent: User) {
        binding.missionAgentConfirmed.layoutAgentConfirmed.root.visibility = View.VISIBLE
        binding.missionAgentConfirmed.layoutAgentConfirmed.tvUsername.text =
            "${agent.firstName} ${agent.lastName}"
        binding.missionAgentConfirmed.layoutAgentConfirmed.btnComm.setOnClickListener(onChatBtnOfSelectedAgentClicked)
    }

    private fun updateSelectedAgentForMissionCancelled(agent: User) {
        binding.missionAgentCancelled.layoutAgentCancelled.root.visibility = View.VISIBLE
        binding.missionAgentCancelled.layoutAgentCancelled.tvUsername.text =
            "${agent.firstName} ${agent.lastName}"
        binding.missionAgentCancelled.layoutAgentCancelled.btnComm.setOnClickListener(onChatBtnOfSelectedAgentClicked)
    }

    private fun updateSelectedAgentForMissionDisputed(agent: User) {
        binding.missionAgentDisputed.layoutAgentDisputed.root.visibility = View.VISIBLE
        binding.missionAgentDisputed.layoutAgentDisputed.tvUsername.text =
            "${agent.firstName} ${agent.lastName}"
        binding.missionAgentDisputed.layoutAgentDisputed.btnComm.setOnClickListener(onChatBtnOfSelectedAgentClicked)
    }

    private fun updateSelectedAgentForMissionCompleted(agent: User) {
        binding.missionAgentCompleted.layoutAgentCompleted.root.visibility = View.VISIBLE
        binding.missionAgentCompleted.layoutAgentCompleted.tvUsername.text =
            "${agent.firstName} ${agent.lastName}"
        binding.missionAgentCompleted.layoutAgentCompleted.btnComm.setOnClickListener(onChatBtnOfSelectedAgentClicked)
    }

    private fun updateUIContentsToPendingAcceptance() {
        binding.layoutHeaderPending.tvCreditsPending.text = vm.mission.price.toString()

        if (vm.mission.selectedAgent != "") {
            vm.selectedAgent.observe(requireActivity(), ::updateSelectedAgentForMissionPending)
            // result assign to selectedAgent and trigger updateSelectedAgentForMissionPending
            vm.getSelectedAgentFromDB()
        }

        binding.missionAgentPending.btnAccept.setOnClickListener(btnAcceptOnClickListener)

        binding.missionAgentPending.btnReject.setOnClickListener(btnRejectOnClickListener)
    }

    private val btnRejectOnClickListener = View.OnClickListener {
        AlertDialog.Builder(requireContext())
            .setTitle(resources.getString(R.string.reject_mission_alert_title))
            .setMessage(resources.getString(R.string.reject_mission_alert_msg))
            .setPositiveButton("Confirm Reject") { _, _ ->
                rejectMission()
            }
            .setNegativeButton("Back", null)
            .show()
    }

    @SuppressLint("CheckResult")
    private fun rejectMission() {
        vm.rejectMission {
            vm.mission = it
            refreshScreen()
        }
    }

    private fun updateSelectedAgentForMissionPending(agent: User) {
        binding.missionAgentPending.layoutAgentPending.tvUsername.text =
            "${agent.firstName} ${agent.lastName}"
        binding.missionAgentPending.layoutAgentPending.btnComm.setOnClickListener(onChatBtnOfSelectedAgentClicked)
    }

    private fun updateMissionContent() {
        binding.missionContent.mission = vm.mission
        binding.missionContent.period = vm.missionDuration
        binding.missionContent.lifecycleOwner = this
    }

    @SuppressLint("CheckResult")
    private fun updateMissionToCompleted() {
        vm.completeMission(vm.mission) {
            vm.mission = it
            navigateToAcceptedMissionCompletionFragment(true)
            // release the suspend amount of the employer for this mission
            //updateEmployerSuspendAmount()
        }
    }

    // release the suspend amount of the employer for this mission
    @SuppressLint("CheckResult")
    private fun updateEmployerSuspendAmount() {
        UserData.currentUserData.onHold -= vm.mission.price.toInt()
        vm.updateUser(UserData.currentUserData) {
            // add the credit of this mission to the balance of the agent
            updateAgentBalance()
        }
    }

    // add the credit of this mission to the balance of the agent
    @SuppressLint("CheckResult")
    private fun updateAgentBalance() {
        vm.selectedAgent.value!!.balance += vm.mission.price.toInt()
        vm.updateUser(vm.selectedAgent.value!!) {
            navigateToAcceptedMissionCompletionFragment(true)
        }
    }

    private fun navigateToAcceptedMissionCompletionFragment(result: Boolean) {
        val action =
            EmployerMissionDetailsFragmentDirections.actionEmployerMissionDetailsFragmentToAcceptedMissionCompletionFragment(
                result,
                vm.selectedAgent.value!!,
                vm.mission
            )
        findNavController().navigate(action)
    }

    private val btnCancelOpenOnClickListener = View.OnClickListener {
        AlertDialog.Builder(requireContext())
            .setTitle(resources.getString(R.string.cancel_mission_alert_title))
            .setMessage(resources.getString(R.string.cancel_open_mission_alert_msg))
            .setPositiveButton("Confirm Cancel") { _, _ ->
                cancelOpenMission()
            }
            .setNegativeButton("Back", null)
            .show()
    }

    @SuppressLint("CheckResult")
    private fun cancelOpenMission() {
        vm.cancelOpenMissionByEmployer {
            vm.mission = it.mission
            UserData.currentUserData = it.user
            refreshScreen()
        }
    }

    private val btnCancelConfirmedOnClickListener = View.OnClickListener {
        if (vm.isMissionStartIn48Hours()) {
            AlertDialog.Builder(requireContext())
                .setTitle(resources.getString(R.string.cancel_mission_alert_title))
                .setMessage(resources.getString(R.string.cancel_confirmed_mission_in_48_hours_alert_msg))
                .setPositiveButton("Confirm Cancel") { _, _ ->
                    cancelConfirmedMissionStartIn48Hours()
                }
                .setNegativeButton("Back", null)
                .show()
        } else {
            AlertDialog.Builder(requireContext())
                .setTitle(resources.getString(R.string.cancel_mission_alert_title))
                .setMessage(resources.getString(R.string.cancel_confirmed_mission_before_48_hours_alert_msg))
                .setPositiveButton("Confirm Cancel") { _, _ ->
                    cancelConfirmedMissionStartBefore48Hours()
                }
                .setNegativeButton("Back", null)
                .show()
        }
    }

    @SuppressLint("CheckResult")
    private fun cancelConfirmedMissionStartIn48Hours() {
        vm.cancelMissionWithin48HoursByEmployer {
            vm.mission = it.mission
            UserData.currentUserData = it.user
            refreshScreen()
        }
    }

    @SuppressLint("CheckResult")
    private fun cancelConfirmedMissionStartBefore48Hours() {
        vm.cancelMissionBefore48HoursByEmployer {
            vm.mission = it.mission
            UserData.currentUserData = it.user
            refreshScreen()
        }
    }

    private fun navigateToHomeFragment() {
        val action =
            EmployerMissionDetailsFragmentDirections.actionEmployerMissionDetailsFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    private val onChatBtnOfSelectedAgentClicked = View.OnClickListener {
        navigateToChatFragment(vm.selectedAgent.value!!)
    }

    private fun navigateToChatFragment(agent: User) {
        val bundle: Bundle = Bundle()

        bundle.putSerializable("mission", vm.mission)
        bundle.putSerializable("agent", agent)
        bundle.putSerializable("toEmail", agent.email)

        findNavController().navigate(
            R.id.action_employerMissionDetailsFragment_to_chatFragment,
            bundle
        )
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param mission Parameter 1.
         * @return A new instance of fragment EmployerMissionDetailsFragment
         */
        @JvmStatic
        fun newInstance(mission: Mission) =
            EmployerMissionDetailsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_MISSION, mission)
                }
            }
    }
}