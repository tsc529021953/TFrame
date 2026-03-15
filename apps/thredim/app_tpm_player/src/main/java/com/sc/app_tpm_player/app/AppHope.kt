package com.sc.app_tpm_player.app

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.WindowManager
import com.sc.app_tpm_player.di.DaggerAppComponent
import com.sc.app_tpm_player.service.TmpServiceDelegate
import com.nbhope.lib_frame.app.HopeBaseApp
import me.jessyan.autosize.AutoSizeConfig
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
            Timber.i("初始化 准备开启服务！")
            TmpServiceDelegate.getInstance().init(this)
//        }
        initAutoSize()
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

    private fun initAutoSize() {
        if (getSystemService(Context.WINDOW_SERVICE) != null) {
            var wm = getSystemService(WINDOW_SERVICE) as WindowManager
            if (wm == null) return
            val point = Point()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                wm.defaultDisplay.getRealSize(point)
            } else {
                wm.defaultDisplay.getSize(point)
            }
            // 竖直
            if (Math.abs(point.x - point.y) < 100) {
                // 用正方形
                AutoSizeConfig.getInstance().designWidthInDp = 480;
                AutoSizeConfig.getInstance().designHeightInDp = 480;
            } else if (point.x < point.y) {
                // 竖直
                AutoSizeConfig.getInstance().designWidthInDp = 680;
                AutoSizeConfig.getInstance().designHeightInDp = 1210;
            } else {
                // 横向
                AutoSizeConfig.getInstance().designWidthInDp = 1210;
                AutoSizeConfig.getInstance().designHeightInDp = 680;
            }
        }
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
