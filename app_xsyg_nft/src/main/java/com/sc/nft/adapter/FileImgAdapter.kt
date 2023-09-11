package com.sc.nft.adapter

import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.sc.nft.R
import com.sc.nft.bean.FileImgBean
import com.sc.nft.databinding.ItemHomeImgBinding
import timber.log.Timber

class FileImgAdapter(items: MutableList<FileImgBean>, var callback: FileImgCallback) :
    BaseQuickAdapter<FileImgBean, BaseViewHolder>(R.layout.item_home_img, items)  {

    override fun convert(holder: BaseViewHolder, item: FileImgBean) {
        val binding = DataBindingUtil.bind<ItemHomeImgBinding>(holder.itemView)
        Timber.i("HETAG convert name ${item.name}")
//        binding!!.nameTv.text = item.name + "\n" + item.address
        binding?.vm = item
        binding?.bgLy?.setOnClickListener {
            callback.onItemClick(item)
        }
    }

    interface FileImgCallback{

        fun onItemClick(item: FileImgBean)

    }
}