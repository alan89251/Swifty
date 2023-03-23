package com.team2.handiwork.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.team2.handiwork.R
import com.team2.handiwork.databinding.RecycleViewChatMessageBinding
import com.team2.handiwork.models.ChatMessage

class ChatRecyclerViewAdapter(val isAgent: Boolean) :
    RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder>() {
    var cloudMessages = arrayListOf<ChatMessage>()

    class ViewHolder(itemBinding: RecycleViewChatMessageBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val binding: RecycleViewChatMessageBinding = itemBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecycleViewChatMessageBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.recycle_view_chat_message, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = cloudMessages[position]
        holder.binding.chatMessage = message

        if (isAgent) {
            if (message.isAgent) {
                holder.binding.meChat.visibility = View.VISIBLE
                holder.binding.thirdUserChat.visibility = View.GONE
            } else {
                holder.binding.thirdUserChat.visibility = View.VISIBLE
                holder.binding.meChat.visibility = View.GONE
            }
        } else {
            // not agent
            if (message.isAgent) {
                holder.binding.thirdUserChat.visibility = View.VISIBLE
                holder.binding.meChat.visibility = View.GONE
            } else {
                holder.binding.meChat.visibility = View.VISIBLE
                holder.binding.thirdUserChat.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = cloudMessages.size

}