package com.team2.handiwork.fragments.profile

import android.content.SharedPreferences
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.team2.handiwork.AppConst
import com.team2.handiwork.R
import com.team2.handiwork.adapter.MyMissionsRecyclerViewAdapter
import com.team2.handiwork.databinding.FragmentViewProfileBinding
import com.team2.handiwork.firebase.Storage
import com.team2.handiwork.models.Comment
import com.team2.handiwork.models.CommentList
import com.team2.handiwork.models.Mission
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.utilities.Ext.Companion.disposedBy
import com.team2.handiwork.viewModel.profile.FragmentViewProfileViewModel

class ViewProfileFragment : BaseProfileFragment<FragmentViewProfileViewModel>() {
    override var vm = FragmentViewProfileViewModel()
    private lateinit var binding: FragmentViewProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewProfileBinding.inflate(inflater, container, false)
        email = requireArguments().getString("targetEmail") as String
        super.onCreateView(inflater, container, savedInstanceState)

        binding.lifecycleOwner = this
        binding.vm = vm

        loadIcon(binding)

        binding.layoutBasicInfo.btnEdit.visibility = View.GONE

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
        binding.layoutBasicInfo.lblPhone.text = "Phone number verified"
        binding.layoutBasicInfo.lblEmail.visibility = View.GONE
        binding.layoutBasicInfo.tvEmail.visibility = View.GONE
        binding.layoutBasicInfo.tvPhone.visibility = View.GONE

        vm.comments.observe(viewLifecycleOwner) { comments ->
            binding.layoutComment.rvComment.adapter = commentAdapter
            commentAdapter.comments = comments
            binding.layoutComment.btnSelect.setOnClickListener { navToViewOtherCommentFragment(comments) }

            if (comments.isEmpty()) {
                binding.layoutComment.root.visibility = View.GONE
                binding.layoutRating.root.visibility = View.GONE
                binding.layoutNoRating.root.visibility = View.VISIBLE
            }
        }

        vm.user.observe(viewLifecycleOwner) {
            (activity as AppCompatActivity?)!!
                .supportActionBar!!.title = "${it.firstName}'s Profile"

            binding.tvMissionTitle.text = "${it.firstName}'s Mission"
        }

        return binding.root
    }

    private fun loadIcon(binding: FragmentViewProfileBinding) {
        val imgUrl = requireArguments().getString("targetIconURL")
        if (imgUrl != "" && imgUrl != null) {
            Log.d("hehehe", "$imgUrl")
            Glide.with(this)
                .load(imgUrl)
                .into(binding.layoutBasicInfo.ivUser)
        } else {
            loadAgentIcon(email)
        }
    }

    private val onIconLoaded: (mission: String) -> Unit = { imgUrl ->
        Glide.with(this)
            .load(imgUrl)
            .into(binding.layoutBasicInfo.ivUser)
    }

    private val onIconLoadFailed: () -> Unit = {
    }

    private fun loadAgentIcon(agentEmail: String) {
        Storage().getImgUrl("User/$agentEmail", onIconLoaded, onIconLoadFailed)
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

    private fun navToViewOtherCommentFragment(comments: List<Comment>) {
        val action = ViewProfileFragmentDirections
            .actionViewProfileFragmentToViewOtherCommentFragment(
                UserData.currentUserData,
                CommentList(comments)
            )
        findNavController().navigate(action)
    }
}