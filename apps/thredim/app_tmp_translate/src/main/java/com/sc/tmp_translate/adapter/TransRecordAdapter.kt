package com.sc.tmp_translate.adapter

import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.sc.tmp_translate.R
import com.sc.tmp_translate.bean.TransRecordBean
import com.sc.tmp_translate.da.RecordRepository
import com.sc.tmp_translate.databinding.ItemRecordBinding
import com.sc.tmp_translate.service.TmpServiceDelegate

/**
 * @author  tsc
 * @date  2025/7/25 14:30
 * @version 0.0.0-1
 * @description
 */
class TransRecordAdapter(data: MutableList<TransRecordBean>, var sp: SharedPreferencesManager, var fontSizeCB: ((view: View, id: Int) -> Unit)) :
    BaseDelegateMultiAdapter<TransRecordBean, BaseViewHolder>(data = data) {

    init {
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<TransRecordBean>() {
            override fun getItemType(data: List<TransRecordBean>, position: Int): Int {
                return 0
            }
        })

        getMultiTypeDelegate()?.addItemType(0, R.layout.item_record)
    }

    fun refreshNumber() {
        try {
            for (i in 0 until data.size) {
                (getViewByPosition(i, R.id.num_tv) as TextView).text = "${(data.indexOf(data[i]) + 1)}."
            }
        } catch (e: Exception) {}
    }

    override fun convert(holder: BaseViewHolder, item: TransRecordBean) {
        val view = holder.itemView
        val binding = DataBindingUtil.bind<ItemRecordBinding>(view) ?: return
        binding.tmp = TmpServiceDelegate.getInstance()
        binding.vm = item
        val zh = context.resources.getString(R.string.language_zn)
        if (item.bean?.isMaster == false) {
            binding.rightLangTv.text = zh
            binding.leftLang2Tv.text = item.lang
        } else {
            binding.rightLangTv.text = item.lang
            binding.leftLang2Tv.text = zh
        }
        binding.numTv.text = "${(data.indexOf(item) + 1)}."
        fontSizeCB.invoke(binding.numTv, R.dimen.main_text_size0)
        fontSizeCB.invoke(binding.textTv, R.dimen.main_text_size0)
        fontSizeCB.invoke(binding.leftLang2Tv, R.dimen.main_text_size0)
        fontSizeCB.invoke(binding.rightLangTv, R.dimen.main_text_size0)
        binding.removeIv.setOnClickListener {
            RecordRepository.removeItem(item, sp)
        }
        binding.playIv.setOnClickListener {
            TmpServiceDelegate.getInstance().setTransPlay(item)
        }
    }

    fun submitList(newItems: List<TransRecordBean>) {
        val diff = DiffUtil.calculateDiff(RecordDiffCallback(data, newItems))
        data = newItems.toMutableList()
        diff.dispatchUpdatesTo(this)
    }
}
