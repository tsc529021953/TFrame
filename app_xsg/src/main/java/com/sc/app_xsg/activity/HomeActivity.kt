package com.sc.app_xsg.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.animation.AlphaInAnimation
import com.chad.library.adapter.base.listener.OnItemLongClickListener
import com.nbhope.app_uhome_local.event.UHomeLocalEvent
import com.sc.app_xsg.R
import com.sc.app_xsg.databinding.ActivityHomeBinding
import com.sc.app_xsg.vm.HomeViewModel
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.common.BasePath
import com.nbhope.lib_frame.utils.HopeUtils
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.nbhope.lib_frame.view.SpaceItemDecoration
import com.nbhope.lib_frame.widget.WrapGridLayoutManager
import com.nbhope.phmina.base.MinaConstants
import com.sc.lib_local_device.adapter.DeviceAdapter
import com.sc.lib_local_device.common.DeviceCommon
import com.sc.lib_local_device.dao.DeviceInfo
import com.sc.lib_local_device.service.MainService
import com.sc.lib_local_device.service.MainServiceImpl
import timber.log.Timber
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2022/8/1 13:12
 * @version 0.0.0-1
 * @description
 * 主页 显示一张主页图片
 * 此处退出需要将界面退出去
 */
@Route(path = BasePath.HOME_PATH)
class HomeActivity : BaseBindingActivity<ActivityHomeBinding, HomeViewModel>() {

    override var layoutId: Int = R.layout.activity_home

    @Inject
    lateinit var spManager: SharedPreferencesManager

    @Autowired
    @JvmField
    var service: MainService? = null

    var adapter: DeviceAdapter? = null

    var devices = ArrayList<DeviceInfo>()

    override fun subscribeUi() {
        Timber.i("$TAG deviceType ${DeviceCommon.deviceType} ${DeviceCommon.recordDeviceInfo != null}")
        if (DeviceCommon.deviceType == DeviceCommon.DeviceType.Ctrl) {
            // Ctrl
            binding.deviceIdTv.visibility = View.GONE
            // 判断有没有设备信息
            if (DeviceCommon.recordDeviceInfo != null) {
                // 有记录的设备信息 关闭整个界面
                binding.dsLayout.visibility = View.GONE
                // 根据当前的连接状态，
            } else {
                // adpater
                initAdapter()
            }
        } else {
            // View
            binding.rvDevice.visibility = View.GONE
        }
    }

    override fun initData() {
        // 监听事件 控制端的
        LiveEBUtil.regist(UHomeLocalEvent::class.java, this, uHomeLocalObserver)
    }

    override fun linkViewModel() {
        binding.vm = viewModel
        viewModel.initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        LiveEBUtil.unRegist(UHomeLocalEvent::class.java, uHomeLocalObserver)
    }

    fun initAdapter() {
        adapter = DeviceAdapter(ArrayList())
        adapter?.setHasStableIds(true)
        adapter?.adapterAnimation = AlphaInAnimation()
        val layoutManager: LinearLayoutManager = WrapGridLayoutManager(this, 4, true)
        val animatorLeft = binding.rvDevice.itemAnimator;
        if (animatorLeft is SimpleItemAnimator) {
            animatorLeft.supportsChangeAnimations = false;
        }
        adapter?.setOnItemClickListener { adapter, view, position ->
            // 选择设备

            // 记录设备

            // 尝试连接

        }
        adapter?.loadMoreModule?.setOnLoadMoreListener {
            Timber.i("LTAG ????????")
        }
        binding.rvDevice.layoutManager = layoutManager
        binding.rvDevice.addItemDecoration(SpaceItemDecoration(arrayOf(10, 0, 10, 20).map {
            HopeUtils.dip2px(
                this,
                it.toFloat()
            )
        }.toMutableList()))
        binding.rvDevice.adapter = adapter
        binding.rvDevice.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
//                if (newState == RecyclerView.SCROLL_STATE_DRAGGING
//                    || newState == RecyclerView.SCROLL_STATE_SETTLING) {
//                    sIsScrolling = true;
//                    Glide.with(activity!!).pauseRequests();
//                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    if (sIsScrolling == true) {
//                        Glide.with(activity!!).resumeRequests();
//                    }
//                    sIsScrolling = false;
//                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        adapter?.setEmptyView(R.layout.layout_device_empty)
        adapter?.setNewInstance(devices)
        adapter?.loadMoreModule?.isAutoLoadMore = false
        adapter?.loadMoreModule?.isEnableLoadMore = false
    }

    @Inject
    override lateinit var viewModel: HomeViewModel

    private val uHomeLocalObserver = Observer<Any> {
        it as UHomeLocalEvent
        when (it.cmd) {
            MinaConstants.CMD_DISCOVER_RS -> {
                var item = it.data as DeviceInfo
                Timber.i("$TAG 设备更新！${adapter == null}")
                var dev = devices.find { d -> d.code == item.code }
                if (dev == null) {
                    devices.add(item)

//                    adapter?.addData(item)
                    Timber.i("$TAG 设备更新2！${devices.size}")
                    adapter?.notifyItemChanged(devices.size)
                    adapter?.loadMoreModule?.loadMoreComplete()
//                    adapter?.setNewInstance()
                } else {
                    if (dev != item) {
                        // 信息更新了

                    }
                }
            }
        }
    }
}