package com.nbhope.lib_frame.app

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkRequest
import android.view.Gravity
import com.alibaba.android.arouter.launcher.ARouter
import com.hjq.toast.Toaster
import com.jeremyliao.liveeventbus.LiveEventBus
import com.lib.frame.dynconfig.HopeDynConfig
import com.nbhope.lib_frame.BuildConfig
import com.nbhope.lib_frame.R
import com.nbhope.lib_frame.constants.HopeConstants
import com.nbhope.lib_frame.di.BaseAppComponent
import com.nbhope.lib_frame.di.DaggerBaseAppComponent
import com.nbhope.lib_frame.integration.AppLifecycles
import com.nbhope.lib_frame.utils.*
import dagger.android.DispatchingAndroidInjector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import xcrash.XCrash
import javax.inject.Inject

/**
 *Created by ywr on 2021/11/10 15:56
 */
open class HopeBaseApp : Application(), App {

    private val injectorMap = HashMap<String, DispatchingAndroidInjector<Any>>()

    @Inject
    lateinit var activityLifecycleImpl: ActivityLifecycleImpl

    private var mAppDelegate: AppLifecycles? = null

    @Inject
    lateinit var spManager: SharedPreferencesManager

    companion object {
        private var appContext: Application? = null

        @JvmStatic
        fun getContext(): Context {
            return appContext!!
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        if (mAppDelegate == null) {
            this.mAppDelegate = AppDelegate(base)
        }
        mAppDelegate?.attachBaseContext(base)
        initXCrash()
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        initTimber()
        initNetConfig()
        mAppDelegate?.onCreate(this)
        if (HopeUtils.isMainProcess(this)) {
            initMain()
        }
        initCoroutines()


        inject()
        registerActivityLifecycleCallbacks(activityLifecycleImpl)

    }

    override fun onTerminate() {
        super.onTerminate()
        mAppDelegate?.onTerminate(this)
        unregisterActivityLifecycleCallbacks(activityLifecycleImpl)
    }


    private fun initMain() {
        initArouter()
        initLiveEventBus()
        initBugly()
        initToast()
    }

    private fun initNetConfig() {
        HopeDynConfig.instence.initDynConfig(this)
    }

    private fun initCoroutines() {
        GlobalScope.launch(Dispatchers.Main) {
            initConstants()
            initNetworkCallback()
        }
    }

    private fun initTimber() {
//        if (BuildConfig.DEBUG) {
//            Timber.plant(Timber.DebugTree())
//        } else {
//            Timber.plant(FileLoggingTree())
//        }
        if (!BuildConfig.DEBUG) {
            // 保存日志在sdcard/Android/data/com.nbhope.hopelauncher/cache 中
            Timber.plant(FileLoggingTree())
        } else {
            // ThreadAwareDebugTree
            Timber.plant(ThreadAwareDebugTree())
        }
    }

    private fun initLiveEventBus() {
        LiveEventBus.get()
            .config()
            .lifecycleObserverAlwaysActive(true)
            .autoClear(false)
    }

    private fun initArouter() {
        if (BuildConfig.DEBUG) {
            ARouter.openLog()     // 打印日志
            ARouter.openDebug()   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this)
    }


    private fun initConstants() {
        HopeConstants.LOCAL_SN = HopeUtils.getSN()
        HopeUtils.getIP()?.let {
            HopeConstants.LOCAL_IP = it
        }
    }

    private fun initBugly() {
//        //自动检查更新开关 true表示初始化时自动检查升级; false表示不会自动检查升级,需要手动调用Beta.checkUpgrade()方法;
//        // 开启自动检测更新
//
////        //自动检查更新开关 true表示初始化时自动检查升级; false表示不会自动检查升级,需要手动调用Beta.checkUpgrade()方法;
////        Beta.autoCheckUpgrade = false;//关闭自动检查更新开关，手动在mainActivity的OnResume中调用Beta.checkUpgrade()
////        String product = HopeReportUtil.getUpgradeChannel();
////        L.i(TAG, "initBugly product:" + product);
////        CrashReport.setAppChannel(getContext(), product); //设置渠道号
////        BuglyWrap.initAndCustomDialog(getContext(), Constants.BUGLY_APP_ID, BuildConfig.DEBUG, R.mipmap.ic_launcher);
//
//
//        //自动检查更新开关 true表示初始化时自动检查升级; false表示不会自动检查升级,需要手动调用Beta.checkUpgrade()方法;
//        Beta.autoInit = true
//        Beta.autoCheckUpgrade = true //关闭自动检查更新开关，手动在mainActivity的OnResume中调用Beta.checkUpgrade()
//        Beta.autoDownloadOnWifi = true
//        Beta.upgradeCheckPeriod = 60 * 1000
//        Beta.showInterruptedStrategy = true
//        Beta.initDelay = 3 * 1000
//        val product: String = HopeUtils.getUpgradeChannel()
//        Timber.i("initBugly product:$product buglyId:${HopeConstants.BuglyId}")
//        CrashReport.setAppChannel(getContext(), product) //设置渠道号
//        val buglyId = HopeConstants.BuglyId
////        if (HopeConstants.COMPANY == "XW") {
////            HopeConstants.BUGLY_ID
////        } else {
////            HopeConstants.THIRD_BUGLY_ID
////        }
////        CrashReport.initCrashReport(this, Constants.BUGLY_ID, BuildConfig.DEBUG)
//        Bugly.init(this, buglyId, BuildConfig.DEBUG)
    }

    private fun initXCrash() {
        //爱奇艺的Crash捕获器 默认存在data/data/com.nbhope.hopelauncher/files/tombstones中
        //注意是根目录下的data,不是sdcard下的data
        val params = XCrash.InitParameters()
        params.setLogDir("$externalCacheDir")
        XCrash.init(this, params)
    }

    private fun initNetworkCallback() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val builder: NetworkRequest.Builder = NetworkRequest.Builder()
        val request: NetworkRequest = builder.build()
        connectivityManager.registerNetworkCallback(request, getAppComponent().networkCallback)

    }


    open fun inject() {
//        DaggerBaseAppComponent.builder().application(this).build().inject(mAppDelegate as AppDelegate)
//        val component = DaggerBaseAppComponent.builder().application(this).build()
//        Timber.d("$packageName $component")
//        component.inject(this)
//        getInjectors()[packageName] = component.androidInjector
    }

    override fun getAppComponent(): BaseAppComponent {
        Preconditions.checkNotNull(mAppDelegate, "%s cannot be null", AppDelegate::class.java.name)
        Preconditions.checkState(mAppDelegate is App, "%s must be implements %s", mAppDelegate!!.javaClass.name, App::class.java.name)
        return (mAppDelegate as App).getAppComponent()
    }

    override fun packageNameList(): List<String> {
        Preconditions.checkNotNull(mAppDelegate, "%s cannot be null", AppDelegate::class.java.name)
        Preconditions.checkState(mAppDelegate is App, "%s must be implements %s", mAppDelegate!!.javaClass.name, App::class.java.name)
        return (mAppDelegate as App).packageNameList()
    }

    fun getInjectors(): HashMap<String, DispatchingAndroidInjector<Any>> {
        return injectorMap
    }

    fun getInjector(className: String): DispatchingAndroidInjector<Any>? {
        val key = packageNameList().find {
            System.out.println("getInjector " + it + " " + className)
            className.startsWith(it)
        }
        return injectorMap.get(key)
    }

    fun initToast() {
        Toaster.init(this)
//        ToastUtils.setView(R.layout.toast_custom_view)
//        ToastUtils.setStyle(com.nbhope.lib_frame.widget.WhiteToastStyle())
//        ToastUtils.setGravity(Gravity.CENTER)
        //        ToastUtils.setView(R.layout.toast_custom_view)
//        ToastUtils.setStyle(com.nbhope.lib_frame.widget.WhiteToastStyle())
//        ToastUtils.setGravity(Gravity.CENTER)
//        Toaster.setView(R.layout.custom_toast)
        Toaster.setGravity(Gravity.BOTTOM, 0, 30)
    }
}
