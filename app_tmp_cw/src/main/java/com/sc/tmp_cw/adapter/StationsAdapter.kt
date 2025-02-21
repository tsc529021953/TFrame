package com.sc.tmp_cw.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.nbhope.lib_frame.bean.FileBean
import com.sc.tmp_cw.R
import com.sc.tmp_cw.bean.CWInfo
import com.sc.tmp_cw.databinding.ItemHomeImgBinding
import com.sc.tmp_cw.databinding.ItemStationBinding
import com.sc.tmp_cw.databinding.ItemVideoBinding
import timber.log.Timber

class StationsAdapter(items: MutableList<CWInfo.StationBean>, var callback: FileImgCallback) :
    BaseQuickAdapter<CWInfo.StationBean, BaseViewHolder>(R.layout.item_station, items)  {

    companion object {
        var itemWidth = 0
    }



    override fun convert(holder: BaseViewHolder, item: CWInfo.StationBean) {
        val binding = DataBindingUtil.bind<ItemStationBinding>(holder.itemView)
        val position = data.indexOf(item)
        var isHC = false
        if (!item.hc.isNullOrEmpty()) {
            isHC = true
            binding?.iv?.setImageResource(R.drawable.ic_line_hc)
            binding?.arrowIv?.visibility = View.VISIBLE
            if (!item.hcColor.isNullOrEmpty()) {
                binding?.arrowIv?.imageTintList = ColorStateList.valueOf(Color.parseColor(item.hcColor[0]))
            }
            binding?.hcTv?.text = item.hc[0]
        } else {
            binding?.iv?.setImageResource(R.drawable.ic_line_point)
            binding?.arrowIv?.visibility = View.GONE
        }

//        if (itemWidth > 0) {
//            System.out.println("itemWidth $itemWidth")
//            binding?.bgLy?.layoutParams?.width = itemWidth
//        }

        if (position % 2 == 0) {
            // 上面
//            binding?.bottomTv?.visibility = View.GONE
//            binding?.topTv?.visibility = View.VISIBLE
            binding?.topTv?.text = item.cn.reversed()
            (binding?.topTv?.layoutParams as ConstraintLayout.LayoutParams).bottomToTop = R.id.iv
            (binding?.topTv?.layoutParams as ConstraintLayout.LayoutParams).topToBottom = -1

            if (isHC) {
                binding?.arrowIv?.rotation = 180f
                (binding?.arrowIv?.layoutParams as ConstraintLayout.LayoutParams).topToBottom = R.id.iv
                (binding?.arrowIv?.layoutParams as ConstraintLayout.LayoutParams).bottomToTop = -1

                (binding?.hcTv?.layoutParams as ConstraintLayout.LayoutParams).topToBottom = R.id.arrow_iv
                (binding?.hcTv?.layoutParams as ConstraintLayout.LayoutParams).bottomToTop = -1
            }
        } else {
//            binding?.bottomTv?.visibility = View.VISIBLE
//            binding?.topTv?.visibility = View.GONE
            binding?.topTv?.text = item.cn
            (binding?.topTv?.layoutParams as ConstraintLayout.LayoutParams).topToBottom = R.id.iv
            (binding?.topTv?.layoutParams as ConstraintLayout.LayoutParams).bottomToTop = -1

            if (isHC) {
                binding?.arrowIv?.rotation = 0f
                (binding?.arrowIv?.layoutParams as ConstraintLayout.LayoutParams).bottomToTop = R.id.iv
                (binding?.arrowIv?.layoutParams as ConstraintLayout.LayoutParams).topToBottom = -1

                (binding?.hcTv?.layoutParams as ConstraintLayout.LayoutParams).topToBottom = -1
                (binding?.hcTv?.layoutParams as ConstraintLayout.LayoutParams).bottomToTop = R.id.arrow_iv
            }
        }
        binding.iv.setOnClickListener { callback?.onItemClick(item, position) }
        binding.topTv.setOnClickListener { callback?.onItemClick(item, position) }

//        Timber.i("HETAG convert name ${item.name}")
////        binding!!.nameTv.text = item.name + "\n" + item.address
////        binding?.vm = item
//        binding?.bgLy?.setOnClickListener {
//            callback.onItemClick(item)
//        }
//        val path = "file://" + item.path
//        Glide.with(binding!!.iv)
//                .load(path)
//                .error(R.drawable.ic_auto_df)
//                .into(binding!!.iv)
//        binding!!.tv.text = item.name
//        if (item.status == 1) binding!!.selectIv.visibility = View.VISIBLE
//        else binding!!.selectIv.visibility = View.GONE
    }

    interface FileImgCallback{

        fun onItemClick(item: CWInfo.StationBean, position: Int)

    }
}
