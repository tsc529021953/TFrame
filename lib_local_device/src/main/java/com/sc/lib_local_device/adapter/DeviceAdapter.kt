package com.sc.lib_local_device.adapter

import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.sc.lib_local_device.R
import com.sc.lib_local_device.dao.DeviceInfo
import com.sc.lib_local_device.databinding.ItemUhomeLocalDeviceBinding
import com.sc.lib_local_device.databinding.ItemUhomeLocalDeviceSmallBinding
import timber.log.Timber

/**
 * @author  tsc
 * @date  2022/9/15 10:34
 * @version 0.0.0-1
 * @description
 */
class DeviceAdapter(items: MutableList<DeviceInfo>? = null, val isSmall: Boolean? = false) :
    BaseDelegateMultiAdapter<DeviceInfo, BaseViewHolder>(items), LoadMoreModule {


    init {
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<DeviceInfo>() {
            override fun getItemType(data: List<DeviceInfo>, position: Int): Int {
                return 0
            }
        })
        getMultiTypeDelegate()?.addItemType(0,  if (isSmall!!) R.layout.item_uhome_local_device_small else R.layout.item_uhome_local_device)
//        getMultiTypeDelegate()?.addItemType(
//            UHLItemViewModel.SCENE,
//            if (isSmall!!) R.layout.item_uhome_local_screen_small else R.layout.item_uhome_local_screen
//        )
    }

    override fun convert(holder: BaseViewHolder, item: DeviceInfo) {
        val imageView = holder.getView<ImageView>(R.id.img)
        // getState(item.item.status)
        imageChange(imageView, item, true)
//                item.setOnImageStateChangeListener(object : UhomeItemViewModelN.OnImageStateChange {
//                    override fun imageStateChange(state: Boolean) {
//                        Timber.i("imageStateChange")
//                        imageChange(imageView, item, state)
//                    }
//                })
        if (isSmall!!) {
            val binding = DataBindingUtil.bind<ItemUhomeLocalDeviceSmallBinding>(holder.itemView)
            binding!!.vm = item
        } else {
            val binding = DataBindingUtil.bind<ItemUhomeLocalDeviceBinding>(holder.itemView)
            binding!!.vm = item
        }
    }

    fun selectItem(position: Int) {

    }

    private fun imageChange(imageView: ImageView, item: DeviceInfo, state: Boolean) {
        val url = ""
//            getUrl(state, item.item?.images)
        Glide.with(imageView)
            .load(url)
            .error(R.mipmap.uhome_ic_device_default)
            .placeholder(R.mipmap.uhome_ic_device_default)
            .into(imageView)
    }

    var domain = ""

    private fun getUrl(state: Boolean, images: List<String>?): String {
        images.apply {
            return domain + (images?.get(if (state) 0 else 1) ?: "")
        }
        return ""
    }

}