package com.sc.nft.service

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
import com.alibaba.android.arouter.launcher.ARouter
import com.nbhope.lib_frame.app.HopeBaseApp
import com.nbhope.lib_frame.arouterpath.RouterPath
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.network.NetworkCallbackModule
import com.nbhope.lib_frame.utils.HopeUtils
import com.nbhope.lib_frame.utils.NetworkUtil
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.sc.nft.R
import com.sc.nft.vm.MainViewModel
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

        const val HEART_TIMER: Long = 120000
    }

    private var thread = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    private val scope = CoroutineScope(thread)

    private val mainBinder: IBinder = MainBinder()

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
//        init()
        appComponent.networkCallback.registNetworkCallback(object : NetworkCallbackModule {
            override fun onAvailable(network: Network?) {
                Timber.i("$TAG onAvailable")
//                if (!minaManager.isConnect()) {
//                    minaManager.startMina()
//                }
                networkAvailable = true
//                init()

                // 联网状态下 判断tcp是否连接 重新连接tcp
            }

            override fun onLost(network: Network?) {
                Timber.i("onLost")
                networkAvailable = false
            }

            override fun onCapabilitiesChanged(
                network: Network?,
                networkCapabilities: NetworkCapabilities
            ) {

            }

        })
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
        startTimer()
    }

    override fun onDestroy() {
        scope.cancel()
//        LiveEBUtil.unRegist(
//            RemoteMessageEvent::class.java,
//            LiveRemoteObserver.liveRemoteMusicObserver
//        )
//        LiveEventBus.get().with(MinaHandlerEvent::class.java.simpleName)
//            .removeObserver(minaManager.minaObserver)
//        minaManager.stopMina()
        stopTimer()
        super.onDestroy()
    }

    inner class MainBinder : Binder() {
        fun getSpeechService(): MainServiceImpl {
            return WeakReference(this@MainServiceImpl).get()!!
        }
    }

    override fun reTimer() {
        Timber.i("NTAG reTimer")
        startTimer()
    }

    override fun init(context: Context?) {

    }

    private fun startTimer() {
        if (posTimerTask == null || posTimerTask!!.cancel()) {
            posTimerTask = object : TimerTask() {
                override fun run() {
                    // 间隔时间到了 尝试一次重新连接
                    Timber.i("NTAG 锁屏")
                    ARouter.getInstance().build(RouterPath.activity_nft_screen).navigation()
                }
            }
        }
        if (posTimer == null)  posTimer = Timer()
        posTimer?.schedule(posTimerTask, HEART_TIMER, HEART_TIMER)
    }

    var lastHeartTimer: Long = 0
    var heartFailCount = 0

    val POSITION = 10086
    var posTimer: Timer? = null
    var posTimerTask: TimerTask? = null

    override fun stopTimer() {
        posTimer?.cancel()
        posTimer = null
        posTimerTask?.cancel()
        posTimerTask = null
    }

}
