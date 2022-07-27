package com.sc.lib_frame.base

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkRequest
import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.sc.lib_frame.BuildConfig
import com.sc.lib_frame.network.NetworkCallback
import com.sc.lib_frame.sp.SharedPreferencesManager
import com.sc.lib_frame.utils.FileLoggingTree
import com.sc.lib_frame.utils.ThreadAwareDebugTree
import timber.log.Timber
import xcrash.XCrash

/**
 *Created by ywr on 2021/11/10 15:56
 */
open class BaseApp : Application() {

    companion object {
        private var appContext: Application? = null

        private var topActivity: Activity? = null

        @JvmStatic
        fun getTopActivity(): Activity? {
            return topActivity
        }

        @JvmStatic
        fun getContext(): Context {
            return appContext!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        initTimber()
        initARouter()
        initSp()
        registerNetWork()
        registerActivityLife()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        initXCrash()
    }

    private fun registerActivityLife() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks{
            override fun onActivityCreated(p0: Activity, p1: Bundle?) {

            }

            override fun onActivityStarted(p0: Activity) {

            }

            override fun onActivityResumed(p0: Activity) {
                topActivity = p0
            }

            override fun onActivityPaused(p0: Activity) {
                topActivity = null
            }

            override fun onActivityStopped(p0: Activity) {

            }

            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

            }

            override fun onActivityDestroyed(p0: Activity) {

            }

        })
    }

    /**
     * 注册网络变化，当网络发生变化时会通知播放
     */
    private fun registerNetWork() {
        val builder = NetworkRequest.Builder()
        val request = builder.build()
        var connmgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connmgr?.registerNetworkCallback(request, NetworkCallback.getInstance())
    }


    private fun initSp() {
        try {
            SharedPreferencesManager.getInstance(this)
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun initXCrash() {
        //爱奇艺的Crash捕获器 默认存在data/data/com.nbhope.hopelauncher/files/tombstones中
        //注意是根目录下的data,不是sdcard下的data
        val params = XCrash.InitParameters()
        params.setLogDir("$externalCacheDir")
        XCrash.init(this, params)
    }

    private fun initARouter() {
        if (BuildConfig.DEBUG) {
            ARouter.openLog()     // 打印日志
            ARouter.openDebug()   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this)
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(ThreadAwareDebugTree())
        } else {
            Timber.plant(FileLoggingTree())
        }
    }
}