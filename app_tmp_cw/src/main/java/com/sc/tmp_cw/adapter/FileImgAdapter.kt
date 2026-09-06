package com.sc.tmp_cw.adapter

import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.nbhope.lib_frame.bean.FileBean
import com.sc.tmp_cw.R
import com.sc.tmp_cw.databinding.ItemHomeImgBinding
import com.sc.tmp_cw.utils.FlavorConfigUtil
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
        val glideRequest = Glide.with(binding!!.iv)
                .load(path)
            .skipMemoryCache(true) // 跳过内存缓存
            .diskCacheStrategy(DiskCacheStrategy.NONE) // 禁用磁盘缓存

        // flavorB 时添加圆角
        if (FlavorConfigUtil.isFlavorB()) {
            glideRequest.transform(RoundedCorners(24))
        }

        glideRequest.into(binding!!.iv)
        binding!!.tv.text = item.name
    }

    interface FileImgCallback{

        fun onItemClick(item: FileBean)

    }
}
