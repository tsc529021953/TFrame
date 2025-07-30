package com.sc.tmp_translate.adapter

import androidx.recyclerview.widget.DiffUtil
import com.sc.tmp_translate.bean.TransRecordBean
import com.sc.tmp_translate.bean.TransTextBean

class RecordDiffCallback(
    private val oldList: List<TransRecordBean>,
    private val newList: List<TransRecordBean>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean {
        return oldList[oldPos].bean?.timer == newList[newPos].bean?.timer
    }

    override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean {
        return oldList[oldPos] == newList[newPos]
    }
}
