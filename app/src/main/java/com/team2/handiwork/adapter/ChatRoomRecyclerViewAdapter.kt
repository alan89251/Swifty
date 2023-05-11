package com.team2.handiwork.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team2.handiwork.R
import com.team2.handiwork.databinding.RecycleViewChatRoomBinding
import com.team2.handiwork.models.Chat
import com.team2.handiwork.models.ServiceType
import io.reactivex.rxjava3.subjects.PublishSubject

class ChatRoomRecyclerViewAdapter() :
    RecyclerView.Adapter<ChatRoomRecyclerViewAdapter.ViewHolder>() {
    var chats = arrayListOf<Chat>()
    var selectedChat: PublishSubject<Chat> = PublishSubject.create()

    class ViewHolder(itemBinding: RecycleViewChatRoomBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val binding: RecycleViewChatRoomBinding = itemBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecycleViewChatRoomBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recycle_view_chat_room,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chats[position]
        holder.binding.chat = chat

        Glide.with(holder.itemView)
            .load(chat.icon)
            .into(holder.binding.ivIcon)

        holder.binding.ivIcon
        holder.binding.layoutChat.setOnClickListener {
            selectedChat.onNext(chat)
        }
    }

    override fun getItemCount(): Int = chats.size

}