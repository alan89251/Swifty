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
    ): View? {
        binding = FragmentEmployerMissionDetailsBinding.inflate(inflater, container, false)
        binding.vm = vm
        binding.lifecycleOwner = this

        updateMissionContent()
        refreshScreen()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun refreshScreen() {
        configLayout()
        updateUIContents()
    }

    private fun configLayout() {
        if (vm.mission.status == MissionStatusEnum.OPEN.value) {
            configLayoutToOpen()
        } else if (vm.mission.status == MissionStatusEnum.CONFIRMED.value) {
            configLayoutToConfirmed()
        } else if (vm.mission.status == MissionStatusEnum.PENDING_ACCEPTANCE.value) {
            configLayoutToPendingAcceptance()
        } else if (vm.mission.status == MissionStatusEnum.CANCELLED.value) {
            configLayoutToCancelled()
        }
        else { // Disputed
            configLayoutToDisputed()
        }
    }

    private fun configLayoutToOpen() {
        // set the layout visibility for mission status OPEN
        binding.layoutHeaderOpen.root.visibility = View.VISIBLE
        binding.layoutHeaderConfirmed.root.visibility = View.INVISIBLE
        binding.layoutHeaderPending.root.visibility = View.INVISIBLE
        binding.layoutHeaderCancelled.root.visibility = View.INVISIBLE
        binding.layoutHeaderDisputed.root.visibility = View.INVISIBLE
        binding.missionContent.root.visibility = View.VISIBLE
        binding.missionAgentOpen.root.visibility = View.VISIBLE
        binding.missionAgentConfirmed.root.visibility = View.INVISIBLE
        binding.missionAgentPending.root.visibility = View.INVISIBLE
        binding.missionAgentCancelled.root.visibility = View.INVISIBLE
        binding.missionAgentDisputed.root.visibility = View.INVISIBLE
    }

    private fun configLayoutToConfirmed() {
        // set the layout visibility for mission status CONFIRMED
        binding.layoutHeaderOpen.root.visibility = View.INVISIBLE
        binding.layoutHeaderConfirmed.root.visibility = View.VISIBLE
        binding.layoutHeaderPending.root.visibility = View.INVISIBLE
        binding.layoutHeaderCancelled.root.visibility = View.INVISIBLE
        binding.layoutHeaderDisputed.root.visibility = View.INVISIBLE
        binding.missionContent.root.visibility = View.VISIBLE
        binding.missionAgentOpen.root.visibility = View.INVISIBLE
        binding.missionAgentConfirmed.root.visibility = View.VISIBLE
        binding.missionAgentPending.root.visibility = View.INVISIBLE
        binding.missionAgentCancelled.root.visibility = View.INVISIBLE
        binding.missionAgentDisputed.root.visibility = View.INVISIBLE
    }

    private fun configLayoutToPendingAcceptance() {
        // set the layout visibility for mission status PENDING ACCEPTANCE
        binding.layoutHeaderOpen.root.visibility = View.INVISIBLE
        binding.layoutHeaderConfirmed.root.visibility = View.INVISIBLE
        binding.layoutHeaderPending.root.visibility = View.VISIBLE
        binding.layoutHeaderCancelled.root.visibility = View.INVISIBLE
        binding.layoutHeaderDisputed.root.visibility = View.INVISIBLE
        binding.missionContent.root.visibility = View.VISIBLE
        binding.missionAgentOpen.root.visibility = View.INVISIBLE
        binding.missionAgentConfirmed.root.visibility = View.INVISIBLE
        binding.missionAgentPending.root.visibility = View.VISIBLE
        binding.missionAgentCancelled.root.visibility = View.INVISIBLE
        binding.missionAgentDisputed.root.visibility = View.INVISIBLE
    }

    private fun configLayoutToCancelled() {
        // set the layout visibility for mission status CANCELLED
        binding.layoutHeaderOpen.root.visibility = View.INVISIBLE
        binding.layoutHeaderConfirmed.root.visibility = View.INVISIBLE
        binding.layoutHeaderPending.root.visibility = View.INVISIBLE
        binding.layoutHeaderCancelled.root.visibility = View.VISIBLE
        binding.layoutHeaderDisputed.root.visibility = View.INVISIBLE
        binding.missionContent.root.visibility = View.INVISIBLE
        binding.missionAgentOpen.root.visibility = View.INVISIBLE
        binding.missionAgentConfirmed.root.visibility = View.INVISIBLE
        binding.missionAgentPending.root.visibility = View.INVISIBLE
        binding.missionAgentCancelled.root.visibility = View.VISIBLE
        binding.missionAgentDisputed.root.visibility = View.INVISIBLE
    }

    private fun configLayoutToDisputed() {
        // set the layout visibility for mission status DISPUTED
        binding.layoutHeaderOpen.root.visibility = View.INVISIBLE
        binding.layoutHeaderConfirmed.root.visibility = View.INVISIBLE
        binding.layoutHeaderPending.root.visibility = View.INVISIBLE
        binding.layoutHeaderCancelled.root.visibility = View.INVISIBLE
        binding.layoutHeaderDisputed.root.visibility = View.VISIBLE
        binding.missionContent.root.visibility = View.INVISIBLE
        binding.missionAgentOpen.root.visibility = View.INVISIBLE
        binding.missionAgentConfirmed.root.visibility = View.INVISIBLE
        binding.missionAgentPending.root.visibility = View.INVISIBLE
        binding.missionAgentCancelled.root.visibility = View.INVISIBLE
        binding.missionAgentDisputed.root.visibility = View.VISIBLE
    }

    private fun updateUIContents() {
        if (vm.mission.status == MissionStatusEnum.OPEN.value) {
            updateUIContentsToOpen()
        } else if (vm.mission.status == MissionStatusEnum.CONFIRMED.value) {
            updateUIContentsToConfirmed()
        } else if (vm.mission.status == MissionStatusEnum.PENDING_ACCEPTANCE.value) {
            updateUIContentsToPendingAcceptance()
        } else if (vm.mission.status == MissionStatusEnum.CANCELLED.value) {
            updateUIContentsToCancelled()
        }
        else { // DISPUTED
            updateUIContentsToDisputed()
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

        // todo CHARLENE set onclicklister on chatting
        adapter.chatAgent.subscribe {
            val bundle: Bundle = Bundle()

            bundle.putSerializable("mission", vm.mission)
            bundle.putBoolean("isAgent", false)

            findNavController().navigate(
                R.id.action_employerMissionDetailsFragment_to_chatFragment,
                bundle
            )
        }


        adapter.selectedAgent.subscribe {
            AlertDialog.Builder(requireContext())
                .setTitle("Select ${it.firstName} ${it.lastName} as your agent?")
                .setMessage(resources.getString(R.string.select_agent_alert_msg))
                .setPositiveButton("Confirm Mission") { _, _ ->
                    vm.selectAgent(vm.mission, it.email)
                        .subscribe { updateMissionResult ->
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
        binding.layoutHeaderConfirmed.btnCancelConfirmed.setOnClickListener(
            btnCancelConfirmedOnClickListener
        )

        if (vm.mission.selectedAgent != "") {
            vm.selectedAgent.observe(requireActivity(), ::updateSelectedAgentForMissionConfirmed)
            // result assign to selectedAgent and trigger updateSelectedAgentForMissionConfirmed
            vm.getSelectedAgentFromDB()
        }

        // set the dispute mission button
        binding.missionAgentConfirmed.btnDispute.visibility =
            if (vm.isCurrentDateAfterMissionEndDate()) View.VISIBLE else View.INVISIBLE
        binding.missionAgentConfirmed.btnDispute.setOnClickListener(btnDisputeOnClickListener)
    }

    private fun updateUIContentsToCancelled() {
        if (vm.mission.selectedAgent != "") {
            vm.selectedAgent.observe(requireActivity(), ::updateSelectedAgentForMissionCancelled)
            // result assign to selectedAgent and trigger updateSelectedAgentForMissionCancelled
            vm.getSelectedAgentFromDB()
        }
    }

    private fun updateUIContentsToDisputed() {
        if (vm.mission.selectedAgent != "") {
            vm.selectedAgent.observe(requireActivity(), ::updateSelectedAgentForMissionDisputed)
            // result assign to selectedAgent and trigger updateSelectedAgentForMissionDisputed
            vm.getSelectedAgentFromDB()
        }
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
        vm.disputeMission()
            .subscribe {
                vm.mission = it
                navigateToHomeFragment()
            }
    }

    private fun updateSelectedAgentForMissionConfirmed(agent: User) {
        binding.missionAgentConfirmed.layoutAgentConfirmed.tvUsername.text =
            "${agent.firstName} ${agent.lastName}"
    }

    private fun updateSelectedAgentForMissionCancelled(agent: User) {
        binding.missionAgentCancelled.layoutAgentConfirmed.tvUsername.text =
            "${agent.firstName} ${agent.lastName}"
    }

    private fun updateSelectedAgentForMissionDisputed(agent: User) {
        binding.missionAgentDisputed.layoutAgentConfirmed.tvUsername.text =
            "${agent.firstName} ${agent.lastName}"
    }

    private fun updateUIContentsToPendingAcceptance() {
        binding.layoutHeaderPending.tvCreditsPending.text = vm.mission.price.toString()

        if (vm.mission.selectedAgent != "") {
            vm.selectedAgent.observe(requireActivity(), ::updateSelectedAgentForMissionPending)
            // result assign to selectedAgent and trigger updateSelectedAgentForMissionPending
            vm.getSelectedAgentFromDB()
        }

        binding.missionAgentPending.btnAccept.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(resources.getString(R.string.accept_mission_result_alert_title))
                .setMessage(resources.getString(R.string.accept_mission_result_alert_msg))
                .setPositiveButton("Confirm Completion") { _, _ ->
                    updateMissionToCompleted()
                }
                .setNegativeButton("Back", null)
                .show()
        }

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
        vm.rejectMission()
            .subscribe {
                vm.mission = it
                navigateToHomeFragment()
            }
    }

    private fun updateSelectedAgentForMissionPending(agent: User) {
        binding.missionAgentPending.layoutAgentPending.tvUsername.text =
            "${agent.firstName} ${agent.lastName}"
    }

    private fun updateMissionContent() {
        binding.missionContent.mission = vm.mission
        binding.missionContent.period = vm.missionDuration
        binding.missionContent.lifecycleOwner = this
    }

    @SuppressLint("CheckResult")
    private fun updateMissionToCompleted() {
        vm.completeMission(vm.mission)
            .subscribe {
                vm.mission = it
                // release the suspend amount of the employer for this mission
                updateEmployerSuspendAmount()
            }
    }

    // release the suspend amount of the employer for this mission
    @SuppressLint("CheckResult")
    private fun updateEmployerSuspendAmount() {
        UserData.currentUserData.onHold -= vm.mission.price.toInt()
        vm.updateUser(UserData.currentUserData)
            .subscribe {
                if (it) {
                    // add the credit of this mission to the balance of the agent
                    updateAgentBalance()
                }
            }
    }

    // add the credit of this mission to the balance of the agent
    @SuppressLint("CheckResult")
    private fun updateAgentBalance() {
        vm.selectedAgent.value!!.balance += vm.mission.price.toInt()
        vm.updateUser(vm.selectedAgent.value!!)
            .subscribe {
                if (it) {
                    navigateToAcceptedMissionCompletionFragment(it)
                }
            }
    }

    private fun navigateToAcceptedMissionCompletionFragment(result: Boolean) {
        val action =
            EmployerMissionDetailsFragmentDirections.actionEmployerMissionDetailsFragmentToAcceptedMissionCompletionFragment(
                result
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
        vm.cancelOpenMissionByEmployer()
            .subscribe {
                vm.mission = it.mission
                UserData.currentUserData = it.user
                navigateToHomeFragment()
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
        vm.cancelMissionWithin48HoursByEmployer()
            .subscribe {
                vm.mission = it.mission
                UserData.currentUserData = it.user
                navigateToHomeFragment()
            }
    }

    @SuppressLint("CheckResult")
    private fun cancelConfirmedMissionStartBefore48Hours() {
        vm.cancelMissionBefore48HoursByEmployer()
            .subscribe {
                vm.mission = it.mission
                UserData.currentUserData = it.user
                navigateToHomeFragment()
            }
    }

    private fun navigateToHomeFragment() {
        val action =
            EmployerMissionDetailsFragmentDirections.actionEmployerMissionDetailsFragmentToHomeFragment()
        findNavController().navigate(action)
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