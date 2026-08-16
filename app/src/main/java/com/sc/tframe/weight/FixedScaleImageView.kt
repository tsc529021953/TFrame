package com.sc.tframe.weight

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager
import android.widget.ImageView
import com.sc.tframe.R

/**
 * 固定比例缩放的 ImageView。
 *
 * 当 [cropHorizontally] 为 true 时，按屏幕真实尺寸（非 View 尺寸）对图片等比缩放并居中裁剪，
 * 确保分屏等场景下图片仍保持全屏级别的缩放比例，不会随 View 变小而缩小。
 */
open class FixedScaleImageView : androidx.appcompat.widget.AppCompatImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attr: AttributeSet?) : super(context, attr) {
        initAttrs(attr)
    }
    constructor(context: Context, attr: AttributeSet?, style: Int) : super(context, attr, style) {
        initAttrs(attr)
    }

    var cropHorizontally: Boolean = false
    var antiAliasScale: Boolean = true

    private var isApplyingScale = false

    private val highQualityPaint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)

    private fun initAttrs(attr: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attr, R.styleable.FixedScaleImageView)
        cropHorizontally = ta.getBoolean(R.styleable.FixedScaleImageView_cropHorizontally, false)
        antiAliasScale = ta.getBoolean(R.styleable.FixedScaleImageView_antiAliasScale, true)
        ta.recycle()
        if (cropHorizontally) scaleType = ScaleType.MATRIX
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (cropHorizontally) applyMatrix()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (!isApplyingScale && cropHorizontally && width > 0 && height > 0) applyMatrix()
    }

    private fun applyMatrix() {
        val d = drawable ?: return
        val vw = width
        val vh = height
        if (vw == 0 || vh == 0) return

        val dw = d.intrinsicWidth
        val dh = d.intrinsicHeight
        if (dw == 0 || dh == 0) return

        val fullW = getRealDisplayWidth().toFloat()
        val fullH = getRealDisplayHeight().toFloat()
        val scale = maxOf(fullW / dw, fullH / dh)

        // 预缩放Bitmap，消除缩放摩尔纹
        if (antiAliasScale && d is BitmapDrawable) {
            val src = d.bitmap
            val sw = (dw * scale).toInt()
            val sh = (dh * scale).toInt()
            if (sw > 0 && sh > 0) {
                isApplyingScale = true
                val scaled = Bitmap.createScaledBitmap(src, sw, sh, true)
                super.setImageDrawable(BitmapDrawable(resources, scaled))
                isApplyingScale = false
                // 缩放后Bitmap尺寸已匹配，用CENTER_CROP裁剪即可
                scaleType = ScaleType.CENTER_CROP
                return
            }
        }

        val matrix = Matrix()
        matrix.setScale(scale, scale)
        matrix.postTranslate((vw - dw * scale) / 2f, (vh - dh * scale) / 2f)
        imageMatrix = matrix
    }

    @Suppress("DEPRECATION")
    private fun getRealDisplayWidth(): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display? = wm.defaultDisplay
        val metrics = DisplayMetrics()
        display?.getRealMetrics(metrics)
        return metrics.widthPixels
    }

    @Suppress("DEPRECATION")
    private fun getRealDisplayHeight(): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display? = wm.defaultDisplay
        val metrics = DisplayMetrics()
        display?.getRealMetrics(metrics)
        return metrics.heightPixels
    }
}
