package com.sc.hetest.app

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.util.Log
import android.view.WindowManager
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import com.nbhope.lib_frame.app.HopeBaseApp
import com.nbhope.lib_frame.utils.HopeUtils
import com.sc.hetest.di.DaggerAppComponent
import com.xbh.sdk3.client.UserAPI
import me.jessyan.autosize.AutoSizeConfig
import timber.log.Timber

/**
 * @author  tsc
 * @date  2022/7/27 17:06
 * @version 0.0.0-1
 * @description
 */
class AppHope : HopeBaseApp(), CameraXConfig.Provider {
    override fun onCreate() {
        super.onCreate()
        if (getSystemService(Context.WINDOW_SERVICE) != null) {
            var wm = getSystemService(WINDOW_SERVICE) as WindowManager
            if (wm == null) return
            val point = Point()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                wm.defaultDisplay.getRealSize(point)
            } else {
                wm.defaultDisplay.getSize(point)
            }
            if (Math.abs(point.x - point.y) < 100) {
                // 用正方形
                AutoSizeConfig.getInstance().designWidthInDp = 480;
                AutoSizeConfig.getInstance().designHeightInDp = 480;
            } else if (point.x < point.y) {
                // 竖直
                AutoSizeConfig.getInstance().designWidthInDp = 540;
                AutoSizeConfig.getInstance().designHeightInDp = 960;
            } else {
                // 横向
                AutoSizeConfig.getInstance().designWidthInDp = 960;
                AutoSizeConfig.getInstance().designHeightInDp = 540;
            }
        }

        try {
            if (HopeUtils.isSystemSign(this))
                UserAPI.getInstance().init(this);
            else Timber.i("初始化 朗国失败！")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Timber.i("初始化 准备开启服务！${HopeUtils.isSystemApp(packageName, this)} ${HopeUtils.isSystemSign(this)}")
    }

    override fun inject() {
        super.inject()
        val component = DaggerAppComponent.builder().appComponent(getAppComponent()).build()
        Timber.d("$packageName $component")
        component.inject(this)
        getInjectors()[packageName] = component.androidInjector
        Timber.d("DaggerAppComponent 注入")
    }

    override fun getCameraXConfig(): CameraXConfig {
        return CameraXConfig.Builder.fromConfig(Camera2Config.defaultConfig())
            .setMinimumLoggingLevel(Log.ERROR).build()
    }
}