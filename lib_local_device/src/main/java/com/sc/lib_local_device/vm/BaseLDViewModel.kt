package com.sc.lib_local_device.vm

import androidx.lifecycle.MutableLiveData
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.utils.HopeUtils
import com.sc.lib_local_device.common.DeviceCommon
import com.sc.lib_local_device.dao.DeviceInfo
import java.util.*

/**
 * @author  tsc
 * @date  2022/8/30 16:54
 * @version 0.0.0-1
 * @description
 * 获取本地的设备信息
 */
open class BaseLDViewModel : BaseViewModel() {

    val deviceInfo = MutableLiveData<DeviceInfo>();

    open fun initData() {
        refreshData()
    }

    open fun refreshData() {
        var di = DeviceInfo()
        di.code = HopeUtils.getSN()
        if (DeviceCommon.deviceType == DeviceCommon.DeviceType.Ctrl)
            di.info.set("连接中...")
        else {
            var ip = HopeUtils.getIP()
            if (ip == null) ip = "当前暂无可以网络"
            di.info.set("${di.code} \n $ip")
        }
        deviceInfo.postValue(di)
    }

    open fun onClicked() {

    }

}