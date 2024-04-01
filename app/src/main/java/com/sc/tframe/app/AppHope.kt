package com.sc.tframe.app

import android.app.Application
import com.nbhope.lib_frame.BuildConfig
import com.nbhope.lib_frame.app.HopeBaseApp
import com.nbhope.lib_frame.utils.FileLoggingTree
import com.nbhope.lib_frame.utils.ThreadAwareDebugTree
import com.sc.lib_float.service.PaintServiceDelegate
import timber.log.Timber

/**
 * @author  tsc
 * @date  2022/7/27 17:06
 * @version 0.0.0-1
 * @description
 */
class AppHope : Application() {
    override fun onCreate() {
        super.onCreate()
        initTimber()

        println("on app onCreate")
        Timber.i("初始化 准备开启服务！")
        PaintServiceDelegate.getInstance().init(this)
    }

    private fun initTimber() {
        if (!BuildConfig.DEBUG) {
            // 保存日志在sdcard/Android/data/com.nbhope.tframe/cache 中
            Timber.plant(FileLoggingTree())
//            Timber.plant(ThreadAwareDebugTree())
        } else {
            Timber.plant(ThreadAwareDebugTree())
        }
    }
}
