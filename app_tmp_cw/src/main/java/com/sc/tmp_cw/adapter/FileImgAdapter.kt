package com.sc.tmp_cw.adapter

import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.nbhope.lib_frame.bean.FileBean
import com.sc.tmp_cw.R
import com.sc.tmp_cw.databinding.ItemHomeImgBinding
import timber.log.Timber

class FileImgAdapter(items: MutableList<FileBean>, var callback: FileImgCallback) :
    BaseQuickAdapter<FileBean, BaseViewHolder>(R.layout.item_home_img, items)  {

    override fun convert(holder: BaseViewHolder, item: FileBean) {
        val binding = DataBindingUtil.bind<ItemHomeImgBinding>(holder.itemView)
        Timber.i("HETAG convert name ${item.name}")
//        binding!!.nameTv.text = item.name + "\n" + item.address
//        binding?.vm = item
        binding?.bgLy?.setOnClickListener {
            callback.onItemClick(item)
        }
        val path = "file://" + item.path
        Glide.with(binding!!.iv)
                .load(path)
                .into(binding!!.iv)
        binding!!.tv.text = item.name
    }

    interface FileImgCallback{

        fun onItemClick(item: FileBean)

    }
}
