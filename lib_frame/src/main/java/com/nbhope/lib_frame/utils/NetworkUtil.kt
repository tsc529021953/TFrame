package com.nbhope.lib_frame.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException

object NetworkUtil {
    fun getIp():String?{
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.getHostAddress()
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }
        return null
    }


    /**
     * 判断网络是否连接
     *
     * @return 结果
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm != null) {
            //如果仅仅是用来判断网络连接
            val info = cm.allNetworkInfo
            if (info != null) {
                for (i in info.indices) {
                    if (info[i].state == NetworkInfo.State.CONNECTED) {
                        return true
                    }
                }
            }
        }
        return false
    }

}