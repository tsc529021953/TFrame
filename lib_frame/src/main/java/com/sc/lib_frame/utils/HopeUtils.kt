package com.sc.lib_frame.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import ch.qos.logback.core.android.SystemPropertiesProxy
import com.sc.lib_frame.base.BaseApp
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*


/**
 *Created by ywr on 2021/11/11 10:11
 */
class HopeUtils {
    companion object {

        /**
         * 获取真实的SN，unknown当做空处理
         *
         * @return
         */
        private val realSN: String
            @SuppressLint("MissingPermission")
            get() {
                var sn = Build.SERIAL.trim { it <= ' ' }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    try {
                        sn = Build.getSerial()
                    } catch (e: Exception) {
                        sn = macAddress().toString()
                    }
                }
                return if ("unknown".equals(sn, ignoreCase = true)
                    || "unknow".equals(sn, ignoreCase = true)) {
                        sn =  SystemPropertiesProxy.getInstance().get("ro.serialno", Build.UNKNOWN)
                    if ("unknown".equals(sn, ignoreCase = true)
                        || "unknow".equals(sn, ignoreCase = true)) ""
                    else sn
                } else sn
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


        /**
         * 获取SN
         * 1.优先获取SN
         * 2.如果SN不存在，则获取GUID
         */
        @JvmStatic
        fun getSN(): String {
            var result = realSN
            if (!isInvalidSN(result)) return result

            //result = getMacAddressByWlan0();
            result = getGUID(BaseApp.getContext())
            return if (!TextUtils.isEmpty(result)) result else ""

        }

        @JvmStatic
        fun getIP(): String? {

            try {
                val en = NetworkInterface.getNetworkInterfaces()
                if (en == null) return null
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
         * 查看是否有天猫app的存在，如果存在则认为时天猫版本
         */
        @JvmStatic
        fun isTianMao(context: Context): Boolean {
            try {
                var info = context.packageManager.getApplicationInfo(
                    "com.alibaba.ailabs.genie.smartapp",
                    0
                )
                return info != null

            } catch (e: Exception) {
                return false
            }
        }

        private fun isInvalidSN(sn: String): Boolean {
            if (TextUtils.isEmpty(sn)) {
                return true
            }
            return if (TextUtils.isEmpty(sn.trim { it <= ' ' })) {
                true
            } else "unknown".equals(sn, ignoreCase = true)
        }

        private fun getGUID(context: Context): String {
            return generateUUID(context)
        }

        @SuppressLint("SimpleDateFormat")
        private fun generateUUID(context: Context): String {
            val deviceType = Build.MODEL
//SDKGlobal.setUUID(code);
            return "HOPE|" + deviceType + "|" + getUUID(context)!!.replace("-", "").toUpperCase()
        }

        private fun getUUID(context: Context): String? {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    val tmDevice = "" + tm.deviceId
                    val tmSerial = "" + tm.simSerialNumber
                    val androidId = "" + Settings.Secure.getString(context.contentResolver, "android_id")
                    val deviceUuid = UUID(androidId.hashCode().toLong(), tmDevice.hashCode().toLong() shl 32 or tmSerial.hashCode().toLong())
                    var uniqueId: String? = deviceUuid.toString()
                    if (uniqueId != null) {
                        uniqueId = uniqueId.replace("-", "")
                    }

                    return uniqueId
                }
            } else {
                val tmDevice = "" + tm.deviceId
                val tmSerial = "" + tm.simSerialNumber
                val androidId = "" + Settings.Secure.getString(context.contentResolver, "android_id")
                val deviceUuid = UUID(androidId.hashCode().toLong(), tmDevice.hashCode().toLong() shl 32 or tmSerial.hashCode().toLong())
                var uniqueId: String? = deviceUuid.toString()
                if (uniqueId != null) {
                    uniqueId = uniqueId.replace("-", "")
                }

                return uniqueId
            }
            return ""
        }
    }
}