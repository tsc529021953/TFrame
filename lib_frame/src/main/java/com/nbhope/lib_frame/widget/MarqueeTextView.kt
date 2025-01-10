package com.nbhope.lib_frame.widget

import android.content.Context
import android.graphics.Rect
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
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
}
