package com.team2.handiwork.adapter

import com.team2.handiwork.models.Comment
import com.team2.handiwork.utilities.Utility

class MoreCommentRecyclerViewAdapter: CommentRecyclerViewAdapter() {
    override fun getDesc(comment: Comment): String {
        return "${Utility.convertLongToDate(comment.createdAt)} ${comment.missionSubServiceType} - ${descOfCommentWriter(comment)}"
    }

    private fun descOfCommentWriter(comment: Comment): String {
        return if (comment.isFromAgent) "review from agent" else "review from employer"
    }
}