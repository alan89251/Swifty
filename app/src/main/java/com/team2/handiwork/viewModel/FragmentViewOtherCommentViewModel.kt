package com.team2.handiwork.viewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team2.handiwork.adapter.MoreCommentRecyclerViewAdapter
import com.team2.handiwork.models.Comment
import com.team2.handiwork.models.User

class FragmentViewOtherCommentViewModel: ViewModel() {
    lateinit var user: User
    lateinit var allComments: List<Comment>
    lateinit var commentsFromAgents: List<Comment>
    lateinit var commentsFromEmployers: List<Comment>
    fun setCommentLists(comments: List<Comment>) {
        allComments = comments
        commentsFromAgents = comments.filter { it.isFromAgent }
        commentsFromEmployers = comments.filter { !it.isFromAgent }
    }
    val commentListType = MutableLiveData(CommentListType.ALL)
    val commentListAdapter = MediatorLiveData<MoreCommentRecyclerViewAdapter>()

    val categories: String
        get() {
            var str = ""
            user.serviceTypeList.forEach {
                str = str + it.name + "\n"
            }
            return str.trimEnd('\n')
        }

    val cancellationRate: String
        get() {
            val total = user.confirmedCancellationCount + user.finishMissionCount
            return if (total == 0) {
                "0%"
            } else {
                val rate = (user.confirmedCancellationCount.toDouble() / total) * 100
                if (rate == rate.toInt().toDouble()) {
                    "%.0f%%".format(rate)
                } else {
                    "%.1f%%".format(rate)
                }
            }
        }

    val rating: Float
        get() {
            if (allComments.isEmpty()) {
                return 0F
            }
            var ratingSum: Double = 0.0
            allComments.forEach {
                ratingSum += it.rating
            }
            return (ratingSum / allComments.size).toFloat()
        }

    val ratingStr: String
        get() = "%.2f".format(rating)

    val commentCountStr: String
        get() = allComments.size.toString()

    init {
        commentListAdapter.addSource(commentListType) {
            val commentsAdapter = MoreCommentRecyclerViewAdapter()
            when (it) {
                CommentListType.ALL -> commentsAdapter.comments = allComments
                CommentListType.AGENT -> commentsAdapter.comments = commentsFromAgents
                CommentListType.EMPLOYER -> commentsAdapter.comments = commentsFromEmployers
            }
            commentListAdapter.value = commentsAdapter
        }
    }

    enum class CommentListType {
        ALL,
        AGENT,
        EMPLOYER
    }
}