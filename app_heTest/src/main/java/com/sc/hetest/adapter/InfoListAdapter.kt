package com.sc.hetest.adapter

import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.sc.hetest.R
import com.sc.hetest.databinding.ActivityInfoItemBinding
import com.sc.hetest.databinding.InfoItemLayoutBinding
import com.sc.hetest.vm.InfoListViewModel
import com.sc.lib_system.util.BluetoothUtils

class InfoListAdapter(data: MutableList<BluetoothUtils.DeviceBean>) :
    BaseQuickAdapter<BluetoothUtils.DeviceBean, BaseViewHolder>(R.layout.activity_info_item, data) {
    override fun convert(holder: BaseViewHolder, item: BluetoothUtils.DeviceBean) {
        val binding = DataBindingUtil.bind<ActivityInfoItemBinding>(holder.itemView)
        binding!!.nameTv.text = item.name
    }
}