package com.team2.handiwork.fragments

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.team2.handiwork.AppConst
import com.team2.handiwork.R
import com.team2.handiwork.adapter.CommentRecyclerViewAdapter
import com.team2.handiwork.databinding.FragmentMyProfileBinding
import com.team2.handiwork.viewModel.ActivityHomeViewModel
import com.team2.handiwork.viewModel.FragmentMyProfileViewModel
import io.reactivex.rxjava3.disposables.Disposable

class MyProfileFragment : Fragment() {
    var vm = FragmentMyProfileViewModel()
    private var disposables = arrayListOf<Disposable>()
    private val homeActivityVm: ActivityHomeViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMyProfileBinding.inflate(inflater, container, false)


        homeActivityVm.currentUser.observe(viewLifecycleOwner) {
            vm.userData.value = it
            binding.layoutBasicInfo.user = it
            vm.categories.value = it.serviceTypeList.joinToString { st ->
                st.name + "\n"
            }
            binding.layoutRating.tvCancelRate.text = vm.calculateCancellationRate(it)
        }

        vm.categories.observe(viewLifecycleOwner) {
            binding.layoutRating.tvCategories.text = it
        }

        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val currentTheme = pref.getInt(AppConst.CURRENT_THEME, 0)
        // currentTheme 1 = employer
        val isAgent = currentTheme == 0

        // todo dummy data
        binding.layoutRating.ratingBar.rating = 5F

        val disposable = vm.getComments(homeActivityVm).subscribe {
            val adapter = CommentRecyclerViewAdapter()
            binding.layoutComment.rvComment.adapter = adapter
            adapter.comments = it

            if (it.isEmpty()) {
                binding.layoutComment.root.visibility = View.GONE
            } else {
                binding.layoutComment.root.visibility = View.VISIBLE
            }
        }

        vm.userData.observe(viewLifecycleOwner) {
            if (isAgent) {
                binding.subscription.visibility = View.VISIBLE
                if (it.serviceTypeList.isEmpty()) {
                    binding.layoutAgentSubscriptionsEmpty.root.visibility = View.VISIBLE
                } else {
                    binding.layoutAgentSubscriptions.root.visibility = View.VISIBLE
                    val place = "Within ${it.distance} km"
                    binding.layoutAgentSubscriptions.tvSubsDistancePlace.text = place
                    val subServiceTypeList: List<String> = it.serviceTypeList.flatMap { st ->
                        st.subServiceTypeList
                    }.map { sst ->
                        sst.name
                    }

                    val count = subServiceTypeList.size
                    val desc = if (count >= 3) {
                        "${subServiceTypeList[0]}, ${subServiceTypeList[1]}, <u>and ${count - 2} more</u>"
                    } else if (count >= 2){
                        "${subServiceTypeList[0]}, ${subServiceTypeList[1]}"
                    } else {
                        subServiceTypeList[0]
                    }

                    binding.layoutAgentSubscriptions.tvSubsServiceType.text = Html.fromHtml(desc)
                }
            }
        }



        binding.lifecycleOwner = this
        disposables.add(disposable)

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

    override fun onDestroy() {
        disposables.forEach { it.dispose() }
        super.onDestroy()
    }
}