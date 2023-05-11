package com.team2.handiwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.team2.handiwork.AppConst
import com.team2.handiwork.adapter.MoreCommentRecyclerViewAdapter
import com.team2.handiwork.databinding.FragmentViewOtherCommentBinding
import com.team2.handiwork.models.CommentList
import com.team2.handiwork.models.User
import com.team2.handiwork.viewModel.FragmentViewOtherCommentViewModel

class ViewOtherCommentFragment: Fragment() {
    private lateinit var binding: FragmentViewOtherCommentBinding
    private lateinit var vm: FragmentViewOtherCommentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = FragmentViewOtherCommentViewModel()

        arguments?.let {
            vm.user = it.getSerializable(ARG_USER) as User
            vm.setCommentLists(it.getSerializable(ARG_COMMENTS) as CommentList)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewOtherCommentBinding.inflate(inflater, container, false)
        binding.vm = vm
        binding.lifecycleOwner = this
        loadIcon(binding.ivUser)


        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.title = "${vm.user.firstName}'s Ratings"

        binding.btnFromAll.setOnClickListener { vm.commentListType.value = FragmentViewOtherCommentViewModel.CommentListType.ALL }
        binding.btnFromAgents.setOnClickListener { vm.commentListType.value = FragmentViewOtherCommentViewModel.CommentListType.AGENT }
        binding.btnFromEmployers.setOnClickListener { vm.commentListType.value = FragmentViewOtherCommentViewModel.CommentListType.EMPLOYER }

        vm.commentListAdapter.observe(viewLifecycleOwner, ::changeCommentListContent)

        return binding.root
    }

    private fun loadIcon(shapeIcon : ShapeableImageView) {
        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val imgUrl = pref.getString(AppConst.PREF_USER_ICON_URL, "")
        if (imgUrl != "") {
            Glide.with(this)
                .load(imgUrl)
                .into(shapeIcon)
        }
    }

    private fun changeCommentListContent(commentsAdapter: MoreCommentRecyclerViewAdapter) {
        binding.rvComments.adapter = commentsAdapter
    }

    companion object {
        const val ARG_USER = "user"
        const val ARG_COMMENTS = "comments"

        @JvmStatic
        fun newInstance(user: User, comments: CommentList) =
            ViewOtherCommentFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_USER, user)
                    putSerializable(ARG_COMMENTS, comments)
                }
            }
    }
}