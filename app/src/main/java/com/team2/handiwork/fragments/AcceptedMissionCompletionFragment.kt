package com.team2.handiwork.fragments

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.team2.handiwork.databinding.FragmentAcceptedMissionCompletionBinding
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.User
import com.team2.handiwork.viewModel.FragmentAcceptedMissionCompletionViewModel

private const val ARG_IS_ACCEPT_MISSION_SUCCESS = "is_accept_mission_success"
private const val ARG_AGENT = "agent"
private const val ARG_MISSION = "mission"

class AcceptedMissionCompletionFragment : Fragment() {
    private lateinit var binding: FragmentAcceptedMissionCompletionBinding
    private lateinit var vm: FragmentAcceptedMissionCompletionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = FragmentAcceptedMissionCompletionViewModel()

        arguments?.let {
            vm.isAcceptMissionSuccess.value = it.getBoolean(ARG_IS_ACCEPT_MISSION_SUCCESS)
            vm.agent = it.getSerializable(ARG_AGENT) as User
            vm.mission = it.getSerializable(ARG_MISSION) as Mission
        }

        // set a listener to receive result sending back from the leave review dialog fragment
        childFragmentManager.setFragmentResultListener(
            LeaveReviewDialogFragment.RESULT_LISTENER_KEY,
            this) { _, bundle ->
            navigateToHomeFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAcceptedMissionCompletionBinding.inflate(inflater, container, false)
        binding.vm = vm
        binding.lifecycleOwner = this

        // remove back button in navigation bar
        (requireActivity() as AppCompatActivity)
            .supportActionBar!!
            .setDisplayHomeAsUpEnabled(false)

        binding.btnLeaveReview.setOnClickListener(btnLeaveReviewOnClickListener)
        binding.btnNavToHome.setOnClickListener(btnNavToHomeOnClickListener)

        redirectTimerThread.execute()

        // Inflate the layout for this fragment
        return binding.root
    }

    private val btnLeaveReviewOnClickListener = View.OnClickListener {
        // prevent the timer thread from doing the navigation afterward
        redirectTimerThread.isDoingNavigation = false

        vm.isBtnLeaveReviewClicked.value = true

        val bundle = Bundle()
        bundle.putBoolean(LeaveReviewDialogFragment.ARG_IS_REVIEWED_FOR_EMPLOYER, false)
        bundle.putSerializable(LeaveReviewDialogFragment.ARG_USER, vm.agent)
        bundle.putSerializable(LeaveReviewDialogFragment.ARG_MISSION, vm.mission)
        val leaveReviewDialogFragment = LeaveReviewDialogFragment()
        leaveReviewDialogFragment.arguments = bundle
        leaveReviewDialogFragment.show(
            childFragmentManager,
            LeaveReviewDialogFragment.TAG
        )
    }

    private val btnNavToHomeOnClickListener = View.OnClickListener {
        // prevent the timer thread from doing the navigation afterward
        redirectTimerThread.isDoingNavigation = false

        // display back button in navigation bar
        (requireActivity() as AppCompatActivity)
            .supportActionBar!!
            .setDisplayHomeAsUpEnabled(false)
        navigateToHomeFragment()
    }

    private fun navigateToHomeFragment() {
        val action =
            AcceptedMissionCompletionFragmentDirections
                .actionAcceptedMissionCompletionFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    // redirect the user to the home screen
    private val redirectTimerThread = @SuppressLint("StaticFieldLeak")
    object: AsyncTask<Void, Void, Void>() {
        var isDoingNavigation = true
        override fun doInBackground(vararg p0: Void?): Void? {
            val timeBeforeRedirect = 10000L // in milliseconds
            Thread.sleep(timeBeforeRedirect)
            return null
        }

        override fun onPostExecute(result: Void?) {
            if (!isDoingNavigation) {
                return
            }
            navigateToHomeFragment()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param isAcceptMissionSuccess Parameter 1.
         * @param agent agent
         * @param mission mission
         * @return A new instance of fragment AcceptedMissionCompletionFragment.
         */
        @JvmStatic
        fun newInstance(isAcceptMissionSuccess: Boolean,
                        agent: User,
                        mission: Mission) =
            AcceptedMissionCompletionFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_ACCEPT_MISSION_SUCCESS, isAcceptMissionSuccess)
                    putSerializable(ARG_AGENT, agent)
                    putSerializable(ARG_MISSION, mission)
                }
            }
    }
}