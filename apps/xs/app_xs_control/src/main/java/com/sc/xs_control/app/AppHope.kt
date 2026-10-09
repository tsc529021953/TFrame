package com.sc.xs_control.app

import com.sc.xs_control.di.DaggerAppComponent
import com.sc.xs_control.service.TmpServiceDelegate
import com.nbhope.lib_frame.app.HopeBaseApp
import me.jessyan.autosize.AutoSizeConfig
import timber.log.Timber

/**
 * @author  tsc
 * @date  2026/10/8
 * @version 0.0.0-1
 * @description
 */
class AppHope : HopeBaseApp() {
    override fun onCreate() {
        super.onCreate()
        println("on app onCreate")
        Timber.i("初始化 准备开启服务！")
        TmpServiceDelegate.getInstance().init(this)
        initAutoSize()
    }

    override fun inject() {
        super.inject()
        val component = DaggerAppComponent.builder().appComponent(getAppComponent()).build()
        Timber.d("$packageName $component")
        component.inject(this)
        getInjectors()[packageName] = component.androidInjector
        Timber.d("DaggerAppComponent 注入")
    }

    private fun initAutoSize() {
        AutoSizeConfig.getInstance().designWidthInDp = 1920;
        AutoSizeConfig.getInstance().designHeightInDp = 1024;
    }
}
