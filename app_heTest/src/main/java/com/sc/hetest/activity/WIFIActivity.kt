package com.sc.hetest.activity

import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.SPUtils
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.common.BasePath
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.sc.hetest.R
import com.sc.hetest.bean.InfoItem
import com.sc.hetest.common.HEPath
import com.sc.hetest.databinding.ActivityInfoBinding
import com.sc.hetest.vm.InfoViewModel
import timber.log.Timber
import javax.inject.Inject


/**
 * author: sc
 * date: 2022/12/10
 */
@Route(path = HEPath.WIFI_PATH)
class WIFIActivity : BaseBindingActivity<ActivityInfoBinding, InfoViewModel>(){

    companion object{

    }

    override var layoutId: Int = R.layout.activity_info

    private val listener = object : NetworkUtils.OnNetworkStatusChangedListener{
        override fun onDisconnected() {
            getWifiInfo()
        }

        override fun onConnected(networkType: NetworkUtils.NetworkType?) {
            getWifiInfo()
        }
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
    }

    override fun initData() {
        viewModel.initData()
        getWifiInfo()
        NetworkUtils.registerNetworkStatusChangedListener(listener)
    }

    private fun getWifiInfo() {
        val info = StringBuilder()
        if (NetworkUtils.isConnected()) {
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

        } else {
            info.append("当前未连接网络")
        }
        viewModel.info.set(info.toString())
    }

    @Inject
    lateinit var spManager: SharedPreferencesManager

    @Inject
    override lateinit var viewModel: InfoViewModel
    override fun linkViewModel() {
        binding.vm = viewModel
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        NetworkUtils.unregisterNetworkStatusChangedListener(listener)
    }
}