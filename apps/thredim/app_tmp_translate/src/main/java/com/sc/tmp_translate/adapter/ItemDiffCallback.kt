package com.sc.tmp_translate.adapter

import androidx.recyclerview.widget.DiffUtil
import com.sc.tmp_translate.bean.TransTextBean

class ItemDiffCallback(
    private val oldList: List<TransTextBean>,
    private val newList: List<TransTextBean>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean {
        return oldList[oldPos].timer == newList[newPos].timer
    }

    override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean {
        return oldList[oldPos] == newList[newPos]
    }
}
