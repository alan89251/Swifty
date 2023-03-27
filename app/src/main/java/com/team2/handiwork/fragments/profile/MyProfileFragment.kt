package com.team2.handiwork.fragments.profile

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.team2.handiwork.AppConst
import com.team2.handiwork.R
import com.team2.handiwork.databinding.CustomServiceTypeDialogBinding
import com.team2.handiwork.databinding.FragmentMyProfileBinding
import com.team2.handiwork.models.Comment
import com.team2.handiwork.models.CommentList
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.viewModel.profile.FragmentMyProfileViewModel
import org.checkerframework.checker.units.qual.Current

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

        vm.comments.observe(viewLifecycleOwner) { comments ->
            binding.layoutComment.rvComment.adapter = commentAdapter
            commentAdapter.comments = comments
            binding.layoutComment.btnSelect.setOnClickListener { navToViewOtherCommentFragment(comments) }

            if (comments.isEmpty()) {
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
                binding.layoutAgentSubscriptions.cancelServiceButton.visibility = View.GONE
                binding.layoutAgentSubscriptionsEmpty.root.visibility = View.GONE
            } else if (it.isEmpty()) {
                binding.layoutAgentSubscriptionsEmpty.root.visibility = View.VISIBLE
                binding.layoutAgentSubscriptions.root.visibility = View.GONE
                binding.layoutAgentSubscriptions.cancelServiceButton.visibility = View.GONE
            } else {
                binding.layoutAgentSubscriptions.root.visibility = View.VISIBLE
                binding.layoutAgentSubscriptions.tvSubsServiceType.visibility = View.VISIBLE
                binding.layoutAgentSubscriptionsEmpty.root.visibility = View.GONE
                binding.layoutAgentSubscriptions.cancelServiceButton.visibility = View.VISIBLE

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

                if (count >= 3) {
                    binding.layoutAgentSubscriptions.tvSubsServiceType.setOnClickListener { view ->
                        showCustomDialog(it)
                    }
                }
            }
        }

        vm.user.observe(viewLifecycleOwner) {
            if (it.distance == 0) {
                binding.layoutAgentSubscriptions.tvSubsDistancePlace.text = "No Distance Filter"
                binding.layoutAgentSubscriptions.cancelDistanceButton.visibility = View.GONE
            } else {
                val place = "Within ${it.distance} km"
                binding.layoutAgentSubscriptions.cancelDistanceButton.visibility = View.VISIBLE
                binding.layoutAgentSubscriptions.tvSubsDistancePlace.text = place
            }
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

        binding.layoutAgentSubscriptions.btnEdit.setOnClickListener {
            findNavController()
                .navigate(
                    MyProfileFragmentDirections
                        .actionMyProfileFragmentToAgentUpdateSubscriptionServiceTypeFragment()
                )
        }

        binding.layoutBasicInfo.btnEdit.setOnClickListener {
            val action =
                MyProfileFragmentDirections.actionMyProfileFragmentToUpdateProfileFragment(UserData.currentUserData)
            findNavController().navigate(action)
        }

        binding.layoutAgentSubscriptions.cancelDistanceButton.setOnClickListener {
            vm.cancelDistanceSubscription()
        }

        binding.layoutAgentSubscriptions.cancelServiceButton.setOnClickListener {
            vm.cancelServiceTypeSubscription()
        }

        return binding.root
    }

    private fun showCustomDialog(serviceType: List<String>) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_service_type_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

        val listView = dialog.findViewById<ListView>(R.id.dialog_list)
        val adapter = ArrayAdapter<String>(requireContext(), R.layout.service_type_dialog_list_item, serviceType)
        listView.adapter = adapter

        val backBtn = dialog.findViewById<TextView>(R.id.dialog_back)
        backBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun navToViewOtherCommentFragment(comments: List<Comment>) {
        val action = MyProfileFragmentDirections
            .actionMyProfileFragmentToViewOtherCommentFragment(
                UserData.currentUserData,
                CommentList(comments)
            )
        findNavController().navigate(action)
    }
}