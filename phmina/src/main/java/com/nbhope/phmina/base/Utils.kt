package com.nbhope.phmina.base

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import com.nbhope.lib_frame.utils.HopeUtils
import timber.log.Timber
import java.net.Inet6Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

/**
 * 工具类
 *
 * @author EthanCo
 * @since 2017/1/19
 */
object Utils {
    /**
     * 获取局域网IP
     *
     * @param context
     * @return 192.168.XX.XX
     */
    fun getLANIP(): String? {
//        var hostIp: String? = null
//        try {
//            val nis = NetworkInterface.getNetworkInterfaces()
//            var ia: InetAddress? = null
//            while (nis.hasMoreElements()) {
//                val ni = nis.nextElement() as NetworkInterface
//                val ias = ni.inetAddresses
//                while (ias.hasMoreElements()) {
//                    ia = ias.nextElement()
//                    if (ia is Inet6Address) {
//                        continue  // skip ipv6
//                    }
//                    val ip = ia.hostAddress
//                    if ("127.0.0.1" != ip) {
//                        hostIp = ia.hostAddress
//                        break
//                    }
//                }
//            }
//        } catch (e: SocketException) {
//            Log.i("yao", "SocketException")
//            e.printStackTrace()
//        }
//        Timber.i("获取局域网IP $hostIp ${HopeUtils.getIP()} ${hostIp == HopeUtils.getIP()}")
//        return hostIp
        return HopeUtils.getIP()
    }

    fun getSn(): String {
       return  HopeUtils.getSN()
    }
}