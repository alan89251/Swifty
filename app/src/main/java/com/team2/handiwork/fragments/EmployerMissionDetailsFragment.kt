package com.team2.handiwork.fragments

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
import com.team2.handiwork.models.Enrollment
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.User
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.viewModel.FragmentEmployerMissionDetailsViewModel

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
    ):  View? {
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
        }
        else if (vm.mission.status == MissionStatusEnum.CONFIRMED.value) {
            configLayoutToConfirmed()
        }
        else { // PENDING_ACCEPTANCE
            configLayoutToPendingAcceptance()
        }
    }

    private fun configLayoutToOpen() {
        // set the layout visibility for mission status OPEN
        binding.layoutHeaderOpen.root.visibility = View.VISIBLE
        binding.layoutHeaderConfirmed.root.visibility = View.INVISIBLE
        binding.layoutHeaderPending.root.visibility = View.INVISIBLE
        binding.missionContent.root.visibility = View.VISIBLE
        binding.missionAgentOpen.root.visibility = View.VISIBLE
        binding.missionAgentConfirmed.root.visibility = View.INVISIBLE
        binding.missionAgentPending.root.visibility = View.INVISIBLE


    }

    private fun configLayoutToConfirmed() {
        // set the layout visibility for mission status CONFIRMED
        binding.layoutHeaderOpen.root.visibility = View.INVISIBLE
        binding.layoutHeaderConfirmed.root.visibility = View.VISIBLE
        binding.layoutHeaderPending.root.visibility = View.INVISIBLE
        binding.missionContent.root.visibility = View.VISIBLE
        binding.missionAgentOpen.root.visibility = View.INVISIBLE
        binding.missionAgentConfirmed.root.visibility = View.VISIBLE
        binding.missionAgentPending.root.visibility = View.INVISIBLE
    }

    private fun configLayoutToPendingAcceptance() {
        // set the layout visibility for mission status PENDING ACCEPTANCE
        binding.layoutHeaderOpen.root.visibility = View.INVISIBLE
        binding.layoutHeaderConfirmed.root.visibility = View.INVISIBLE
        binding.layoutHeaderPending.root.visibility = View.VISIBLE
        binding.missionContent.root.visibility = View.VISIBLE
        binding.missionAgentOpen.root.visibility = View.INVISIBLE
        binding.missionAgentConfirmed.root.visibility = View.INVISIBLE
        binding.missionAgentPending.root.visibility = View.VISIBLE
    }

    private fun updateUIContents() {
        if (vm.mission.status == MissionStatusEnum.OPEN.value) {
            updateUIContentsToOpen()
        }
        else if (vm.mission.status == MissionStatusEnum.CONFIRMED.value) {
            updateUIContentsToConfirmed()
        }
        else { // PENDING_ACCEPTANCE
            updateUIContentsToPendingAcceptance()
        }
    }

    private fun updateUIContentsToOpen() {
        binding.layoutHeaderOpen.tvCreditsOpen.text = vm.mission.price.toString()
        binding.layoutHeaderOpen.btnCancelOpen.setOnClickListener(btnCancelOpenOnClickListener)
        vm.enrollments.observe(requireActivity(), ::getAgentsFromDB)
        // result assign to vm.enrollments and trigger getAgentsFromDB
        vm.getEnrollmentsFromDB()
    }

    @SuppressLint("CheckResult")
    private fun getAgentsFromDB(enrollments: List<Enrollment>) {
        val emails: List<String> = enrollments.map {
            it.agent
        }
        vm.getAgentsByEmails(emails)
            .subscribe {
                updateAgentList(enrollments, it)
            }
    }

    @SuppressLint("CheckResult")
    private fun updateAgentList(enrollments: List<Enrollment>, agents: List<User>) {
        val agentMap: Map<String, User> = agents.map {
            it.email to it
        }
            .toMap()

        val adapter = Agent1RecyclerViewAdapter(enrollments, agentMap)
        binding.missionAgentOpen.rvAgents.adapter = adapter
        adapter.selectedEnrollment.subscribe {
            AlertDialog.Builder(requireContext())
                .setTitle("Select ${it.agent} as your agent?")
                .setMessage(resources.getString(R.string.select_agent_alert_msg))
                .setPositiveButton("Confirm Mission") { _, _ ->
                    it.selected = true
                    vm.updateSelectedEnrollment(it)
                        .subscribe { updateSelectedEnrollmentResult ->
                            if (updateSelectedEnrollmentResult) {
                                updateMissionToConfirmed()
                            }
                        }
                }
                .setNegativeButton("Back", null)
                .show()
        }
    }

    @SuppressLint("CheckResult")
    private fun updateMissionToConfirmed() {
        vm.mission.status = MissionStatusEnum.CONFIRMED.value
        vm.updateMission(vm.mission)
            .subscribe { updateMissionResult ->
                if (updateMissionResult) {
                    refreshScreen()
                }
            }
    }

    private fun updateUIContentsToConfirmed() {
        binding.layoutHeaderConfirmed.tvCreditsConfirmed.text = vm.mission.price.toString()
        binding.layoutHeaderConfirmed.btnCancelConfirmed.setOnClickListener(btnCancelConfirmedOnClickListener)
        vm.selectedEnrollment.observe(requireActivity()) {
            // if cannot find any selected enrollment, do not get agent from db
            // also make the related UIs invisible
            if (it.enrollmentId == "") {
                binding.missionAgentConfirmed.layoutAgentConfirmed.root.visibility = View.INVISIBLE
                return@observe
            }

            // result assign to selectedAgent and trigger updateSelectedAgentForMissionConfirmed
            vm.getSelectedAgentFromDB()
        }
        vm.selectedAgent.observe(requireActivity(), ::updateSelectedAgentForMissionConfirmed)
        // result assign to selectedEnrollment
        vm.getSelectedEnrollmentFromDB()
    }

    private fun updateSelectedAgentForMissionConfirmed(agent: User) {
        binding.missionAgentConfirmed.layoutAgentConfirmed.tvUsername.text =
            "${agent.firstName} ${agent.lastName}"
    }

    private fun updateUIContentsToPendingAcceptance() {
        binding.layoutHeaderPending.tvCreditsPending.text = vm.mission.price.toString()
        vm.selectedEnrollment.observe(requireActivity()) {
            // if cannot find any selected enrollment, do not get agent from db
            // also make the related UIs invisible
            if (it.enrollmentId == "") {
                binding.missionAgentPending.layoutAgentPending.root.visibility = View.INVISIBLE
                return@observe
            }

            // result assign to selectedAgent and trigger updateSelectedAgentForMissionPending
            vm.getSelectedAgentFromDB()
        }
        vm.selectedAgent.observe(requireActivity(), ::updateSelectedAgentForMissionPending)
        // result assign to selectedEnrollment
        vm.getSelectedEnrollmentFromDB()

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
    }

    private fun updateSelectedAgentForMissionPending(agent: User) {
        binding.missionAgentPending.layoutAgentPending.tvUsername.text =
            "${agent.firstName} ${agent.lastName}"
    }

    private fun updateMissionContent() {
        binding.missionContent.mission = vm.mission
        binding.missionContent.lifecycleOwner = this
    }

    @SuppressLint("CheckResult")
    private fun updateMissionToCompleted() {
        vm.mission.status = MissionStatusEnum.COMPLETED.value
        vm.updateMission(vm.mission)
            .subscribe {
                if (it) {
                    // release the suspend amount of the employer for this mission
                    updateEmployerSuspendAmount()
                }
            }
    }

    // release the suspend amount of the employer for this mission
    @SuppressLint("CheckResult")
    private fun updateEmployerSuspendAmount() {
        UserData.currentUserData.suspendAmount -= vm.mission.price.toInt()
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
        val action = EmployerMissionDetailsFragmentDirections
            .actionEmployerMissionDetailsFragmentToAcceptedMissionCompletionFragment(
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
        UserData.currentUserData.suspendAmount -= vm.mission.price.toInt()
        UserData.currentUserData.balance += vm.mission.price.toInt()
        vm.updateUser(UserData.currentUserData)
            .subscribe {
                if (it) {
                    updateMissionToCancelled()
                }
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
        }
        else {
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
        UserData.currentUserData.suspendAmount -= vm.mission.price.toInt()
        vm.updateUser(UserData.currentUserData)
            .subscribe {
                if (it) {
                    updateMissionToCancelled()
                }
            }
    }

    @SuppressLint("CheckResult")
    private fun cancelConfirmedMissionStartBefore48Hours() {
        UserData.currentUserData.suspendAmount -= vm.mission.price.toInt()
        UserData.currentUserData.balance += vm.mission.price.toInt()
        vm.updateUser(UserData.currentUserData)
            .subscribe {
                if (it) {
                    updateMissionToCancelled()
                }
            }
    }

    @SuppressLint("CheckResult")
    private fun updateMissionToCancelled() {
        vm.mission.status = MissionStatusEnum.CANCELLED.value
        vm.updateMission(vm.mission)
            .subscribe {
                if (it) {
                    navigateToHomeFragment()
                }
            }
    }

    private fun navigateToHomeFragment() {
        val action = EmployerMissionDetailsFragmentDirections
            .actionEmployerMissionDetailsFragmentToHomeFragment()
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