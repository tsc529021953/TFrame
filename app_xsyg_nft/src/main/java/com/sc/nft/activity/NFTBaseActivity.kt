package com.sc.nft.activity

import android.view.GestureDetector
import android.view.MotionEvent
import androidx.databinding.ViewDataBinding
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.launcher.ARouter
import com.nbhope.lib_frame.arouterpath.RouterPath
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.base.BaseViewModel
import com.sc.nft.service.MainService
import timber.log.Timber
import javax.inject.Inject


/**
 * author: sc
 * date: 2023/9/10
 */
open class NFTBaseActivity<T : ViewDataBinding, VM : BaseViewModel> : BaseBindingActivity<T, VM>() {

    override var layoutId: Int = -1

    private var gestureDetector: GestureDetector? = null

    private var myGestureDetector: MyGestureDetector? = null

    @Autowired
    @JvmField
    var mainService: MainService? = null

    override fun subscribeUi() {
        //实例化MyGestureDetector
        myGestureDetector = MyGestureDetector() //实例化GestureDetector并将MyGestureDetector实例传入
        gestureDetector = GestureDetector(this, myGestureDetector)
        if (mainService == null)
            mainService = ARouter.getInstance().build(RouterPath.service_nft).navigation() as MainService
        Timber.i("NTAG subscribeUi $mainService")
    }

    override fun initData() {

    }

    override fun linkViewModel() {

    }

    override lateinit var viewModel: VM

//    /** * 重写onTouchEvent返回一个gestureDetector的屏幕触摸事件  */
//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        Timber.i("NTAG onTouchEvent $mainService")
//        return gestureDetector.onTouchEvent(event)
//    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            Timber.i("NTAG onTouchEvent $mainService")
            mainService?.reTimer()
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        if (event?.action == MotionEvent.ACTION_DOWN) {
//            Timber.i("NTAG onTouchEvent $mainService")
//            mainService?.reTimer()
//        }
        return super.onTouchEvent(event)
    }

    /** * 自定义MyGestureDetector类继承SimpleOnGestureListener  */
    internal class MyGestureDetector : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
//            if (e1.getX() - e2.getX() > com.sc.lib_weather.utils.LocationUtil.MIN_DISTANCE) {
//                Toast.makeText(this@MainActivity, "左滑", Toast.LENGTH_SHORT).show()
//            } else if (e2.getX() - e1.getX() > com.sc.lib_weather.utils.LocationUtil.MIN_DISTANCE) {
//                Toast.makeText(this@MainActivity, "右滑", Toast.LENGTH_SHORT).show()
//            } else if (e1.getY() - e2.getY() > com.sc.lib_weather.utils.LocationUtil.MIN_DISTANCE) {
//                Toast.makeText(this@MainActivity, "上滑", Toast.LENGTH_SHORT).show()
//            } else if (e2.getY() - e1.getY() > com.sc.lib_weather.utils.LocationUtil.MIN_DISTANCE) {
//                Toast.makeText(this@MainActivity, "下滑", Toast.LENGTH_SHORT).show()
//            }
            return true
        }
    }
}