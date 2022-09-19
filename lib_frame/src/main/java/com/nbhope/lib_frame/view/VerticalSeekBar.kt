package com.nbhope.lib_frame.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent





/**
 *  Create: enjie
 *  Date: 2021/1/7
 *  Describe:
 */
class VerticalSeekBar : androidx.appcompat.widget.AppCompatSeekBar {
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {

    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

    }
    constructor(context: Context) : super(context) {}

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(h, w, oldh, oldw)
    }

    @Synchronized
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec)
        setMeasuredDimension(measuredHeight, measuredWidth)
    }

    @Synchronized
    override fun onDraw(canvas: Canvas) {
        canvas.rotate(-90.0f)
        canvas.translate((-height).toFloat(), 0.0f)
        super.onDraw(canvas)
    }

    @Synchronized
    override fun setProgress(progress: Int) {
        super.setProgress(progress)
        onSizeChanged(width, height, 0, 0)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        }
        progress = when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP ->{
                (max - max * event.y / height).toInt()
            }
            else -> return super.onTouchEvent(event)
        }
        return true
    }
}