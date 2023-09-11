package com.nbhope.lib_frame.utils

import android.content.Context
import android.content.Intent
import java.util.*

/**
 * @Author qiukeling
 * @Date 2022/3/9-10:16 AM
 * @Email qiukeling@nbhope.cn
 */
class LockScreenHelper {

    companion object {
        val RESUME_EVENT = "com.thinkhome.RESUME_EVENT"
        val TOUCH_EVENT = "com.thinkhome.TOUCH_EVENT"

        private var timerTask: TimerTask? = null
        private var timer = Timer()

        fun postTouchEvent(context: Context) {
            executeDebounce {
                val intent = Intent(TOUCH_EVENT)
//                intent.setClassName(context, "com.luzx.base.com.sc.nft.receiver.DropMenuEventReceiver")
                //调用Context的sendBroadcast()方法，将广播发送出去。所有监听这条广播的接收器就都会接收到这条广播。
                context.sendBroadcast(intent)
            }
        }

        fun postResumeEvent(context: Context) {
            val intent = Intent(RESUME_EVENT)
//            intent.setClassName(context, "com.luzx.base.com.sc.nft.receiver.DropMenuEventReceiver")
            //调用Context的sendBroadcast()方法，将广播发送出去。所有监听这条广播的接收器就都会接收到这条广播。
            context.sendBroadcast(intent)
        }

        private fun executeDebounce(runnable: () -> Unit) {
            timerTask?.cancel()
            timerTask = object : TimerTask() {
                override fun run() {
                    runnable.invoke()
                }
            }
            timer.schedule(timerTask, 2000)
        }
    }
}