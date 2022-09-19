package com.nbhope.lib_frame.view

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 *Created by ywr on 2021/2/25 15:37
 */
class SpaceItemDecoration : ItemDecoration {
    private var mSpace: MutableList<Int>

    constructor(space: MutableList<Int>) {
        this.mSpace = space
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(mSpace[0], mSpace[1], mSpace[2], mSpace[3])
    }
}