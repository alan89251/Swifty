package com.team2.handiwork.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
            vm.comments = it.getSerializable(ARG_COMMENTS) as CommentList
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

        return binding.root
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