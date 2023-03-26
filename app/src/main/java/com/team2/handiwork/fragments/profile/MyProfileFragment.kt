package com.team2.handiwork.fragments.profile

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.team2.handiwork.AppConst
import com.team2.handiwork.R
import com.team2.handiwork.databinding.FragmentMyProfileBinding
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.viewModel.profile.FragmentMyProfileViewModel

class MyProfileFragment : BaseProfileFragment<FragmentMyProfileViewModel>() {
    override var vm = FragmentMyProfileViewModel()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        email = UserData.currentUserData.email
        super.onCreateView(inflater, container, savedInstanceState)

        val binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = vm

        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val currentTheme = pref.getInt(AppConst.CURRENT_THEME, 0)
        // currentTheme 1 = employer
        val isAgent = currentTheme == 0

        vm.comments.observe(viewLifecycleOwner) {
            binding.layoutComment.rvComment.adapter = commentAdapter
            commentAdapter.comments = it

            if (it.isEmpty()) {
                binding.layoutComment.root.visibility = View.GONE
            } else {
                binding.layoutComment.root.visibility = View.VISIBLE
            }
        }

        vm.typeList.observe(viewLifecycleOwner) {
            if (!isAgent) return@observe
            val distance = UserData.currentUserData.distance

            binding.subscription.visibility = View.VISIBLE
            if (it.isEmpty() && distance != 0) {
                binding.layoutAgentSubscriptions.root.visibility = View.VISIBLE
            } else if (it.isEmpty()) {
                binding.layoutAgentSubscriptionsEmpty.root.visibility = View.VISIBLE
            } else {
                binding.layoutAgentSubscriptions.root.visibility = View.VISIBLE
                binding.layoutAgentSubscriptions.tvSubsServiceType.visibility = View.VISIBLE

                val count = it.size
                val desc = if (count >= 3) {
                    "${it[0]}, ${it[1]}, <u>and ${count - 2} more</u>"
                } else if (count == 2) {
                    "${it[0]}, ${it[1]}"
                } else if (count == 1) {
                    it[0]
                } else {
                    ""
                }
                binding.layoutAgentSubscriptions.tvSubsServiceType.text = Html.fromHtml(desc)
            }
        }

        vm.user.observe(viewLifecycleOwner) {
            val place = "Within ${it.distance} km"
            binding.layoutAgentSubscriptions.tvSubsDistancePlace.text = place
        }


        binding.btnViewMission.setOnClickListener {
            findNavController().navigate(
                R.id.action_myProfileFragment_to_myMissionsFragment,
            )
        }

        binding.layoutAgentSubscriptionsEmpty.btnUpdateMissionSub.setOnClickListener {
            findNavController()
                .navigate(
                    MyProfileFragmentDirections
                        .actionMyProfileFragmentToAgentUpdateSubscriptionServiceTypeFragment()
                )
        }

        binding.layoutComment.btnSelect.setOnClickListener {
            // todo nav to view page
        }

        binding.layoutAgentSubscriptions.btnEdit.setOnClickListener {
            findNavController()
                .navigate(
                    MyProfileFragmentDirections
                        .actionMyProfileFragmentToAgentUpdateSubscriptionServiceTypeFragment()
                )
        }

        return binding.root
    }
}