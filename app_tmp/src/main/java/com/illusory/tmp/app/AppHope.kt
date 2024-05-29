package com.illusory.tmp.app

import android.app.Application
import com.illusory.tmp.service.TmpServiceDelegate
import com.nbhope.lib_frame.BuildConfig
import com.nbhope.lib_frame.app.HopeBaseApp
import com.nbhope.lib_frame.utils.DeviceUtil
import com.nbhope.lib_frame.utils.FileLoggingTree
import com.nbhope.lib_frame.utils.ThreadAwareDebugTree
import com.sdkapi.api.SdkApi
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
            println("on app onCreate")
            Timber.i("初始化 准备开启服务！")
            TmpServiceDelegate.getInstance().init(this)
//        }
        SdkApi.newInstance(this)
    }

}
