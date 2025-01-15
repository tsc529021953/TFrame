package com.sc.tmp_cw.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Network
import android.net.NetworkCapabilities
import android.os.*
import android.view.View
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.alibaba.android.arouter.launcher.ARouter
import com.dlong.dl10netassistant.OnNetThreadListener
import com.dlong.dl10netassistant.UdpMultiThread
import com.google.gson.Gson
import com.nbhope.lib_frame.app.AppManager
import com.nbhope.lib_frame.app.HopeBaseApp
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.network.NetworkCallback
import com.nbhope.lib_frame.network.NetworkCallbackModule
import com.nbhope.lib_frame.utils.*
import com.sc.tmp_cw.R
import com.sc.tmp_cw.activity.UrgentNotifyActivity
import com.sc.tmp_cw.bean.CWInfo
import com.sc.tmp_cw.constant.MessageConstant
import com.sc.tmp_cw.inter.ITmpService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
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

        var SERVER_PORT = 16680
        var SERVER_IP = "234.55.66.80"
    }

    private val mBinder: IBinder = BaseBinder()

    lateinit var mScope: CoroutineScope

    /*view*/
    private var rootView: View? = null

    lateinit var mainHandler: MainHandler

    private var timerHandler: TimerHandler? = null

    lateinit var networkCallback: NetworkCallback

    private var gson = Gson()

    private val MSG_FLOAT_SHOW = 100
    private val MSG_FLOAT_UPDATE = 101
    private val MSG_FLOAT_HIDE = 102
    private val CONNECT_MSG = 1002

    /*udp*/
    private var comThread: UdpMultiThread? = null

    var cwInfo = CWInfo()
    private var isFirstLink = true

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
        networkCallback.registNetworkCallback(networkCallbackModule)
        Timber.i("XTAG service Create " + HopeUtils.getIP())
        initAppData(this) {
            rtspUrlObs.set(cwInfo.rtspUrl)
            if (NetworkUtil.isNetworkConnected(this))
                reBuild() // onCreate
            isFirstLink = false
        }
        urgentNotifyMsgObs.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                val m = urgentNotifyMsgObs.get()
                Timber.e("紧急通知 $m")
//                if (!m.isNullOrEmpty())
                LiveEBUtil.post(RemoteMessageEvent(MessageConstant.CMD_URGENT_NOTICE, m ?: ""))
                if (!m.isNullOrEmpty() && AppManager.appManager?.topActivity != null && AppManager.appManager!!.topActivity!!::class.java.simpleName != UrgentNotifyActivity::class.java.simpleName) {
                    ARouter.getInstance().build(MessageConstant.ROUTH_URGENT_NOTIFY).navigation(this@TmpServiceImpl)
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        timerHandler?.stop()
        release()
    }

    private fun initNotice() {
        var builder: Notification.Builder? = Notification.Builder(this)
        builder!!.setSmallIcon(R.drawable.logo).setWhen(System.currentTimeMillis())
        builder.setContentTitle("KeepAppAlive")
        builder.setContentText(resources.getString(R.string.app_name) + "服务 is running...")
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

    override var stationStatusObs = ObservableField<String>("")
    override var stationObs = ObservableField<String>("")
    override var timeObs = ObservableField<String>("")
    override var titleObs = ObservableField<String>("")
    override var rtspUrlObs = ObservableField<String>("")
    override var urgentNotifyMsgObs = ObservableField<String>("")

    override fun test(msg: String) {
        MessageHandler.test(msg, this)
    }

    private var onNetThreadListener = object : OnNetThreadListener {
        override fun onAcceptSocket(ipAddress: String) {
            // 不需要
        }

        override fun onConnectFailed(ipAddress: String) {
            // 打开端口失败
            Timber.i("onConnectFailed $ipAddress")
        }

        override fun onConnected(ipAddress: String) {
            // 打开端口成功
            Timber.i("onConnected $ipAddress")
        }

        override fun onDisconnect(ipAddress: String) {
            // 关闭UDP
            Timber.i("onDisconnect $ipAddress")
        }

        override fun onError(ipAddress: String, error: String) {
            // 发生错误
            Timber.i("onError $ipAddress $error")
        }

        override fun onReceive(ipAddress: String, port: Int, time: Long, data: ByteArray) {
            // 接受到信息，ipAddress消息来源地址，port消息来源端口，time消息到达时间，data消息内容
            var msg = DataUtil.byteArray2HexString(data)
            Timber.i("onReceive $ipAddress $port $time ${data.size} $msg")
            // 判断是否是hex
            mScope.launch {
                MessageHandler.handleMessage(msg, this@TmpServiceImpl)
            }
        }
    }

    override fun init(context: Context?) {

    }

    override fun reBuild() {
        try {
            release()
        } catch (e: Exception) {
            Timber.e("tcpBroadThread close fail ${e.message}")
        }
        try {
            if (comThread == null) {
                Timber.i("组播创建 $SERVER_IP $SERVER_PORT")
                comThread = UdpMultiThread(SERVER_IP, SERVER_PORT, onNetThreadListener)
            }
            comThread?.start()
        } catch (e: Exception) {
            Timber.e("tcpBroadThread create fail ${e.message}")
        }
    }

    override fun write(msg: String) {

    }

    fun getStationStr(id: Int): String {
        if (cwInfo != null && !cwInfo.stations.isNullOrEmpty()) {
            val item = cwInfo.stations!!.find { it.id == id }
            if (item != null) {
                return "${item.cn} ${item.en}"
            }
        }
        return ""
    }

    fun getUrgentNotifyStr(id: Int): String {
        if (cwInfo != null && !cwInfo.urgentNotify.isNullOrEmpty()) {
            val item = cwInfo.urgentNotify!!.find { it.id == id }
            if (item != null) {
                return item.msg
            }
        }
        return ""
    }

    private fun release() {
        comThread?.stop()
        comThread?.close()
    }

    /*方法*/
    private fun setStation(station: String) {
        stationObs.set(station)
    }


    /**/

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

    var networkCallbackModule: NetworkCallbackModule = object : NetworkCallbackModule {
        override fun onAvailable(network: Network?) {
            if (!isFirstLink)
                reBuild() // 联网
        }

        override fun onLost(network: Network?) {

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

    fun initAppData(context: Context, callback: () -> Unit) {
        mScope.launch {
            var path = Environment.getExternalStorageDirectory().absolutePath + MessageConstant.PATH_BASE_FILE
            var file = File(path)
            val analysis = { msg: String? ->
                if (msg != null) {
                    cwInfo = gson.fromJson(msg, CWInfo::class.java)
                    SERVER_PORT = cwInfo.port
                    SERVER_IP = cwInfo.ip
                    titleObs.set(cwInfo.title)
                    Timber.i("站点数量 ${cwInfo?.stations?.size}")
                }
                callback.invoke()
            }
            val copyFun = {
                val json = FileUtil.getFromAssets(MessageConstant.PATH_CONFIG_FILE, context)
                analysis.invoke(json)
                val res = FileUtil.copyAssetFile(context, MessageConstant.PATH_CONFIG_FILE, path)
                Timber.i("KTAG copyFun $res")
            }
            path += MessageConstant.PATH_CONFIG_FILE
            if (!file.exists()) {
                file.mkdirs()
                Timber.i("KTAG 路径不存在 $path")
                // copy
                copyFun.invoke()
            } else {
                file = File(path)
                if (file.exists()) {
                    val json = FileUtil.readFile(path)
                    analysis.invoke(json)
                } else {
                    copyFun.invoke()
                }
            }
        }
    }

}
