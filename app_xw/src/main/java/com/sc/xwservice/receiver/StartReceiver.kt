package com.sc.xwservice.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sc.xwservice.app.App.Companion.TAG
import timber.log.Timber

/**
 * @author  tsc
 * @date  2022/7/30 15:08
 * @version 0.0.0-1
 * @description
 */
class StartReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1?.action == Intent.ACTION_BOOT_COMPLETED) {
            Timber.i("$TAG 开机自启动了")
        }
    }
}