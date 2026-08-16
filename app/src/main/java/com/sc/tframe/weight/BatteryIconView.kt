package com.sc.tframe.weight

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import com.sc.tframe.R
import kotlin.math.max
import kotlin.math.min

/**
 *
 * @author sicai.tang@geely.com
 * @since 2026/4/17
 */
class BatteryIconView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_BATTERY_BODY_RADIUS_DP = -1f
        private const val DEFAULT_BATTERY_HEAD_START_RADIUS_DP = -1f
        private const val DEFAULT_BATTERY_HEAD_END_RADIUS_DP = -1f
        private const val DEFAULT_MIN_BATTERY_LEVEL_DISPLAY = 20
    }

    /**
     * 电池电量 0-100
     */
    var batteryLevel: Int = 30
        set(value) {
            field = max(0, min(100, value))
            invalidate()
        }

    /**
     * 是否正在充电
     */
    var isCharging: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 电池外壳圆角半径（dp）
     */
    var batteryBodyRadiusDp: Float = DEFAULT_BATTERY_BODY_RADIUS_DP
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 电池头左侧圆角半径（dp）
     */
    var batteryHeadStartRadiusDp: Float = DEFAULT_BATTERY_HEAD_START_RADIUS_DP
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 电池头右侧圆角半径（dp）
     */
    var batteryHeadEndRadiusDp: Float = DEFAULT_BATTERY_HEAD_END_RADIUS_DP
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 剩余电量颜色
     */
    var remainingBatteryColor: Int = Color.parseColor("#ffffff")
        set(value) {
            field = value
            invalidate()
        }
    var chargingBatteryColor: Int = Color.parseColor("#2D9122")
        set(value) {
            field = value
            invalidate()
        }
    var minBatteryColor: Int = Color.parseColor("#ff0000")
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 已消耗电量颜色
     */
    var consumedBatteryColor: Int = Color.parseColor("#33000000")
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 电量过低时显示的最小电量值（避免显示异常）
     */
    var minBatteryLevelDisplay: Int = DEFAULT_MIN_BATTERY_LEVEL_DISPLAY
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 充电图标 Drawable
     */
    var chargingIconDrawable: Drawable? = null
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 充电图标颜色
     */
    var chargingIconColor: Int = Color.WHITE
        set(value) {
            field = value
            invalidate()
        }



    private val remainingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val consumedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val path = Path()
    private val rectF = RectF()

    init {
        context.withStyledAttributes(attrs, R.styleable.BatteryIconView) {
            batteryLevel = getInt(R.styleable.BatteryIconView_batteryLevel, 30)
            isCharging = getBoolean(R.styleable.BatteryIconView_isCharging, false)
            batteryBodyRadiusDp = getDimension(R.styleable.BatteryIconView_batteryBodyRadius, dpToPx(DEFAULT_BATTERY_BODY_RADIUS_DP))
            batteryHeadStartRadiusDp = getDimension(R.styleable.BatteryIconView_batteryHeadStartRadius, dpToPx(DEFAULT_BATTERY_HEAD_START_RADIUS_DP))
            batteryHeadEndRadiusDp = getDimension(R.styleable.BatteryIconView_batteryHeadEndRadius, dpToPx(DEFAULT_BATTERY_HEAD_END_RADIUS_DP))
            remainingBatteryColor = getColor(R.styleable.BatteryIconView_remainingBatteryColor, Color.parseColor("#ffffff"))
            chargingBatteryColor = getColor(R.styleable.BatteryIconView_chargingBatteryColor, Color.parseColor("#2D9122"))
            minBatteryColor = getColor(R.styleable.BatteryIconView_minBatteryColor, Color.parseColor("#ff0000"))
            consumedBatteryColor = getColor(R.styleable.BatteryIconView_consumedBatteryColor, Color.parseColor("#33000000"))
            minBatteryLevelDisplay = getInt(R.styleable.BatteryIconView_minBatteryLevelDisplay, DEFAULT_MIN_BATTERY_LEVEL_DISPLAY)
            chargingIconColor = getColor(R.styleable.BatteryIconView_chargingIconColor, Color.WHITE)
            
            // 从 XML 加载 Drawable
            val drawableResId = getResourceId(R.styleable.BatteryIconView_chargingIconDrawable, -1)
            if (drawableResId != -1) {
                chargingIconDrawable = androidx.core.content.ContextCompat.getDrawable(context, drawableResId)
            }
        }

        remainingPaint.color = remainingBatteryColor
        consumedPaint.color = consumedBatteryColor
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = dpToPx(100f).toInt() // 默认宽度
        val desiredHeight = dpToPx(40f).toInt() // 默认高度

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> min(desiredWidth, widthSize)
            else -> desiredWidth
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> min(desiredHeight, heightSize)
            else -> desiredHeight
        }

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val availableWidth = width - paddingLeft - paddingRight
        val availableHeight = height - paddingTop - paddingBottom
        // 计算电池区域宽度
        val batteryAreaWidth = availableWidth.toFloat()
        val batteryAreaHeight = availableHeight.toFloat()
        // 电池尺寸比例
        val totalBatteryWidth = min(batteryAreaWidth, batteryAreaHeight * (4f / 3f))
        val totalBatteryHeight = totalBatteryWidth * (24.4f / 40f)

        val batteryBodyWidth = totalBatteryWidth * (37.22f / 40f)
        val batterySpacerWidth = totalBatteryWidth * (0.56f / 40f)
        val batteryHeadWidth = totalBatteryWidth * (2.22f / 40f)
        val batteryHeadHeight = totalBatteryHeight * (7.78f / 24.4f)

        val startX = paddingLeft.toFloat()
        val startY = paddingTop + (availableHeight - totalBatteryHeight) / 2f

        // 绘制电池外壳（已消耗电量背景）
        val batteryBodyRadius = if (batteryBodyRadiusDp >= 0) {
            dpToPx(batteryBodyRadiusDp)
        } else totalBatteryHeight * (7f / 24.4f)

        rectF.set(startX, startY, startX + batteryBodyWidth, startY + totalBatteryHeight)
        canvas.drawRoundRect(rectF, batteryBodyRadius, batteryBodyRadius, consumedPaint)

        // 计算剩余电量显示
        val isFullyCharged = batteryLevel == 100 // 是否充满

        val uiBatteryLevel = if (batteryLevel in 0 until minBatteryLevelDisplay) {
            remainingPaint.color = minBatteryColor
            batteryLevel // minBatteryLevelDisplay
        } else {
            if (isCharging)  remainingPaint.color = chargingBatteryColor
            else remainingPaint.color = remainingBatteryColor
            batteryLevel
        }
        val remainingBatteryWidth = batteryBodyWidth * uiBatteryLevel / 100f
        val remainingSmall = remainingBatteryWidth < batteryBodyRadius
        val remainingNearFull = remainingBatteryWidth > batteryBodyWidth - batteryBodyRadius
        // 计算右侧实际可用的圆角半径（不能超过剩余宽度减去左侧圆角）
        val batteryBodyEndRadius = if (isFullyCharged) batteryBodyRadius else 0f
        // 绘制剩余电量
        path.reset()

        val left = startX
        val top = startY
        val right = startX + remainingBatteryWidth
        val bottom = startY + totalBatteryHeight

        if (remainingSmall || remainingNearFull) {
            // 情况1：电量很小，需要裁剪左侧圆角
            // 情况2：电量接近充满，需要裁剪右侧圆角
            // 创建完整的电池外壳路径作为裁剪区域
            val clipPath = Path()
            rectF.set(startX, startY, startX + batteryBodyWidth, startY + totalBatteryHeight)
            clipPath.addRoundRect(rectF, batteryBodyRadius, batteryBodyRadius, Path.Direction.CW)
                    
            // 创建剩余电量的矩形路径
            val remainingPath = Path()
            remainingPath.addRect(left, top, right, bottom, Path.Direction.CW)
                    
            // 使用布尔运算求交集，确保左侧圆角与底部圆角完美贴合
            path.op(clipPath, remainingPath, Path.Op.INTERSECT)
        } else {
            // 情况3：电量足够显示完整左侧圆角
            // 左上圆角
            path.moveTo(left, top + batteryBodyRadius)
            path.quadTo(left, top, left + batteryBodyRadius, top)

            // 右侧处理
            if (isFullyCharged) {
                // 右侧有圆角
                path.lineTo(right - batteryBodyEndRadius, top)
                path.quadTo(right, top, right, top + batteryBodyEndRadius)
                path.lineTo(right, bottom - batteryBodyEndRadius)
                path.quadTo(right, bottom, right - batteryBodyEndRadius, bottom)
            } else {
                // 右侧直角
                path.lineTo(right, top)
                path.lineTo(right, bottom)
            }

            // 左下圆角
            path.lineTo(left + batteryBodyRadius, bottom)
            path.quadTo(left, bottom, left, bottom - batteryBodyRadius)
        }


        path.close()
        canvas.drawPath(path, remainingPaint)

        // 绘制充电图标
        if (isCharging && chargingIconDrawable != null) {
            val chargingIconHeight = totalBatteryHeight * 0.5f
            val chargingIconWidth = chargingIconHeight // 保持正方形比例
            val chargingIconOffsetX = startX + (batteryBodyWidth - chargingIconWidth) / 2f
            val chargingIconOffsetY = startY + (totalBatteryHeight - chargingIconHeight) / 2f
            
            // 设置图标边界和颜色
            chargingIconDrawable!!.setBounds(
                chargingIconOffsetX.toInt(),
                chargingIconOffsetY.toInt(),
                (chargingIconOffsetX + chargingIconWidth).toInt(),
                (chargingIconOffsetY + chargingIconHeight).toInt()
            )
            
            // 应用颜色滤镜
            chargingIconDrawable!!.setTint(chargingIconColor)
            
            // 绘制图标
            chargingIconDrawable!!.draw(canvas)
        }

        // 绘制电池头
        val batteryHeadStartRadius = if (batteryHeadStartRadiusDp >= 0) dpToPx(batteryHeadStartRadiusDp)
            else batteryHeadWidth * (0.5f / 2.2f)
        val batteryHeadEndRadius =  if (batteryHeadEndRadiusDp >= 0) dpToPx(batteryHeadEndRadiusDp)
        else batteryHeadWidth * (1.7f / 2.2f)
        val batteryHeadOffsetX = startX + batteryBodyWidth + batterySpacerWidth
        val batteryHeadOffsetY = startY + totalBatteryHeight / 2f - batteryHeadHeight / 2f
        // 画笔设置
        val batteryHeadColor = if (isFullyCharged) remainingPaint.color else consumedPaint.color
        val headPaint = if (isFullyCharged) remainingPaint else consumedPaint
        headPaint.color = batteryHeadColor
        // 路径绘制
        path.reset()
        val headLeft = batteryHeadOffsetX
        val headTop = batteryHeadOffsetY
        val headRight = batteryHeadOffsetX + batteryHeadWidth
        val headBottom = batteryHeadOffsetY + batteryHeadHeight

        // 左上圆角
        path.moveTo(headLeft, headTop + batteryHeadStartRadius)
        path.quadTo(headLeft, headTop, headLeft + batteryHeadStartRadius, headTop)

        // 右上圆角
        path.lineTo(headRight - batteryHeadEndRadius, headTop)
        path.quadTo(headRight, headTop, headRight, headTop + batteryHeadEndRadius)

        // 右下圆角
        path.lineTo(headRight, headBottom - batteryHeadEndRadius)
        path.quadTo(headRight, headBottom, headRight - batteryHeadEndRadius, headBottom)

        // 左下圆角
        path.lineTo(headLeft + batteryHeadStartRadius, headBottom)
        path.quadTo(headLeft, headBottom, headLeft, headBottom - batteryHeadStartRadius)

        path.close()
        canvas.drawPath(path, headPaint)


    }

    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }

    private fun spToPx(sp: Float): Float {
        return sp * resources.displayMetrics.scaledDensity
    }

    private fun pxToSp(px: Float): Float {
        return px / resources.displayMetrics.scaledDensity
    }
}