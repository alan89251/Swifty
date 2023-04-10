package com.team2.handiwork.fragments.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.team2.handiwork.AppConst
import com.team2.handiwork.R
import com.team2.handiwork.adapter.ChatRoomRecyclerViewAdapter
import com.team2.handiwork.base.fragment.DisposeFragment
import com.team2.handiwork.databinding.FragmentChatRoomBinding
import com.team2.handiwork.models.Chat
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.utilities.Ext.Companion.disposedBy
import com.team2.handiwork.viewModel.chat.FragmentChatRoomViewModel

class ChatRoomFragment : DisposeFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentChatRoomBinding.inflate(inflater, container, false)
        val vm = FragmentChatRoomViewModel()
        binding.vm = vm
        binding.lifecycleOwner = this

        val adapter = ChatRoomRecyclerViewAdapter()
        binding.rvChat.adapter = adapter

        // share preference get theme
        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val currentTheme = pref.getInt(AppConst.CURRENT_THEME, 0)
        val isAgent = currentTheme == 0

        adapter.selectedChat.subscribe {
            val bundle = Bundle()
            val agent = vm.chatUserMap[it.uid]!!
            bundle.putSerializable("agent", agent)
            bundle.putSerializable("missionId", vm.chatInfo.missionId)

            if (isAgent) {
                bundle.putSerializable("toEmail", vm.chatInfo.employer)
                bundle.putSerializable("clientImgUrl", it.icon)
                vm.repo.updateChatIsReadByAgent(it.missionId, it.uid)
            } else {
                bundle.putSerializable("toEmail", agent.email)
                bundle.putSerializable("clientImgUrl", UserData.currentUserData.imageURi)
                vm.repo.updateChatIsReadByEmployer(it.missionId, it.uid)
            }

            findNavController().navigate(
                R.id.action_chatRoomFragment_to_chatFragment,
                bundle,
            )
        }

        if (isAgent) {
            Log.d(UserData.currentUserData.uid, UserData.currentUserData.email)
            vm.repo.fetchChatInfoByAgent(
                UserData.currentUserData.uid,
                UserData.currentUserData.email,
            ).subscribe {
                val list = it.flatMap { chatInfo ->
                    vm.chatInfo = chatInfo
                    val chats = chatInfo.users.values.map { userMap ->
                        val chat = Chat()
                        vm.chatUserMap[userMap.uid] = userMap
                        chat.missionName = chatInfo.missionName
                        chat.icon = chatInfo.imageURi
                        chat.uid = userMap.uid
                        chat.name = chatInfo.name
                        chat.isRead = userMap.agentIsRead
                        chat.missionId = chatInfo.missionId
                        chat
                    }
                    chats
                }
                val originalSize = adapter.chats.size
                val currentSize = list.size
                adapter.chats = ArrayList(list)
                adapter.notifyItemRangeChanged(originalSize, currentSize - originalSize)
            }.disposedBy(disposeBag)

        } else {
            vm.repo.fetchChatInfoByEmployer(
                UserData.currentUserData.email
            ).subscribe {
                val list = it.flatMap { chatInfo ->
                    vm.chatInfo = chatInfo
                    val chats = chatInfo.users.values.map { userMap ->
                        val chat = Chat()
                        vm.chatUserMap[userMap.uid] = userMap
                        chat.missionName = chatInfo.missionName
                        chat.icon = userMap.imageURi
                        chat.uid = userMap.uid
                        chat.name = userMap.name
                        chat.isRead = userMap.employerIsRead
                        chat.missionId = chatInfo.missionId
                        chat
                    }
                    chats
                }
                val originalSize = adapter.chats.size
                val currentSize = list.size
                adapter.chats = ArrayList(list)
                adapter.notifyItemRangeChanged(originalSize, currentSize - originalSize)
            }.disposedBy(disposeBag)
        }

        return binding.root
    }
}