package com.sc.app_xsg.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.webkit.ValueCallback
import androidx.lifecycle.Observer
import com.sc.lib_frame.ITNotice
import com.sc.lib_frame.bean.TMessage
import com.sc.lib_frame.utils.LiveEBUtil
import com.sc.lib_frame.utils.json.BaseGsonUtils
import com.sc.app_xsg.app.App.Companion.TAG
import com.sc.app_xsg.config.MessageConst
import com.sc.lib_frame.common.BaseMessage
import com.xdandroid.hellodaemon.AbsWorkService
import timber.log.Timber
import java.util.*

/**
 * @author  tsc
 * @date  2022/7/30 14:52
 * @version 0.0.0-1
 * @description
 * 信息输送器 主要是将 时间 天气 定位信息传递给小屏
 * 1. 开启定时器，定时传输
 */
class MainService : AbsWorkService() {

    companion object{
        const val TIMER: Long = 40000
    }

    private val liveRemoteObserver = Observer<Any> {
        it as TMessage
        Timber.i("$TAG aidlMessage ${it?.cmd} ${it.toString()}")
        if (it.cmd == BaseMessage.PERMISSION_GET){

        }
//            weatherListener?.registerListener(this, weatherCB)
    }

    override fun onCreate() {
        super.onCreate()
        Timber.i("$TAG 服务创建")
        // 开启定时器
//        timer?.schedule(timerTask, 0 , TIMER)
        LiveEBUtil.registForever(TMessage::class.java, liveRemoteObserver)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("$TAG onStartCommand flags:$flags, startId:$startId [$this] ${Thread.currentThread()}")

//        val pendingIntent: PendingIntent =
//            Intent(this, ForegroundDemoAct::class.java).let { notificationIntent ->
//                PendingIntent.getActivity(this, 0, notificationIntent, 0)
//            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chanId = "f-channel"
            val chan = NotificationChannel(chanId, "XWService",
                NotificationManager.IMPORTANCE_NONE)
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(chan)
            Timber.d("$TAG 服务调用startForeground")

            val notification: Notification =
                Notification.Builder(applicationContext, chanId)
                    .setContentTitle("小屏中控")
                    .setContentText("用于向小屏传递信息")
//                    .setSmallIcon(R.drawable.f_zan_1)
//                    .setContentIntent(pendingIntent)
                    .build()
            startForeground(1, notification)
        } else {
            Timber.d( "$TAG ${Build.VERSION.SDK_INT} < O(API 26) ")
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        LiveEBUtil.unRegist(TMessage::class.java, liveRemoteObserver)
    }

    private val mBinder = object : ITNotice.Stub(){
        override fun aidlMessage(message: TMessage?) {
            Timber.i("$TAG aidlMessage ${message?.cmd} ${message.toString()}")
        }

    }

    override fun onBind(intent: Intent?, alwaysNull: Void?): IBinder? {
        Timber.i(" onBind")
        return mBinder
    }

    override fun shouldStopService(intent: Intent?, flags: Int, startId: Int): Boolean {
        Timber.i(" shouldStopService")
        return false
    }

    override fun startWork(intent: Intent?, flags: Int, startId: Int) {
        Timber.i(" startWork")
    }

    override fun stopWork(intent: Intent?, flags: Int, startId: Int) {
        Timber.i(" stopWork")
    }

    override fun isWorkRunning(intent: Intent?, flags: Int, startId: Int): Boolean {
        Timber.i(" isWorkRunning")
        return true
    }

    override fun onServiceKilled(rootIntent: Intent?) {
        Timber.i(" onServiceKilled $rootIntent")
    }
}