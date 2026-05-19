package com.nbhope.lib_frame.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import timber.log.Timber
import java.lang.reflect.Field


class MarqueeTextView : AppCompatTextView {

    // 优化：缓存反射字段，避免重复查找
    private var marqueeFieldCache: Field? = null
    private var speedFieldCache: Field? = null
    private var lastSpeed: Float = -1f

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        isFocusable = true
        isFocusableInTouchMode = true
        setSingleLine()
        ellipsize = TextUtils.TruncateAt.MARQUEE
        marqueeRepeatLimit = -1
        setDelay(1000)
        // 优化：启用硬件加速图层以提升性能，减少与视频播放的冲突
        setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        isFocusable = true
        isFocusableInTouchMode = true
        setSingleLine()
        ellipsize = TextUtils.TruncateAt.MARQUEE
        marqueeRepeatLimit = -1
        setDelay(1000)
        // 优化：启用硬件加速图层以提升性能
        setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
    }

    private fun setDelay(delay: Int) {
        try {
            val marqueeField: Field = TextView::class.java.getDeclaredField("mMarquee")
            marqueeField.setAccessible(true)
            val marquee: Any? = marqueeField.get(this) ?: return
            if (marquee != null) {
                val delayField: Field = marquee.javaClass.getDeclaredField("MARQUEE_DELAY")
                delayField.setAccessible(true)
                delayField.setInt(marquee, delay) // 设置为 0 毫秒，表示没有延迟
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        if (focused) {
            super.onFocusChanged(focused, direction, previouslyFocusedRect)
        }
    }

    override fun onWindowFocusChanged(focused: Boolean) {
        if (focused) {
            super.onWindowFocusChanged(focused)
        }
    }

    override fun isFocused(): Boolean {
        return true
    }

    /**
     * 利用反射 设置跑马灯的速度
     * 在onLayout中调用即可
     *
     * @param newSpeed 新的速度
     */
    @SuppressLint("PrivateApi")
    fun setMarqueeSpeed(newSpeed: Float) {
        // 优化：如果速度没有变化，直接返回
        if (Math.abs(newSpeed - lastSpeed) < 0.01f) {
            return
        }
        
        try {
            // 获取走马灯配置对象
            val tvClass = Class.forName("android.widget.TextView")
            
            // 优化：缓存 marquee 字段
            if (marqueeFieldCache == null) {
                marqueeFieldCache = tvClass.getDeclaredField("mMarquee")
                marqueeFieldCache?.isAccessible = true
            }
            
            val marquee = marqueeFieldCache?.get(this) ?: return
            
            // 设置新的速度
            val marqueeClass: Class<*> = marquee.javaClass
            
            // 优化：缓存 speed 字段
            if (speedFieldCache == null) {
                try {
                    speedFieldCache = marqueeClass.getDeclaredField("mPixelsPerSecond")
                } catch (e: NoSuchFieldException) {
                    // 尝试其他可能的字段名
                    try {
                        speedFieldCache = marqueeClass.getDeclaredField("mScrollUnit")
                    } catch (e2: NoSuchFieldException) {
                        Timber.e("找不到速度字段")
                        return
                    }
                }
                speedFieldCache?.isAccessible = true
            }
            
            speedFieldCache?.setFloat(marquee, newSpeed)
            lastSpeed = newSpeed
            
        } catch (e: Exception) {
            Timber.e("设置跑马灯速度失败: ${e.message}")
        }
    }

}
