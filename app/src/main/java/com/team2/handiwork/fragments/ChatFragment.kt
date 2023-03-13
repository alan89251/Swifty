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
import com.team2.handiwork.models.ChatMessage
import com.team2.handiwork.models.Mission
import com.team2.handiwork.utilities.PushMessagingHelper
import com.team2.handiwork.viewModel.FragmentChatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ChatFragment : Fragment() {

    private lateinit var mMission: Mission
    private var mIsAgent: Boolean = false
    private var mChatAgentEmail: String = ""
    private var mToken: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = com.team2.handiwork.databinding.FragmentChatBinding.inflate(inflater, container, false)
        val vm = FragmentChatViewModel()
        var targetUserEmail: String = ""
        // todo temp
        arguments?.let {
            mIsAgent = it.getBoolean("isAgent")
            mMission = it.getSerializable("mission") as Mission
            mChatAgentEmail = it.getString("chatAgent", "")
            vm.mission.value = mMission
            // get the device toke for the other person
            targetUserEmail = if (mIsAgent) {
                mMission.employer
            } else {
                mChatAgentEmail
            }
        }

        binding.vm = vm
        binding.lifecycleOwner = this
        val adapter = ChatRecyclerViewAdapter(mIsAgent)
        binding.rvChat.adapter = adapter
        binding.btnSendMsg.isEnabled = false

        vm.mission.observe(viewLifecycleOwner) {
            vm.updatePeriod()
            binding.layoutStatus.tvStatus.text = getString(vm.getMissionStatusString(it.status))

            // todo duplicate code
            val backgroundDrawable = GradientDrawable()
            backgroundDrawable.shape = GradientDrawable.RECTANGLE
            val cornerRadius = 20.0f
            backgroundDrawable.cornerRadius = cornerRadius
            backgroundDrawable.setColor(
                ContextCompat.getColor(
                    requireContext(),
                    vm.convertStatusColor(it.status)
                )
            )
            binding.layoutStatus.cvBackground.background = backgroundDrawable
        }

        vm.repo.fetchMessage(
            mChatAgentEmail,
            vm.mission.value!!.missionId,
        ).subscribe {
            val originalMsgSize = adapter.cloudMessages.size
            val cloudMsgSize = it.size
            adapter.cloudMessages = it
            adapter.notifyItemRangeChanged(originalMsgSize, cloudMsgSize - 1)
        }

        binding.btnSendMsg.setOnClickListener {
            val title = getString(R.string.app_name) + ": New message from " + targetUserEmail
            val body = binding.etMessage.text.toString()
            sendPushMessage(title, body)
            binding.etMessage.setText("")
            val chatMessage = ChatMessage(text = body, isAgent = mIsAgent)
            vm.repo.addMessage(
                mChatAgentEmail,
                vm.mission.value!!.missionId,
                chatMessage,
            )
            binding.etMessage.text.clear()
        }

        Firebase.firestore.collection(FirebaseCollectionKey.USERS.displayName)
            .document(targetUserEmail)
            .get().addOnSuccessListener { document ->
                binding.btnSendMsg.isEnabled = true
                if (document.data != null) {
                    val user = document.toObject<com.team2.handiwork.models.User>()
                    mToken = user!!.fcmDeviceToken
                    (activity as AppCompatActivity?)!!.supportActionBar!!.title =
                        "Chat With ${user.firstName} ${user.lastName}"
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
                    mToken, title, body, mChatAgentEmail, mMission.missionId, mIsAgent
                )
            }
        }
    }
}