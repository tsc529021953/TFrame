package com.sc.tmp_cw.view

import android.os.Environment
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.nbhope.lib_frame.base.BaseBindingFragment
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.utils.FileUtil
import com.nbhope.lib_frame.utils.HandleClick
import com.nbhope.lib_frame.utils.HopeUtils
import com.nbhope.lib_frame.utils.toast.ToastUtil
import com.nbhope.lib_frame.view.SpaceItemDecoration
import com.nbhope.lib_frame.view.ZoomView
import com.nbhope.lib_frame.widget.WrapLinearLayoutManager
import com.sc.tmp_cw.R
import com.sc.tmp_cw.adapter.PlaylistAdapter
import com.sc.tmp_cw.adapter.StationsAdapter
import com.sc.tmp_cw.bean.CWInfo
import com.sc.tmp_cw.constant.MessageConstant
import com.sc.tmp_cw.constant.MessageConstant.PATH_STATION
import com.sc.tmp_cw.databinding.FragmentStationInfoSearchBinding
import com.sc.tmp_cw.service.MessageHandler
import com.sc.tmp_cw.service.TmpServiceDelegate
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

    override fun linkViewModel() {

    }

    override fun subscribeUi() {
        if (handleClick == null)
            handleClick = HandleClick(500, 2)
        binding.backBtn.setOnClickListener {
            if (binding.zoom.visibility == View.VISIBLE)
                close()
            else
                InteractiveFragment.iFragment?.back()
        }

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
        // 双击关闭
        binding.zoom.setOnTouchListener { view, motionEvent ->
            InteractiveFragment.iFragment?.clicked()
            return@setOnTouchListener false
        }
        binding.zoom.setOnClickListener {
            handleClick?.handle {
                close()
            }
        }
    }

    private fun close() {
        binding.zoom.visibility = View.GONE
        binding.zoom.reset()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            binding.zoom.reset()
        } else {
            close()
        }
    }

    override fun onPause() {
        super.onPause()
        close()
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
                .into(binding!!.lineIv)
    }
}
