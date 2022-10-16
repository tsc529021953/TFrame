package com.sc.lib_local_device.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Network
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.*
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.jeremyliao.liveeventbus.LiveEventBus
import com.lib.halo.HaloManagerImp
import com.lib.tcp.event.MinaHandlerEvent
import com.nbhope.app_uhome_local.event.UHomeLocalEvent
import com.nbhope.lib_frame.app.HopeBaseApp
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.network.NetworkCallbackModule
import com.nbhope.lib_frame.utils.HopeUtils
import com.nbhope.lib_frame.utils.NetworkUtil
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.nbhope.phmina.base.HaloType
import com.nbhope.phmina.base.MinaConstants
import com.nbhope.phmina.bean.data.ClientInfo
import com.sc.lib_local_device.R
import com.sc.lib_local_device.common.DeviceCommon
import com.sc.lib_local_device.dao.CmdItem
import com.sc.lib_local_device.dao.DeviceInfo
import kotlinx.coroutines.*
import timber.log.Timber
import java.lang.Thread.sleep
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.Executors

/**
 * @author  tsc
 * @date  2022/7/30 14:52
 * @version 0.0.0-1
 * @description
 * 1. 开启组播，传送自身信息 包含 code ip type
 * 2. 监听组播，ctrl 显示列表，选择后 发送给展示端 包含 code
 * 3. 展示端收到信息，连接控制端
 * 4. 展示端连接成功后，监听控制消息
 */
class MainServiceImpl : Service() , MainService{

    companion object {
        const val TAG = "XTAG"

        const val PORT = 8091
        const val NOTICE_ID = 100
        var CHANNEL_ONE_NAME = "UHomeLocalServiceImpl"
        var CHANNEL_ONE_ID = HopeUtils.getSN()

        const val DEVICE_CTRL = "2019052300000366"
        const val SCENE_CTRL = "2021120700000123"

        private const val COMMAND_TUYA = "DUI.SmartHome."

        const val HEART_TIMER: Long = 15000
    }

    private var thread = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    private val scope = CoroutineScope(thread)

    private val mainBinder: IBinder = MainBinder()

    private val minaManager = MainMinaManager()

    private lateinit var wakeLock: PowerManager.WakeLock

    private lateinit var multicastLock: WifiManager.MulticastLock

    // 组播
    private var mMinaCtrl: MinaLinkManager? = null

    private var networkAvailable = false

    private var clientOpened = false

    private var serverOpened = false

    lateinit var spManager: SharedPreferencesManager

    override fun onBind(p0: Intent?): IBinder? {
        return this.mainBinder
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("LTAG onCreate")
        val appComponent = (application as HopeBaseApp).getAppComponent()
        spManager = appComponent.sharedPreferencesManager
        // 读取一下记录的本地网关ip
        // 此处回调为开始的设备查找信息+后来的控制信息传递
        mMinaCtrl = object : MinaLinkManager(HaloManagerImp().also {
            it.getClientInfo().also {
                it.type = 1
                it.name = HopeUtils.getSN()
            }
        }) {
            // 传递tcp服务器状态 断开时
            override fun notifyTcpServiceState(opened: Boolean) {
//                var params = JsonObject()
//                params.addProperty("state", opened)
//                notifyInterPhoneMsg(MinaConstants.CMD_SERVICE_STATE, params, null)
                Timber.i("$TAG ServiceState $opened")
                serverOpened = opened
                if (!opened)
                    LiveEBUtil.post(UHomeLocalEvent(MinaConstants.CMDLOCAL_DISCONNECT, ""))
//                    LiveEBUtil.post(UHomeLocalEvent(MinaConstants.CMDLOCAL_CONNECT, ""))
//                else  LiveEBUtil.post(UHomeLocalEvent(MinaConstants.CMDLOCAL_DISCONNECT, ""))
            }

            override fun notifyClientState(opened: Boolean) {
//                var params = JsonObject()
//                params.addProperty("state", opened)
//                notifyInterPhoneMsg(MinaConstants.CMD_CLIENT_STATE, params, null)
                Timber.i("$TAG ClientState $opened")
                clientOpened = opened
                if (opened) {
                    // 关闭定时器
                    if (DeviceCommon.deviceType == DeviceCommon.DeviceType.Ctrl) {
                        stopTimer()
                    }
                    LiveEBUtil.post(UHomeLocalEvent(MinaConstants.CMDLOCAL_CONNECT, ""))
                }
                else {
                    // 开启定时器
                    if (DeviceCommon.deviceType == DeviceCommon.DeviceType.Ctrl)
                        startTimer()
                    LiveEBUtil.post(UHomeLocalEvent(MinaConstants.CMDLOCAL_DISCONNECT, ""))
                }
            }

            override fun notifyReceiverMsg(cmd: String, params: JsonElement?, srcMsg: String?) {
//                notifyInterPhoneMsg(cmd, params, srcMsg)
                Timber.i("$TAG ReceiverMsg $cmd $srcMsg $params")
                when (cmd) {
                    MinaConstants.CMD_DISCOVER_RS -> {
                        // 当设备查找到
                        var reciver = Gson().fromJson<ClientInfo>(
                            params, ClientInfo::class.java
                        )
                        if (DeviceCommon.deviceType == DeviceCommon.DeviceType.Ctrl &&
                            reciver.hopeSn != HopeUtils.getSN() && reciver.deviceType == 1) {
                            // 查找到新设备 通知给界面
                                Timber.i("XTAG 查找到新设备 ${reciver.localIp} ${reciver.hopeSn}")
                            var item = DeviceInfo(reciver.hopeSn, reciver.localIp)
                            LiveEBUtil.post(UHomeLocalEvent(MinaConstants.CMD_DISCOVER_RS, item))
                        }
                    }
                    MinaConstants.CMD_CHANGE_GROUP, MinaConstants.CMD_CHANGE_INDEX, MinaConstants.CMD_CHANGE_SIGN -> {
                        try {
                            var reciver = Gson().fromJson<CmdItem>(
                                params, CmdItem::class.java
                            )
                            Timber.i("$TAG 接收到控制消息")
                            if (reciver == null) return
                            LiveEBUtil.post(UHomeLocalEvent(cmd, reciver))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }
            }
        }
//        init()
        appComponent.networkCallback.registNetworkCallback(object : NetworkCallbackModule {
            override fun onAvailable(network: Network?) {
                Timber.i("$TAG onAvailable")
//                if (!minaManager.isConnect()) {
//                    minaManager.startMina()
//                }
                networkAvailable = true
                init()

                // 联网状态下 判断tcp是否连接 重新连接tcp
            }

            override fun onLost(network: Network?) {
                Timber.i("onLost")
                networkAvailable = false
                // 网络断开 断开tcp连接
                mMinaCtrl?.distoryAll()
//                if (minaManager.isConnect()) {
//                    minaManager.stopMina()
//                }
                LiveEBUtil.post(UHomeLocalEvent(MinaConstants.CMDLOCAL_DISCONNECT, ""))
            }

            override fun onCapabilitiesChanged(
                network: Network?,
                networkCapabilities: NetworkCapabilities
            ) {

            }

        })
        // tcp 连接信息的回调
        LiveEventBus.get().with(MinaHandlerEvent::class.java.simpleName)
            .observeForever(minaManager.minaObserver)
        LiveEBUtil.registForever(
            RemoteMessageEvent::class.java,
            LiveRemoteObserver.liveRemoteMusicObserver
        )
        LiveRemoteObserver.mainServiceImpl = this
        var builder: Notification.Builder? = Notification.Builder(this)
        builder!!.setSmallIcon(R.mipmap.ic_home).setWhen(System.currentTimeMillis())
        builder.setContentTitle("KeepAppAlive")
        builder.setContentText("XSGService is running...")
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
        startForeground(NOTICE_ID, builder.build())
        // 如果觉得常驻通知栏体验不好
        // 可以通过启动CancelNoticeService，将通知移除，oom_adj值不变
        var intent: Intent? = Intent(this, CancelNoticeService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //android8.0以上通过startForegroundService启动service
            startForegroundService(intent);
        } else {
            startService(intent);
        }


        //SCREEN_DIM_WAKE_LOCK
        wakeLock = (this.getSystemService(Context.POWER_SERVICE) as PowerManager).newWakeLock(
            PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.SCREEN_DIM_WAKE_LOCK,
            "speech::WakeAndLock"
        )
        multicastLock = (this.getSystemService(Context.WIFI_SERVICE) as WifiManager).createMulticastLock("multicast.test")
        multicastLock.acquire()
    }

    override fun onDestroy() {
        scope.cancel()
        stopTimer()
        mMinaCtrl?.distoryAll()
        multicastLock?.release()
        LiveEBUtil.unRegist(
            RemoteMessageEvent::class.java,
            LiveRemoteObserver.liveRemoteMusicObserver
        )
//        LiveEventBus.get().with(MinaHandlerEvent::class.java.simpleName)
//            .removeObserver(minaManager.minaObserver)
//        minaManager.stopMina()
        super.onDestroy()
    }

    fun init() {
        // 创建组播监听
//        // 读取一下记录的本地网关ip
        Timber.d("XTAG init ${Build.VERSION.SDK_INT >= Build.VERSION_CODES.M}")
//        if (uHomeLocalIP == null || uHomeLocalIP == "") {
//            Timber.d("LTAG 未读取到记录的网关ip")
//            // 弹出弹框，用于输入ip
//            return
//        }
//        minaManager.init(PORT, uHomeLocalIP!!)
        if (!NetworkUtil.isNetworkConnected(baseContext)) {
//            // 当前网络不可用
            networkAvailable = true
            Timber.d("XTAG 当前无可用网络")
            Toast.makeText(baseContext, "当前无可用网络！", Toast.LENGTH_SHORT).show()
            return
        }
        Timber.i("XTAG init view")
        networkAvailable = true
        createMultiCast()

        onChangeType()
    }

    // 用于创建用于寻找设备的线程 组播
    private fun createMultiCast(){
        Timber.i("$TAG createMultiCast $networkAvailable")
        if (networkAvailable) {
            mMinaCtrl?.createMultiCast()
        }
    }

    override fun onChangeType() {
        // 首先尝试关闭所有
        if (DeviceCommon.deviceType == DeviceCommon.DeviceType.View) {
            // 创建tcp服务器
            mMinaCtrl?.distoryClient()
            Timber.i("$TAG 创建tcp服务器")
            mMinaCtrl?.createTcpService()
        } else {
            // 创建tcp客户端
            mMinaCtrl?.distoryService()
            // 尝试连接之前那个
            if (DeviceCommon.recordDeviceInfo != null) {
                connectServer(DeviceCommon.recordDeviceInfo.ip)
            }
        }
    }

    override fun connectServer(ip: String) {
        GlobalScope.launch (Dispatchers.IO){

            val status = mMinaCtrl?.iHaloManager?.isRunning(HaloType.TCP_CLIENT)
            Timber.i("XTAG connectServer $ip $status")
            if (status == null) return@launch
            if (status!!) {
                if (mMinaCtrl?.iHaloManager?.getCurrentTcpServiceIp() != ip)
                {
                    mMinaCtrl?.distoryClient()
                    sleep(500)
                }
            }
            mMinaCtrl?.createTcpClient(ip)
            // 存储信息
            if (DeviceCommon.recordDeviceInfo != null && DeviceCommon.recordDeviceInfo.ip != ip) {
                DeviceCommon.recordDeviceInfo.ip = ip
                DeviceCommon.saveRecordDeviceInfo(spManager, DeviceCommon.recordDeviceInfo)
            }
            Timber.i("XTAG connectServer end $ip ${mMinaCtrl?.iHaloManager?.getCurrentTcpServiceIp()}")
        }
    }

    override fun getStatus(type: HaloType): Boolean {
        return when (type) {
            HaloType.TCP_CLIENT -> clientOpened
            HaloType.TCP_SERVER -> serverOpened
            else -> mMinaCtrl?.iHaloManager?.isRunning(type) == true
        }
    }

    override fun sendMulMessage(msg: String) {
        Timber.i("XTAG sendMulMessage $msg")
        mMinaCtrl?.iHaloManager?.mutilSendMsg(msg)
    }

    override fun sendClientMessage(msg: String) {
        Timber.i("$TAG sendClientMessage $msg")
        mMinaCtrl?.iHaloManager?.clientSendMsg(msg)
    }

    override fun init(context: Context?) {

    }

    inner class MainBinder : Binder() {
        fun getSpeechService(): MainServiceImpl {
            return WeakReference(this@MainServiceImpl).get()!!
        }
    }


    private fun startTimer() {
        if (posTimer != null && posTimerTask != null) return
        posTimer = Timer()
        posTimerTask = object : TimerTask() {
            override fun run() {
                // 间隔时间到了 尝试一次重新连接
                if (networkAvailable) connectServer(DeviceCommon.recordDeviceInfo.ip)
            }
        }
        posTimer?.schedule(posTimerTask, 0, HEART_TIMER)
    }

    var lastHeartTimer: Long = 0
    var heartFailCount = 0

    val POSITION = 10086
    var posTimer: Timer? = null
    var posTimerTask: TimerTask? = null
    var posHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == POSITION) {
                // 传递给主线程更新
//                receiveRemotePosition(gtCurrentPosition())
            }
        }
    }

    private fun stopTimer() {
        posTimer?.cancel()
        posTimer = null
        posTimerTask?.cancel()
        posTimerTask = null
    }

}