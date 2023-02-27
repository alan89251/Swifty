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
import com.team2.handiwork.enum.MissionStatusEnum
import com.team2.handiwork.models.Enrollment
import com.team2.handiwork.models.Mission
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

        configLayout()
        updateUIContents()

        // Inflate the layout for this fragment
        return binding.root
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
        updateMissionContent()
        binding.layoutHeaderOpen.tvCreditsOpen.text = vm.mission.price.toString()
        vm.enrollments.observe(requireActivity(), ::updateAgentList)
        // result assign to vm.enrollments and trigger updateAgentList
        vm.getEnrollmentsFromDB()
    }

    @SuppressLint("CheckResult")
    private fun updateAgentList(enrollments: List<Enrollment>) {
        val adapter = Agent1RecyclerViewAdapter(enrollments)
        binding.missionAgentOpen.rvAgents.adapter = adapter
        adapter.selectedEnrollment.subscribe {
            AlertDialog.Builder(requireContext())
                .setTitle("Select ${it.agent} as your agent?")
                .setMessage(resources.getString(R.string.select_agent_alert_msg))
                .setPositiveButton("Confirm Mission", { _, _ ->
                    it.selected = true
                    vm.updateSelectedEnrollment(it)
                })
                .setNegativeButton("Back", null)
                .show()
        }
    }

    private fun updateUIContentsToConfirmed() {
        updateMissionContent()
        binding.layoutHeaderOpen.tvCreditsOpen.text = vm.mission.price.toString()
        vm.selectedEnrollment.observe(requireActivity(), ::updateSelectedAgentForMissionConfirmed)
        // result assign to selectedEnrollment and trigger updateSelectedAgent
        vm.getSelectedEnrollmentFromDB()
    }

    private fun updateSelectedAgentForMissionConfirmed(enrollment: Enrollment) {
        binding.missionAgentConfirmed.layoutAgentConfirmed.tvUsername.text = enrollment.agent
    }

    private fun updateUIContentsToPendingAcceptance() {
        updateMissionContent()
        binding.layoutHeaderOpen.tvCreditsOpen.text = vm.mission.price.toString()
        vm.selectedEnrollment.observe(requireActivity(), ::updateSelectedAgentForMissionPending)
        // result assign to selectedEnrollment and trigger updateSelectedAgent
        vm.getSelectedEnrollmentFromDB()
        binding.missionAgentPending.btnAccept.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(resources.getString(R.string.accept_mission_result_alert_title))
                .setMessage(resources.getString(R.string.accept_mission_result_alert_msg))
                .setPositiveButton("Confirm Completion", { _, _ ->
                    updateMissionToCompleted()
                })
                .setNegativeButton("Back", null)
                .show()
        }
    }

    private fun updateSelectedAgentForMissionPending(enrollment: Enrollment) {
        binding.missionAgentPending.layoutAgentPending.tvUsername.text = enrollment.agent
    }

    private fun updateMissionContent() {
        binding.missionContent.mission = vm.mission
    }

    @SuppressLint("CheckResult")
    private fun updateMissionToCompleted() {
        vm.mission.status = MissionStatusEnum.COMPLETED.value
        vm.updateMission(vm.mission)
            .subscribe {
                if (it) {
                    updateEmployerSuspendAmount()
                }
            }
    }

    @SuppressLint("CheckResult")
    private fun updateEmployerSuspendAmount() {
        UserData.currentUserData.suspendAmount -= vm.mission.price.toInt()
        vm.updateEmployerSuspendAmount(UserData.currentUserData)
            .subscribe {
                if (it) {
                    val action = EmployerMissionDetailsFragmentDirections
                        .actionEmployerMissionDetailsFragmentToAcceptedMissionCompletionFragment(
                            it
                        )
                    findNavController().navigate(action)
                }
            }
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