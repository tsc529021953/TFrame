package com.xs.xs_by.vm

import android.R.attr.fromDegrees
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import com.google.gson.Gson
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.xs.xs_by.bean.AppInfo
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
        val AppList = arrayListOf<AppInfo>(
            AppInfo("无线投屏2", "com.android.settings"),
            AppInfo("白板书写2", "com.illusory.isdb"),
            AppInfo("文件管理2", "com.android.documentsui"),
            AppInfo("更多应用2", "com.android.settings/com.android.settings.SubSettings")
        )

        const val BASE_FILE = "/THREDIM_MEDIA/"
        const val CONFIG_FILE = "AppInfo.json"
    }

    private var mScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var gson = Gson()

    var rotateAnimation: RotateAnimation? = null

    fun initData() {

    }

    fun startAnimation(view: View) {
        if (rotateAnimation == null) {
            rotateAnimation = RotateAnimation(
                0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
            )
            rotateAnimation?.duration = 5000
            rotateAnimation?.repeatCount = Animation.INFINITE // 设置动画无限次数重复
            rotateAnimation?.interpolator = LinearInterpolator() // 设置动画的插值器，这里使用线性插值
        }
        view.startAnimation(rotateAnimation)
    }

    fun stopAnimation(view: View) {
        view.clearAnimation()
    }

}
