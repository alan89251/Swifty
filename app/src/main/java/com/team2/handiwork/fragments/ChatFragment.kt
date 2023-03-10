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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentChatBinding.inflate(inflater, container, false)
        val vm = FragmentChatViewModel()

        arguments?.let {
            mIsAgent = it.getBoolean("isAgent")
            mMission = it.getSerializable("mission") as Mission
            mChatAgentId = it.getString("chatAgent", "")
            vm.mission.value = mMission
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
        binding.rvChat.adapter = ChatRecyclerViewAdapter(mIsAgent)
        binding.btnSendMsg.isEnabled = false

        binding.btnSendMsg.setOnClickListener {
            val title = getString(R.string.app_name) + " has a new chat message"
            val body = binding.etMessage.text.toString()
            sendPushMessage(title, body)

            // todo charleen implements chat save to firestore




            binding.etMessage.setText("")
        }

        // get the device toke for the other person
        val uid: String = if (mIsAgent) {
            mMission.employer
        } else {
            mChatAgentId
        }
        Firebase.firestore
            .collection(FirebaseCollectionKey.USERS.displayName).document(uid)
            .get()
            .addOnSuccessListener { document ->
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
                activity?.let { PushMessagingHelper(it.applicationContext) }
                    ?.sendPushMessage(mToken, title, body, mChatAgentId, mMission.missionId, mIsAgent)
            }
        }
    }
}