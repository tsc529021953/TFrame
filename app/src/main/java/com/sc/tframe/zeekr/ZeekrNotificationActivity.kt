package com.sc.tframe.zeekr

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.content.pm.PackageManager
import android.os.Build
import android.widget.RemoteViews
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.sc.tframe.R

/**
 *
 * @author sicai.tang@geely.com
 * @since 2026/6/1
 */
class ZeekrNotificationActivity : AppCompatActivity() {

    private var mNotifyId = 1
    private var mFlag = PendingIntent.FLAG_UPDATE_CURRENT
    private lateinit var mBuilder: NotificationCompat.Builder

    private val mStopAction = "com.sc.tframe.action.STOP"
    private val mDoneAction = "com.sc.tframe.action.DONE"
    private val mCustomChannelId = "custom_channel"
    private val mCustomNotificationId = 1001

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zeekr_notification)
        findViewById<TextView>(R.id.view_notify_tv).setOnClickListener { createNotificationForCustom() }
    }

    private fun createNotificationForCustom() {
        // Android 13+ (API 33) 需要通知权限
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS")
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf("android.permission.POST_NOTIFICATIONS"), 100)
                Toast.makeText(this, "请先授权通知权限", Toast.LENGTH_SHORT).show()
                return
            }
        }

        mNotifyId++
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 适配8.0及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(mCustomChannelId, "CustomChannel", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }

        // 适配12.0及以上
        mFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        // 添加自定义通知view
        val views = RemoteViews(packageName, R.layout.notification_vertical_actions)

        // 添加暂停继续事件
        val intentStop = Intent(mStopAction)
        val pendingIntentStop = PendingIntent.getBroadcast(this, 0, intentStop, mFlag)
//        views.setOnClickPendingIntent(R.id.btn_stop, pendingIntentStop)

        // 添加完成事件
        val intentDone = Intent(mDoneAction)
        val pendingIntentDone = PendingIntent.getBroadcast(this, 0, intentDone, mFlag)
//        views.setOnClickPendingIntent(R.id.btn_done, pendingIntentDone)

        // 创建Builder
        mBuilder = NotificationCompat.Builder(this, mCustomChannelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_eye_open))
            .setAutoCancel(true)
            .setCustomContentView(views)
            .setCustomBigContentView(views) // 设置自定义通知view

        // 发起通知（用mNotifyId作为通知ID，每次点击弹出独立通知）
        manager.notify(mNotifyId, mBuilder.build())
    }
}