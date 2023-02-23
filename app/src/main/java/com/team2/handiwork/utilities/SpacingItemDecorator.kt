package com.team2.handiwork.utilities

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecorator(horizontalSpace: Int) : RecyclerView.ItemDecoration() {
    private var _horizontalSpace = horizontalSpace

    init {
        _horizontalSpace = horizontalSpace
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.right = _horizontalSpace
    }
}