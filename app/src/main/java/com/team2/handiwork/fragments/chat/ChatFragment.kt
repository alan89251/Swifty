package com.team2.handiwork.fragments.chat

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.team2.handiwork.AppConst
import com.team2.handiwork.R
import com.team2.handiwork.adapter.ChatRecyclerViewAdapter
import com.team2.handiwork.base.fragment.DisposeFragment
import com.team2.handiwork.databinding.FragmentChatBinding
import com.team2.handiwork.models.*
import com.team2.handiwork.utilities.Ext.Companion.disposedBy
import com.team2.handiwork.utilities.Ext.Companion.toChatUser
import com.team2.handiwork.utilities.PushMessagingHelper
import com.team2.handiwork.viewModel.chat.FragmentChatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ChatFragment : DisposeFragment() {

    private var vm = FragmentChatViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = FragmentChatBinding.inflate(
            inflater,
            container,
            false,
        )
        binding.vm = vm
        binding.lifecycleOwner = this
        vm.misAgent = isAgent()

        var agent = requireArguments().getSerializable("agent")
        val toEmail = requireArguments().getSerializable("toEmail") as String
        val clientImgUrl = requireArguments().getSerializable("clientImgUrl") as String

        if (agent is User) {
            agent = (agent as User).toChatUser()
        } else {
            agent as ChatUser
        }

        var missionId = ""
        val mission = requireArguments().getSerializable("mission")
        if (mission != null) {
            vm.mission.value = mission as Mission
            missionId = mission.missionId
            vm.sendBtnEnabled.value = true
        } else {
            missionId = requireArguments().getString("missionId") as String
            vm.missionRepo
                .getMissionById(missionId)
                .subscribe {
                    vm.mission.value = it
                    vm.sendBtnEnabled.value = true
                }.disposedBy(disposeBag)
        }

        val adapter = ChatRecyclerViewAdapter(vm.misAgent, agent.imageURi, clientImgUrl)
        binding.rvChat.adapter = adapter

        vm.agent.value = agent

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
                    requireContext(), vm.convertStatusColor(it.status)
                )
            )
            binding.layoutStatus.cvBackground.background = backgroundDrawable
        }

        vm.repo.fetchMessage(
            agent.email,
            missionId,
        ).subscribe {
            val originalMsgSize = adapter.cloudMessages.size
            val cloudMsgSize = it.size
            adapter.cloudMessages = it
            binding.rvChat.smoothScrollToPosition(cloudMsgSize)
            adapter.notifyItemRangeChanged(originalMsgSize, cloudMsgSize - originalMsgSize)
        }.disposedBy(disposeBag)

        binding.btnSendMsg.setOnClickListener {
            if (vm.employer.value == null) {
                Toast.makeText(
                    this.requireContext(),
                    "Something wrong on employer value init",
                    Toast.LENGTH_LONG
                ).show()
            }

            val fromUser = vm.fromUser.value!!
            val toUser = vm.toUser.value!!

            val title = getString(R.string.app_name) + ": New message from " + toUser.email
            val body = binding.etMessage.text.toString()

            sendPushMessage(title, body)

            val chatMessage = ChatMessage(text = body, isAgent = vm.misAgent)

            val chatInfo: ChatInfo = ChatInfo()
            chatInfo.employer = vm.employer.value!!.email
            chatInfo.name = vm.employer.value!!.name
            chatInfo.imageURi = vm.employer.value!!.imageURi

            chatInfo.missionName =
                "${vm.mission.value!!.serviceType} ${vm.mission.value!!.subServiceType}"
            chatInfo.users = mapOf(agent.uid to agent)

            if (!vm.misAgent) {
                agent.employerIsRead = true
                agent.agentIsRead = false
            } else {
                agent.agentIsRead = true
                agent.employerIsRead = false
            }

            vm.repo.addMessage(
                vm.agent.value!!.email,
                vm.mission.value!!.missionId,
                chatMessage,
                chatInfo,
            )

            binding.etMessage.text.clear()
        }

        vm.getNotificationToken(toEmail)

        vm.toUser.observe(viewLifecycleOwner) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title =
                "Chat With ${it.name}"
        }


        return binding.root
    }


    private fun sendPushMessage(title: String, body: String) {
        if (vm.mToken.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                activity?.let { PushMessagingHelper(it.applicationContext) }?.sendPushMessage(
                    vm.mToken,
                    title,
                    body,
                    vm.agent.value!!.email,
                    vm.mission.value!!.missionId,
                    vm.misAgent
                )
            }
        }
    }

    private fun isAgent(): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val currentTheme = pref.getInt(AppConst.CURRENT_THEME, 0)
        return currentTheme == 0
    }
}