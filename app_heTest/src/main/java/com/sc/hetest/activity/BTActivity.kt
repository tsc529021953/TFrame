package com.sc.hetest.activity

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.os.Build
import android.view.View
import android.widget.ListAdapter
import android.widget.SimpleAdapter
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.SPUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.animation.AlphaInAnimation
import com.chad.library.adapter.base.listener.OnItemLongClickListener
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.common.BasePath
import com.nbhope.lib_frame.utils.HopeUtils
import com.nbhope.lib_frame.utils.PermissionUtil
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.nbhope.lib_frame.view.SpaceItemDecoration
import com.nbhope.lib_frame.widget.WrapGridLayoutManager
import com.nbhope.phmusic.player.AudioFocusManager
import com.nbhope.phmusic.player.SilentPlayer
import com.sc.hetest.R
import com.sc.hetest.adapter.InfoItemAdapter
import com.sc.hetest.adapter.InfoListAdapter
import com.sc.hetest.bean.InfoItem
import com.sc.hetest.common.HECommon
import com.sc.hetest.common.HEPath
import com.sc.hetest.databinding.*
import com.sc.hetest.vm.*
import com.sc.lib_system.bean.DeviceBean
import com.sc.lib_system.util.BluetoothUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.ArrayList
import javax.inject.Inject

/**
 * author: sc
 * date: 2022/12/10
 */
@Route(path = HEPath.BT_PATH)
class BTActivity : BaseBindingActivity<ActivityInfoListBinding, InfoListViewModel>(),
    BluetoothUtils.BluetoothInterface {

    companion object {

    }

    private var PERMISSIONS: Array<String> = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    var list = ArrayList<DeviceBean>()

    override var layoutId: Int = R.layout.activity_info_list

    var bluetoothAdapter: BluetoothAdapter? = null

    var adapter: InfoListAdapter? = null

    override fun subscribeUi() {
        binding.failTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.BT_PATH, InfoItem.STATE_FAIL)
            finish()
        }
        binding.successTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.BT_PATH, InfoItem.STATE_SUCCESS)
            finish()
        }
        initAdapter()
        binding.listSrl.setOnRefreshListener {
            Timber.i("HETAG ${binding.listSrl.isRefreshing} ${BluetoothUtils.getInstance().isEnabled}")
            if (!BluetoothUtils.getInstance().isEnabled) return@setOnRefreshListener
            Timber.i("HETAG 刷新列表")
            startDiscovery()
//            viewModel.viewModelScope.launch(Dispatchers.Main) {
//                delay(4000)
//                Timber.i("HETAG 到时")
//                if (binding.listSrl.isRefreshing)
//                    binding.listSrl.isRefreshing = false
//            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!PermissionUtil.hasPermissionsGranted(this, PERMISSIONS)) {
                requestPermissions(PERMISSIONS, 10086)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10086) {
            if (PermissionUtil.hasPermissionsGranted(this, PERMISSIONS)) {
                startDiscovery()
            }
        }
    }

    override fun initData() {
        viewModel.initData()
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        BluetoothUtils.getInstance().initBluetooth(this);
        BluetoothUtils.getInstance().setBluetoothListener(this);
        getBTInfo()
        Timber.i("HETAG BluetoothUtils.getInstance().isEnabled ${BluetoothUtils.getInstance().isEnabled}")
        if (!BluetoothUtils.getInstance().isEnabled) {
            BluetoothUtils.getInstance().enable()
            viewModel.viewModelScope.launch(Dispatchers.Main) {
                Timber.i("HETAG 延时一秒")
                delay(2000)
                Timber.i("HETAG 延时完成")
                getBTInfo(true)
            }
        }
    }

    private fun initAdapter() {
        adapter = InfoListAdapter(list)
        adapter?.setHasStableIds(true);
        binding.listRv.adapter = adapter
        adapter?.adapterAnimation = AlphaInAnimation()
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val animatorLeft = binding.listRv.itemAnimator;
        if (animatorLeft is SimpleItemAnimator) {
            animatorLeft.supportsChangeAnimations = false;
        }
//        adapter?.setOnItemLongClickListener(object : OnItemLongClickListener {
//            override fun onItemLongClick(
//                adapter: BaseQuickAdapter<*, *>,
//                view: View,
//                position: Int
//            ): Boolean {
//
//                return false
//            }
//        })
//        adapter?.setOnItemClickListener { adapter, view, position ->
////            val item = infoList?.get(position)
////            ARouter.getInstance().build(item?.path).navigation(this )
//        }
        binding.listRv.layoutManager = layoutManager
        binding.listRv.addItemDecoration(SpaceItemDecoration(arrayOf(5, 0, 5, 5).map {
            HopeUtils.dip2px(
                this,
                it.toFloat()
            )
        }.toMutableList()))

//        binding.listRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//            }
//
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//            }
//        })
        Timber.i("HETAG initAdapter")
    }

    private fun getBTInfo(isDouble: Boolean = false, discovery: Boolean = true) {
        val info = StringBuilder()
        if (bluetoothAdapter == null) {
            info.append("当前设备不支持蓝牙！")
            return
        }
        val res = bluetoothAdapter?.isEnabled == true
        Timber.i("HETAG 当前蓝牙状态 $res")
        info.append(if (res) "蓝牙已打开" else if (isDouble) "蓝牙打开失败" else "蓝牙尚未打开,正在为您打开")
        info.append("\n")
        info.append("蓝牙名称：${bluetoothAdapter?.name} \n")
        info.append("MAC地址：${DeviceUtils.getMacAddress()} \n")
        if (res) {
            info.append("蓝牙数量：${list?.size} \n")
            if (discovery)
                startDiscovery()
        }
        viewModel.info.set(info.toString())
    }

    private fun startDiscovery() {
        binding.listSrl.isRefreshing = true
        if (BluetoothUtils.getInstance().isEnabled)
            BluetoothUtils.getInstance().startDiscovery()
    }

    @Inject
    lateinit var spManager: SharedPreferencesManager

    @Inject
    override lateinit var viewModel: InfoListViewModel
    override fun linkViewModel() {
        binding.vm = viewModel
    }

    override fun getBluetoothList(deviceBeans: ArrayList<DeviceBean>?) {
        binding.listSrl.isRefreshing = false
        if (deviceBeans == null) return
        var da = ArrayList<DeviceBean>()
        da.addAll(deviceBeans)
        viewModel.viewModelScope.launch(Dispatchers.Main) {
            Timber.i("HETAG ${da?.size}")
            if (!da.isNullOrEmpty() && da.size > 0) {
                list = da
                adapter?.data = list
                adapter?.notifyDataSetChanged()
                getBTInfo(true, false)
            } else {
//                startDiscovery()
            }
        }
    }
}