package com.nbhope.lib_frame.app

import android.app.Application
import android.content.Context
import com.nbhope.lib_frame.di.DaggerBaseAppComponent
import com.nbhope.lib_frame.integration.AppLifecycles
import com.nbhope.lib_frame.di.BaseAppComponent
import com.nbhope.lib_frame.integration.ManifestParser
import com.nbhope.lib_frame.utils.HopeUtils
import com.nbhope.lib_frame.utils.Preconditions
import timber.log.Timber


/**
 * @Author qiukeling
 * @Date 2020/5/7-10:37 AM
 * @Email qiukeling@nbhope.cn
 */
class AppDelegate(context: Context) : App, AppLifecycles {
    private var mAppLifecycles: MutableList<AppLifecycles>? = ArrayList()

    private var mModules = ManifestParser(context).parse()

    private var packageName = ManifestParser(context).packageName()

    private var mApplication: Application? = null

    private lateinit var appComponent: BaseAppComponent

    init {
        if (mAppLifecycles != null) {
            for (module in mModules) {
                //将框架外部, 开发者实现的 Application 的生命周期回调 (AppLifecycles) 存入 mAppLifecycles 集合 (此时还未注册回调)
                module.injectAppLifecycle(context, mAppLifecycles!!)

                //将框架外部, 开发者实现的 Activity 的生命周期回调 (ActivityLifecycleCallbacks) 存入 mActivityLifecycles 集合 (此时还未注册回调)
//            module.injectActivityLifecycle(context, mActivityLifecycles)
            }
        }
    }

    override fun getAppComponent(): BaseAppComponent {
        Preconditions.checkNotNull(appComponent,
                "%s == null, first call %s#onCreate(Application) in %s#onCreate()",
                BaseAppComponent::class.java.name, javaClass.name, if (mApplication == null) Application::class.java.name else mApplication!!.javaClass.name)
        return appComponent
    }

    override fun attachBaseContext(base: Context) {
        if (mAppLifecycles == null) return
        for (lifecycle in mAppLifecycles!!) {
            lifecycle.attachBaseContext(base)
        }
    }

    override fun onCreate(application: Application) {
        this.mApplication = application
        if (mAppLifecycles == null) return
        appComponent = DaggerBaseAppComponent.builder().application(application).build()
        appComponent.inject(this)
        Timber.d("AppBaseComponent 注入")
        this.mModules = null
        for (lifecycle in mAppLifecycles!!) {
            //是Remote进程且不是RemoteLib
            //即Remote进程中只初始化RemoteLib,其他Lib不初始化
            //但是对于RemoteLib而言
            //主进程会初始化一次,Remote进程还会初始化一次
            if (HopeUtils.isRemoteProcess(application) && !HopeUtils.isRemoteLib(lifecycle)) {
                continue
            }
            lifecycle.onCreate(application)
        }
    }

    override fun onTerminate(application: Application) {
        if (!mAppLifecycles.isNullOrEmpty()) {
            for (lifecycle in mAppLifecycles!!) {
                lifecycle.onTerminate(mApplication!!)
            }
        }
        this.mAppLifecycles = null
        this.mApplication = null
    }

    override fun packageNameList(): List<String> {
        return packageName
    }


}