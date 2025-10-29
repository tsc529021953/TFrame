package com.sc.tmp_translate.weight

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class AudioWaveView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val amplitudes = mutableListOf<Float>()  // 保存波形数据
    private val maxPoints = 200                      // 控制绘制点数量
    private val paint = Paint().apply {
        color = Color.WHITE
        strokeWidth = 4f
        isAntiAlias = true
    }

    fun addAmplitude(amp: Float) {
        if (amplitudes.size > maxPoints) amplitudes.removeAt(0)
        amplitudes.add(amp)
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val midY = height / 2f
        val stepX = width / maxPoints.toFloat()
        for (i in amplitudes.indices) {
            val amp = amplitudes[i] * (height / 2f)
            canvas.drawLine(
                i * stepX, midY - amp,
                i * stepX, midY + amp,
                paint
            )
        }
    }
}
