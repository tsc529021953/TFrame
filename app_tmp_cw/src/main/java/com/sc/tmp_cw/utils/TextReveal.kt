package com.sc.tmp_cw.utils

import android.animation.ValueAnimator
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.TextView

/**
 * 方案K文字浮现动画:整段文字先占位(断行不变),每个字单独控制透明度
 * 渐显带宽度取文字长度约1/8,短文本近似逐字、长文本近似扫过渐显
 */
object TextReveal {

    /**
     * 逐字浮现
     * @return 对应的ValueAnimator(无内容时不启动),供调用方管理取消
     */
    fun revealText(tv: TextView, delay: Long, duration: Long): ValueAnimator {
        val anim = ValueAnimator.ofFloat(0f, 1f)
        val text = tv.text?.toString() ?: return anim
        if (text.isEmpty()) return anim
        val fade = (text.length / 8).coerceAtLeast(1)
        val spannable = SpannableString(text)
        val spans = Array(text.length) { FadeCharSpan() }
        for (i in spans.indices) spannable.setSpan(spans[i], i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv.text = spannable
        anim.duration = duration
        anim.startDelay = delay
        anim.interpolator = LinearInterpolator()
        anim.addUpdateListener { animation ->
            val head = (animation.animatedValue as Float) * (spans.size + fade)
            for (i in spans.indices) spans[i].alpha = ((head - i) / fade).coerceIn(0f, 1f)
            tv.invalidate()
        }
        anim.start()
        return anim
    }

    /** 横条(如标题下划线)从左向右生长 */
    fun growFromLeft(v: View, delay: Long, duration: Long) {
        v.pivotX = 0f
        v.scaleX = 0f
        v.animate()
                .scaleX(1f)
                .setStartDelay(delay)
                .setDuration(duration)
                .setInterpolator(DecelerateInterpolator())
                .start()
    }

    /** 每个字一个span,只改透明度 */
    private class FadeCharSpan : CharacterStyle() {
        var alpha = 0f
        override fun updateDrawState(tp: TextPaint) {
            tp.alpha = (alpha * 255).toInt()
        }
    }
}
