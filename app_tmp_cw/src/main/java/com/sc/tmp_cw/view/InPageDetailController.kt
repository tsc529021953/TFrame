package com.sc.tmp_cw.view

import android.animation.Animator
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.nbhope.lib_frame.bean.FileBean
import com.nbhope.lib_frame.utils.FileUtil
import com.sc.tmp_cw.R
import com.sc.tmp_cw.utils.TextReveal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/** 页内详情浮层的视图集合(各fragment布局中同名浮层) */
class DetailOverlayViews(
        val rootLy: View,
        val iv: ImageView,
        val titleTv: TextView,
        val subtitleTv: TextView,
        val underlineV: View,
        val textTv: TextView
)

/**
 * 方案2(flavorB)页内详情浮层控制器:卡片图飞入 + 标题/下划线/正文逐字浮现
 * CateFragment与TravelFragment共用
 */
class InPageDetailController(
        private val fragment: Fragment,
        private val scope: CoroutineScope,
        private val views: DetailOverlayViews
) {

    private val mainHandler = Handler(Looper.getMainLooper())

    /** 文字浮现动画,关闭时统一取消 */
    private val revealAnims = mutableListOf<Animator>()

    /** 飞行图片(独立放大层) */
    private var flyIv: ImageView? = null

    /** 浮层打开时间点,用于正文浮现不早于飞行落位 */
    private var shownAt = 0L

    private val noIntroduceMsg: String by lazy { fragment.getString(R.string.no_introduce) }

    fun isShown(): Boolean = views.rootLy.visibility == View.VISIBLE

    fun show(item: FileBean, bigList: List<FileBean>?, textList: List<FileBean>?, cardIv: View?) {
        if (isShown()) return
        shownAt = SystemClock.uptimeMillis()
        // 背景浮现(减速曲线收尾,慢一点不突兀)
        views.rootLy.alpha = 0f
        views.rootLy.visibility = View.VISIBLE
        views.rootLy.animate().alpha(1f).setDuration(550)
                .setInterpolator(DecelerateInterpolator()).start()
        // 大图:优先bigList大图,无则用卡片图(此时落位后直接硬切,不做交叉渐变);
        // 小图作占位避免大图未就绪时空白
        val big = bigList?.find { it.name.startsWith(item.name) }
        views.iv.alpha = 0f
        Glide.with(fragment)
                .load("file://" + (big ?: item).path)
                .override(960, 540)
                .centerInside()
                .transform(RoundedCorners(24))
                .thumbnail(Glide.with(fragment)
                        .load("file://" + item.path)
                        .dontAnimate()
                        .transform(RoundedCorners(24)))
                .into(views.iv)
        // 标题/副标题/竖线/正文:统一在txt读取完成后赋值并浮现(见loadText)
        loadText(item, textList)
        // 飞行图片:等浮层完成布局后再取落位坐标
        views.rootLy.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                views.rootLy.viewTreeObserver.removeOnGlobalLayoutListener(this)
                if (!isShown()) return
                // 飞行仅在卡片图就绪时进行,否则大图直接淡入
                if (cardIv !is ImageView || !flyCardToDetail(cardIv, big != null)) {
                    views.iv.animate().alpha(1f).setDuration(600).start()
                }
            }
        })
    }

    /**
     * 标题/副标题/正文:异步读取textList文件(按名称前缀匹配),
     * 第一行→标题,第二行→副标题,其余→正文;
     * 无txt(或读取失败)时标题用图片名、副标题为空字符串。
     * 读取完成后依次浮现(标题不早于450ms,正文不早于850ms)
     */
    private fun loadText(item: FileBean, textList: List<FileBean>?) {
        views.titleTv.text = ""
        views.subtitleTv.text = ""
        views.textTv.text = ""
        scope.launch(Dispatchers.IO) {
            val textItem = textList?.find { it.name.startsWith(item.name) }
            var title: String? = null
            var subtitle: String? = null
            var content: String? = null
            if (textItem != null) {
                val raw = try {
                    FileUtil.readFile(textItem.path)
                } catch (e: Exception) {
                    Timber.e(e, "读取文件失败: ${textItem.path}")
                    null
                }
                if (!raw.isNullOrEmpty()) {
                    val lines = raw.lines()
                    title = lines.firstOrNull()?.trim()?.takeIf { it.isNotEmpty() }
                    subtitle = lines.getOrNull(1)?.trim()?.takeIf { it.isNotEmpty() }
                    content = lines.drop(2).joinToString("\n").trim().takeIf { it.isNotEmpty() }
                }
            }
            mainHandler.post {
                if (!fragment.isAdded || !isShown()) return@post
                views.titleTv.text = title ?: item.name
                revealAnims.add(TextReveal.revealText(views.titleTv, 350, 450))
                TextReveal.growFromTop(views.underlineV, 500, 450)
                views.subtitleTv.text = subtitle ?: ""
                revealAnims.add(TextReveal.revealText(views.subtitleTv, 500, 600))
                if (content != null) {
                    views.textTv.text = content
                    val delay = (shownAt + 850 - SystemClock.uptimeMillis()).coerceAtLeast(0)
                    revealAnims.add(TextReveal.revealText(views.textTv, delay, 1200))
                } else {
                    views.textTv.text = noIntroduceMsg
                    revealAnims.add(TextReveal.revealText(views.textTv, 850, 1200))
                }
            }
        }
    }

    /**
     * 卡片图从原位放大飞向详情大图位置(带轻微回弹),落位后与真实大图交叉淡入。
     * 起点取卡片图在卡片内的实际显示矩形(剔除fitCenter留白);终点按图片宽高比
     * 推算大图在detailIv中的centerInside显示矩形,保证落位与真实显示位置一致,
     * 交叉淡入时不发生尺寸突变
     * @param crossFade true=有bigList大图,落位后飞行层与大图1s交叉渐变;
     *                  false=无big,底层即卡片图本身,落位后直接硬切
     * @return true表示飞行已启动;false表示卡片图未就绪,由调用方兜底
     */
    private fun flyCardToDetail(cardIv: ImageView, crossFade: Boolean): Boolean {
        if (!fragment.isAdded) return false
        val drawable = cardIv.drawable ?: return false
        val dW = drawable.intrinsicWidth
        val dH = drawable.intrinsicHeight
        if (dW <= 0 || dH <= 0) return false
        val constantState = drawable.constantState ?: return false
        // 卡片图实际显示矩形(卡片视图坐标,已含fitCenter缩放)
        val cardContent = RectF(0f, 0f, dW.toFloat(), dH.toFloat())
        cardIv.imageMatrix.mapRect(cardContent)
        val flyW = cardContent.width()
        val flyH = cardContent.height()
        if (flyW <= 0f || flyH <= 0f) return false
        val fly = ImageView(fragment.requireContext())
        fly.setImageDrawable(constantState.newDrawable(fragment.resources))
        (views.rootLy as? ViewGroup)?.addView(fly, FrameLayout.LayoutParams(flyW.toInt(), flyH.toInt()))
        flyIv = fly
        // 卡片显示矩形换算到浮层根坐标
        val rootLoc = IntArray(2)
        views.rootLy.getLocationOnScreen(rootLoc)
        val cardLoc = IntArray(2)
        cardIv.getLocationOnScreen(cardLoc)
        fly.x = cardLoc[0] - rootLoc[0] + cardContent.left
        fly.y = cardLoc[1] - rootLoc[1] + cardContent.top
        fly.pivotX = flyW / 2f
        fly.pivotY = flyH / 2f
        // 目标:大图在detailIv内的centerInside内容矩形(按卡片图宽高比推算)
        val ivLoc = IntArray(2)
        views.iv.getLocationOnScreen(ivLoc)
        val vw = views.iv.width.toFloat()
        val vh = views.iv.height.toFloat()
        val ratio = dW.toFloat() / dH
        val tw: Float
        val th: Float
        if (vw / vh > ratio) {
            tw = vh * ratio
            th = vh
        } else {
            tw = vw
            th = vw / ratio
        }
        val targetLeft = ivLoc[0] - rootLoc[0] + (views.iv.width - tw) / 2f
        val targetTop = ivLoc[1] - rootLoc[1] + (views.iv.height - th) / 2f
        // 等比缩放到落位尺寸(宽高比一致),平移使内容矩形对齐目标
        fly.animate()
                .scaleX(tw / flyW)
                .scaleY(th / flyH)
                .x(targetLeft + (tw - flyW) / 2f)
                .y(targetTop + (th - flyH) / 2f)
                .setDuration(750)
                .setInterpolator(OvershootInterpolator(0.05f))
                .withEndAction {
                    // 落位矩形与真实大图显示区重合:
                    // 有big→飞行层渐隐、真实大图渐显(1s交叉);
                    // 无big→底层与飞行层是同一张图,直接硬切
                    if (crossFade) {
                        crossFadeWhenImageReady(fly)
                    } else {
                        views.iv.alpha = 1f
                        removeFlyWhenImageReady(fly)
                    }
                }
                .start()
        return true
    }

    /** 关闭浮层(淡出,加速曲线收尾) */
    fun close() {
        if (!isShown()) return
        cancelAnims()
        views.rootLy.animate().alpha(0f).setDuration(550)
                .setInterpolator(AccelerateInterpolator())
                .withEndAction {
                    reset()
                }.start()
    }

    /** 直接收起浮层,不播动画(页面切走/定时器到达) */
    fun reset() {
        cancelAnims()
        views.rootLy.animate().cancel()
        views.iv.animate().cancel()
        flyIv?.let { removeFlyView(it) }
        views.rootLy.visibility = View.GONE
        views.rootLy.alpha = 1f
        views.iv.alpha = 0f
        views.textTv.text = ""
        views.titleTv.text = ""
        views.subtitleTv.text = ""
        if (fragment.isAdded) Glide.with(fragment).clear(views.iv)
    }

    /** 供宿主fragment实现TimerIntercept时调用:浮层打开时先关浮层,返回true表示已消费 */
    fun onTimerReach(): Boolean {
        if (!isShown()) return false
        reset()
        return true
    }

    private fun cancelAnims() {
        revealAnims.forEach { it.cancel() }
        revealAnims.clear()
        flyIv?.animate()?.cancel()
    }

    /**
     * 交叉渐变:飞行层渐隐、真实大图同步渐显(1s)。大图已就绪才开始(否则渐显的是空白);
     * 未就绪时飞行层暂留(位置与大图显示区重合,视觉无差),待drawable出现的第一帧再开始
     */
    private fun crossFadeWhenImageReady(fly: ImageView) {
        if (views.iv.drawable != null) {
            crossFade(fly)
            return
        }
        views.iv.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                if (views.iv.drawable == null) return true
                views.iv.viewTreeObserver.removeOnPreDrawListener(this)
                if (flyIv === fly) crossFade(fly)
                return true
            }
        })
    }

    /**
     * 飞行层渐隐 + 大图渐显,1s同步,结束后移除飞行层。
     * 注意:两层同时半透明期间叠加透明度低于1,紫色背景会轻微透出(中点约25%),
     * 若观感发暗可改回"大图先垫底、只渐隐飞行层"的做法
     */
    private fun crossFade(fly: ImageView) {
        views.iv.animate()
                .alpha(1f)
                .setDuration(1000)
                .setInterpolator(DecelerateInterpolator())
                .start()
        // fly.animate()复用移动动画的ViewPropertyAnimator,需重置插值器,
        // 否则残留Overshoot会让alpha冲过0再回弹,表现为渐隐结尾闪一下
        fly.animate()
                .alpha(0f)
                .setDuration(1000)
                .setInterpolator(DecelerateInterpolator())
                .withEndAction {
                    removeFlyView(fly)
                }
                .start()
    }

    /**
     * 无big时的落位处理:底层即卡片图本身,drawable就绪后立即移除飞行层
     * (两者是同一张图,硬切无视觉差异,交叉渐变反而会无谓地变暗)
     */
    private fun removeFlyWhenImageReady(fly: ImageView) {
        if (views.iv.drawable != null) {
            removeFlyView(fly)
            return
        }
        views.iv.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                if (views.iv.drawable == null) return true
                views.iv.viewTreeObserver.removeOnPreDrawListener(this)
                if (flyIv === fly) removeFlyView(fly)
                return true
            }
        })
    }

    private fun removeFlyView(fly: ImageView) {
        (views.rootLy as? ViewGroup)?.takeIf { fly.parent === it }?.removeView(fly)
        if (flyIv === fly) flyIv = null
    }
}
