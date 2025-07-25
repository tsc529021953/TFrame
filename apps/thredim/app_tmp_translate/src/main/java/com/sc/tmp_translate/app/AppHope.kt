package com.sc.tmp_translate.app

import android.app.Activity
import android.app.Application
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import com.nbhope.lib_frame.app.HopeBaseApp
import com.nbhope.lib_frame.utils.HopeUtils
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.sc.tmp_translate.di.DaggerAppComponent
import com.sc.tmp_translate.service.TmpServiceDelegate
import timber.log.Timber
import javax.inject.Inject


/**
 * @author  tsc
 * @date  2022/7/27 17:06
 * @version 0.0.0-1
 * @description
 */
class AppHope : HopeBaseApp() {

    private var activityList: ArrayList<Activity>? = null

    private var fontScale = 1f

    override fun onCreate() {
//        if (DeviceUtil.isMainProcess(this)) {
            super.onCreate()
//            initTimber()
            println("on app onCreate")
            Timber.i("初始化 准备开启服务！${HopeUtils.getVerName(this)}")
            TmpServiceDelegate.getInstance().init(this)
//        }
//        registerActivityLifecycleCallbacks(activityListener)
    }

    override fun onTerminate() {
        super.onTerminate()
//        unregisterActivityLifecycleCallbacks(activityListener)
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
        Timber.d("DaggerAppComponent 注入")
    }
}
