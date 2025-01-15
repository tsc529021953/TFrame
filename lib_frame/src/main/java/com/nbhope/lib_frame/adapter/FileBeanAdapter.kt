package com.nbhope.lib_frame.adapter

import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.nbhope.lib_frame.R
import com.nbhope.lib_frame.bean.FileBean
import timber.log.Timber
import com.nbhope.lib_frame.databinding.ItemFileBeanBinding

class FileBeanAdapter(items: MutableList<FileBean>, var callback: FileBeanCallback) :
    BaseQuickAdapter<FileBean, BaseViewHolder>(R.layout.item_file_bean, items)  {

    override fun convert(holder: BaseViewHolder, item: FileBean) {
        val binding = DataBindingUtil.bind<ItemFileBeanBinding>(holder.itemView)
        Timber.i("HETAG convert name ${item.name}")
//        binding!!.nameTv.text = item.name + "\n" + item.address
//        binding?.vm = item
        binding?.bgLy?.setOnClickListener {
            callback.onItemClick(item)
        }
        binding?.tv?.text = item.name
    }

    interface FileBeanCallback{

        fun onItemClick(item: FileBean)

    }
}
