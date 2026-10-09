package com.sc.xs_control.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sc.xs_control.MainActivity

/**
 * @author  tsc
 * @date  2026/10/8
 * @version 0.0.0-1
 * @description 开机唤醒广播
 */
class StartReceiver : BroadcastReceiver() {

    companion object {

        const val BOOT_COMPLETED = Intent.ACTION_BOOT_COMPLETED

        const val SECRET_CODE = "android.provider.Telephony.SECRET_CODE"

    }

    override fun onReceive(p0: Context?, p1: Intent?) {
        println("接收到唤醒广播0 ${p1?.action}")
        when (p1?.action) {
            SECRET_CODE, BOOT_COMPLETED -> {
                println("接收到唤醒广播 ${p1?.action}")
//                val intent = Intent(p0!!, MainActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                p0.startActivity(intent)
            }
        }
    }
}
