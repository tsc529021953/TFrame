package com.sc.tmp_translate.adapter

import android.view.Gravity
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.sc.tmp_translate.R
import com.sc.tmp_translate.bean.TransTextBean
import com.sc.tmp_translate.databinding.ItemMoreDisplayBinding
import com.sc.tmp_translate.databinding.ItemSameDisplayBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author  tsc
 * @date  2025/7/25 14:30
 * @version 0.0.0-1
 * @description
 * 总共是三种显示状态
 * 同显
 * 主屏
 * 副屏
 */
class TransTextAdapter(data: MutableList<TransTextBean>, var isMore: Boolean, var isMain: Boolean, var fontSizeCB: ((view: View, id: Int) -> Unit)) : BaseDelegateMultiAdapter<TransTextBean, BaseViewHolder>(data = data) {

    init {
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<TransTextBean>() {
            override fun getItemType(data: List<TransTextBean>, position: Int): Int {
                return if (isMore) 0 else 1
            }
        })

        getMultiTypeDelegate()?.addItemType(0, R.layout.item_more_display)
        getMultiTypeDelegate()?.addItemType(1, R.layout.item_same_display)
    }

    override fun convert(holder: BaseViewHolder, item: TransTextBean) {
        val view = holder.itemView
        var margin = view.resources.getDimension(R.dimen.trans_bean_margin).toInt()
        when (holder.itemViewType) {
            0 -> {
                val binding = DataBindingUtil.bind<ItemMoreDisplayBinding>(view) ?: return
                binding.timeTv.text = item.time
                if (isMain) {
                    // 客人在左，显示翻译  主人在右,显示文字
                    if (item.isMaster) {
                        binding.textTv.text = item.text
                        (binding.timeTv.layoutParams as ConstraintLayout.LayoutParams).startToStart = -1
                        (binding.textTv.layoutParams as ConstraintLayout.LayoutParams).marginStart = margin
                    } else {
                        binding.textTv.text = item.transText
                        (binding.timeTv.layoutParams as ConstraintLayout.LayoutParams).endToEnd = -1
                        (binding.textTv.layoutParams as ConstraintLayout.LayoutParams).marginEnd = margin
                    }
                } else {
                    // 客人在右,显示文字  主人在左，显示翻译
                    if (item.isMaster) {
                        binding.textTv.text = item.transText
                        (binding.timeTv.layoutParams as ConstraintLayout.LayoutParams).endToEnd = -1
                        (binding.textTv.layoutParams as ConstraintLayout.LayoutParams).marginEnd = margin
                    } else {
                        binding.textTv.text = item.text
                        (binding.timeTv.layoutParams as ConstraintLayout.LayoutParams).startToStart = -1
                        (binding.textTv.layoutParams as ConstraintLayout.LayoutParams).marginStart = margin
                    }
                }
                fontSizeCB.invoke(binding.textTv, R.dimen.main_text_size1)
                fontSizeCB.invoke(binding.timeTv, R.dimen.main_text_size0)
            }
            1 -> {
                val binding = DataBindingUtil.bind<ItemSameDisplayBinding>(view) ?: return
                binding.textTv.text = item.text
                binding.timeTv.text = item.time
                binding.transTextTv.text = item.transText
                if (item.isMaster) {
                    (binding.timeTv.layoutParams as ConstraintLayout.LayoutParams).endToEnd = -1
                    (binding.textLy.layoutParams as ConstraintLayout.LayoutParams).marginEnd = margin
                } else {
                    (binding.timeTv.layoutParams as ConstraintLayout.LayoutParams).startToStart = -1
                    (binding.textLy.layoutParams as ConstraintLayout.LayoutParams).marginStart = margin
                }
                fontSizeCB.invoke(binding.textTv, R.dimen.main_text_size1)
                fontSizeCB.invoke(binding.transTextTv, R.dimen.main_text_size1)
                fontSizeCB.invoke(binding.timeTv, R.dimen.main_text_size0)
            }
        }
    }

}
