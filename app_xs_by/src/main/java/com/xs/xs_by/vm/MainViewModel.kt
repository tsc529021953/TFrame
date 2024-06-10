package com.xs.xs_by.vm

import android.R.attr.fromDegrees
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.databinding.ObservableBoolean
import com.google.gson.Gson
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.xs.xs_by.bean.ThemeBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject


/**
 * @author  tsc
 * @date  2024/4/26 14:43
 * @version 0.0.0-1
 * @description
 */
class MainViewModel @Inject constructor(val spManager: SharedPreferencesManager) : BaseViewModel() {

    companion object {

        const val BASE_FILE = "/THREDIM_MEDIA/"
        const val CONFIG_FILE = "AppInfo.json"

        var themeBean = ThemeBean("幸福婚庆", "001", false)
    }

    private var mScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var gson = Gson()

    var rotateAnimation: RotateAnimation? = null

    var themeStateObs = ObservableBoolean(false)

    private var animator: ObjectAnimator? = null

    fun initData() {

    }

    fun startAnimation(view: View) {
//        if (rotateAnimation == null) {
//            rotateAnimation = RotateAnimation(
//                0f, 360f,
//                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
//            )
//            rotateAnimation?.duration = 5000
//            rotateAnimation?.repeatCount = Animation.INFINITE // 设置动画无限次数重复
//            rotateAnimation?.interpolator = LinearInterpolator() // 设置动画的插值器，这里使用线性插值
//        }
//        view.startAnimation(rotateAnimation)
        if (animator == null) {
            animator = ObjectAnimator.ofFloat(view, "rotation", 0F, 360F)
            animator!!.duration = 5000
            animator!!.repeatCount = ObjectAnimator.INFINITE
//            animator!!.repeatMode = ObjectAnimator.REVERSE
            animator!!.interpolator = LinearInterpolator()
            animator?.start()
        } else animator?.resume()

    }

    fun pauseAnimation(view: View) {
//        view.clearAnimation()
        animator?.pause()
    }

    fun stopAnimation(view: View) {
//        view.clearAnimation()
        animator?.cancel()
    }

}
