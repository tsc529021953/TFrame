package com.sc.float_setting.service

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
import android.widget.ImageView
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.databinding.Observable
import androidx.databinding.ObservableInt
import com.google.gson.Gson
import com.nbhope.lib_frame.app.HopeBaseApp
import com.nbhope.lib_frame.network.NetworkCallback
import com.nbhope.lib_frame.network.NetworkCallbackModule
import com.nbhope.lib_frame.utils.HopeUtils
import com.nbhope.lib_frame.utils.TimerHandler
import com.petterp.floatingx.FloatingX
import com.petterp.floatingx.assist.FxGravity
import com.petterp.floatingx.assist.FxScopeType
import com.petterp.floatingx.listener.IFxViewLifecycle
import com.sc.float_setting.R
import com.sc.float_setting.inter.ISerial
import com.sc.float_setting.inter.ITmpService
import com.sc.float_setting.utils.SerialHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.ref.WeakReference


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

        const val SERVER_PORT = 60000
    }

    private val mBinder: IBinder = BaseBinder()

    private lateinit var mScope: CoroutineScope

    /*view*/
    private var rootView: View? = null

    lateinit var mainHandler: MainHandler

    private var timerHandler: TimerHandler? = null

    lateinit var networkCallback: NetworkCallback

    private var gson = Gson()

    private var brightObs = ObservableInt(0)

    private var serialHelper: SerialHelper? = null

    private var brightSB: AppCompatSeekBar? = null

    override fun onCreate() {
        super.onCreate()
        initNotice()
        init(application)
        mScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        mainHandler = MainHandler(Looper.getMainLooper())
        networkCallback = (application as HopeBaseApp).getAppComponent().networkCallback
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initFloat()
        } else {
            Timber.i("$TAG 版本太低，请重新适配 ${Build.VERSION.SDK_INT}")
        }
        networkCallback.registNetworkCallback(networkCallbackModule)
        Timber.i("XTAG service Create " + HopeUtils.getIP())

        serialHelper = SerialHelper(object : ISerial {
            override fun onBrightnessChanged(brightness: Int) {
                Timber.i("onBrightnessChanged $brightness")
                brightObs.set(brightness)
                mScope.launch(Dispatchers.Main) {
                    brightSB?.progress = brightness
                }
            }
        })
        serialHelper?.init()
        brightObs.set(serialHelper!!.ratio)
    }

    override fun onDestroy() {
        super.onDestroy()
        timerHandler?.stop()
        serialHelper?.release()
    }

    private fun initNotice() {
        var builder: Notification.Builder? = Notification.Builder(this)
        builder!!.setSmallIcon(R.drawable.logo).setWhen(System.currentTimeMillis())
        builder.setContentTitle("KeepAppAlive")
        builder.setContentText("FLOAT_SETTING_Service is running...")
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
        val dm = application.resources.displayMetrics
        FloatingX.install {
            setContext(application)
            setLayout(R.layout.float_view)
            // 系统浮窗记得声明权限
            setY((dm.heightPixels / 4).toFloat())
            setScopeType(FxScopeType.SYSTEM)
            setGravity(FxGravity.LEFT_OR_BOTTOM)
//            setViewLifecycle(object : IFxViewLifecycle {
//                override fun initView(view: View) {
//                    //这里初始化
//                    initView()
//                }
//            })


//            setEnableLog(true)
//            setOnClickListener {
//                Timber.i("FTAG click $it ${FloatingX.control().getView()}")
//                // 如果显示，则隐藏
//            }
        }
        showFloat()
    }

    fun initView() {
        Timber.i("iconIV initView")
        if (rootView == null) {
            rootView = FloatingX.control().getView()
            val iconIV = rootView!!.findViewById<ImageView>(R.id.icon_iv)
            val closeIV = rootView!!.findViewById<ImageView>(R.id.close_iv)
            brightSB = rootView!!.findViewById<AppCompatSeekBar>(R.id.bright_sb)
            brightSB?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    // 调节亮度
                    serialHelper?.sendBrightness(seekBar!!.progress)
                }
            })
            brightSB?.progress = brightObs.get()
            System.out.println("iconIV init $iconIV")
            val ctrlLy = rootView!!.findViewById<View>(R.id.ctrl_ly)
            iconIV.setOnClickListener {
                System.out.println("iconIV click")
                ctrlLy?.visibility = View.VISIBLE
            }
            closeIV.setOnClickListener {
                System.out.println("iconIV closeIV")
                ctrlLy?.visibility = View.GONE
            }
        }
    }

    override fun init(context: Context) {

    }

    override fun showFloat() {
        System.out.println("iconIV showFloat")
        mainHandler.removeMessages(MSG_FLOAT_HIDE)
        mainHandler.sendEmptyMessage(MSG_FLOAT_SHOW)
    }

    override fun hideFloat(delayMillis: Long) {
        mainHandler.sendEmptyMessage(MSG_FLOAT_HIDE)
    }

    override fun write(msg: String) {

    }

    private val MSG_FLOAT_SHOW = 100
    private val MSG_FLOAT_UPDATE = 101
    private val MSG_FLOAT_HIDE = 102

    inner class MainHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_FLOAT_HIDE -> {
                    FloatingX.control().hide()
                    mainHandler.removeMessages(MSG_FLOAT_HIDE)
                }

                MSG_FLOAT_SHOW -> {
                    FloatingX.control().show()
                    mainHandler.removeMessages(MSG_FLOAT_SHOW)
                    initView()
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

    override fun reBuild() {

    }

    var networkCallbackModule: NetworkCallbackModule = object : NetworkCallbackModule {
        override fun onAvailable(network: Network?) {
            reBuild()
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
