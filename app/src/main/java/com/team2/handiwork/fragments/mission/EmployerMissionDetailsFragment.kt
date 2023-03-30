package com.team2.handiwork.fragments.mission

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.team2.handiwork.R
import com.team2.handiwork.databinding.FragmentEmployerMissionDetailsBinding
import com.team2.handiwork.enums.MissionStatusEnum
import com.team2.handiwork.firebase.Storage
import com.team2.handiwork.fragments.LeaveReviewDialogFragment
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.User
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
        vm.doRefreshScreen = ::refreshScreen

        updateMissionContent()
        refreshScreen()

        binding.missionAgentCompleted.btnLeaveReview.setOnClickListener(
            btnLeaveReviewOnClickListener
        )
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

        when (vm.mission.value!!.status) {
            MissionStatusEnum.OPEN -> {
                binding.layoutHeaderOpen.root.visibility = View.VISIBLE
                binding.missionAgentOpen.root.visibility = View.VISIBLE
            }
            MissionStatusEnum.CONFIRMED -> {
                binding.layoutHeaderConfirmed.root.visibility = View.VISIBLE
                binding.missionAgentConfirmed.root.visibility = View.VISIBLE
            }
            MissionStatusEnum.PENDING_ACCEPTANCE -> {
                binding.layoutHeaderPending.root.visibility = View.VISIBLE
                binding.missionAgentPending.root.visibility = View.VISIBLE
            }
            MissionStatusEnum.CANCELLED -> {
                binding.layoutHeaderCancelled.root.visibility = View.VISIBLE
                binding.missionAgentCancelled.root.visibility = View.VISIBLE
            }
            MissionStatusEnum.DISPUTED -> {
                binding.layoutHeaderDisputed.root.visibility = View.VISIBLE
                binding.missionAgentDisputed.root.visibility = View.VISIBLE
            }
            MissionStatusEnum.COMPLETED -> {
                binding.layoutHeaderCompleted.root.visibility = View.VISIBLE
                binding.missionAgentCompleted.root.visibility = View.VISIBLE
            }
            else -> {}
        }
    }

    private fun updateUIContents() {
        when (vm.mission.value!!.status) {
            MissionStatusEnum.OPEN -> vm.getAgentsFromDB()
            MissionStatusEnum.CONFIRMED,
            MissionStatusEnum.PENDING_ACCEPTANCE,
            MissionStatusEnum.CANCELLED,
            MissionStatusEnum.DISPUTED,
            MissionStatusEnum.COMPLETED -> {
                if (vm.mission.value!!.selectedAgent != "") {
                    vm.getSelectedAgentFromDB()
                    loadAgentIcon(vm.mission.value!!.selectedAgent)
                }
            }
            else -> {}
        }
    }

    private val onIconLoaded: (mission: String) -> Unit = { imgUrl ->
        vm.iconImageUrl.value = imgUrl
    }

    private val onIconLoadFailed: () -> Unit = {

    }

    private fun loadImage(string: String, shapeableImageView: ShapeableImageView) {
        Glide.with(this)
            .load(string)
            .into(shapeableImageView)
    }

    private fun loadAgentIcon(agent: String) {
        Storage().getImgUrl("User/$agent", onIconLoaded, onIconLoadFailed)
    }

    private val btnLeaveReviewOnClickListener = View.OnClickListener {
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

    private fun updateMissionContent() {
        binding.missionContent.mission = vm.mission.value!!
        binding.missionContent.period = vm.missionDuration
        binding.missionContent.lifecycleOwner = this
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