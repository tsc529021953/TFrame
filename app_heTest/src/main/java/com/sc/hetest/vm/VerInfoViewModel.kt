package com.sc.hetest.vm

import android.os.Build
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.RomUtils
import com.nbhope.lib_frame.app.HopeBaseApp
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.utils.HopeUtils
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import java.lang.StringBuilder
import java.util.*
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2022/8/30 16:31
 * @version 0.0.0-1
 * @description
 */
class VerInfoViewModel @Inject constructor(val spManager: SharedPreferencesManager) : BaseViewModel() {

    var deviceInfo = ObservableField<String>()

    fun initData() {
        var info = StringBuilder()
        info.append("Android版本：Android" + Build.VERSION.RELEASE + "\n")
        info.append("ROM版本：" + RomUtils.getRomInfo().version + "\n")
        info.append("TP版本：" + "XHG-165GG-215" + "\n")
        info.append("LCM版本：${Build.DISPLAY}" + "" + "\n")
        info.append("MAC：" + DeviceUtils.getMacAddress() + "\n")
        info.append("SN：" + HopeUtils.getSN() + "\n")
        deviceInfo.set(info.toString())
    }

}