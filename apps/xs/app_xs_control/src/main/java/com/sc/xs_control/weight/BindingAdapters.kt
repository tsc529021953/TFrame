package com.sc.xs_control.weight

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.sc.xs_control.R

/**
 * @author  tsc
 * @date  2026/10/15
 * @version 0.0.0-1
 * @description DataBinding 适配器
 *
 * 说明：databinding 库对 android:src 只内置了 String/Uri/Drawable 参数的适配器，
 * 不支持 @drawable 资源引用（int）三元表达式，故提供自定义适配器供图标随状态切换。
 */
object BindingAdapters {

    /** 静音状态图标：已静音显示"静音"图标(ic_voice_1)，未静音显示"取消静音"图标(ic_voice_2) */
    @JvmStatic
    @BindingAdapter("mutedIcon")
    fun setMutedIcon(view: ImageView, muted: Boolean) {
        view.setImageResource(if (muted) R.drawable.ic_voice_1 else R.drawable.ic_voice_2)
    }
}
