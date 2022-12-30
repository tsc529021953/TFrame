package com.sc.hetest.adapter

import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.sc.hetest.R
import com.sc.hetest.bean.InfoItem
import com.sc.hetest.databinding.ActivityInfoItemBinding
import com.sc.hetest.databinding.InfoItemLayoutBinding
import com.sc.hetest.vm.InfoListViewModel
import com.sc.lib_system.bean.DeviceBean
import com.sc.lib_system.util.BluetoothUtils
import timber.log.Timber

class InfoListAdapter(items: MutableList<DeviceBean>) :
    BaseQuickAdapter<DeviceBean, BaseViewHolder>(R.layout.activity_info_item, items)  {

    override fun convert(holder: BaseViewHolder, item: DeviceBean) {
        val binding = DataBindingUtil.bind<ActivityInfoItemBinding>(holder.itemView)
        Timber.i("HETAG convert name ${item.name}")
        binding!!.nameTv.text = item.name + "\n" + item.address
    }
}