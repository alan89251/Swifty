package com.team2.handiwork.fragments.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.team2.handiwork.R
import com.team2.handiwork.adapter.ChatRoomRecyclerViewAdapter
import com.team2.handiwork.base.fragment.DisposalFragment
import com.team2.handiwork.databinding.FragmentChatRoomBinding
import com.team2.handiwork.models.Chat
import com.team2.handiwork.singleton.UserData
import com.team2.handiwork.utilities.Ext.Companion.disposedBy
import com.team2.handiwork.viewModel.chat.FragmentChatRoomViewModel

class ChatRoomFragment : DisposalFragment() {

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

        adapter.selectedChat.subscribe {
            vm.repo.updateChatIsRead(it.missionId, it.uid)
            val bundle = Bundle()
            bundle.putSerializable("agent", vm.chatUserMap[it.uid])
            bundle.putSerializable("missionId", vm.chatInfo.missionId)

            findNavController().navigate(
                R.id.action_chatRoomFragment_to_chatFragment,
                bundle,
            )
        }


        vm.repo.fetchChatInfo(UserData.currentUserData.email).subscribe {
            val list = it.flatMap { chatInfo ->
                vm.chatInfo = chatInfo
                val chats = chatInfo.users.values.map { userMap ->
                    val chat = Chat()
                    vm.chatUserMap[userMap.uid] = userMap
                    chat.missionName = chatInfo.missionName
                    chat.icon = userMap.imageURi
                    chat.uid = userMap.uid
                    chat.name = "${userMap.firstName} ${userMap.lastName}"
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

        return binding.root
    }
}