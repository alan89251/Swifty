package com.team2.handiwork.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.team2.handiwork.R
import com.team2.handiwork.databinding.RecycleViewCommentBinding
import com.team2.handiwork.models.Comment
import com.team2.handiwork.utilities.Utility

open class CommentRecyclerViewAdapter :
    RecyclerView.Adapter<CommentRecyclerViewAdapter.ViewHolder>() {
    var comments = listOf<Comment>()

    class ViewHolder(itemBinding: RecycleViewCommentBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val binding: RecycleViewCommentBinding = itemBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecycleViewCommentBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recycle_view_comment,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = comments[position]
        val desc = getDesc(item)
        holder.binding.tvCommentDesc.text = desc
        holder.binding.tvComment.text = "\"${item.content}\""
        holder.binding.ratingBar.rating = item.rating
    }

    open protected fun getDesc(comment: Comment): String {
        return "${Utility.convertLongToDate(comment.createdAt)} ${comment.firstname} ${comment.lastname}"
    }

    override fun getItemCount(): Int = comments.size
}