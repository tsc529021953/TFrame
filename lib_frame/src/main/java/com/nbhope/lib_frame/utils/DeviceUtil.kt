package com.nbhope.lib_frame.utils
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils
import org.apache.commons.codec.digest.DigestUtils
import java.net.NetworkInterface
import java.net.SocketException
import kotlin.experimental.and
import kotlin.experimental.xor

object DeviceUtil {

    private const val UNKNOWN = "unknown"
    const val PARENT_ID = 753396045774098432L //影音娱乐 Parent Id

    fun getRealSN(): String {
        val sn = HopeUtils.getSN()
        return if ("unknown".equals(sn, ignoreCase = true)) {
            ""
        } else sn
    }

    fun getDeviceCate(): String {
        /*val model = Build.MODEL
        if (TextUtils.isEmpty(model)) {
            return UNKNOWN
        }
        val index = model.indexOf("-") + 1
        if (index < 0 || index >= model.length) {
            return UNKNOWN
        }
        val deviceCate = model.substring(index)
        return if (TextUtils.isEmpty(deviceCate)) {
            UNKNOWN
        } else deviceCate*/
        return getDeviceName()
    }

    fun getDeviceName(): String {
        return Build.MODEL
//        return "HOPE-S7"
    }

    fun getMacAddress(): String {
        var macAddress: String? = null
        val buf = StringBuffer()
        var networkInterface: NetworkInterface? = null
        try {
            networkInterface = NetworkInterface.getByName("wlan0")
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("eth0")
            }
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("eth1")
            }
            if (networkInterface == null) {
                return "02:00:00:00:00:02"
            }
            val addr = networkInterface.hardwareAddress
            for (b in addr) {
                buf.append(String.format("%02X:", b))
            }
            if (buf.isNotEmpty()) {


                buf.deleteCharAt(buf.length - 1)
            }
            macAddress = buf.toString()
        } catch (e: SocketException) {
            e.printStackTrace()
            return "02:00:00:00:00:02"
        }
        return macAddress
    }

    fun getPlayerVersion(context: Context): String {
        return try {
            val packageManager = context.packageManager
            val packInfo = packageManager.getPackageInfo(context.packageName, 0)
            packInfo.versionName
        } catch (e: Exception) {
            UNKNOWN
        }
    }

    fun getPlayerVersionCode(context: Context): Long {
        return try {
            val packageManager = context.packageManager
            val packInfo = packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packInfo.longVersionCode
            } else {
                packInfo.versionCode.toLong()
            }
        } catch (e: Exception) {
            0L
        }
    }

    fun getUpgradeChannel(buildType:String): String {
        val model = Build.MODEL.toLowerCase().replace("-", "_")
        var product = Build.PRODUCT

        product = "$buildType - $model - $product" // +" - "+ Build.DEVICE;
        return product
    }


    private fun bytesXor(bytes: ByteArray?): Byte {
        if (null == bytes || bytes.size == 0) {
            throw NullPointerException()
        }
        var byteValue = bytes[0]
        for (i in 1 until bytes.size) {
            byteValue = byteValue xor bytes[i]
        }
        return byteValue
    }

    @Throws(java.lang.Exception::class)
    fun getSignature(model: String, version: String, software: String): String {
        val stringBuffer = StringBuffer(model)
        stringBuffer.append(version).append(software)
        val md5String: String = DigestUtils.md5Hex(stringBuffer.toString()).toUpperCase()
        val bs = md5String.toByteArray()
        val byteXor: Byte = bytesXor(bs)
        var des = Integer.toHexString((byteXor and 0xFF.toByte()).toInt()).toUpperCase()
        if (des.length == 1) {
            des = "0" + des
        }
        return des
    }


    //判断是否是主进程
    @Throws(PackageManager.NameNotFoundException::class)
    fun isMainProcess(context: Context): Boolean {
        return isPidOfProcessName(context, android.os.Process.myPid(), getMainProcessName(context))
    }

    /**
     * 判断该进程ID是否属于该进程名
     * @param context
     * @param pid 进程ID
     * @param p_name 进程名
     * @return true属于该进程名
     */
    fun isPidOfProcessName(context: Context, pid: Int, p_name: String?): Boolean {
        if (p_name == null) return false
        var isMain = false
        val am = context.getSystemService(Application.ACTIVITY_SERVICE) as ActivityManager
        //遍历所有进程
        for (process in am.runningAppProcesses) {
            if (process.pid == pid) {
                //进程ID相同时判断该进程名是否一致
                if (process.processName == p_name) {
                    isMain = true
                }
                break
            }
        }
        return isMain
    }

    //获取当前进程的名称
    @Throws(PackageManager.NameNotFoundException::class)
    fun getMainProcessName(context: Context): String? {
        return context.packageManager.getApplicationInfo(context.packageName, 0).processName
    }

    fun checkAppInstalled(context: Context, pkgName: String): Boolean {
        try {
            if (TextUtils.isEmpty(pkgName)) {
                return false
            }
            val packageManager = context.packageManager
            // 获取已安装的app信息
            val pkgInfos = packageManager.getInstalledPackages(0)
            if (pkgInfos != null) {
                for (i in pkgInfos.indices) {
                    val pkg = pkgInfos[i].packageName
                    if (pkgName == pkg) {
                        return true
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return false
    }
}
