package com.sc.hetest.adapter

import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.sc.hetest.R
import com.sc.hetest.bean.InfoItem
import com.sc.hetest.databinding.InfoItemLayoutBinding
import timber.log.Timber

/**
 * author: sc
 * date: 2022/12/10
 */
class InfoItemAdapter(items: MutableList<InfoItem>? = null):
    BaseDelegateMultiAdapter<InfoItem, BaseViewHolder>(items) {

    init {
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<InfoItem>() {
            override fun getItemType(data: List<InfoItem>, position: Int): Int {
                return 0
            }
        })
        getMultiTypeDelegate()?.addItemType(0, R.layout.info_item_layout)

    }

    override fun convert(holder: BaseViewHolder, item: InfoItem) {
        val binding = DataBindingUtil.bind<InfoItemLayoutBinding>(holder.itemView)
        val success = holder.itemView.context.resources.getDrawable(R.drawable.info_item_shape_success)
        val fail = holder.itemView.context.resources.getDrawable(R.drawable.info_item_shape_fail)
        binding?.titleTv?.text = item.title
        Timber.i("HETAG ${item.state}")
        when (item.state) {
            InfoItem.STATE_SUCCESS -> {
                binding?.itLy?.background = success
            }
            InfoItem.STATE_FAIL -> {
                binding?.itLy?.background = fail
            }
        }
    }

}