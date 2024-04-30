package com.sc.lib_float.service

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
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.nbhope.lib_frame.dialog.TipDialog
import com.nbhope.lib_frame.dialog.TipNFDialog
import com.nbhope.lib_frame.utils.ShellUtil
import com.nbhope.lib_frame.utils.ValueHolder
import com.nbhope.lib_frame.utils.toast.ToastUtil
import com.nbhope.lib_frame.widget.IconFontView
import com.petterp.floatingx.FloatingX
import com.petterp.floatingx.assist.FxGravity
import com.petterp.floatingx.assist.FxScopeType
import com.sc.lib_float.R
import com.sc.lib_float.inter.IPaintService
import com.sc.lib_float.module.LineManager
import com.sc.lib_float.widget.DrawView
import kotlinx.coroutines.*
import timber.log.Timber
import java.lang.ref.WeakReference

/**
 * @author  tsc
 * @date  2024/4/1 15:50
 * @version 0.0.0-1
 * @description
 */
class PaintServiceImpl : Service(), IPaintService {

    companion object {
        const val TAG = "FTAG"

        var CHANNEL_ONE_NAME = "PaintServiceImpl"
        var CHANNEL_ONE_ID = PaintServiceImpl::class.java.simpleName
        const val SERVICE_NOTICE_ID = 100
    }

    private val mBinder: IBinder = PaintBinder()

    lateinit var mainHandler: MainHandler

    lateinit var lineManager: LineManager

    private lateinit var mScope: CoroutineScope

    override fun onBind(p0: Intent?): IBinder? {
        return this.mBinder
    }

    override fun onCreate() {
        super.onCreate()
        Timber.i("$TAG onCreate")
        initNotice()
        init(application)
        mScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        mainHandler = MainHandler(Looper.getMainLooper())
        lineManager = LineManager(mScope, application)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initFloat()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("$TAG onDestroy")
    }

    fun initNotice() {
        var builder: Notification.Builder? = Notification.Builder(this)
        builder!!.setSmallIcon(R.mipmap.icon).setWhen(System.currentTimeMillis())
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

    inner class PaintBinder : Binder() {
        fun getPaintService(): PaintServiceImpl {
            return WeakReference(this@PaintServiceImpl).get()!!
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initFloat() {
        val dm = application.resources.displayMetrics
        FloatingX.install {
            setContext(application)
            setLayout(R.layout.paint_float_button)
            // 系统浮窗记得声明权限
            setY((dm.heightPixels / 4).toFloat())
            setScopeType(FxScopeType.SYSTEM)
            setGravity(FxGravity.LEFT_OR_BOTTOM)
//            setEnableLog(true)
//            setOnClickListener {
//                Timber.i("FTAG click $it ${FloatingX.control().getView()}")
//                // 如果显示，则隐藏
//            }
        }
        // 读取记录 以及当前页面判断是否要隐藏悬浮窗
//        showFloat()
        initDraw()
//        hideFloat(0)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initDraw() {
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
            lineManager.paintView = View.inflate(baseContext, R.layout.view_paint, null)
            lineManager.drawView = lineManager.paintView!!.findViewById(R.id.draw_view)
//            tvMarqueeContent = rootView!!.findViewById(R.id.marquee_content)
//            ivVoice = rootView!!.findViewById(R.id.iv_voice)
//            rootView!!.setOnClickListener {
//                stopSpeech(it)
//            }
            windowManager.addView(lineManager.paintView, layoutParams)
        }
//
////        hideFloat(0)
//        Timber.d("dialog, hideFloat1")
//        point = null
//        windowManager = null
//        layoutParams = null

    }

    override fun init(context: Context) {
        //
    }

    override fun showFloat() {
        mainHandler.removeMessages(MSG_FLOAT_HIDE)
        if (!FloatingX.control().isShow())
            mainHandler.sendEmptyMessage(MSG_FLOAT_SHOW)
    }

    override fun hideFloat(delayMillis: Long) {
        if (!FloatingX.control().isShow()) return
        mainHandler.sendEmptyMessage(MSG_FLOAT_HIDE)
    }

    override fun showLine() {
        if (!FloatingX.control().isShow()) return
        mainHandler.sendEmptyMessage(MSG_LINE_SHOW)
    }

    override fun hideLine() {
        if (!FloatingX.control().isShow()) return
        mainHandler.sendEmptyMessage(MSG_LINE_HIDE)
    }

    override fun showDraw() {
        if (!FloatingX.control().isShow()) return
        mainHandler.sendEmptyMessage(MSG_DRAW_SHOW)
    }

    override fun hideDraw() {
        if (!FloatingX.control().isShow()) return
        mainHandler.sendEmptyMessage(MSG_DRAW_HIDE)
    }

    private val MSG_FLOAT_SHOW = 100
    private val MSG_FLOAT_UPDATE = 101
    private val MSG_FLOAT_HIDE = 102
    private val MSG_FLOAT_VOICE = 103
    private val MSG_FLOAT_SCROLL = 104
    private val MSG_FLOAT_STOP_SCROLL = 105
    private val MSG_LINE_SHOW = 106
    private val MSG_LINE_HIDE = 107
    private val MSG_DRAW_SHOW = 108
    private val MSG_DRAW_HIDE = 109

    inner class MainHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_FLOAT_HIDE -> {
                    FloatingX.control().hide()
                    mainHandler.removeMessages(MSG_FLOAT_HIDE)
                }
                MSG_FLOAT_SHOW -> {
                    FloatingX.control().show()
                    initView()
                    mainHandler.removeMessages(MSG_FLOAT_SHOW)
                }
                MSG_LINE_SHOW -> {
                    lineManager.showLine()
                    mainHandler.removeMessages(MSG_LINE_SHOW)
                }
                MSG_LINE_HIDE -> {
                    lineManager.hideLine()
                    mainHandler.removeMessages(MSG_LINE_HIDE)
                }
                MSG_DRAW_SHOW -> {
                    lineManager.showDraw()
                    mainHandler.removeMessages(MSG_DRAW_SHOW)
                }
                MSG_DRAW_HIDE -> {
                    lineManager.hideDraw()
                    mainHandler.removeMessages(MSG_DRAW_HIDE)
                }
            }
        }
    }

    fun initView() {
        if (lineManager.rootView == null) {
            lineManager.initView(FloatingX.control().getView())
            lineManager.saveIFV?.setOnClickListener {
                if (lineManager.drawView?.visibility == View.GONE) {
                    ToastUtil.showS(R.string.text_no_open_draw)
                    return@setOnClickListener
                }
                ValueHolder.setValue {
                    // 截图保存
                    val dic = "/Paint/Texture"
                    val path = Environment.getExternalStorageDirectory().absolutePath + dic
                    val saveCB: ((cb: () -> Unit) -> Unit) = {
                        mScope.launch {
                            hideFloat(0)
                            delay(500)
                            val callback = {
                                ShellUtil.shotScreen(path, dic) {
                                    Timber.i("KTAG shotScreen $it")
                                    if (it == 0) {
                                        ToastUtil.showS("保存成功，路径为$path")
                                    } else ToastUtil.showS("保存失败，路径为$path")
                                }
                            }
                            callback.invoke()
                            delay(500)
                            showFloat()
                        }
                    }
                    TipNFDialog.showInfoTip(application,
                        message = resources.getString(R.string.text_save_or_code),
                        sureStr = resources.getString(R.string.text_yes),
                        cancelStr = resources.getString(R.string.text_no),
                        callBack = {
                            saveCB.invoke {
                                // TODO 生成分享二维码
                            }
                            return@showInfoTip true
                        },
                        cancelCallBack = {
                            saveCB.invoke {  }
                            return@showInfoTip true
                        })
                }
            }
        }
    }
}
