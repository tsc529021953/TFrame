package com.illusory.tmp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.*
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.illusory.tmp.R
import com.illusory.tmp.inter.ITmpService
import com.nbhope.lib_frame.utils.GpioUtils
import com.nbhope.lib_frame.utils.TimerHandler
import com.nbhope.lib_frame.utils.ViewUtil.Companion.immersionTitle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber
import java.io.*
import java.lang.ref.WeakReference
import java.nio.charset.Charset

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

        const val BASE_FILE = "/THREDIM_MEDIA/"
        const val CONFIG_FILE = "串口同步配置.txt"
    }

    private val mBinder: IBinder = BaseBinder()

    private lateinit var mScope: CoroutineScope

    /*view*/
    private var rootView: View? = null

    lateinit var mainHandler: MainHandler

    private var timerHandler: TimerHandler? = null

    override fun onCreate() {
        super.onCreate()
        initNotice()
        init(application)
        mScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        mainHandler = MainHandler(Looper.getMainLooper())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initFloat()
        } else {
            Timber.i("$TAG 版本太低，请重新适配 ${Build.VERSION.SDK_INT}")
        }
        initGPIO()
    }

    override fun onDestroy() {
        super.onDestroy()
        timerHandler?.stop()
    }

    fun initNotice() {
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
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
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

    var testindex= 0

    /**
     * 好，现在功能验证可以了。接下来的改动是：
     * 1、主板重启后又恢复成out状态，所以需要通过代码来在透明屏框架启动时就将IO1、IO2设为in
     * 2、逻辑为检测到进站传感器IO1由1变0时开启黑画面，出站传感器IO2由1变0时隐藏黑画面露出视频。
     * 3、那么就需要读取两个IO口索引。所以配置文件改为：第一行为0表示触发条件1->0，为1表示0->1，第四行表示IO1索引，第五行表示IO2索引
     *
     */
    fun initGPIO() {
        val callback = {
            var index = 59
            var index2 = 60
            var param = 0
            var path = Environment.getExternalStorageDirectory().absolutePath + BASE_FILE
            var file = File(path)
            if (!file.exists()) {
                file.mkdirs()
                Timber.i("$TAG 路径不存在 $path")
            } else {
                path += CONFIG_FILE
                file = File(path)
                try {
                    val isr = InputStreamReader(FileInputStream(file), "UTF-8")
                    val br = BufferedReader(isr)
                    param = br.readLine().toIntOrNull() ?: param
                    for (i in 0 until 2) {
                        br.readLine()
                    }
                    index = br.readLine().toIntOrNull() ?: index
                    index2 = br.readLine().toIntOrNull() ?: index2
                } catch (e: Exception) {
                    Timber.e("$TAG readConfig err ${e.message}")
                }
            }
            Timber.i("$TAG index $index index2 $index2 $param")
            timerHandler = TimerHandler(1000) {
                var value = "-1"
                var value2 = "-1"
                try {
                    value = GpioUtils.getGpioValue(index)
                    value2 = GpioUtils.getGpioValue(index2)
                    Timber.i("$TAG getGpioValue g1 $value g2 $value2")
                } catch (e: Exception) {
                    e.printStackTrace()
                    Timber.e("$TAG err ${e.message}")
                }

                /*测试*/
//                testindex++
//                value = if (testindex % 10 == 0) "1" else "0"

                if (value == "$param") {
                    showFloat()
                } else if (value2 == "$param") hideFloat(0)

//                if (value == "0") {
//                    hideFloat(0)
//                } else if (value == "1")
            }
            timerHandler?.start()
        }
        try {
            GpioUtils.upgradeRootPermissionForExport()
            // 开启定时器，定时1s获取当前IO口状态
            callback.invoke()
        } catch (e: Exception) {
            callback.invoke()
            e.printStackTrace()
            Timber.e("$TAG initGPIO err ${e.message}")
        }
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
//                    immersionTitle(rootView!!.window);
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
}
