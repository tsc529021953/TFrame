package com.sc.tmp_cw

import android.Manifest
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.Gravity
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.alibaba.android.arouter.launcher.ARouter
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.network.NetworkCallback
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.nbhope.lib_frame.utils.TimerHandler
import com.nbhope.phfame.utils.VoiceUtil
import com.sc.tmp_cw.constant.MessageConstant
import com.sc.tmp_cw.databinding.ActivityMainBinding
import com.sc.tmp_cw.inter.IMainView
import com.sc.tmp_cw.service.TmpServiceDelegate
import com.sc.tmp_cw.service.TmpServiceImpl
import com.sc.tmp_cw.vm.MainViewModel
import com.sc.tmp_cw.weight.KeepStateNavigator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


/**
 * @author  tsc
 * @date  2024/4/25 17:47
 * @version 0.0.0-1
 * @description
1. 标题栏 背景 渐变色
1）欢迎词 文字+背景  （显隐）
2）时间信息 （从协议解析，x±50px）
3）站点信息 运行停靠到站信息（从PIS接口协议解析） （每过10秒在x,y±50px内随机更新位置；
进站触发闪烁，离站触发停止闪烁）
4）
2.互动查询区域 （开机默认播放PIS流媒体；
若连不上组播IP，则默认播放本地指定路径首个视频（宣传片）；
激活互动则中止播放；
点击退出互动则恢复播放）

3.播放区域
4.快捷按钮区域 隐藏在右侧，有显隐动画
1）退出互动接收组播
2）锁定解锁【密码】
3）资源管理【密码】
4）系统设置【密码】
5）观景模式
0.0.0-2
 1）自动设置为主程序
 2）列车开进来的动画
 */
class MainActivity : BaseBindingActivity<ActivityMainBinding, MainViewModel>(), IMainView {

    companion object {

        const val TAG_LIST = "TAG_LIST"
        const val TAG_HOME = "TAG_HOME"
        const val TAG_BACK = "TAG_BACK"

        const val REQUEST_CODE = 10085

        var PERMISSIONS = arrayListOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        )

        const val PLAY_IMAGE_TIME = 15000L
        const val CTRL_LAYOUT_VIEW_TIME = 10000L

        var iMain: IMainView? = null

    }

    override var layoutId: Int = R.layout.activity_main

    @Inject
    override lateinit var viewModel: MainViewModel

    @Inject
    lateinit var networkCallback: NetworkCallback

    private lateinit var mNavHostFragment: NavHostFragment

    private var currLayout: View? = null

    private lateinit var navController: NavController

    var iconAnimator: ObjectAnimator? = null

    /**
     * 左侧上方图标动画
     */
    var timerHandler: TimerHandler? = null

    var drawLayoutTimerHandler: TimerHandler? = null

    private var listener = androidx.lifecycle.Observer<Any> {
        it as RemoteMessageEvent
        when (it.cmd) {
            MessageConstant.SERVICE_INIT_SUCCESS -> {
                if (TmpServiceDelegate.service() != null)
                    binding.service = TmpServiceDelegate.service()!!
                Timber.i("SERVICE_INIT_SUCCESS ${TmpServiceDelegate.service()}")
            }
            MessageConstant.CMD_BACK_HOME -> {
                homeDelay()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        iMain = this
        super.onCreate(savedInstanceState)
        hideSystemUI()
        System.out.println("onCreate ??? $requestedOrientation ${requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE}")
        if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        binding.rightLy.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        binding.rightLy.setScrimColor(Color.TRANSPARENT)
        if (TmpServiceImpl.isFirstInitVoice) {
            TmpServiceImpl.isFirstInitVoice = false
//            Timber.i("voice2 ${viewModel.spManager.getInt(MessageConstant.SP_PARAM_DEFAULT_VOICE_OPEN, 1)}")
            if (viewModel.spManager.getInt(MessageConstant.SP_PARAM_DEFAULT_VOICE_OPEN, 1) == 1) {
                val voice = viewModel.spManager.getInt(MessageConstant.SP_PARAM_VOICE, 0)
                Timber.i("set default voice $voice")
                if (voice != VoiceUtil.getVolume(this))
                    VoiceUtil.setVolume(this, voice)
            } else
                VoiceUtil.setScience(this)
        }
    }

    override fun onResume() {
        super.onResume()
        checkSpeed()
        timerHandler?.start()
        TmpServiceDelegate.service()?.hideFloat(0)
    }

    override fun subscribeUi() {
        if (TmpServiceDelegate.service() != null)
            binding.service = TmpServiceDelegate.service()!!
        binding.vm = viewModel
        initParam()
        if (checkPermissions(true)) {
            init()
        }

        /*初始导航*/
        mNavHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        // setup custom navigator
        val navigator = KeepStateNavigator(this, mNavHostFragment.childFragmentManager, R.id.nav_host_fragment)
        navController = mNavHostFragment.navController
        navController.navigatorProvider.addNavigator(navigator)
        navController.setGraph(R.navigation.home_navigation)
//        layoutClick(binding.rtspBtn)


        binding.listBtn.setOnClickListener {
            binding.rightLy.openDrawer(Gravity.RIGHT)
        }
        binding.homeBtn.setOnClickListener {
            home()
        }

//        binding.rtspBtn.setOnClickListener {
//            layoutClick(binding.rtspBtn) {
//                navController.navigate(R.id.navigation_stream_media, null)
//            }
//        }
//        binding.localBtn.setOnClickListener {
//            layoutClick(binding.localBtn) {
//                navController.navigate(R.id.navigation_local, null)
//            }
//        }
        binding.fjBtn.setOnClickListener {
            layoutClick(binding.jhBtn, true) {
                ARouter.getInstance().build(MessageConstant.ROUTH_SCENERY).navigation(this)
            }
        }
        binding.jhBtn.setOnClickListener {
//            TmpServiceDelegate.service()?.test("")
            layoutClick(binding.jhBtn, true) {
                navController.navigate(R.id.navigation_interactive, null)
            }

//            TmpServiceDelegate.service()?.test("")
        }
//        ARouter.getInstance().build(MessageConstant.ROUTH_PARAM).navigation(this)

        /*自动收起侧边栏*/
        binding.rightLy.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(p0: View, p1: Float) {

            }

            override fun onDrawerOpened(p0: View) {
                if (drawLayoutTimerHandler == null) {
                    drawLayoutTimerHandler = TimerHandler(MessageConstant.MAIN_DRAW_LAYOUT_TIME) {
                        this@MainActivity.runOnUiThread {
                            try {
                                binding.rightLy.closeDrawer(Gravity.RIGHT)
                            } catch (e: Exception) {}
                        }
                    }
                }
                drawLayoutTimerHandler?.start()
            }

            override fun onDrawerClosed(p0: View) {
                drawLayoutTimerHandler?.stop()
            }

            override fun onDrawerStateChanged(p0: Int) {

            }
        })
        binding.rightDl.setOnTouchListener { view, motionEvent ->
            drawLayoutTimerHandler?.start()
            return@setOnTouchListener false
        }
    }

    fun initParam() {
        val finishTime = viewModel.spManager.getLong(MessageConstant.SP_FINISH_TIME, MessageConstant.FINISH_TIME)
        if (finishTime != MessageConstant.FINISH_TIME)
            MessageConstant.FINISH_TIME = finishTime
    }

    private fun checkSpeed() {
        val speed = viewModel.spManager.getFloat(MessageConstant.SP_MARQUEE_SPEED, 1f)
        System.out.println("当前速度 $speed")
        if (speed != viewModel.speedObs.get()) {
            Timber.i("当前速度 $speed")
            binding.titleLy2.stationTv.setMarqueeSpeed(speed)
//            binding.titleLy2.statusTv.setMarqueeSpeed(speed)
        }
    }

    private fun layoutClick(view: View, hideDL: Boolean = false, callback: (() -> Unit)? = null) {
        if (currLayout == view) return

        if (hideDL) {
            binding.rightLy.closeDrawer(Gravity.RIGHT)
        }
        callback?.invoke()
//        if (currLayout != null) {
//            currLayout!!.isSelected = false
//            currLayout!!.scaleX = 1f
//            currLayout!!.scaleY = 1f
//        }
////        if (currLayout is TextView)
////            (currLayout as TextView).textSize = DisplayUtil.px2sp(this, resources.getDimension(R.dimen.home_text_tab_size)).toFloat()
//        view.isSelected = true
//        currLayout = view
//        currLayout!!.scaleX = 1.2f
//        currLayout!!.scaleY = 1.2f
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && checkPermissions(false)) {
            System.out.println("onRequestPermissionsResult ")
            init()
        }
    }

    override fun initData() {
        LiveEBUtil.regist(RemoteMessageEvent::class.java, this, listener)
    }

    override fun linkViewModel() {

    }

    override fun onDestroy() {
        super.onDestroy()
        LiveEBUtil.unRegist(RemoteMessageEvent::class.java, listener)
        iconAnimator?.pause()
        drawLayoutTimerHandler?.stop()
    }

    private fun init() {
        //
        viewModel.initData()
        iconAnimator = ObjectAnimator.ofFloat(binding.titleLy2.logoLy, "alpha", 1f, 0f)
        iconAnimator?.duration = MessageConstant.MAIN_ANIMATION_TIME
        iconAnimator?.repeatMode = ObjectAnimator.REVERSE
        iconAnimator?.repeatCount = 1
        iconAnimator?.start()

        timerHandler = TimerHandler(MessageConstant.MAIN_ANIMATION_TIME_INTERVAL) {
            iconAnimator?.start()
        }
        timerHandler?.start()
    }

    override fun onPause() {
        super.onPause()
        timerHandler?.stop()
    }

    private fun checkPermissions(request: Boolean = false): Boolean {
        var hasPermission = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (i in 0 until PERMISSIONS.size) {
                val permission = PERMISSIONS[i]
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    hasPermission = false
                }
            }
            if (!hasPermission && request) {
                //
                this.requestPermissions(PERMISSIONS.toTypedArray(), REQUEST_CODE)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    // 检查是否有存储权限
                    val granted = Environment.isExternalStorageManager()
                    if (!granted) {
                        // 在activity中请求存储权限
                        val intent: Intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                            .setData(Uri.parse("package:$packageName"))
                        startActivityForResult(intent, 0)
                    }
                }
            }
        }

        return hasPermission
    }


    override fun show(tag: ArrayList<String>) {
        System.out.println("show tag $tag")
        try {
            binding.listSf.visibility = if (tag.contains(TAG_LIST)) View.VISIBLE else View.GONE
            binding.homeSf.visibility = if (tag.contains(TAG_HOME)) View.VISIBLE else View.GONE
        } catch (e: Exception) {}
    }

    fun homeDelay() {
        this@MainActivity.runOnUiThread {
            try {
                home()
            } catch (e: Exception) {
                GlobalScope.launch {
                    delay(100)
                    homeDelay()
                }
            }
        }

    }

    override fun home() {
        try {
            navController.navigate(R.id.navigation_local, null)
        } catch (e: Exception) {}
    }


}
