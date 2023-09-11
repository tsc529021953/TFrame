package com.sc.nft.activity

import android.media.Image
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.MotionEvent
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.nbhope.lib_frame.arouterpath.RouterPath
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.utils.HopeUtils
import com.nbhope.lib_frame.utils.ViewUtil
import com.sc.nft.R
import com.sc.nft.databinding.ActivityMainBinding
import com.sc.nft.databinding.ActivityScreenBinding
import com.sc.nft.service.MainService
import me.jessyan.autosize.AutoSizeConfig
import timber.log.Timber
import java.util.*

/**
 * author: sc
 * date: 2023/9/10
 */
@Route(path = RouterPath.activity_nft_screen)
class ScreenActivity : BaseBindingActivity<ActivityScreenBinding, BaseViewModel>() {

    override var layoutId: Int = R.layout.activity_screen

    val POSITION = 10086

    private var posTimer: Timer? = null

    var posTimerTask: TimerTask? = null

    @Autowired
    @JvmField
    var mainService: MainService? = null

    companion object{
        const val HEART_TIMER: Long = 2000
    }

    var posHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == POSITION) {
                // 传递给主线程更新
//                receiveRemotePosition(gtCurrentPosition())
            }
        }
    }

    override fun subscribeUi() {
        ViewUtil.immersionTitle(this)
        startTimer()
        binding.bgLy.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    this@ScreenActivity.finish()
                }
                return false
            }
        })
        if (mainService == null)
            mainService = ARouter.getInstance().build(RouterPath.service_nft).navigation() as MainService
        mainService?.stopTimer()
    }

    override fun initData() {

    }

    override fun linkViewModel() {

    }

    override var viewModel = BaseViewModel()

    private fun startTimer() {
        if (posTimer != null && posTimerTask != null) return
        posTimer = Timer()
        posTimerTask = object : TimerTask() {
            override fun run() {
                // 间隔时间到了 尝试一次重新连接
                randomImage()
            }
        }
        posTimer?.schedule(posTimerTask, 0, HEART_TIMER)
    }

    fun randomImage() {
        val display = windowManager.defaultDisplay
        var wb = AutoSizeConfig.getInstance().screenWidth * 1.00f / AutoSizeConfig.getInstance().designWidthInDp
        var width = AutoSizeConfig.getInstance().screenWidth - wb * 300
        var hb = AutoSizeConfig.getInstance().screenHeight * 1.00f / AutoSizeConfig.getInstance().designHeightInDp
        var height = AutoSizeConfig.getInstance().screenHeight - hb * 200
        var x = Random().nextInt(width.toInt()) //display.width
        var y = Random().nextInt(height.toInt()) // display.height
//        Timber.i("NTAG 移动位置！$x , $y ${AutoSizeConfig.getInstance().screenWidth} " +
//                "${AutoSizeConfig.getInstance().screenHeight} $wb $hb ${display.width} ${display.height}")
        binding.screenIv .setTranslationX(x.toFloat())
        binding.screenIv .setTranslationY(y.toFloat())
//        binding.screenIv.scrollTo(x, y)
    }

    override fun onDestroy() {
        super.onDestroy()
        mainService?.reTimer()
        posTimer?.cancel()
        posTimer = null
        posTimerTask?.cancel()
        posTimerTask = null
    }
}
