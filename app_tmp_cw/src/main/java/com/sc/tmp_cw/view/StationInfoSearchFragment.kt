package com.sc.tmp_cw.view

import android.os.Environment
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.nbhope.lib_frame.base.BaseBindingFragment
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.utils.FileUtil
import com.nbhope.lib_frame.utils.HandleClick
import com.nbhope.lib_frame.utils.toast.ToastUtil
import com.nbhope.lib_frame.view.SpaceItemDecoration
import com.nbhope.lib_frame.view.ZoomView
import com.nbhope.lib_frame.widget.WrapLinearLayoutManager
import com.sc.tmp_cw.R
import com.sc.tmp_cw.adapter.StationsAdapter
import com.sc.tmp_cw.bean.CWInfo
import com.sc.tmp_cw.constant.MessageConstant
import com.sc.tmp_cw.constant.MessageConstant.PATH_STATION
import com.sc.tmp_cw.databinding.FragmentStationInfoSearchBinding
import com.sc.tmp_cw.service.TmpServiceDelegate
import com.sc.tmp_cw.utils.BigImageLoader
import com.sc.tmp_cw.utils.FlavorConfigUtil
import timber.log.Timber
import java.io.File

/**
 * @author  tsc
 * @date  2025/1/10 11:26
 * @version 0.0.0-1
 * @description
 */
class StationInfoSearchFragment: BaseBindingFragment<FragmentStationInfoSearchBinding, BaseViewModel>(),StationsAdapter.FileImgCallback {

    override var layoutId: Int = R.layout.fragment_station_info_search

    var adapter: StationsAdapter? = null

    private var handleClick: HandleClick? = null

    var rootWidth = 0
    var rootHeight = 0

    // flavorB：外部整图路径（/TMP/station.png|.jpg|.bmp，存在则整页改为单张可缩放大图，和 LineFragment 一致）
    private var flavorBigImage: String? = null

    override fun linkViewModel() {

    }

    override fun subscribeUi() {
        if (handleClick == null)
            handleClick = HandleClick(500, 2)

        if (FlavorConfigUtil.isFlavorB()) {
            val root = Environment.getExternalStorageDirectory().path + MessageConstant.PATH_STATION_SEARCH
            flavorBigImage = FileUtil.getPath(root)
            Timber.i("flavorB station search image: $root -> $flavorBigImage")
        }

        binding.backBtn.setOnClickListener {
            if (flavorBigImage == null) {
                // 原逻辑：浮层开着先关浮层
                if (binding.zoom.visibility == View.VISIBLE)
                    close()
                else
                    InteractiveFragment.iFragment?.back()
            } else {
                // 整图模式下返回直接退出本页
                InteractiveFragment.iFragment?.back()
            }
        }

        if (flavorBigImage == null) {
            setupStationList()
            // 双击关闭
            binding.zoom.setOnClickListener {
                handleClick?.handle {
                    close()
                }
            }
        } else {
            // flavorB：隐藏列表相关 UI，整页直接展示一张可缩放的外部图片
            // 标题/logo 按客户要求保留，悬浮显示在图片上方（层级在 flavorB 布局中调整）
            binding.lineLy.visibility = View.GONE
            binding.stationRv.visibility = View.GONE
            binding.zoom.visibility = View.VISIBLE
            loadBigImage()
        }

        binding.zoom.setOnTouchListener { view, motionEvent ->
            InteractiveFragment.iFragment?.clicked()
            // 整图模式下不需要移动和缩放：消费掉事件，ZoomView 的拖动/双指缩放不生效
            flavorBigImage != null
        }
    }

    /**
     * 原站点列表逻辑（flavorA / flavorB 未放置整图时的兜底）
     */
    private fun setupStationList() {
        val data = if (TmpServiceDelegate.service() != null) ArrayList(TmpServiceDelegate.service()!!.getCWInfo().stations) else arrayListOf()
        val size = data.size?: 0
        adapter = StationsAdapter(data, this)

        Timber.i("StationInfoSearchFragment ${adapter?.data?.size}")
        val ly = WrapLinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        if (size > 0) {
            binding.stationRv.post {
                val width = binding.stationRv.measuredWidth
                val pointWidth = resources.getDimension(R.dimen.si_item_width)
                val intervalWidth = (width - resources.getDimension(R.dimen.si_rv_padding) * 2 - resources.getDimension(R.dimen.si_item_width) * size) * 1.00 / (size - 1)
                StationsAdapter.itemWidth = (intervalWidth * 2 + pointWidth).toInt()
                binding.stationRv.addItemDecoration(SpaceItemDecoration(arrayListOf(0, 0, (intervalWidth).toInt(), 0)))
            }
        }
        binding.stationRv.layoutManager = ly
        binding.stationRv.adapter = adapter
    }

    /**
     * 加载 flavorB 整图（8K 防OOM、贴边铺满、缓存加速），见 BigImageLoader
     */
    private fun loadBigImage() {
        flavorBigImage?.let { BigImageLoader.load(this, binding.lineIv, it, File(it)) }
    }

    private fun close() {
        binding.zoom.visibility = View.GONE
        binding.zoom.reset()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (flavorBigImage != null) {
            // 整图模式：重新显示时重置缩放并重新加载（走缓存很快）
            if (!hidden) {
                binding.zoom.reset()
                loadBigImage()
            }
        } else {
            if (!hidden) {
                binding.zoom.reset()
            } else {
                close()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (flavorBigImage == null) close()
    }

    override fun initData() {

    }

    override fun onItemClick(item: CWInfo.StationBean, position: Int) {
        InteractiveFragment.iFragment?.clicked()
        // 到路径下查找是否有对应
        val root = Environment.getExternalStorageDirectory().path + PATH_STATION + (position + 1)
        var path = FileUtil.getPath(root)
        if (path == null) {
            ToastUtil.showS("未找到站点信息！")
            return
        }
        if (binding.zoom.visibility == View.GONE) {
            binding.zoom.visibility = View.VISIBLE
        } else binding.zoom.reset()
        Glide.with(binding!!.lineIv)
                .load(path)
            .skipMemoryCache(true) // 跳过内存缓存
            .diskCacheStrategy(DiskCacheStrategy.NONE) // 禁用磁盘缓存
                .into(binding!!.lineIv)
    }
}
