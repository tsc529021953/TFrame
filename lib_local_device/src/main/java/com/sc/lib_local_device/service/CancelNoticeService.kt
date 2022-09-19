package com.sc.lib_local_device.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import com.sc.lib_local_device.R

/**
 * @Author qiukeling
 * @Date 2020/4/28-3:23 PM
 * @Email qiukeling@nbhope.cn
 */
class CancelNoticeService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val builder = Notification.Builder(this)
        builder.setSmallIcon(R.mipmap.ic_home)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //修改安卓8.1以上系统报错
            val notificationChannel = NotificationChannel(MainServiceImpl.CHANNEL_ONE_ID, MainServiceImpl.CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_MIN);
            notificationChannel.enableLights(false);//如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
            notificationChannel.setShowBadge(false);//是否显示角标
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET;
            val manager = getSystemService (NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(notificationChannel);
            builder.setChannelId(MainServiceImpl.Companion.CHANNEL_ONE_ID);
        }
        startForeground(MainServiceImpl.NOTICE_ID, builder.build())
        // 开启一条线程，去移除DaemonService弹出的通知
        Thread(Runnable {
            // 延迟1s
            SystemClock.sleep(1000)
            // 取消CancelNoticeService的前台
            stopForeground(true)
            // 移除SpeechService弹出的通知
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(MainServiceImpl.NOTICE_ID)
            // 任务完成，终止自己
            stopSelf()
        }).start()
        return super.onStartCommand(intent, flags, startId)
    }
}