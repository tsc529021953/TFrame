package com.sc.nft.app

import android.app.Application
import com.nbhope.lib_frame.app.HopeBaseApp
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

        Timber.i("初始化 准备开启服务！")
    }
}
