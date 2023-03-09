package com.team2.handiwork.fragments

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.team2.handiwork.R
import com.team2.handiwork.adapter.ChatRecyclerViewAdapter
import com.team2.handiwork.databinding.FragmentChatBinding
import com.team2.handiwork.enums.MissionStatusEnum
import com.team2.handiwork.models.Mission
import com.team2.handiwork.utilities.Utility
import com.team2.handiwork.viewModel.FragmentChatViewModel


class ChatFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentChatBinding.inflate(inflater, container, false)
        val vm = FragmentChatViewModel()

        var isAgent = false

        arguments?.let {
            isAgent = it.getBoolean("isAgent")
            val mission = it.getSerializable("mission") as Mission
            vm.mission.value = mission
            // todo set name
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Chat With ??"
        }

        vm.mission.observe(viewLifecycleOwner) {
            vm.updatePeriod()
            vm.missionStatusDisplay.value!!.value = it.status

            // todo duplicate code
            val backgroundDrawable = GradientDrawable()
            backgroundDrawable.shape = GradientDrawable.RECTANGLE
            val cornerRadius = 20.0f
            backgroundDrawable.cornerRadius = cornerRadius
            backgroundDrawable.setColor(
                ContextCompat.getColor(
                    requireContext(), Utility.convertStatusColor(it.status)
                )
            )
            binding.layoutStatus.cvBackground.background = backgroundDrawable
        }

        vm.missionStatusDisplay.observe(viewLifecycleOwner) {
            // todo duplicate code
            binding.layoutStatus.tvStatus.text = when (it.value) {
                // handle components show or hidden
                MissionStatusEnum.OPEN.value -> getString(R.string.status_open)
                MissionStatusEnum.PENDING_ACCEPTANCE.value -> getString(R.string.status_pending)
                MissionStatusEnum.CONFIRMED.value -> getString(R.string.status_confirmed)
                MissionStatusEnum.ENROLLED.value -> getString(R.string.status_enrolled)
                MissionStatusEnum.COMPLETED.value -> getString(R.string.status_completed)
                MissionStatusEnum.CANCELLED.value -> getString(R.string.status_cancel)
                else -> ""
            }

        }

        binding.vm = vm
        binding.lifecycleOwner = this
        binding.rvChat.adapter = ChatRecyclerViewAdapter(isAgent)

        binding.btnSendMsg.setOnClickListener {
            // todo samuel implement chat
        }

        return binding.root
    }
}