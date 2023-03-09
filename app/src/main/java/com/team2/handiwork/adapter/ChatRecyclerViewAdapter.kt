package com.team2.handiwork.adapter

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
    // todo store db?
    val messages = arrayListOf<ChatMessage>(
        ChatMessage(text = "Hello! I'm interested!"),
        ChatMessage(text = "Do you have the tools?"),
        ChatMessage(text = "No, we dont't, Please bring it yourself. Thanks!", isAgent = true),
        ChatMessage(text = "Do you have the tools?", isAgent = false),
    )

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
        val message = messages[position]
        holder.binding.chatMessage = message
        if (position == 0 || position == 1) {
            holder.binding.systemChatDialog.visibility = View.VISIBLE
        } else if (isAgent) {
            if (message.isAgent) {
                holder.binding.meChat.visibility = View.VISIBLE
            } else {
                holder.binding.thirdUserChat.visibility = View.VISIBLE
            }
        } else {
            // not agent
            if (message.isAgent) {
                holder.binding.thirdUserChat.visibility = View.VISIBLE
            } else {
                holder.binding.meChat.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int = messages.size

}