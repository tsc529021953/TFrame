package com.sc.tmp_cw.adapter

import android.graphics.Color
import android.view.View
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.nbhope.lib_frame.bean.FileBean
import com.sc.tmp_cw.R
import com.sc.tmp_cw.databinding.ItemHomeImgBinding
import com.sc.tmp_cw.databinding.ItemVideoBinding
import timber.log.Timber

class PlaylistAdapter(items: MutableList<FileBean>, var callback: FileImgCallback) :
    BaseQuickAdapter<FileBean, BaseViewHolder>(R.layout.item_video, items)  {

    override fun convert(holder: BaseViewHolder, item: FileBean) {
        val binding = DataBindingUtil.bind<ItemVideoBinding>(holder.itemView)
        Timber.i("HETAG convert name ${item.name}")
//        binding!!.nameTv.text = item.name + "\n" + item.address
//        binding?.vm = item
        binding?.bgLy?.setOnClickListener {
            callback.onItemClick(item)
        }
        val path = "file://" + item.path
        Glide.with(binding!!.iv)
                .load(path)
                .error(R.drawable.ic_auto_df)
                .into(binding!!.iv)
        binding!!.tv.text = item.name
        if (item.status == 1) binding!!.selectIv.visibility = View.VISIBLE
        else binding!!.selectIv.visibility = View.GONE
    }

    interface FileImgCallback{

        fun onItemClick(item: FileBean)

    }
}
