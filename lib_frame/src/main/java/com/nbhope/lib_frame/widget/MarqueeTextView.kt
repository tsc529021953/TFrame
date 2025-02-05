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

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        isFocusable = true
        isFocusableInTouchMode = true
        setSingleLine()
        ellipsize = TextUtils.TruncateAt.MARQUEE
        marqueeRepeatLimit = -1
        setDelay(1000)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        isFocusable = true
        isFocusableInTouchMode = true
        setSingleLine()
        ellipsize = TextUtils.TruncateAt.MARQUEE
        marqueeRepeatLimit = -1
        setDelay(1000)
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
        System.out.println("SpanTextView setMarqueeSpeed $newSpeed")
        try {
            // 获取走马灯配置对象
            val tvClass = Class.forName("android.widget.TextView")
            val marqueeField = tvClass.getDeclaredField("mMarquee")
            marqueeField.isAccessible = true
            val marquee = marqueeField[this] ?: return
            // 设置新的速度
            val marqueeClass: Class<*> = marquee.javaClass
            // 速度变量的名称可能与此示例的不相同 可自行打印查看
            for (field in marqueeClass.declaredFields) {
                Timber.i("SpanTextView ${field.name}")
            }
            // SDK中的是mPixelsPerMs，但我的开发机是下面的名称
            val speedField = marqueeClass.getDeclaredField("mPixelsPerSecond") //低版本：mScrollUnit
            speedField.isAccessible = true
            val orgSpeed = speedField[marquee] as Float
            // 这里设置了相对于原来的20倍
            speedField[marquee] = newSpeed
            // Log.i("SpanTextView", "setMarqueeSpeed: " + orgSpeed);
            //  Log.i("SpanTextView", "setMarqueeSpeed: " + newSpeed);
        } catch (e: ClassNotFoundException) {
            Timber.e("SpanTextView setMarqueeSpeed: 设置跑马灯速度失败 ${e.message}")
        } catch (e: NoSuchFieldException) {
            Timber.e("SpanTextView setMarqueeSpeed: 设置跑马灯速度失败 ${e.message}")
        } catch (e: IllegalAccessException) {
            Timber.e("SpanTextView setMarqueeSpeed: 设置跑马灯速度失败 ${e.message}")
        }
    }

}
