package com.nbhope.lib_frame.utils


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.SpannableString
import android.text.Spanned
import android.text.SpannedString
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import ch.qos.logback.core.android.SystemPropertiesProxy
import com.alibaba.android.arouter.launcher.ARouter
import com.nbhope.lib_frame.app.App
import com.nbhope.lib_frame.app.AppManager
import com.nbhope.lib_frame.app.HopeBaseApp
import com.nbhope.lib_frame.di.BaseAppComponent
import com.nbhope.lib_frame.integration.AppLifecycles
import timber.log.Timber
import java.io.File
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.security.MessageDigest
import java.util.*
import kotlin.experimental.and


/**
 * ================================================
 * 一些框架常用的工具
 * 摘自
 *
 * Created by JessYan on 2015/11/23.
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
class HopeUtils private constructor() {

    init {
        throw IllegalStateException("you can't instantiate me!")
    }



    companion object {
        var mToast: Toast? = null

        var mApps:Array<String> = arrayOf("com.nbhope.hopelauncher", "com.nbhope.app.more", "com.nbhope.app.interphone", "com.nbhope.app.music", "com.nbhope.app.r4", "com.nbhope.app.uhome", "com.nbhope.app.setting")

        private fun getProcessName(context: Context): String? {
            val processName = AppUtil.getCurProcessName(context)
            Timber.i("processName:$processName")
            return processName
        }

        fun isMainProcess(context: Context): Boolean {
//            return "com.nbhope.hopelauncher" == getProcessName(context)
//            return mApps.contains(getProcessName(context))
            return true
        }

        fun isRemoteProcess(context: Context): Boolean {
            return "com.nbhope.hopelauncher:remote" == getProcessName(context)
        }

        fun isRemoteLib(lifecycle: AppLifecycles): Boolean {
            lifecycle.javaClass.`package`?.let {
                return it.name == "com.lib.remote.di"
            }
            return false
        }


        /**
         * 获取真实的SN，unknown当做空处理
         *
         * @return
         */
        private val realSN: String
            @SuppressLint("MissingPermission")
            get() {
                val sn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    try {
                        Build.getSerial()
                    } catch (e: Exception) {
                        Build.SERIAL.trim { it <= ' ' }
                    }
                } else {
                    Build.SERIAL.trim { it <= ' ' }
                }
//                val sn = Build.SERIAL.trim { it <= ' ' }
                return if ("unknown".equals(sn, ignoreCase = true)) {
                    SystemPropertiesProxy.getInstance().get("ro.serialno", Build.UNKNOWN)
                } else sn
            }

        /**
         * 设置hint大小
         *
         * @param size
         * @param v
         * @param res
         */
        fun setViewHintSize(context: Context, size: Int, v: TextView, res: Int) {
            val ss = SpannableString(
                getResources(context).getString(
                    res
                )
            )
            // 新建一个属性对象,设置文字的大小
            val ass = AbsoluteSizeSpan(size, true)
            // 附加属性到文本
            ss.setSpan(ass, 0, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            // 设置hint
            v.hint = SpannedString(ss) // 一定要进行转换,否则属性会消失
        }

        /**
         * dp 转 px
         *
         * @param context [Context]
         * @param dpValue `dpValue`
         * @return `pxValue`
         */
        @JvmStatic
        fun dip2px(context: Context, dpValue: Float): Int {
            val scale = getResources(context).displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }

        /**
         * px 转 dp
         *
         * @param context [Context]
         * @param pxValue `pxValue`
         * @return `dpValue`
         */
        fun pix2dip(context: Context, pxValue: Int): Int {
            val scale = getResources(context).displayMetrics.density
            return (pxValue / scale + 0.5f).toInt()
        }

        /**
         * sp 转 px
         *
         * @param context [Context]
         * @param spValue `spValue`
         * @return `pxValue`
         */
        fun sp2px(context: Context, spValue: Float): Int {
            val fontScale = getResources(context).displayMetrics.scaledDensity
            return (spValue * fontScale + 0.5f).toInt()
        }

        /**
         * px 转 sp
         *
         * @param context [Context]
         * @param pxValue `pxValue`
         * @return `spValue`
         */
        fun px2sp(context: Context, pxValue: Float): Int {
            val fontScale = getResources(context).displayMetrics.scaledDensity
            return (pxValue / fontScale + 0.5f).toInt()
        }

        /**
         * 获得资源
         */
        fun getResources(context: Context): Resources {
            return context.resources
        }

        /**
         * 得到字符数组
         */
        fun getStringArray(context: Context, id: Int): Array<String> {
            return getResources(context).getStringArray(id)
        }

        /**
         * 从 dimens 中获得尺寸
         *
         * @param context
         * @param id
         * @return
         */
        fun getDimens(context: Context, id: Int): Int {
            return getResources(context).getDimension(id).toInt()
        }

        /**
         * 从 dimens 中获得尺寸
         *
         * @param context
         * @param dimenName
         * @return
         */
        fun getDimens(context: Context, dimenName: String): Float {
            return getResources(context).getDimension(
                getResources(context).getIdentifier(
                    dimenName,
                    "dimen",
                    context.packageName
                )
            )
        }

        /**
         * 从String 中获得字符
         *
         * @return
         */

        fun getString(context: Context, stringID: Int): String {
            return getResources(context).getString(stringID)
        }

        /**
         * 从String 中获得字符
         *
         * @return
         */
        fun getString(context: Context, strName: String): String {
            return getString(
                context,
                getResources(context).getIdentifier(
                    strName,
                    "string",
                    context.packageName
                )
            )
        }


        /**
         * 根据 layout 名字获得 id
         *
         * @param layoutName
         * @return
         */
        fun findLayout(context: Context, layoutName: String): Int {
            return getResources(context)
                .getIdentifier(layoutName, "layout", context.packageName)
        }

        /**
         * 填充view
         *
         * @param detailScreen
         * @return
         */
        fun inflate(context: Context, detailScreen: Int): View {
            return View.inflate(context, detailScreen, null)
        }

        /**
         * 单例 toast
         *
         * @param string
         */
        fun makeText(context: Context, string: String) {
            if (mToast == null) {
                mToast = Toast.makeText(context, string, Toast.LENGTH_SHORT)
            }
            mToast!!.setText(string)
            mToast!!.show()
            mToast=null
        }


        /**
         * 通过资源id获得drawable
         *
         * @param rID
         * @return
         */
        fun getDrawablebyResource(context: Context, rID: Int): Drawable {
            return getResources(context).getDrawable(rID)
        }


        /**
         * 获得屏幕的宽度
         *
         * @return
         */
        fun getScreenWidth(context: Context): Int {
            return getResources(context).displayMetrics.widthPixels
        }

        /**
         * 获得屏幕的高度
         *
         * @return
         */
        fun getScreenHeidth(context: Context): Int {
            return getResources(context).displayMetrics.heightPixels
        }

        /**
         * 获得颜色
         */
        fun getColor(context: Context, rid: Int): Int {
            return ContextCompat.getColor(context, rid)
        }


        /**
         * 移除孩子
         *
         * @param view
         */
        fun removeChild(view: View) {
            val parent = view.parent
            if (parent is ViewGroup) {
                parent.removeView(view)
            }
        }

        fun isNotEmpty(obj: Any?): Boolean {
            return !isEmpty(obj)
        }

        fun isEmpty(obj: Any?): Boolean {
            return if (obj is CharSequence) {
                obj.isEmpty()
            } else {
                obj == null
            }
        }

        /**
         * MD5
         *
         * @param string
         * @return
         * @throws Exception
         */
        fun encodeToMD5(string: String): String {
            var hash = ByteArray(0)
            try {
                hash = MessageDigest.getInstance("MD5").digest(
                    string.toByteArray(charset("UTF-8"))
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val hex = StringBuilder(hash.size * 2)
            for (b in hash) {
                if ((b and 0xFF.toByte()) < 0x10) {
                    hex.append("0")
                }
                hex.append(Integer.toHexString((b and 0xFF.toByte()).toInt()))
            }
            return hex.toString()
        }

        /**
         * 全屏,并且沉侵式状态栏
         *
         * @param activity
         */
        fun statuInScreen(activity: Activity) {
            val attrs = activity.window.attributes
            attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
            activity.window.attributes = attrs
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        fun startActivityByArouter(arouterPath: String,context: Context, bundle: Bundle? = null) {
            if (bundle == null) {
                ARouter.getInstance().build(arouterPath).navigation(context)
            } else {
                ARouter.getInstance().build(arouterPath).with(bundle).navigation(context)
            }
        }

        /**
         * 执行 [AppManager.killAll]
         */
        fun killAll() {
            AppManager.appManager?.killAll()
        }

        /**
         * 执行 [AppManager.appExit]
         */
        fun exitApp() {
            AppManager.appManager?.appExit()
        }

        /**
         * 全局获得BaseAppCompont 快速拿取里面的单例
         */
        fun obtainAppComponentFromContext(context: Context): BaseAppComponent {
            return (context.applicationContext as App).getAppComponent()
        }

        @JvmStatic
        fun getIP(): String? {

            try {
                val en = NetworkInterface.getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val intf = en.nextElement()
                    val enumIpAddr = intf.inetAddresses
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress = enumIpAddr.nextElement()
                        if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                            return inetAddress.hostAddress
                        }
                    }
                }
            } catch (ex: SocketException) {
                Timber.e("XTAG 获取ip $ex")
                ex.printStackTrace()
            }

            return null
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
            try {
                result = getGUID(HopeBaseApp.getContext())
            } catch (e:Exception) {
                result = macAddress()!!;
            }
            return if (!TextUtils.isEmpty(result)) result else ""

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

        // 获取手机的唯一标识符: deviceId
        fun getSpeechDeviceId(context: Context): String {
            val telephonyMgr = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            var imei = try {
                telephonyMgr.deviceId
            } catch (e: SecurityException) {
                ""
            }
            var serial = HopeUtils.getSN()
            val uuid: String
            if (TextUtils.isEmpty(imei)) {
                imei = "unkown"
            } else if (TextUtils.isEmpty(serial)) {
                serial = "unkown"
            }
            Timber.i("imei:$imei  serial:$serial")
            uuid = UUID.nameUUIDFromBytes((imei + serial).toByteArray()).toString()
            return uuid
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


        /**
         * Get a resource id from an attribute id.
         * @param context
         * @param attrId
         * @return the resource id
         */
        fun getResourceFromAttribute(context: Context, attrId: Int): Int {
            val a: TypedArray = context.theme.obtainStyledAttributes(intArrayOf(attrId))
            val resId: Int = a.getResourceId(0, 0)
            a.recycle()
            return resId
        }

//        fun getUpgradeChannel(): String {
//            val buildType: String = BuildConfig.BUILD_TYPE
//            val model = HopeConstants.DEVICE_NAME.toLowerCase(Locale.ROOT).replace("-", "_")
//            var product = Build.BRAND
//            product = "$buildType - $model - $product"
//            return product
//        }

        //获取系统固件
        fun getProperty(key: String, defaultValue: String): String {
            var value = defaultValue
            try {
                val c = Class.forName("android.os.SystemProperties")
                val get = c.getMethod("get", String::class.java, String::class.java)
                value = get.invoke(null, key, "unknown") as String
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            } finally {
                return value
            }
        }


        fun updataLocalMusic(application: Context) {
            updataMusicByPath(getStoragePath(application, false), application)
            updataMusicByPath(getStoragePath(application, true), application)
        }

//        fun updataSDMusic(application: Context) {
//            updataMusicByPath(getStoragePath(application, true), application)
//        }

        private fun updataMusicByPath(path: String, application: Context) {
            //保存歌曲绝对路径的数组，这个用于MediaScannerConnection.scanFile（）第二个参数
            var songTotalPath: Array<String?>? = null
            //现在假设要扫描sd卡下的opo目录，“/”这个斜杠别丢了，接下来用到的file相关方法啊啥的建议参考下File的类文档
            val f = File(path)

            if (f.isDirectory) {
                //测试f这个路径表示的文件是否是一个目录
                val files: Array<File> = f.listFiles() //返回一个抽象（绝对）路径名数组，这些路径名表示此抽象路径名表示的目录中的文件
                if (files != null) {
                    //初始化数组长度
                    songTotalPath = arrayOfNulls(files.size)
                    for (i in songTotalPath.indices) {
                        //默认路径，这里初始化数组每一项，只是单纯的防止后面用第二种方式扫描文件带来的空指针异常，无实际意义
                        songTotalPath[i] = path
                    }
                    for (i in files.indices) {
                        if (files[i].isFile) {
                            //如果 扫到的是文件，那么把具体路径存到songTotalPath下
                            songTotalPath[i] = path + "/" + files[i].name
                        }
                    }
                }
                //这里就可以直接用了，第三个这里用文件的后缀名，为空
                MediaScannerConnection.scanFile(application, songTotalPath, null) { path, uri ->
//                    Timber.d("本地歌曲已经更新")
                }
            }

        }

        /**
         * 通过反射调用获取内置存储和外置sd卡根路径(通用)
         *
         * @param mContext    上下文
         * @param is_removale 是否可移除，false返回内部存储路径，true返回外置SD卡路径
         * @return
         */
        private fun getStoragePath(mContext: Context, is_removale: Boolean): String {
            var path = ""
            //使用getSystemService(String)检索一个StorageManager用于访问系统存储功能。
            val mStorageManager = mContext.getSystemService(Context.STORAGE_SERVICE) as StorageManager
            var storageVolumeClazz: Class<*>? = null
            try {
                storageVolumeClazz = Class.forName("android.os.storage.StorageVolume")
                val getVolumeList = mStorageManager.javaClass.getMethod("getVolumeList")
                val getPath = storageVolumeClazz.getMethod("getPath")
                val isRemovable = storageVolumeClazz.getMethod("isRemovable")
                val result = getVolumeList.invoke(mStorageManager)
                for (i in 0 until java.lang.reflect.Array.getLength(result)) {
                    val storageVolumeElement = java.lang.reflect.Array.get(result, i)
                    path = getPath.invoke(storageVolumeElement) as String
                    val removable = isRemovable.invoke(storageVolumeElement) as Boolean
                    if (is_removale == removable) {
                        return path
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return path
        }

        //判断Activity是否Destroy
        fun isDestroy(activity: Activity?): Boolean {
            return activity == null || activity.isFinishing ||  activity.isDestroyed
        }

        fun getVerName(context: Context): String {
            var verName = ""
            try {
                verName = context.getPackageManager().
                getPackageInfo(context.getPackageName(), 0).versionName;
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return verName;
        }


        public fun isSystemApp(pkgName: String, context: Context): Boolean {
            val isSystemApp: Boolean
            val pm: PackageManager = context.packageManager
            //下面是一个系统级权限
            val permission = PackageManager.PERMISSION_GRANTED ==
                    pm.checkPermission("android.permission.OVERRIDE_WIFI_CONFIG", pkgName)
            isSystemApp = permission
            return isSystemApp
        }

        /**
         * 根据包名判断app是否具有系统签名
         * @param context 上下文
         * @param packageName app包名
         * @return true 有系统签名; false 没有系统签名
         */
        fun isSystemSign(context: Context, packageName: String? = null): Boolean {
            val packageManager = context.packageManager
            val uid1: Int = getUid(context)
            val systemUID = 1000
            return packageManager.checkSignatures(uid1, systemUID) == PackageManager.SIGNATURE_MATCH
        }

        fun getUid(context: Context, packageName: String? = null): Int {
            val name = packageName ?: context.packageName
            var uid = 0
            try {
                val pm = context.packageManager
                val ai = pm.getApplicationInfo(name, PackageManager.GET_META_DATA)
                uid = ai.uid
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return uid
        }
    }
}
