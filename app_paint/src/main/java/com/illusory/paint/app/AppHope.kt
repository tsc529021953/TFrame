package com.illusory.paint.app

import android.app.Application
import com.nbhope.lib_frame.BuildConfig
import com.nbhope.lib_frame.app.HopeBaseApp
import com.nbhope.lib_frame.utils.FileLoggingTree
import com.nbhope.lib_frame.utils.ThreadAwareDebugTree
import com.sc.lib_float.service.PaintServiceDelegate
import timber.log.Timber
import com.illusory.paint.di.DaggerAppComponent

/**
 * @author  tsc
 * @date  2022/7/27 17:06
 * @version 0.0.0-1
 * @description
 */
class AppHope : HopeBaseApp() {

    override fun onCreate() {
        super.onCreate()

        println("on app onCreate")
        Timber.i("初始化 准备开启服务！")
        PaintServiceDelegate.getInstance().init(this)
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
