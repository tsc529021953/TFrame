package com.sc.lib_float.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.sc.lib_float.*
import com.sc.lib_float.bean.PaintItem
import com.sc.lib_float.enum.PaintState
import timber.log.Timber


/**
 * @author  tsc
 * @date  2024/4/7 10:02
 * @version 0.0.0-1
 * @description
 * 可回退10步
 * 10步之前的存储到bitmap
 *
 */
class DrawView : View {

    companion object {
        const val MAX_POINT = 5

        const val DOWN_VALUE = 261
        val DOWN_LIST = arrayListOf<Int>()
        val UP_LIST = arrayListOf<Int>()
    }

    constructor(context: Context?) : this(context, null, -1, -1)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, -1, -1)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, -1)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        initPenPaint()
//        path = Path()
    }

    var screenWidth = 0
    var screenHeight = 0

    private lateinit var penPaint: Paint
    private lateinit var eraserPaint: Paint
//    private lateinit var path: Path
//    var preX = 0f
//    var preY = 0f

    var cacheBitmap: Bitmap? = null

    //定义缓冲区Cache的Canvas对象
    var cacheCanvas: Canvas? = null
    var recordBitmaps = ArrayList<Bitmap>()
    var recordPaintItems = ArrayList<PaintItem>()
    var currentPaintItems = ArrayList<PaintItem>()

    /*标记*/
    var paintState = PaintState.PAINTING

    fun initPenPaint() {
//        val cornerPathEffect = CornerPathEffect(200F)
        penPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//        penPaint.setPathEffect(cornerPathEffect)
        penPaint.setColor(Color.WHITE)
        penPaint.isDither = true
        penPaint.setFlags(Paint.DITHER_FLAG)
        penPaint.setStyle(Paint.Style.STROKE)
        penPaint.strokeJoin = Paint.Join.ROUND
        penPaint.strokeCap = Paint.Cap.ROUND
        penPaint.setStrokeWidth(15F)
        penPaint.setAntiAlias(true)

        eraserPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        eraserPaint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.CLEAR))
        eraserPaint.isDither = true
        eraserPaint.setFlags(Paint.DITHER_FLAG)
        eraserPaint.setStyle(Paint.Style.STROKE)
        eraserPaint.strokeJoin = Paint.Join.ROUND
        eraserPaint.strokeCap = Paint.Cap.ROUND
        eraserPaint.setStrokeWidth(15F)
        eraserPaint.setAntiAlias(true)

        // 初始多点触摸数据
        for (i in 0 until MAX_POINT) {
            val code = i * DOWN_VALUE - (if (i == 0) 0 else (i - 1)) * 5
            DOWN_LIST.add(code)
            UP_LIST.add(code + 1)
        }
        //
        val dm = context.resources.displayMetrics
        screenWidth = dm.widthPixels
        screenHeight = dm.heightPixels
        cacheBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        cacheCanvas = Canvas()
        cacheCanvas?.setBitmap(cacheBitmap)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event!!.x
        val y = event!!.y
//        Timber.i("KTAG onTouchEvents ${event!!.action}")
        when {
            MotionEvent.ACTION_POINTER_DOWN == event!!.action ||
                    DOWN_LIST.contains(event!!.action) -> {
                onDown(event)
            }

            MotionEvent.ACTION_MOVE == event!!.action -> {
                currentPaintItems.forEach {
                    val pointerIndex = event.findPointerIndex(it.pointerId)
                    if (pointerIndex < 0) return true
                    val x = event.getX(pointerIndex)
                    val y = event.getY(pointerIndex)
                    it.path?.quadTo(it.x, it.y, x, y)
                    it.x = x
                    it.y = y
                }
            }
            MotionEvent.ACTION_UP == event!!.action ||
                    MotionEvent.ACTION_CANCEL == event!!.action ||
                    MotionEvent.ACTION_POINTER_UP == event!!.action ||
                    UP_LIST.contains(event!!.action) -> {
                onUp(event)
            }
        }
        invalidate() //在UI线程刷新VIew

        return true
    }

    fun onDown(event: MotionEvent?) {
        if (event == null) return
        if (currentPaintItems.size < MAX_POINT) {
            val pointerId = event!!.getPointerId(event!!.actionIndex)
            val pointerIndex = event!!.findPointerIndex(pointerId)
            val x = event!!.getX(pointerIndex)
            val y = event!!.getY(pointerIndex)
            val paintItem =
                PaintItem(if (paintState == PaintState.PAINTING) penPaint else eraserPaint, Path(), pointerId)
            paintItem.x = x
            paintItem.y = y
            paintItem.path?.moveTo(x, y)
//            Timber.i(paintItem.toString())
            currentPaintItems.add(paintItem)
        }
    }

    fun onUp(event: MotionEvent?) {
        val pointerId = event!!.getPointerId(event!!.actionIndex)
        val item = currentPaintItems.find { it.pointerId == pointerId }
        if (item != null) {
            recordPaintItems.add(item)
            if (recordPaintItems.size == MAX_POINT * 2) {
                // 超限，取出前20个存到bitmap
                Timber.i("KTAG 超限")
                for (i in 0 until MAX_POINT) {
                    val item2 = recordPaintItems.get(i)
                    if (item2.path != null && item2.paint != null)
                        cacheCanvas?.drawPath(item2.path!!,item2.paint!!)
                }
                recordPaintItems = ArrayList(recordPaintItems.subList(MAX_POINT, recordPaintItems.size))
            }
            currentPaintItems.remove(item)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
//        canvas?.drawPath(path, penPaint)
        val p = Paint()
        //将cacheBitmap绘制到该View
        //将cacheBitmap绘制到该View
        canvas?.drawBitmap(cacheBitmap!!, 0F, 0F, p)
        recordPaintItems.forEach {
            if (it.paint != null && it.path != null)
                canvas?.drawPath(it.path!!, it.paint!!)
        }
        currentPaintItems.forEach {
            if (it.paint != null && it.path != null)
                canvas?.drawPath(it.path!!, it.paint!!)
        }
        Timber.i("KTAG onDraw ${recordPaintItems.size} ${currentPaintItems.size}")
    }

    fun pen() {
        paintState = PaintState.PAINTING
    }

    // 图片加载

    // 设置背景颜色

    // 画笔类型

    // 画笔粗细

    // 画笔颜色

    // 橡皮
    fun eraser() {
        paintState = PaintState.ERASER
    }

    // 橡皮大小

    // 图像清除
    fun clear() {
        currentPaintItems.clear()
        recordPaintItems.clear()
        cacheCanvas?.drawColor(0,PorterDuff.Mode.CLEAR)
        invalidate()
    }

    // 图片输出

    //
}
