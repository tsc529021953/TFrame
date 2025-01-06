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
import android.view.View
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.network.NetworkCallback
import com.nbhope.lib_frame.utils.DisplayUtil
import com.sc.tmp_cw.databinding.ActivityMainBinding
import com.sc.tmp_cw.vm.MainViewModel
import kotlinx.coroutines.delay
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

        const val ANIMATION_TIMER = 2000L
        const val HIDE_TIMER = 5000L
    }

    override var layoutId: Int = R.layout.activity_main

    @Inject
    override lateinit var viewModel: MainViewModel

    @Inject
    lateinit var networkCallback: NetworkCallback

    private var listener = androidx.lifecycle.Observer<Any> {
        it as RemoteMessageEvent
        when (it.cmd) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        System.out.println("onCreate ??? $requestedOrientation ${requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE}")
        if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

    }

    override fun subscribeUi() {
        binding.vm = viewModel
        if (checkPermissions(true)) {
            init()
        }
        binding.rightVLy.setOnClickListener {
            // TODO 添加动画
//            binding.rightLy.visibility = View.VISIBLE
            showSlidingView(binding.rightLy, true, ANIMATION_TIMER, HIDE_TIMER)
        }
        binding.rightLy.post {
//            binding.rightLy.visibility = View.GONE
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && checkPermissions(false)) {
            System.out.println("onRequestPermissionsResult ")
            init()
        }
    }

    override fun initData() {

    }

    override fun linkViewModel() {

    }

    override fun onDestroy() {
        super.onDestroy()

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

    private fun showSlidingView(
        slidingView: View,
        isRight: Boolean = true,
        aTime: Long = ANIMATION_TIMER,
        hideTime: Long = HIDE_TIMER
    ) {
        // 启动从左侧滑出的动画
        var w = slidingView.width.toFloat()
        var width = DisplayUtil.getScreenWidth(this).toFloat()
        System.out.println("width $w $width ")
        slidingView.translationX = if (isRight) w else -w
        System.out.println("translationX ${slidingView.translationX}")

        // 设置 View 为可见
        slidingView.visibility = View.VISIBLE
//        slidingView.animate()
//            .translationX(0f)
//            .setDuration(aTime)  // 1秒钟滑动到屏幕中
//            .withEndAction {
//                // 在滑动完成后，延迟5秒钟，然后开始滑出
//                if (hideTime > 0) {
//                    viewModel.launch {
//                        delay(hideTime)
//                        this@MainActivity.runOnUiThread {
//                            slideOutAndHide(slidingView, isRight)
//                        }
//                    }
//                }
//            }

        val slideInAnimator = ObjectAnimator.ofFloat(slidingView, "translationX", 0f)
        slideInAnimator.duration = aTime // 滑动动画时长
        slideInAnimator.start()
        // 延迟五秒后，启动滑回左侧并隐藏的动画
        viewModel.launch {
            delay(hideTime)
            this@MainActivity.runOnUiThread {
                slideOutAndHide(slidingView)
            }
        }
    }

    private fun slideOutAndHide(slidingView: View, isRight: Boolean = true, aTime: Long = ANIMATION_TIMER) {
        var w = slidingView.width.toFloat()
//        slidingView.animate()
//            .translationX(if (isRight) w else -w)  // 滑出屏幕
//            .setDuration(aTime)  // 1秒钟滑出
//            .withEndAction {
//                // 动画结束后隐藏视图
//                slidingView.visibility = View.GONE
//            }
        val slideOutAnimator = ObjectAnimator.ofFloat(slidingView, "translationX", -w)
        slideOutAnimator.duration = aTime // 滑动动画时长
        slideOutAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                // 动画结束后，隐藏 View
                slidingView.visibility = View.GONE
            }
        })
        slideOutAnimator.start()
    }
}
