package com.sc.tmp_cw

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.navigation.fragment.NavHostFragment
import com.alibaba.android.arouter.launcher.ARouter
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.network.NetworkCallback
import com.nbhope.lib_frame.utils.AnimationUtil
import com.nbhope.lib_frame.utils.DisplayUtil
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.nbhope.lib_frame.utils.toast.ToastUtil
import com.sc.tmp_cw.constant.MessageConstant
import com.sc.tmp_cw.databinding.ActivityMainBinding
import com.sc.tmp_cw.service.TmpServiceDelegate
import com.sc.tmp_cw.vm.MainViewModel
import com.sc.tmp_cw.weight.KeepStateNavigator
import kotlinx.coroutines.delay
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

 */
class MainActivity : BaseBindingActivity<ActivityMainBinding, MainViewModel>() {

    companion object {

        const val REQUEST_CODE = 10085

        var PERMISSIONS = arrayListOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        )

        const val PLAY_IMAGE_TIME = 15000L
        const val CTRL_LAYOUT_VIEW_TIME = 10000L

    }

    override var layoutId: Int = R.layout.activity_main

    @Inject
    override lateinit var viewModel: MainViewModel

    @Inject
    lateinit var networkCallback: NetworkCallback

    private lateinit var mNavHostFragment: NavHostFragment

    private var currLayout: View? = null

    private var listener = androidx.lifecycle.Observer<Any> {
        it as RemoteMessageEvent
        when (it.cmd) {
            MessageConstant.SERVICE_INIT_SUCCESS -> {
                if (TmpServiceDelegate.service() != null)
                    binding.service = TmpServiceDelegate.service()!!
                Timber.i("SERVICE_INIT_SUCCESS ${TmpServiceDelegate.service()}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI()
        System.out.println("onCreate ??? $requestedOrientation ${requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE}")
        if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

    }

    override fun subscribeUi() {
        binding.vm = viewModel
        if (TmpServiceDelegate.service() != null)
            binding.service = TmpServiceDelegate.service()!!
        if (checkPermissions(true)) {
            init()
        }

        /*初始导航*/
        mNavHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        // setup custom navigator
        val navigator = KeepStateNavigator(this, mNavHostFragment.childFragmentManager, R.id.nav_host_fragment)
        val navController = mNavHostFragment.navController
        navController.navigatorProvider.addNavigator(navigator)
        navController.setGraph(R.navigation.home_navigation)
        layoutClick(binding.rtspBtn)


        binding.listBtn.setOnClickListener {
            binding.rightLy.openDrawer(Gravity.RIGHT)
        }

        binding.rtspBtn.setOnClickListener {
            layoutClick(binding.rtspBtn) {
                navController.navigate(R.id.navigation_stream_media, null)
            }
        }
        binding.localBtn.setOnClickListener {
            layoutClick(binding.localBtn) {
                ToastUtil.showS(R.string.no_make)
            }
        }
        binding.fjBtn.setOnClickListener {
            ARouter.getInstance().build(MessageConstant.ROUTH_SCENERY).navigation(this)
        }
        binding.jhBtn.setOnClickListener {
            TmpServiceDelegate.service()?.test("")
            layoutClick(binding.jhBtn) {
                ToastUtil.showS(R.string.no_make)
//                ARouter.getInstance().build(MessageConstant.ROUTH_URGENT_NOTIFY).navigation(this)
            }
        }
    }

    private fun layoutClick(view: View, callback: (() -> Unit)? = null) {
        if (currLayout == view) return
        callback?.invoke()
        if (currLayout != null) {
            currLayout!!.isSelected = false
            currLayout!!.scaleX = 1f
            currLayout!!.scaleY = 1f
        }
//        if (currLayout is TextView)
//            (currLayout as TextView).textSize = DisplayUtil.px2sp(this, resources.getDimension(R.dimen.home_text_tab_size)).toFloat()
        view.isSelected = true
        currLayout = view
        currLayout!!.scaleX = 1.2f
        currLayout!!.scaleY = 1.2f
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
    }

    private fun init() {
        //
        viewModel.initData()
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
            }
        }
        return hasPermission
    }

    val handler = Handler()


}
