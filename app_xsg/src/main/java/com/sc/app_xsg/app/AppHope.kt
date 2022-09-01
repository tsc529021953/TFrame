package com.sc.app_xsg.app

import android.content.Intent
import android.os.Build
import com.sc.lib_frame.app.HopeBaseApp
import com.sc.app_xsg.service.MainService
import com.xdandroid.hellodaemon.DaemonEnv
import timber.log.Timber
import com.sc.app_xsg.di.DaggerAppComponent
import com.sc.lib_frame.utils.SharedPreferencesManager
import com.sc.lib_local_device.common.DeviceCommon
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2022/7/27 17:06
 * @version 0.0.0-1
 * @description
 */
class AppHope : HopeBaseApp() {

    companion object{
        const val TAG = "XSG_TAG"
    }

    @Inject
    lateinit var spManager: SharedPreferencesManager

    override fun onCreate() {
        super.onCreate()

        Timber.i("$TAG 初始化 准备开启服务！")
        initService()

        // 读取设备类型
        DeviceCommon.readDeviceType(spManager)
        // 读取上次连接的设备
        DeviceCommon.readRecordDeviceInfo(spManager)
    }

    private fun initService() {
        // 判断服务是否已经起来了 已经起来了则无需再启动

        DaemonEnv.initialize(this, MainService::class.java, 1)
        var i = Intent(this, MainService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(i)
        } else this.startService(i)
    }

    override fun inject() {
        super.inject()
        val component = DaggerAppComponent.builder().appComponent(getAppComponent()).build()
        Timber.d("$packageName $component")
        component.inject(this)
        getInjectors()[packageName] = component.androidInjector
        Timber.d("DaggerAppComponent 注入")
    }

}