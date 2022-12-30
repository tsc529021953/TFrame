package com.sc.hetest.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.Utils
import com.chad.library.adapter.base.animation.AlphaInAnimation
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.utils.HopeUtils
import com.nbhope.lib_frame.utils.PermissionUtil
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.nbhope.lib_frame.view.SpaceItemDecoration
import com.sc.hetest.R
import com.sc.hetest.adapter.InfoListAdapter
import com.sc.hetest.bean.InfoItem
import com.sc.hetest.common.HEPath
import com.sc.hetest.databinding.ActivityInfoListBinding
import com.sc.hetest.vm.InfoListViewModel
import com.sc.lib_system.bean.DeviceBean
import com.sc.lib_system.util.BluetoothUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


/**
 * author: sc
 * date: 2022/12/10
 */
@Route(path = HEPath.WIFI_PATH)
class WIFIActivity : BaseBindingActivity<ActivityInfoListBinding, InfoListViewModel>(){

    companion object{

    }

    private var PERMISSIONS: Array<String> = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.CHANGE_WIFI_STATE,
    )

    override var layoutId: Int = R.layout.activity_info_list

    var adapter: InfoListAdapter? = null

    var list = ArrayList<DeviceBean>()

    private val listener = object : NetworkUtils.OnNetworkStatusChangedListener{
        override fun onDisconnected() {
            getWifiInfo()
        }

        override fun onConnected(networkType: NetworkUtils.NetworkType?) {
            getWifiInfo()
        }
    }

    private val consumer = Utils.Consumer<NetworkUtils.WifiScanResults> {

    }

    override fun subscribeUi() {
        binding.failTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.WIFI_PATH, InfoItem.STATE_FAIL)
            finish()
        }
        binding.successTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.WIFI_PATH, InfoItem.STATE_SUCCESS)
            finish()
        }
        initAdapter()

        binding.listSrl.setOnRefreshListener {
            Timber.i("HETAG ${binding.listSrl.isRefreshing} ${BluetoothUtils.getInstance().isEnabled}")
            Timber.i("HETAG 刷新列表")
            getWifiList()
            viewModel.viewModelScope.launch(Dispatchers.Main) {
                delay(4000)
                Timber.i("HETAG 到时")
                if (binding.listSrl.isRefreshing)
                    binding.listSrl.isRefreshing = false
            }
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
                getWifiInfo()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            10086 -> {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(this)) {
//                    getWifiInfo()
//                } else
                    getWifiInfo(2)
            }
        }
    }

    override fun initData() {
        viewModel.initData()
        getWifiInfo()
        NetworkUtils.registerNetworkStatusChangedListener(listener)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NetworkUtils.addOnWifiChangedConsumer(consumer)
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

    private fun getWifiList() {
        if (NetworkUtils.getWifiEnabled()) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                binding.listSrl.isRefreshing = true
                var result = NetworkUtils.getWifiScanResult()
                Timber.i("HETAG list ${result.allResults.size}")
                list.clear()
                list = result.allResults.map {
                    DeviceBean(it.SSID, it.BSSID)
                } as ArrayList<DeviceBean>
                viewModel.viewModelScope.launch(Dispatchers.Main) {
                    adapter?.setNewInstance(list)
                    adapter?.notifyDataSetChanged()
                    binding.listSrl.isRefreshing = false
                }
            }
        }
    }
    
    private fun getWifiInfo(index: Int = 0) {
        val info = StringBuilder()
        if (!PermissionUtil.hasPermissionsGranted(this, PERMISSIONS)) {
            info.append("正在申请权限\n")
        }
        else if (NetworkUtils.isConnected()) {
            info.append("网络类型：${NetworkUtils.getNetworkType()}" + "\n")
            if (NetworkUtils.isWifiConnected()) {
                val wifiManager = this.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiManager.connectionInfo
                Timber.d("HETAG ${wifiInfo.toString()}")
                info.append("网络名称：${wifiInfo.ssid}\n")
                val level = WifiManager.calculateSignalLevel(wifiInfo.rssi, 5)
                info.append("网络强度：$level\n")
                info.append("链接速度：${wifiInfo.linkSpeed}${WifiInfo.LINK_SPEED_UNITS}\n")
            }
            info.append("IP地址：${NetworkUtils.getIPAddress(true)}\n")
            info.append("网关 IP：${NetworkUtils.getGatewayByWifi()}\n")
            info.append("子网掩码 IP：${NetworkUtils.getNetMaskByWifi()}\n")
            info.append("服务端 IP：${NetworkUtils.getServerAddressByWifi()}\n")
//            info.append("域名IP：${NetworkUtils.getDomainAddress()}")
            getWifiList()
        } else {
            info.append("当前未连接网络\n")
            if (!NetworkUtils.getWifiEnabled()) {
                if (index == 1) {
                    info.append("WIFI打开失败$index\n")
                    if (index < 2 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        info.append("将为您打开设置界面$index\n")
                        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivityForResult(intent, 10086)
//                        if (!Settings.System.canWrite(this)) {
//                            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:$packageName"))
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                            startActivityForResult(intent, 10086)
//                        }
                    }
                } else {
                    info.append("WIFI未打开，正在为您打开\n")
                    NetworkUtils.setWifiEnabled(true)
                    viewModel.viewModelScope.launch(Dispatchers.Main) {
                        Timber.i("HETAG 延时一秒")
                        delay(2000)
                        Timber.i("HETAG 延时完成")
                        getWifiInfo(1)
                    }
                }
            }
        }
        viewModel.info.set(info.toString())
    }

    @Inject
    lateinit var spManager: SharedPreferencesManager

    @Inject
    override lateinit var viewModel: InfoListViewModel
    override fun linkViewModel() {
        binding.vm = viewModel
    }

    override fun onDestroy() {
        super.onDestroy()
        NetworkUtils.unregisterNetworkStatusChangedListener(listener)
        NetworkUtils.removeOnWifiChangedConsumer(consumer)
    }
}