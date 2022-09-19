package com.sc.lib_local_device.vm

import androidx.lifecycle.MutableLiveData
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.utils.HopeUtils
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
        var di = DeviceInfo()
        di.code = HopeUtils.getSN()

        deviceInfo.postValue(di)
    }

    open fun onClicked() {

    }

}