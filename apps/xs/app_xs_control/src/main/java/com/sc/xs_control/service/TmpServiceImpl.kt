package com.sc.xs_control.service

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
import androidx.databinding.ObservableBoolean
import com.nbhope.lib_frame.app.HopeBaseApp
import com.nbhope.lib_frame.network.NetworkCallback
import com.nbhope.lib_frame.network.NetworkCallbackModule
import com.nbhope.lib_frame.utils.HopeUtils
import com.nbhope.lib_frame.utils.NetworkUtil
import com.sc.xs_control.R
import com.sc.xs_control.constant.CommandConstant
import com.sc.xs_control.inter.ITmpService
import com.sc.xs_control.weight.UdpThread
import timber.log.Timber
import java.lang.ref.WeakReference

/**
 * @author  tsc
 * @date  2026/10/8
 * @version 0.0.0-1
 * @description 指令服务
 * 1. 依据《指令代码2.xlsx》(2026/10/15更新版) 生成对应指令发射器，与界面按钮绑定发送指令（统一UDP）；
 * 2. 当前无指令接收协议，监听9128端口，收到数据仅在日志中打印。
 *
 * 备注：
 * - 大厅电子屏/换装体验 开/关为字符串指令：开="0"、关="1"，各发对应IP；
 * - 8 位掩码（发.102）：bit1=大厅电子屏(PC)，bit2~bit7=电视1~6，bit8未使用，
 *   PC 开/关除字符串指令外还需同时发 bit1 掩码（新表调整，旧表为 bit1~6=电视1~6）；
 * - 表格中设备控制 IP 段 192.169.1.x 为笔误，已按 192.168.1.x 配置；
 * - 一键开关(设备) 全开/全关：大厅电子屏/换装体验发字符串指令，
 *   电视发 8 位掩码指令。
 */
class TmpServiceImpl : ITmpService, Service() {

    companion object {
        const val TAG = "XCTAG"

        var CHANNEL_ONE_NAME = "TmpServiceImpl"
        var CHANNEL_ONE_ID = TmpServiceImpl::class.java.simpleName
        const val SERVICE_NOTICE_ID = 101
    }

    private val mBinder: IBinder = BaseBinder()

    private lateinit var mainHandler: MainHandler

    lateinit var networkCallback: NetworkCallback

    /* 指令发射/监听线程 */
    private var udpThread: UdpThread? = null

    /* view */
    private var rootView: View? = null

    /** 网络是否正常（侧栏底部联机状态显示，界面通过 ITmpService.getNetObs 获取） */
    private val mNetObs = ObservableBoolean(false)

    override fun getNetObs(): ObservableBoolean? {
        return mNetObs
    }

    override fun onCreate() {
        super.onCreate()
        initNotice()
        init(application)
        mainHandler = MainHandler(Looper.getMainLooper())
        networkCallback = (application as HopeBaseApp).getAppComponent().networkCallback
        networkCallback.registNetworkCallback(networkCallbackModule)
        mNetObs.set(NetworkUtil.isNetworkConnected(this))
        Timber.i("$TAG service Create " + HopeUtils.getIP())
        reBuild()
    }

    override fun onDestroy() {
        super.onDestroy()
        udpThread?.close()
    }

    private fun initNotice() {
        var builder: Notification.Builder? = Notification.Builder(this)
        builder!!.setSmallIcon(R.drawable.logo).setWhen(System.currentTimeMillis())
        builder.setContentTitle("KeepAppAlive")
        builder.setContentText(resources.getString(R.string.app_name) + "服务 is running...")
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

    override fun showFloat() {
        mainHandler.removeMessages(MSG_FLOAT_HIDE)
        mainHandler.sendEmptyMessage(MSG_FLOAT_SHOW)
    }

    override fun hideFloat(delayMillis: Long) {
        mainHandler.sendEmptyMessage(MSG_FLOAT_HIDE)
    }

    override fun write(msg: String) {

    }

    /**
     * 重建UDP监听（网络可用时重建）
     */
    override fun reBuild() {
        try {
            udpThread?.close()
        } catch (e: Exception) {
            Timber.e("$TAG udpThread close fail ${e.message}")
        }
        try {
            // 监听 9128，收到数据打印日志（当前无接收协议）
            udpThread = UdpThread(CommandConstant.LOCAL_LISTEN_PORT)
            udpThread?.onReceive = { ip, port, data ->
                // 预留：后续补充接收协议处理
                Timber.i("$TAG UDP onReceive $ip:$port len=${data.size}")
            }
            udpThread?.start()
        } catch (e: Exception) {
            Timber.e("$TAG udpThread create fail ${e.message}")
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

    var networkCallbackModule: NetworkCallbackModule = object : NetworkCallbackModule {
        override fun onAvailable(network: Network?) {
            mNetObs.set(true)
            Timber.i("$TAG network available")
            reBuild()
        }

        override fun onLost(network: Network?) {
            // 某个网络断开后再整体确认一次
            mNetObs.set(NetworkUtil.isNetworkConnected(this@TmpServiceImpl))
            Timber.i("$TAG network lost")
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

    // ==================== 指令发送 ====================

    /** 设备控制 一键开关（大厅电子屏/换装体验发字符串指令，电视发8位掩码） */
    override fun sendDeviceAllCmd(on: Boolean) {
        val strCmd = if (on) CommandConstant.CMD_STR_ON else CommandConstant.CMD_STR_OFF
        sendHex(CommandConstant.DEVICE_IP_PC, strCmd)
        sendHex(CommandConstant.DEVICE_IP_AIO, strCmd)
        val mask = if (on) CommandConstant.MASK_DEVICE_ALL_ON else CommandConstant.MASK_DEVICE_ALL_OFF
        sendHex(CommandConstant.DEVICE_IP_TV, mask)
    }

    /** 大厅电子屏 开/关：发字符串指令（开="0"、关="1"），并同步发 bit1 掩码到 .102 */
    override fun sendPcCmd(on: Boolean) {
        val cmd = if (on) CommandConstant.CMD_STR_ON else CommandConstant.CMD_STR_OFF
        sendHex(CommandConstant.DEVICE_IP_PC, cmd)
        // 新表：PC 占 8 位掩码 bit1，开关时同步发往电视掩码目标
        val mask = if (on) CommandConstant.MASK_PC_ON else CommandConstant.MASK_PC_OFF
        sendHex(CommandConstant.DEVICE_IP_TV, mask)
    }

    /** 换装体验(一体机) 开/关：发字符串指令（开="0"、关="1"） */
    override fun sendAioCmd(on: Boolean) {
        val cmd = if (on) CommandConstant.CMD_STR_ON else CommandConstant.CMD_STR_OFF
        sendHex(CommandConstant.DEVICE_IP_AIO, cmd)
    }

    /** 大厅电子屏 播放/暂停：发字符串指令（"play"/"pause"）到 .90:2368 */
    override fun sendPcMediaCmd(isPlay: Boolean) {
        sendMediaCmd(0, isPlay)
    }

    /** 电视 index: 1~6 播放/暂停：发字符串指令（"play"/"pause"）到 .(89+index):2368 */
    override fun sendTvMediaCmd(index: Int, isPlay: Boolean) {
        if (index < 1 || index > 6) {
            Timber.e("$TAG 电视序号越界 $index")
            return
        }
        sendMediaCmd(index, isPlay)
    }

    /**
     * 播放/暂停指令
     * @param deviceIndex 0=大厅电子屏(PC)，1~6=电视1~6，对应 MEDIA_IPS 下标
     */
    private fun sendMediaCmd(deviceIndex: Int, isPlay: Boolean) {
        val cmd = if (isPlay) CommandConstant.CMD_MEDIA_PLAY else CommandConstant.CMD_MEDIA_PAUSE
        sendHex(CommandConstant.MEDIA_IPS[deviceIndex], CommandConstant.MEDIA_PORT, cmd)
    }

    /** 大厅电子屏 静音/取消静音（"voice:0"/"voice:100"，发 .90:2368） */
    override fun sendPcVoiceCmd(mute: Boolean) {
        sendVoiceCmd(0, mute)
    }

    /** 电视 index: 1~6 静音/取消静音（"voice:0"/"voice:100"，发 .(89+index):2368） */
    override fun sendTvVoiceCmd(index: Int, mute: Boolean) {
        if (index < 1 || index > 6) {
            Timber.e("$TAG 电视序号越界 $index")
            return
        }
        sendVoiceCmd(index, mute)
    }

    /**
     * 静音/取消静音指令（格式与 xs_mediaplay 的 MessageConstant.CMD_VOICE 协议一致）
     * @param deviceIndex 0=大厅电子屏(PC)，1~6=电视1~6，对应 MEDIA_IPS 下标
     */
    private fun sendVoiceCmd(deviceIndex: Int, mute: Boolean) {
        val cmd = if (mute) CommandConstant.CMD_VOICE_MUTE else CommandConstant.CMD_VOICE_UNMUTE
        sendHex(CommandConstant.MEDIA_IPS[deviceIndex], CommandConstant.MEDIA_PORT, cmd)
    }

    /** 电视 index: 1~6 */
    override fun sendTvCmd(index: Int, on: Boolean) {
        if (index < 1 || index > 6) {
            Timber.e("$TAG 电视序号越界 $index")
            return
        }
        // 8位掩码：bit1=PC(恒为'x')，bit2~bit7=电视1~6，bit8未使用；
        // 仅控制位写1/0，其余位发'x'（设备端不改变）
        val sb = StringBuilder(8)
        for (i in 0 until 8) {
            sb.append(if (i == index) (if (on) '1' else '0') else 'x')
        }
        sendHex(CommandConstant.DEVICE_IP_TV, "setr=$sb")
    }

    /** 灯光控制 一键开关 */
    override fun sendLightAllCmd(on: Boolean) {
        // 16位掩码，一键全开/全关
        val mask = if (on) CommandConstant.MASK_LIGHT_ALL_ON else CommandConstant.MASK_LIGHT_ALL_OFF
        sendHex(CommandConstant.LIGHT_IP, mask)
    }

    /** 顶面照明 一键 */
    override fun sendLightCeilingAllCmd(on: Boolean) {
        // bit1~bit3
        sendLightCmd(0, 3, on)
    }

    /** 顶面照明 区域 index: 1~3 */
    override fun sendLightCeilingAreaCmd(index: Int, on: Boolean) {
        sendLightAreaCmd(0, index, on)
    }

    /** 墙面照明 一键 */
    override fun sendLightWallAllCmd(on: Boolean) {
        // bit4~bit6
        sendLightCmd(3, 6, on)
    }

    /** 墙面照明 区域 index: 1~3 */
    override fun sendLightWallAreaCmd(index: Int, on: Boolean) {
        sendLightAreaCmd(3, index, on)
    }

    /** 空间区域灯光 一键 */
    override fun sendLightSpaceAllCmd(on: Boolean) {
        // bit7~bit9
        sendLightCmd(6, 9, on)
    }

    /** 空间区域灯光 区域 index: 1~3 */
    override fun sendLightSpaceAreaCmd(index: Int, on: Boolean) {
        sendLightAreaCmd(6, index, on)
    }

    /**
     * 灯光分组指令
     * @param start 组起始bit下标（含）：0=顶面，3=墙面，6=空间
     * @param end   组结束bit下标（不含）：顶面0..3，墙面3..6，空间6..9
     */
    private fun sendLightCmd(start: Int, end: Int, on: Boolean) {
        if (start < 0 || end > 9 || start >= end) {
            Timber.e("$TAG 灯光分组bit范围非法 start=$start end=$end")
            return
        }
        // 16位掩码，仅控制位写1/0，其余位（含未用位）发'x'（设备端不改变）
        val sb = StringBuilder(16)
        for (i in 0 until 9) {
            sb.append(if (i in start until end) (if (on) '1' else '0') else 'x')
        }
        sb.append("xxxxxxx") // bit10~bit16 未使用
        sendHex(CommandConstant.LIGHT_IP, "setr=$sb")
    }

    /**
     * 灯光分区域内单区指令
     * @param offset 组起始bit下标
     * @param index  区域 1~3
     */
    private fun sendLightAreaCmd(offset: Int, index: Int, on: Boolean) {
        if (index < 1 || index > 3) {
            Timber.e("$TAG 区域序号越界 $index")
            return
        }
        // 16位掩码，仅控制位写1/0，其余位（含未用位）发'x'（设备端不改变）
        val sb = StringBuilder(16)
        for (i in 0 until 9) {
            sb.append(if (i == offset + index - 1) (if (on) '1' else '0') else 'x')
        }
        sb.append("xxxxxxx")
        sendHex(CommandConstant.LIGHT_IP, "setr=$sb")
    }

    /**
     * 发送指令（字符串转字节后UDP发送，默认端口9128）
     */
    private fun sendHex(ip: String, cmd: String) {
        sendHex(ip, CommandConstant.UDP_PORT, cmd)
    }

    /**
     * 发送指令（字符串转字节后UDP发送）
     */
    private fun sendHex(ip: String, port: Int, cmd: String) {
        Timber.i("$TAG 发送指令 -> $ip:$port cmd=$cmd")
        udpThread?.send(ip, port, cmd.toByteArray())
    }

}
