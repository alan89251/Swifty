package com.team2.handiwork.viewModel

import androidx.lifecycle.ViewModel
import com.team2.handiwork.models.CommentList
import com.team2.handiwork.models.User

class FragmentViewOtherCommentViewModel: ViewModel() {
    lateinit var user: User
    lateinit var comments: CommentList

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
            if (comments.isEmpty()) {
                return 0F
            }
            var ratingSum: Double = 0.0
            comments.forEach {
                ratingSum += it.rating
            }
            return (ratingSum / comments.size).toFloat()
        }

    val ratingStr: String
        get() = "%.2f".format(rating)

    val commentCountStr: String
        get() = comments.size.toString()
}