package com.sc.app_xsg.app

import android.content.Intent
import android.os.Build
import com.sc.lib_frame.base.BaseApp
import com.sc.app_xsg.service.MainService
import com.xdandroid.hellodaemon.DaemonEnv
import timber.log.Timber

/**
 * @author  tsc
 * @date  2022/7/27 17:06
 * @version 0.0.0-1
 * @description
 */
class App : BaseApp() {

    companion object{
        const val TAG = "XSG_TAG"
    }

    override fun onCreate() {
        super.onCreate()

        Timber.i("$TAG 初始化 准备开启服务！")
        initService()
    }

    private fun initService() {
        // 判断服务是否已经起来了 已经起来了则无需再启动

        DaemonEnv.initialize(this, MainService::class.java, 1)
        var i = Intent(this, MainService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(i)
        } else this.startService(i)
    }
}