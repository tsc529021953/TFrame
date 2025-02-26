package com.nbhope.lib_frame.utils

import android.view.View
import com.nbhope.lib_frame.app.HopeBaseApp
import com.nbhope.lib_frame.di.BaseAppComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author  tsc
 * @date  2025/1/7 14:54
 * @version 0.0.0-1
 * @description
 */
object AnimationUtil {

    const val ANIMATION_TIMER = 2000L
    const val HIDE_TIMER = 2000L

    fun setInitPoint(slidingView: View, pointX: Float = -1f, isRight: Boolean = true) {
        var w = slidingView.width.toFloat()
        slidingView.translationX = if (pointX >= 0) pointX else if (isRight) w else -w
    }

    fun showSlidingView(
            slidingView: View,
            isRight: Boolean = true,
            aTime: Long = ANIMATION_TIMER,
            hideTime: Long = HIDE_TIMER, runEndCB: (() -> Unit)? = null
    ) {
        // 启动从左侧滑出的动画
        var w = slidingView.width.toFloat()
        var width = DisplayUtil.getScreenWidth(HopeBaseApp.getContext()).toFloat()
        System.out.println("width $w $width ")
//        slidingView.translationX = if (isRight) w else -w
        System.out.println("translationX ${slidingView.translationX}")

        // 设置 View 为可见
        slidingView.visibility = View.VISIBLE
        slidingView.animate()
                .translationX(0f)
                .setDuration(aTime)  // 1秒钟滑动到屏幕中
                .withEndAction {
                    runEndCB?.invoke()
                    // 在滑动完成后，延迟5秒钟，然后开始滑出
                    if (hideTime > 0) {
                        GlobalScope.launch {
                            delay(hideTime)
                            GlobalScope.launch(Dispatchers.Main) {
                                slideOutAndHide(slidingView, isRight)
                            }
                        }
                    }
                }
    }

    fun slideToEnd(slidingView: View, aTime: Long = ANIMATION_TIMER, runEndCB: (() -> Unit)? = null) {
        var width = DisplayUtil.getScreenWidth(HopeBaseApp.getContext()).toFloat()
        slidingView.animate()
                .translationX(width)  // 滑出屏幕
                .setDuration(aTime)  // 1秒钟滑出
                .withEndAction {
                    // 动画结束后隐藏视图
//                    slidingView.visibility = View.GONE
                    runEndCB?.invoke()
                }
    }

    fun slideOutAndHide(slidingView: View, isRight: Boolean = true, aTime: Long = ANIMATION_TIMER) {
        var w = slidingView.width.toFloat()
        slidingView.animate()
                .translationX(if (isRight) w else -w)  // 滑出屏幕
                .setDuration(aTime)  // 1秒钟滑出
                .withEndAction {
                    // 动画结束后隐藏视图
                    slidingView.visibility = View.GONE
                }
    }

}
