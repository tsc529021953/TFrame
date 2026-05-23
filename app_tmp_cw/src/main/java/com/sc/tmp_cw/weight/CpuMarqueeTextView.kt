package com.sc.tmp_cw.weight

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import com.nbhope.lib_frame.utils.DisplayUtil
import com.sc.tmp_cw.R
import timber.log.Timber

/**
 * 基于 CPU 绘制的轻量级跑马灯
 * 使用 ValueAnimator + translationX 实现，不占用 GPU 资源
 * 支持动态速度调节
 * 
 * 特性：
 * 1. 使用软件层类型（LAYER_TYPE_SOFTWARE）避免与视频播放争抢 GPU 资源
 * 2. 通过父容器设置 clipChildren="true" 确保文本只在指定区域内可见
 * 3. 自动检测文本宽度，只有超出视图宽度时才启动跑马灯
 * 4. 支持运行时动态调整速度
 */
class CpuMarqueeTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_SPEED = 0 // 默认速度 0-100
        private const val MIN_SPEED = 1
        private const val MAX_SPEED = 100
    }

    private var currentSpeed = DEFAULT_SPEED
    private var isRunning = false
    private var translationAnimator: android.animation.ValueAnimator? = null

    // 缓存计算结果
    private var textWidth = 0f
    private var viewWidth = 0f

    init {
        // 禁用硬件加速，使用 CPU 绘制
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        // 设置单行和跑马灯效果
        isSingleLine = true
        ellipsize = android.text.TextUtils.TruncateAt.MARQUEE
        marqueeRepeatLimit = -1 // 无限循环

        // 确保始终获得焦点以启动跑马灯
        isSelected = true
        isFocusable = true
        isFocusableInTouchMode = true
        
        // 启用裁剪，确保内容不溢出边界
        clipToOutline = true
//        isClipToPadding = true

        // 解析自定义属性
//        attrs?.let {
//            val typedArray = context.obtainStyledAttributes(it, R.styleable.MarqueeView)
//            val speed = typedArray.getInt(R.styleable.MarqueeView_speed, DEFAULT_SPEED)
//            setSpeed(speed)
//            typedArray.recycle()
//        }
    }

    /**
     * 设置跑马灯速度
     * @param speed 速度值 1-100，数值越大速度越快
     */
    fun setSpeed(speed: Int) {
//        currentSpeed = speed.coerceIn(MIN_SPEED, MAX_SPEED)
        System.out.println("SpanTextView setSpeed $speed")
        setMarqueeSpeed(currentSpeed.toFloat())
        // 如果正在运行，重新启动以应用新速度
        if (isRunning) {
            stopMarquee()
            startMarquee()
        }
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

    /**
     * 获取当前速度
     */
    fun getSpeed(): Int {
        return currentSpeed
    }

    /**
     * 启动跑马灯
     */
    fun startMarquee() {
        if (isRunning) return

        // 测量文本宽度
        textWidth = paint.measureText(text?.toString() ?: "")
        viewWidth = width.toFloat()

        // 只有当文本宽度大于视图宽度时才启动
        if (textWidth <= viewWidth || text.isNullOrEmpty()) {
            return
        }

        isRunning = true

        // 使用 ValueAnimator 实现平滑滚动
        val duration = calculateDuration()
//        translationAnimator = android.animation.ValueAnimator.ofFloat(viewWidth, -textWidth).apply {
//            this.duration = duration
//            interpolator = LinearInterpolator()
//            repeatCount = android.animation.ValueAnimator.INFINITE
//            repeatMode = android.animation.ValueAnimator.RESTART
//
//            addUpdateListener { animator ->
//                translationX = animator.animatedValue as Float
//            }
//
//            start()
//        }
    }

    /**
     * 停止跑马灯
     */
    fun stopMarquee() {
        isRunning = false
        translationAnimator?.cancel()
        translationAnimator = null
        translationX = 0f
    }

    /**
     * 暂停跑马灯
     */
    fun pauseMarquee() {
        if (isRunning) {
            translationAnimator?.pause()
        }
    }

    /**
     * 恢复跑马灯
     */
    fun resumeMarquee() {
        if (isRunning) {
            translationAnimator?.resume()
        } else {
            startMarquee()
        }
    }

    /**
     * 根据速度计算动画持续时间
     * 速度越快，持续时间越短
     */
    private fun calculateDuration(): Long {
        // 基础持续时间：文本宽度 / 速度系数
        // 速度 1 = 最慢 (10000ms), 速度 100 = 最快 (1000ms)
        val baseDuration = 10000L
        val minDuration = 1000L

        val duration = baseDuration - ((currentSpeed - 1) * (baseDuration - minDuration) / (MAX_SPEED - 1))
        return duration.coerceAtLeast(minDuration)
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)

        // 文本变化时重新启动
        if (isRunning) {
            stopMarquee()
            post { startMarquee() }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w.toFloat()

        // 尺寸变化时重新启动
        if (isRunning) {
            stopMarquee()
            post { startMarquee() }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopMarquee()
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: android.graphics.Rect?) {
        // 始终保持选中状态
        if (!isSelected) {
            isSelected = true
        }
        super.onFocusChanged(true, direction, previouslyFocusedRect)
    }

    override fun isFocused(): Boolean {
        // 始终返回 true，确保跑马灯持续运行
        return true
    }
}
