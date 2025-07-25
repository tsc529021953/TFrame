package com.sc.tmp_cw.app

import com.sc.tmp_cw.di.DaggerAppComponent
import com.sc.tmp_cw.service.TmpServiceDelegate
import com.nbhope.lib_frame.app.HopeBaseApp
import com.nbhope.lib_frame.di.DaggerBaseAppComponent
import com.nbhope.lib_frame.utils.DeviceUtil
import timber.log.Timber

/**
 * @author  tsc
 * @date  2022/7/27 17:06
 * @version 0.0.0-1
 * @description
 */
class AppHope : HopeBaseApp() {
    override fun onCreate() {
//        if (DeviceUtil.isMainProcess(this)) {
            super.onCreate()
//            initTimber()
            println("on app onCreate")
            Timber.i("初始化 准备开启服务！${DeviceUtil.getPlayerVersionCode(this)}")
            TmpServiceDelegate.getInstance()?.init(this)
//        }
    }

    private fun initTimber() {
//        if (!BuildConfig.DEBUG) {
//            // 保存日志在sdcard/Android/data/com.nbhope.tframe/cache 中
//            Timber.plant(FileLoggingTree())
////            Timber.plant(ThreadAwareDebugTree())
//        } else {
//            Timber.plant(ThreadAwareDebugTree())
//        }
    }

    override fun inject() {
        super.inject()
        val component = DaggerAppComponent.builder().appComponent(getAppComponent()).build()
        Timber.d("$packageName $component")
        component.inject(this)
        getInjectors()[packageName] = component.androidInjector
    }
}
