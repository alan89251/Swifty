package com.team2.handiwork.fragments

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.team2.handiwork.R
import com.team2.handiwork.adapter.ChatRecyclerViewAdapter
import com.team2.handiwork.databinding.FragmentChatBinding
import com.team2.handiwork.enums.FirebaseCollectionKey
import com.team2.handiwork.enums.MissionStatusEnum
import com.team2.handiwork.models.ChatMessage
import com.team2.handiwork.models.Mission
import com.team2.handiwork.utilities.PushMessagingHelper
import com.team2.handiwork.utilities.Utility
import com.team2.handiwork.viewModel.FragmentChatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ChatFragment : Fragment() {

    private lateinit var mMission: Mission
    private var mIsAgent: Boolean = false
    private var mChatAgentId: String = ""
    private var mToken: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = FragmentChatBinding.inflate(inflater, container, false)
        val vm = FragmentChatViewModel()
        var email: String = ""
        // todo temp
        arguments?.let {
            mIsAgent = it.getBoolean("isAgent")
            mMission = it.getSerializable("mission") as Mission
            mChatAgentId = it.getString("chatAgent", "")
            vm.mission.value = mMission
            // get the device toke for the other person
            email = if (mIsAgent) {
                mMission.employer
            } else {
                mChatAgentId
            }
            (activity as AppCompatActivity?)!!.supportActionBar!!.title =
                "Chat With $email"
        }

        binding.vm = vm
        binding.lifecycleOwner = this
        val adapter = ChatRecyclerViewAdapter(mIsAgent)
        binding.rvChat.adapter = adapter
        binding.btnSendMsg.isEnabled = false

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

        vm.repo.fetchMessage(
            email,
            vm.mission.value!!.missionId,
        ).subscribe {
            if (it.isEmpty()) {
                vm.initMsg.value = true
            }
            val originalMsgSize = adapter.cloudMessages.size
            val cloudMsgSize = it.size
            adapter.cloudMessages = it
            adapter.notifyItemRangeChanged(originalMsgSize, cloudMsgSize - 1)
        }

        vm.initMsg.observe(viewLifecycleOwner) {
            if (!it) return@observe
            vm.repo.addMessages(
                email,
                vm.mission.value!!.missionId, vm.getInitDefaultMessages()
            )
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

        binding.btnSendMsg.setOnClickListener {
            val title = getString(R.string.app_name) + " has a new chat message"
            val body = binding.etMessage.text.toString()
            sendPushMessage(title, body)
            binding.etMessage.setText("")
            val chatMessage = ChatMessage(text = body, isAgent = mIsAgent)
            vm.repo.addMessage(
                email,
                vm.mission.value!!.missionId,
                chatMessage,
            )
            binding.etMessage.text.clear()
        }

        Firebase.firestore.collection(FirebaseCollectionKey.USERS.displayName).document(email)
            .get().addOnSuccessListener { document ->
                binding.btnSendMsg.isEnabled = true
                if (document.data != null) {
                    val user = document.toObject<com.team2.handiwork.models.User>()
                    mToken = user!!.fcmDeviceToken
                }
            }.addOnFailureListener { e ->
                binding.btnSendMsg.isEnabled = true
                Log.e("ChatFragment", "Error reading document", e)
            }

        return binding.root
    }


    private fun sendPushMessage(title: String, body: String) {
        if (mToken.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                activity?.let { PushMessagingHelper(it.applicationContext) }?.sendPushMessage(
                    mToken, title, body, mChatAgentId, mMission.missionId, mIsAgent
                )
            }
        }
    }
}