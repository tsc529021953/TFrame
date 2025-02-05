package com.nbhope.lib_frame.utils

import android.os.Handler
import timber.log.Timber
import java.util.*

class TimerHandler(private val interval: Long, private var hour: Int = -1, var callback: () -> Unit) {
    private var handler = Handler()
    private var runnable: Runnable? = null

    fun start() {
        if (runnable != null) {
            stop()
        }

        runnable = object : Runnable {
            override fun run() {
                System.out.println("onPlayerStateChanged时间到达 $this $runnable")
                if (runnable == null) return
                callback()
                if (runnable == null) return
                handler.postDelayed(this, interval)
            }
        }
        Timber.i("KTAG timer bef $hour")
        if (hour == -1)
            handler.postDelayed(runnable!!, interval)
        else {
            val calendar: Calendar = Calendar.getInstance()
            // 获取当前小时
            val currentHour: Int = calendar.get(Calendar.HOUR_OF_DAY)
            val timer = if (hour - currentHour > 0) hour - currentHour else 24 + (hour - currentHour)
            Timber.i("KTAG timer $timer")
            handler.postDelayed(runnable!!, (3600000 * timer).toLong())
        }
    }

    fun stop() {
        System.out.println("onPlayerStateChanged时间到达停止")
        runnable?.let {
            handler.removeCallbacks(it)
        }
        runnable = null
        handler.removeCallbacksAndMessages(null)
    }

    companion object{
        fun getTodayFlag(date: Date?): String {
            val cal = Calendar.getInstance()
            cal.time = date
            cal[Calendar.HOUR_OF_DAY]
            val hour = cal[Calendar.HOUR_OF_DAY]
            when {
                hour < 5 -> {
                    return "凌晨"
                }
                hour < 12 -> {
                    return "上午"
                }
                hour < 13 -> {
                    return "中午"
                }
                hour < 18 -> {
                    return "下午"
                }
                hour < 24 -> {
                    return "晚上"
                }
                else -> return ""
            }
        }

    }
}
