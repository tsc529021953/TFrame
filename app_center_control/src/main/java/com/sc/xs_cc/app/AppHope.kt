package com.sc.xs_cc.app

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter
import com.nbhope.lib_frame.app.HopeBaseApp
import com.nbhope.lib_frame.arouterpath.RouterPath
import timber.log.Timber
import com.sc.xs_cc.di.DaggerAppComponent

/**
 * @author  tsc
 * @date  2022/7/27 17:06
 * @version 0.0.0-1
 * @description
 */
class AppHope : HopeBaseApp() {
    override fun onCreate() {
        super.onCreate()

        Timber.i("初始化 准备开启服务！")
        // 服务里面需要存在 定时器（闹钟）udp消息发送 tcp消息发送 https数据访问
//        ARouter.getInstance().build(RouterPath.service_nft).navigation(this)
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
