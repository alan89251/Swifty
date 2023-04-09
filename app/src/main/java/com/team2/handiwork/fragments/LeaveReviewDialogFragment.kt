package com.team2.handiwork.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.bumptech.glide.Glide
import com.team2.handiwork.databinding.BottomSheetLeaveReviewBinding
import com.team2.handiwork.models.Mission
import com.team2.handiwork.models.User
import com.team2.handiwork.viewModel.FragmentLeaveReviewDialogViewModel

class LeaveReviewDialogFragment : DialogFragment() {
    private lateinit var binding: BottomSheetLeaveReviewBinding
    private lateinit var vm: FragmentLeaveReviewDialogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = FragmentLeaveReviewDialogViewModel()

        arguments?.let {
            vm.isReviewedForEmployer = it.getBoolean(ARG_IS_REVIEWED_FOR_EMPLOYER)
            vm.mission = it.getSerializable(ARG_MISSION) as Mission
            if (vm.isReviewedForEmployer) {
                vm.getUserFromDB(vm.mission.employer)
            } else {
                vm.user.value = it.getSerializable(ARG_USER) as User
            }
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = BottomSheetLeaveReviewBinding.inflate(LayoutInflater.from(context))
        binding.vm = vm
        binding.lifecycleOwner = this

        binding.btnStar1.setOnClickListener(btnStarOnClickListener)
        binding.btnStar2.setOnClickListener(btnStarOnClickListener)
        binding.btnStar3.setOnClickListener(btnStarOnClickListener)
        binding.btnStar4.setOnClickListener(btnStarOnClickListener)
        binding.btnStar5.setOnClickListener(btnStarOnClickListener)
        binding.btnSelect.setOnClickListener(btnSelectOnClickListener)
        binding.btnClose.setOnClickListener(btnCloseOnClickListener)
        binding.btnReset.setOnClickListener(btnResetOnClickListener)

        vm.user.observe(this){
            Glide.with(requireContext())
                .load(vm.user.value?.imageURi)
                .into(binding.ivUser)
        }

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    private val btnSelectOnClickListener = View.OnClickListener {
        vm.addComment(binding.etComment.text.toString(), ::onSubmittedReview)
    }

    private fun onSubmittedReview() {
        setFragmentResult(
            RESULT_LISTENER_KEY,
            bundleOf(RESULT_ARG_IS_USER_REVIEWED to true)
        )
        closeDialog()
    }

    private val btnCloseOnClickListener = View.OnClickListener {
        closeDialog()
    }

    private val btnResetOnClickListener = View.OnClickListener {
        binding.etComment.setText("")
        vm.rating.value = FragmentLeaveReviewDialogViewModel.DEFAULT_RATING
    }

    fun closeDialog() {
        dismiss()
    }

    private val btnStarOnClickListener = View.OnClickListener {
        when (it.id) {
            binding.btnStar1.id -> vm.rating.value = 1
            binding.btnStar2.id -> vm.rating.value = 2
            binding.btnStar3.id -> vm.rating.value = 3
            binding.btnStar4.id -> vm.rating.value = 4
            binding.btnStar5.id -> vm.rating.value = 5
        }
    }

    companion object {
        const val TAG = "LeaveReviewDialog"
        const val ARG_USER = "user"
        const val ARG_IS_REVIEWED_FOR_EMPLOYER = "isReviewedForEmployer"
        const val ARG_MISSION = "mission"
        const val RESULT_LISTENER_KEY = "LeaveReviewDialogResult"
        const val RESULT_ARG_IS_USER_REVIEWED = "isUserReviewed"
    }
}