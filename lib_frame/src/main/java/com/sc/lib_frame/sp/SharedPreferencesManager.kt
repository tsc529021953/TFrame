package com.sc.lib_frame.sp

import android.app.Application
import com.sc.lib_frame.utils.HopeUtils
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

/**
 *Created by ywr on 2021/11/11 8:46
 */
class SharedPreferencesManager private constructor(private val app: Application) {

    private val tary = TraySpManager(app)

    companion object {


        @Volatile
        private var instance: SharedPreferencesManager? = null

        fun getInstance(app: Application) =
            instance ?: synchronized(this) {
                instance ?: SharedPreferencesManager(app).also { instance = it }
            }

        fun getInstance() = instance


        /*对讲广播相关*/
        private const val INTERPHONE_GUIDE = "interphone_guide"
        private const val INTERPHONE_DISTURB = "interphone_disturb"

        private const val INTERPHONE_NAME = "INTERPHONE_NAME"  //对讲广播设备名称
        private var DEVICE_NAME = "DEVICE_NAME" //自定义的设备名称
        private var IS_STARTED_SYNC_PLAY = "IS_STARTED_SYNC_PLAY" //同步播放是否已开启
        /*对讲广播相关*/

    }

    fun getDeviceName(): String? {
        return tary.getString(DEVICE_NAME, getDefName())
    }

    fun setDeviceName(deviceName: String) {
        tary.putString(DEVICE_NAME, deviceName)
    }

    private fun getDefName(): String? {
        val sn = HopeUtils.getSN()
        return if (sn == null) {
            "unKnow"
        } else {
            val length = sn.length
            val start = if (length > 6) length - 7 else 0
            sn.substring(start)
        }
    }

    @Throws(SocketException::class)
    fun macAddress(): String? {
        var address: String? = "unKnow"
        // 把当前机器上访问网络的接口存入 Enumeration集合中
        val interfaces: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
        while (interfaces.hasMoreElements()) {
            val netWork: NetworkInterface = interfaces.nextElement()
            // 如果存在硬件地址并可以使用给定的当前权限访问，则返回该硬件地址（通常是 MAC）。
            val by = netWork.hardwareAddress
            if (by == null || by.size == 0) {
                continue
            }
            val builder = StringBuilder()
            for (b in by) {
                builder.append(String.format("%02X:", b))
            }
            if (builder.length > 0) {
                builder.deleteCharAt(builder.length - 1)
            }
            val mac = builder.toString()
            // 从路由器上在线设备的MAC地址列表，可以印证设备Wifi的 name 是 wlan0
            if (netWork.name == "wlan0") {
                address = mac
            }
        }
        return address
    }

    fun getIsNotDisturb(): Boolean = tary!!.isBoolean(INTERPHONE_DISTURB, false)

    fun saveIsNotDisturb(isNotDisturb: Boolean) {
        tary!!.putBoolean(INTERPHONE_DISTURB, isNotDisturb)
    }

    fun setStartedSync(isStarted: Boolean) {
        tary.putBoolean(IS_STARTED_SYNC_PLAY, isStarted)
    }

    fun isStartedSync(): Boolean {
        return tary.isBoolean(IS_STARTED_SYNC_PLAY, false)
    }

}