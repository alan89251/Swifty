package com.team2.handiwork.fragments.profile

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.team2.handiwork.R
import com.team2.handiwork.adapter.MyMissionsRecyclerViewAdapter
import com.team2.handiwork.databinding.FragmentViewProfileBinding
import com.team2.handiwork.models.Mission
import com.team2.handiwork.utilities.Ext.Companion.disposedBy
import com.team2.handiwork.viewModel.profile.FragmentViewProfileViewModel

class ViewProfileFragment : BaseProfileFragment<FragmentViewProfileViewModel>() {
    override var vm = FragmentViewProfileViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentViewProfileBinding.inflate(inflater, container, false)
        email = requireArguments().getSerializable("targetEmail") as String
        super.onCreateView(inflater, container, savedInstanceState)

        binding.lifecycleOwner = this
        binding.vm = vm


        vm.getMissions(email).subscribe {
            vm.missions.value = it
        }.disposedBy(disposeBag)

        vm.missions.observe(viewLifecycleOwner) {
            val missionAdapter = MyMissionsRecyclerViewAdapter(changeDrawableColor, onMissionClick)
            binding.rvMission.layoutManager = GridLayoutManager(requireContext(), 2)
            missionAdapter.setList(it)
            binding.rvMission.adapter = missionAdapter
        }

        // set view invisibility
//        binding.layoutBasicInfo.lblEmail.text = "Phone number verified"
//        binding.layoutBasicInfo.tvEmail.visibility = View.GONE
//        binding.layoutBasicInfo.tvPhone.visibility = View.GONE

        vm.comments.observe(viewLifecycleOwner) {
//            if (it.isEmpty()) {
//                binding.layoutComment.root.visibility = View.GONE
//                binding.layoutRating.root.visibility = View.GONE
//                binding.layoutNoRating.root.visibility = View.VISIBLE
//            }
        }

        vm.user.observe(viewLifecycleOwner) {
            (activity as AppCompatActivity?)!!
                .supportActionBar!!.title = "${it.firstName}'s Profile"
        }





        return binding.root
    }

    private val changeDrawableColor: (textView: TextView, mission: Mission) -> Unit =
        { textView, mission ->
            val backgroundDrawable = GradientDrawable()
            backgroundDrawable.shape = GradientDrawable.RECTANGLE
            val cornerRadius = 20.0f
            backgroundDrawable.cornerRadius = cornerRadius
            backgroundDrawable.setColor(
                ContextCompat.getColor(
                    requireContext(),
                    vm.convertStatusColor(mission.status)
                )
            )
            textView.background = backgroundDrawable
        }

    private val onMissionClick: (mission: Mission) -> Unit = {
        val bundle: Bundle = Bundle()
        bundle.putSerializable("mission", it)
        findNavController().navigate(
            R.id.action_viewProfileFragment_to_agentMissionDetailFragment,
            bundle
        )
    }
}