package com.sc.xwservice.app

import android.content.Intent
import android.os.Build
import com.nbhope.lib_frame.app.HopeBaseApp
import com.sc.xwservice.di.DaggerAppComponent
import com.sc.xwservice.service.InfoService
import com.xdandroid.hellodaemon.DaemonEnv
import timber.log.Timber

/**
 * @author  tsc
 * @date  2022/7/27 17:06
 * @version 0.0.0-1
 * @description
 */
class AppHope : HopeBaseApp() {

    companion object{
        const val TAG = "XWSTAG"
    }

    override fun onCreate() {
        super.onCreate()

        Timber.i("$TAG 初始化 准备开启服务！")
        initService()
    }

    override fun inject() {
        super.inject()
        val component = DaggerAppComponent.builder().appComponent(getAppComponent()).build()
        Timber.d("$packageName $component")
        component.inject(this)
        getInjectors()[packageName] = component.androidInjector
        Timber.d("DaggerAppComponent 注入")
    }

    fun initService() {
        // 判断服务是否已经起来了 已经起来了则无需再启动

        DaemonEnv.initialize(this, InfoService::class.java, 1)
        var i = Intent(this, InfoService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(i)
        } else this.startService(i)
    }
}
