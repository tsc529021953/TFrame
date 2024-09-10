package com.xs.xs_ctrl.service

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
import androidx.annotation.RequiresApi
import com.dlong.dl10netassistant.OnNetThreadListener
import com.dlong.dl10netassistant.TcpClientThread
import com.dlong.dl10netassistant.TcpServerThread
import com.dlong.dl10netassistant.UdpBroadThread
import com.google.gson.Gson
import com.nbhope.lib_frame.app.HopeBaseApp
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.network.NetworkCallback
import com.nbhope.lib_frame.network.NetworkCallbackModule
import com.nbhope.lib_frame.utils.DataUtil
import com.nbhope.lib_frame.utils.HopeUtils
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.nbhope.lib_frame.utils.TimerHandler
import com.xs.xs_ctrl.R
import com.xs.xs_ctrl.constant.MessageConstant
//import com.xs.xs_ctrl.bean.OneCtrlBean
//import com.xs.xs_ctrl.bean.OneCtrlPage
//import com.xs.xs_ctrl.bean.ThemeBean
//import com.xs.xs_ctrl.constant.MessageConstant
import com.xs.xs_ctrl.inter.ITmpService
import com.xs.xs_ctrl.utils.SPUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber
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

        const val SERVER_PORT = 10000
    }

    private val mBinder: IBinder = BaseBinder()

    private lateinit var mScope: CoroutineScope

    /*view*/
    private var rootView: View? = null

    lateinit var mainHandler: MainHandler

    private var timerHandler: TimerHandler? = null

    private var tcpClient1: TcpClientThread? = null
    private var tcpClient2: TcpClientThread? = null
    private var tcpClient3: UdpBroadThread? = null

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
        networkCallback.registNetworkCallback(networkCallbackModule)
        Timber.i("XTAG service Create " + HopeUtils.getIP())

        MessageConstant.ip = SPUtils.getValue(this, MessageConstant.SP_IP, MessageConstant.ip).toString()
        MessageConstant.port = SPUtils.getValue(this, MessageConstant.SP_PORT, MessageConstant.port) as Int
        MessageConstant.ip2 = SPUtils.getValue(this, MessageConstant.SP_IP2, MessageConstant.ip2).toString()
        MessageConstant.port2 = SPUtils.getValue(this, MessageConstant.SP_PORT2, MessageConstant.port2) as Int
        MessageConstant.port3 = SPUtils.getValue(this, MessageConstant.SP_PORT3, MessageConstant.port3) as Int
        reBuild()
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
        builder.setContentText("XS_MP_Service is running...")
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

    override fun init(context: Context) {

    }

    fun release() {
        try {
            tcpClient1?.close()
            tcpClient2?.close()
            tcpClient3?.close()
        } catch (e: Exception) {}
    }

    override fun showFloat() {
        mainHandler.removeMessages(MSG_FLOAT_HIDE)
        mainHandler.sendEmptyMessage(MSG_FLOAT_SHOW)
    }

    override fun hideFloat(delayMillis: Long) {
        mainHandler.sendEmptyMessage(MSG_FLOAT_HIDE)
    }

    override fun write(msg: String) {
        Timber.i("XTAG write ${tcpClient1 == null} $msg")
        if (tcpClient1 != null) {
//            tcpClient1?.send(MessageConstant.ip, MessageConstant.port, msg.toByteArray(Charsets.UTF_8))
//            tcpClient1?.send(msg.toByteArray(Charsets.UTF_8))
            val bytes = DataUtil.hexStringToBytes(msg) ?: return
            tcpClient1?.send(bytes)
        }
    }

    override fun write2(msg: String) {
        Timber.i("XTAG write2 ${tcpClient2 == null} $msg")
        if (tcpClient2 != null) {
            // MessageConstant.ip2, MessageConstant.port2,
            tcpClient2?.send(msg.toByteArray(Charsets.UTF_8))
        }
    }

    override fun writeMedia(ip: String, msg: String) {
        Timber.i("XTAG writeMedia ${tcpClient3 == null} $msg")
        if (tcpClient3 != null) {
            tcpClient3?.send(ip, MessageConstant.port3, msg.toByteArray(Charsets.UTF_8))
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
            var msg = DataUtil.byteToString(data) // data.toString(Charsets.UTF_8)
            Timber.i("XTAG onReceive ipAddress $ipAddress $port $msg")
            msg = msg.trim().replace("\r\n", "").toLowerCase()

            when (msg) {
                MessageConstant.CMD_PLAY,
                MessageConstant.CMD_PAUSE,
                MessageConstant.CMD_STOP,
                MessageConstant.CMD_PRE,
                MessageConstant.CMD_UPPER,
                MessageConstant.CMD_LOWER,
                MessageConstant.CMD_NEXT -> {
                    LiveEBUtil.post(RemoteMessageEvent(msg, ""))
                }
                else -> {
                    if (msg.startsWith(MessageConstant.CMD_VOICE)) {
                        LiveEBUtil.post(RemoteMessageEvent(MessageConstant.CMD_VOICE,  msg.split(':')[1]))
                    } else if (msg.startsWith(MessageConstant.CMD_POSITION)) {
                        LiveEBUtil.post(RemoteMessageEvent(MessageConstant.CMD_POSITION,  msg.split(':')[1]))
                    }
                }
            }
        }

    }

    override fun reBuild() {
        Timber.i("XTAG reBuild")
        try {
            tcpClient1?.close()
        } catch (e: Exception) {}
        tcpClient1 = TcpClientThread(MessageConstant.ip, MessageConstant.port, onNetThreadListener)
        tcpClient1?.start()

        try {
            tcpClient2?.close()
        } catch (e: Exception) {}
        tcpClient2 = TcpClientThread(MessageConstant.ip2, MessageConstant.port2, onNetThreadListener)
        tcpClient2?.start()

        try {
            tcpClient3?.close()
        } catch (e: Exception) {}
        tcpClient3 = UdpBroadThread(MessageConstant.port3, onNetThreadListener)
        tcpClient3?.start()
//        Timber.i("XTAG reBuild state ${tcpClient1?.isConnected()} ${tcpClient2?.isConnected()}")
    }

    var networkCallbackModule: NetworkCallbackModule = object : NetworkCallbackModule {
        override fun onAvailable(network: Network?) {
            reBuild()
        }

        override fun onLost(network: Network?) {
            release()
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

}
