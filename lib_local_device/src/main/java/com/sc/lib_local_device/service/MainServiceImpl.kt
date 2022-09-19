package com.sc.lib_local_device.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.jeremyliao.liveeventbus.LiveEventBus
import com.lib.halo.HaloManagerImp
import com.lib.tcp.event.MinaHandlerEvent
import com.nbhope.app_uhome_local.event.UHomeLocalEvent
import com.nbhope.lib_frame.ITNotice
import com.nbhope.lib_frame.app.HopeBaseApp
import com.nbhope.lib_frame.bean.TMessage
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.nbhope.lib_frame.common.BaseMessage
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.network.NetworkCallbackModule
import com.nbhope.lib_frame.utils.HopeUtils
import com.nbhope.lib_frame.utils.NetworkUtil
import com.nbhope.phmina.base.MinaConstants
import com.nbhope.phmina.bean.data.ClientInfo
import com.sc.lib_local_device.R
import com.sc.lib_local_device.common.DeviceCommon
import com.sc.lib_local_device.dao.DeviceInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import timber.log.Timber
import java.lang.ref.WeakReference
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

    }

    private var thread = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    private val scope = CoroutineScope(thread)

    private val mainBinder: IBinder = MainBinder()

    private val minaManager = MainMinaManager()

    private lateinit var wakeLock: PowerManager.WakeLock

    // 组播
    private var mMinaCtrl: MinaLinkManager? = null

    private var networkAvailable = false

    override fun onBind(p0: Intent?): IBinder? {
        return this.mainBinder
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("LTAG onCreate")
        val appComponent = (application as HopeBaseApp).getAppComponent()
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
            }

            override fun notifyClientState(opened: Boolean) {
//                var params = JsonObject()
//                params.addProperty("state", opened)
//                notifyInterPhoneMsg(MinaConstants.CMD_CLIENT_STATE, params, null)
                Timber.i("$TAG ClientState $opened")
            }

            override fun notifyReceiverMsg(cmd: String, params: JsonElement?, srcMsg: String?) {
//                notifyInterPhoneMsg(cmd, params, srcMsg)
                Timber.i("$TAG ReceiverMsg $cmd $srcMsg $params")
                if (cmd == MinaConstants.CMD_DISCOVER_RS) {
                    // 当设备查找到
                    var reciver = Gson().fromJson<ClientInfo>(
                        params, ClientInfo::class.java
                    )
                    if (DeviceCommon.deviceType == DeviceCommon.DeviceType.Ctrl &&
                        reciver.hopeSn != HopeUtils.getSN()) {
                        // 查找到新设备 通知给界面
                        var item = DeviceInfo(reciver.hopeSn, reciver.localIp)
                        LiveEBUtil.post(UHomeLocalEvent(MinaConstants.CMD_DISCOVER_RS, item))
                    }
                }
            }
        }
        init()
        appComponent.networkCallback.registNetworkCallback(object : NetworkCallbackModule {
            override fun onAvailable(network: Network?) {
//                if (!minaManager.isConnect()) {
//                    minaManager.startMina()
//                }
                networkAvailable = true
                init()
                Timber.i("$TAG onAvailable")
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
        builder.setContentText("SpeechService is running...")
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
    }

    override fun onDestroy() {
        scope.cancel()
        mMinaCtrl?.distoryAll()
        LiveEBUtil.unRegist(
            RemoteMessageEvent::class.java,
            LiveRemoteObserver.liveRemoteMusicObserver
        )
//        LiveEventBus.get().with(MinaHandlerEvent::class.java.simpleName)
//            .removeObserver(minaManager.minaObserver)
//        minaManager.stopMina()
        super.onDestroy()
    }

    private fun init() {
        // 创建组播监听
//        // 读取一下记录的本地网关ip
//        Timber.d("LTAG init ${Build.VERSION.SDK_INT >= Build.VERSION_CODES.M} $uHomeLocalIP ${minaManager.isConnect()}")
//        if (uHomeLocalIP == null || uHomeLocalIP == "") {
//            Timber.d("LTAG 未读取到记录的网关ip")
//            // 弹出弹框，用于输入ip
//            return
//        }
//        minaManager.init(PORT, uHomeLocalIP!!)
        if (!NetworkUtil.isNetworkAvailable(baseContext)) {
//            // 当前网络不可用
            networkAvailable = true
            Toast.makeText(baseContext, "当前无可用网络！", Toast.LENGTH_SHORT).show()
            return
        }
        networkAvailable = true
        createMultiCast()
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
        mMinaCtrl?.distoryClient()
        mMinaCtrl?.distoryService()
        if (DeviceCommon.deviceType == DeviceCommon.DeviceType.View) {
            // 创建tcp服务器
            mMinaCtrl?.createTcpService()
        } else {
            // 创建tcp客户端

        }
    }

    override fun connectServer(ip: String) {
        mMinaCtrl?.distoryClient()
        mMinaCtrl?.createTcpClient(ip)
    }

    override fun init(context: Context?) {

    }

    inner class MainBinder : Binder() {
        fun getSpeechService(): MainServiceImpl {
            return WeakReference(this@MainServiceImpl).get()!!
        }
    }

}