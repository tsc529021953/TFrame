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
class LauncherViewModel @Inject constructor(val spManager: SharedPreferencesManager) : BaseViewModel() {


    private var mScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var gson = Gson()

    private var animator: ObjectAnimator? = null

    fun initData() {

    }

    fun startAnimation(view: View) {
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
        animator?.pause()
    }

    fun stopAnimation(view: View) {
        animator?.cancel()
    }

}
