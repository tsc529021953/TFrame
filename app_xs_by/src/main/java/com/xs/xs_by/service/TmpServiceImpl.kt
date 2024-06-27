package com.xs.xs_by.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.net.Network
import android.net.NetworkCapabilities
import android.os.*
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.dlong.dl10netassistant.OnNetThreadListener
import com.dlong.dl10netassistant.UdpBroadThread
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.nbhope.lib_frame.app.HopeBaseApp
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.network.NetworkCallback
import com.nbhope.lib_frame.network.NetworkCallbackModule
import com.nbhope.lib_frame.utils.FileUtil
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.nbhope.lib_frame.utils.TimerHandler
import com.xs.xs_by.R
import com.xs.xs_by.bean.NetBean
import com.xs.xs_by.bean.OneCtrlBean
import com.xs.xs_by.bean.OneCtrlPage
import com.xs.xs_by.bean.ThemeBean
import com.xs.xs_by.constant.BYConstants
import com.xs.xs_by.inter.ITmpService
import com.xs.xs_by.utils.SPUtils
import com.xs.xs_by.vm.MainViewModel
import com.xs.xs_by.vm.OneCtrlViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import java.io.UnsupportedEncodingException
import java.lang.ref.WeakReference
import java.util.*


/**
 * @author  tsc
 * @date  2024/4/12 13:28
 * @version 0.0.0-1
 * @description
 * 添加SDK *
 * 配置文件 *
 * 隐藏界面 *
 * 定时测试
 * 混淆
 */
class TmpServiceImpl : ITmpService, Service() {

    companion object {
        const val TAG = "TTAG"

        var CHANNEL_ONE_NAME = "TmpServiceImpl"
        var CHANNEL_ONE_ID = TmpServiceImpl::class.java.simpleName
        const val SERVICE_NOTICE_ID = 101

        const val BASE_FILE = "/XS_MEDIA/"
        const val CONFIG_FILE = "AppInfo.json"
    }

    private val mBinder: IBinder = BaseBinder()

    private lateinit var mScope: CoroutineScope

    /*view*/
    private var rootView: View? = null

    lateinit var mainHandler: MainHandler

    private var timerHandler: TimerHandler? = null

    private var udpBroadThread: UdpBroadThread? = null
    private var udpBroadThread2: UdpBroadThread? = null

    lateinit var networkCallback: NetworkCallback

    private var gson = Gson()

    override fun onCreate() {
        super.onCreate()
        initNotice()
        init(application)
        mScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        mainHandler = MainHandler(Looper.getMainLooper())
        networkCallback = (application as HopeBaseApp).getAppComponent().networkCallback
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            initFloat()
//        } else {
//            Timber.i("$TAG 版本太低，请重新适配 ${Build.VERSION.SDK_INT}")
//        }
//        networkCallback.registNetworkCallback(networkCallbackModule)
        Timber.i("XTAG service Create")
//        initAppData(this) {
//            Timber.i("XTAG service initAppData $ip:$port")
//
//        }
        BYConstants.ip = SPUtils.getValue(this, BYConstants.SP_IP, BYConstants.ip).toString()
        BYConstants.port = SPUtils.getValue(this, BYConstants.SP_PORT, BYConstants.port) as Int
        BYConstants.ip2 = SPUtils.getValue(this, BYConstants.SP_IP2, BYConstants.ip2).toString()
        BYConstants.port2 = SPUtils.getValue(this, BYConstants.SP_PORT2, BYConstants.port2) as Int
        reBuild()
    }

    override fun onDestroy() {
        super.onDestroy()
        timerHandler?.stop()
    }

    private fun initNotice() {
        var builder: Notification.Builder? = Notification.Builder(this)
        builder!!.setSmallIcon(R.drawable.ic_tmp).setWhen(System.currentTimeMillis())
        builder.setContentTitle("KeepAppAlive")
        builder.setContentText("PaintService is running...")
//        startForeground(SPEECH_NOTICE_ID, builder.build())
//        // 如果觉得常驻通知栏体验不好
//        // 可以通过启动CancelNoticeService，将通知移除，oom_adj值不变
//        val intent = Intent(this, CancelNoticeService::class.java)
//        startService(intent)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //修改安卓8.1以上系统报错
            var notificationChannel: NotificationChannel? = NotificationChannel(
                CHANNEL_ONE_ID,
                CHANNEL_ONE_NAME,
                NotificationManager.IMPORTANCE_MIN
            )
            notificationChannel!!.enableLights(false)//如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
            notificationChannel.setShowBadge(false)//是否显示角标
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
            var manager: NotificationManager? =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager?.createNotificationChannel(notificationChannel)
            builder.setChannelId(CHANNEL_ONE_ID)
            notificationChannel = null
            manager = null
        }
        startForeground(SERVICE_NOTICE_ID, builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun initFloat() {
        var windowManager: WindowManager? =
            getSystemService(Context.WINDOW_SERVICE) as WindowManager
        var layoutParams: WindowManager.LayoutParams? = WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams!!.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            layoutParams!!.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        layoutParams.format = PixelFormat.RGBA_8888
        layoutParams.gravity = Gravity.LEFT or Gravity.TOP
        layoutParams.flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.x = 0
        var point: Point? = Point()
        (windowManager!!.defaultDisplay.getSize(point))
        layoutParams.y = point!!.y - layoutParams.height + 50

        Timber.i("$TAG canDrawOverlays ${Settings.canDrawOverlays(this)}")
        if (Settings.canDrawOverlays(this)) {
            rootView = View.inflate(baseContext, R.layout.float_tmp_view, null)
//            tvMarqueeContent = rootView!!.findViewById(R.id.marquee_content)
//            ivVoice = rootView!!.findViewById(R.id.iv_voice)
            rootView!!.setOnClickListener {
                hideFloat(100)
            }
            windowManager.addView(rootView, layoutParams)
//            showDraw()
//            showFloat()
            hideFloat(0)
        }
//
////        hideFloat(0)
//        Timber.d("dialog, hideFloat1")
//        point = null
//        windowManager = null
//        layoutParams = null

    }

    override fun init(context: Context) {

    }

    override fun showFloat() {
        mainHandler.removeMessages(MSG_FLOAT_HIDE)
        mainHandler.sendEmptyMessage(MSG_FLOAT_SHOW)
    }

    override fun hideFloat(delayMillis: Long) {
        mainHandler.sendEmptyMessage(MSG_FLOAT_HIDE)
    }

    override fun write(msg: String) {
        Timber.i("XTAG write ${udpBroadThread == null} $msg")
        if (udpBroadThread != null) {
            udpBroadThread?.send(BYConstants.ip, BYConstants.port, msg.toByteArray(Charsets.UTF_8))
        }
    }

    override fun write2(msg: String) {
        Timber.i("XTAG write2 ${udpBroadThread2 == null} $msg")
        if (udpBroadThread2 != null) {
            udpBroadThread2?.send(BYConstants.ip2, BYConstants.port2, msg.toByteArray(Charsets.UTF_8))
        }
    }

    private val MSG_FLOAT_SHOW = 100
    private val MSG_FLOAT_UPDATE = 101
    private val MSG_FLOAT_HIDE = 102

    inner class MainHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_FLOAT_HIDE -> {
                    if (rootView?.visibility != View.GONE)
                        rootView?.visibility = View.GONE
                    mainHandler.removeMessages(MSG_FLOAT_HIDE)
                }

                MSG_FLOAT_SHOW -> {
                    if (rootView?.visibility != View.VISIBLE)
                        rootView?.visibility = View.VISIBLE
                    mainHandler.removeMessages(MSG_FLOAT_SHOW)
                }
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return this.mBinder
    }

    inner class BaseBinder : Binder() {
        fun getPaintService(): TmpServiceImpl {
            return WeakReference(this@TmpServiceImpl).get()!!
        }
    }

    private var onNetThreadListener = object : OnNetThreadListener {
        override fun onAcceptSocket(ipAddress: String) {
            Timber.i("XTAG onAcceptSocket ipAddress $ipAddress")
        }

        override fun onConnectFailed(ipAddress: String) {
            Timber.i("XTAG onConnectFailed ipAddress $ipAddress")
        }

        override fun onConnected(ipAddress: String) {
            Timber.i("XTAG onConnected ipAddress $ipAddress")
        }

        override fun onDisconnect(ipAddress: String) {
            Timber.i("XTAG onDisconnect ipAddress $ipAddress")
        }

        override fun onError(ipAddress: String, error: String) {
            Timber.i("XTAG onError ipAddress $ipAddress $error")
        }

        override fun onReceive(ipAddress: String, port: Int, time: Long, data: ByteArray) {
            val msg = byteToString(data) // data.toString(Charsets.UTF_8)
            Timber.i("XTAG onReceive ipAddress $ipAddress $port $msg")
            if (msg.contains(BYConstants.CMD_TABLE)) {
                LiveEBUtil.post(RemoteMessageEvent(BYConstants.CMD_TABLE, msg))
            } else if (msg.contains(BYConstants.CMD_GROUP)) {
                // 当前桌面处于关闭状态，需要关闭
                LiveEBUtil.post(RemoteMessageEvent(BYConstants.CMD_GROUP, msg))
            } else {
                try {
                    val json = JSONObject(msg)
                    if (json.has("cmd")) {
                        when (json.getString("cmd")) {
                            BYConstants.CMD_LIST -> {
                                val theme = Gson().fromJson(msg, ThemeBean::class.java)
                                if (theme != null) {
                                    MainViewModel.themeBean = theme
                                }
                                if (json.has("list")) {
                                    val arr = json.getJSONArray("list")
                                    val list = arrayListOf<OneCtrlBean>()
                                    for (i in 0 until arr.length()) {
                                        val item = arr.getJSONObject(i)
                                        val scene = gson.fromJson<OneCtrlBean.SceneBean>(
                                            item.toString(),
                                            OneCtrlBean.SceneBean::class.java
                                        )
                                        Timber.i("XTAG name $scene")
                                        val one = OneCtrlBean(scene)
                                        list.add(one)
                                    }
                                    OneCtrlViewModel.PAGE_LIST.clear()
                                    var size = list.size / 4
                                    if (size <= 0) size = 1
                                    for (i in 0 until size) {
                                        val page = OneCtrlPage()
                                        for (j in 0 until 4) {
                                            if (j + i * 4 < list.size) {
                                                page.ctrlBeans.add(list[j + i * 4])
                                            }
                                        }
                                        OneCtrlViewModel.PAGE_LIST.add(page)
                                    }
                                }
                                // 获取到list,直接清除覆盖替换
                                LiveEBUtil.post(RemoteMessageEvent(BYConstants.CMD_LIST, ""))
                            }

                            BYConstants.CMD_THEME -> {
                                LiveEBUtil.post(RemoteMessageEvent(BYConstants.CMD_THEME, msg))
                            }

                            BYConstants.CMD_SCENE -> {
                                val scene = gson.fromJson<OneCtrlBean.SceneBean>(msg, OneCtrlBean.SceneBean::class.java)
                                Timber.i("XTAG name $scene")
//                                val one = OneCtrlBean(scene)
                                // 查找是否存在
                                for (i in 0 until OneCtrlViewModel.PAGE_LIST.size) {
                                    val item = OneCtrlViewModel.PAGE_LIST[i]
                                    for (j in 0 until item.ctrlBeans.size) {
                                        val it = item.ctrlBeans[j]
                                        if (it.id == scene.id && it.switchObs.get() != scene.switch) {
//                                            one.apply {
//                                                OneCtrlViewModel.PAGE_LIST[i].ctrlBeans[j].name =
//                                            }
                                            OneCtrlViewModel.PAGE_LIST[i].ctrlBeans[j].switchObs.set(scene.switch)
                                            val json = JsonObject()
                                            json.addProperty("i", i)
                                            json.addProperty("j", j)
                                            LiveEBUtil.post(RemoteMessageEvent(BYConstants.CMD_SCENE, json.toString()))
                                            return
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }

    override fun reBuild() {
        udpBroadThread?.close()
        udpBroadThread = UdpBroadThread(BYConstants.port, onNetThreadListener)
        udpBroadThread?.start()

        udpBroadThread2?.close()
        udpBroadThread2 = UdpBroadThread(BYConstants.port2, onNetThreadListener)
        udpBroadThread2?.start()
    }

    var networkCallbackModule: NetworkCallbackModule = object : NetworkCallbackModule {
        override fun onAvailable(network: Network?) {
            reBuild()
        }

        override fun onLost(network: Network?) {
            udpBroadThread?.close()
            udpBroadThread2?.close()
        }

        override fun onCapabilitiesChanged(network: Network?, networkCapabilities: NetworkCapabilities) {
            if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                when {
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        Timber.i("onCapabilitiesChanged: 网络类型为wifi")
                    }

                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        Timber.i("onCapabilitiesChanged: 网络类型为以太网")
                    }

                    else -> {
                        Timber.i("onCapabilitiesChanged: 其他网络")
                    }
                }
            }
        }
    }

    fun byteToString(data: ByteArray): String {
        var index = data.size
        for (i in data.indices) {
            if (data[i].toInt() == 0) {
                index = i
                break
            }
        }
        val temp = ByteArray(index)
        Arrays.fill(temp, 0.toByte())
        System.arraycopy(data, 0, temp, 0, index)
        val str: String
        try {
            str = String(temp, charset("GBK"))
        } catch (e: UnsupportedEncodingException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            return ""
        }
        return str
    }

//    fun initAppData(context: Context, callback: () -> Unit) {
//        mScope.launch {
//            var path = Environment.getExternalStorageDirectory().absolutePath + BASE_FILE
//            var file = File(path)
//            val copyFun = {
//                val res = FileUtil.copyAssetFile(context, CONFIG_FILE, path)
//                Timber.i("KTAG copyFun $res")
//            }
//            path += CONFIG_FILE
//            if (!file.exists()) {
//                file.mkdirs()
//                Timber.i("KTAG 路径不存在 $path")
//                // copy
//                copyFun.invoke()
//            } else {
//                file = File(path)
//                if (file.exists()) {
//                    val json = FileUtil.readFile(path)
//                    val netBean = gson.fromJson(json, NetBean::class.java)
//                    port = netBean.port
//                    ip = netBean.ip ?: ip
////                    val applist = gson.fromJson<ArrayList<NetBean>>(json, object : TypeToken<List<AppInfo?>?>() {}.type)
////                    Timber.i("KTAG json $json ${applist.size}")
////                    for (i in 0 until applist.size) {
////                        val app = applist[i]
////                        if (i < AppList.size) {
////                            AppList[i].name = app.name
////                        }
////                        Timber.i("KTAG app $app")
////                    }
//                    callback.invoke()
//                } else {
//                    copyFun.invoke()
//                }
//            }
//        }
//    }

}
