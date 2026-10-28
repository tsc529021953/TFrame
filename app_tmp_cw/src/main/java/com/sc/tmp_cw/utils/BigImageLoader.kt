package com.sc.tmp_cw.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import timber.log.Timber
import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * 8K 大图加载工具（LineFragment / StationInfoSearchFragment 共用）
 *
 * 目标：完整贴边显示、加载流畅、不 OOM：
 * - 完整：等比解码（fitCenter）不裁剪，整张图都能看到；图片比例与屏幕一致时用
 *   FIT_XY 拉伸贴满（视觉无变形），比例差异大时等比完整显示、长边贴边；
 * - 防 OOM：RGB_565（内存减半）+ 按屏幕面积 1~2 倍降采样解码，8K 原图不会整张进内存；
 * - 提速：先读图片边界，若原图足够大则把解码目标压到原图的一半，BitmapFactory 会走
 *   inSampleSize=2 采样解码（约 4 倍速度），避免 8K 全量解码后再缩小；
 * - 流畅：降采样结果以 JPEG 写入磁盘缓存并启用内存缓存，二次加载只需解码小图；
 *   外部文件签名（修改时间+大小），客户替换 /TMP 下的图片后缓存自动失效重新解码；
 * - 预加载：[preload] 在页面打开前提前解码进缓存，进入页面直接命中秒出
 *   （options 必须与 [load] 完全一致才能命中同一条缓存）。
 *
 * 注：曾按"图库直出"思路试过 ARGB_8888 + 按显示尺寸 1:1 解码 + PNG 无损缓存，
 * 实机反而更糊（已回退）；此版本为实测显示正常、客户认可的方案，勿随意改动解码策略。
 */
object BigImageLoader {

    /** 解码目标相对屏幕尺寸的倍数上限，越大越清晰、越耗内存越慢 */
    private const val MAX_SCALE = 2

    /** 解码像素总量上限（约 5120*3200，RGB_565 下约 32MB），防止异常尺寸把内存顶爆 */
    private const val MAX_DECODE_PIXELS = 5120L * 3200

    /**
     * 图片宽高比与屏幕宽高比相差在此比例内（2%）时直接 FIT_XY 拉伸贴满四边，
     * 视觉上无变形。客户若要求比例不同也强制贴满无空边，调大该值即可（1 = 全部拉伸）。
     */
    private const val FIT_XY_ASPECT_TOLERANCE = 0.02f

    /**
     * 加载一张可缩放大图到 [target]，完整显示（不裁剪），比例接近时贴满四边。
     *
     * @param source Glide 数据源（外部文件路径 / 资源 id）
     * @param file   source 对应的外部文件，非空时参与缓存签名，文件替换后自动重新加载
     */
    fun load(fragment: Fragment, target: ImageView, source: Any, file: File? = null) {
        // 延迟到布局完成后执行，保证视图已就绪
        fragment.view?.post {
            if (fragment.isDetached || !fragment.isAdded) return@post
            target.scaleType = chooseScaleType(target, source, file)
            Glide.with(fragment)
                .load(source)
                .apply(buildOptions(target.context, source, file))
                .into(target)
        }
    }

    /**
     * 提前解码并写入缓存（在页面入口处调用，用户点进页面前已解码完成，打开即显示）。
     * 仅对存在的外部文件调用。
     */
    fun preload(context: Context, path: String) {
        try {
            val file = File(path)
            if (!file.exists() || !file.isFile) return
            Glide.with(context)
                .load(path)
                .apply(buildOptions(context, path, file))
                .preload()
        } catch (e: Exception) {
            Timber.e(e, "preload big image fail: $path")
        }
    }

    /**
     * 显示方式：图片与控件宽高比接近时 FIT_XY 拉伸贴满四边（无变形），
     * 否则 FIT_CENTER 等比完整显示（不裁剪，长边贴边，短边居中）。
     */
    private fun chooseScaleType(target: ImageView, source: Any, file: File?): ImageView.ScaleType {
        val srcSize = readSourceSize(source, file) ?: return ImageView.ScaleType.FIT_CENTER
        if (target.width <= 0 || target.height <= 0) return ImageView.ScaleType.FIT_CENTER
        val srcAspect = srcSize.first.toFloat() / srcSize.second
        val viewAspect = target.width.toFloat() / target.height
        return if (abs(srcAspect - viewAspect) / viewAspect < FIT_XY_ASPECT_TOLERANCE) {
            ImageView.ScaleType.FIT_XY
        } else {
            ImageView.ScaleType.FIT_CENTER
        }
    }

    /**
     * 构建与 load/preload 完全一致的 RequestOptions（保证命中同一条缓存）。
     * 统一按屏幕尺寸计算目标（本工具只用于整屏大图页），与具体控件测量无关。
     */
    private fun buildOptions(context: Context, source: Any, file: File?): RequestOptions {
        val dm = context.resources.displayMetrics
        val screenW = dm.widthPixels
        val screenH = dm.heightPixels
        // 默认目标 = 屏幕 * MAX_SCALE；若原图够大，则压到原图一半，让 BitmapFactory
        // 走 inSampleSize=2 直接采样解码（解码速度约 4 倍），解码量约为原来的 1/4
        var scale = MAX_SCALE.toFloat()
        val srcSize = readSourceSize(source, file)
        if (srcSize != null) {
            val sampleScale = min(
                srcSize.first / (2f * screenW),
                srcSize.second / (2f * screenH)
            )
            scale = max(1f, min(MAX_SCALE.toFloat(), sampleScale))
        }
        var dw = (screenW * scale).toLong()
        var dh = (screenH * scale).toLong()
        if (dw * dh > MAX_DECODE_PIXELS) {
            val ratio = kotlin.math.sqrt(MAX_DECODE_PIXELS.toDouble() / (dw * dh))
            dw = (dw * ratio).toLong()
            dh = (dh * ratio).toLong()
        }
        val options = RequestOptions()
            .format(DecodeFormat.PREFER_RGB_565)
            .fitCenter() // 完整：等比解码不裁剪，整张图可见
            .override(dw.toInt(), dh.toInt())
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE) // 只缓存降采样结果，不重复缓存 8K 原图
            .encodeFormat(Bitmap.CompressFormat.JPEG) // JPEG 编解码远快于 PNG，二次加载更快
            .encodeQuality(90)
            .skipMemoryCache(false)
            .placeholder(android.R.color.transparent)
        if (file != null) {
            // 外部文件可能被替换，签名带上修改时间+大小，替换后自动失效缓存
            options.signature(ObjectKey("${file.lastModified()}_${file.length()}"))
        }
        return options
    }

    /** 只读图片边界（不解码像素），拿原图宽高用于计算采样尺寸和显示比例 */
    private fun readSourceSize(source: Any, file: File?): Pair<Int, Int>? {
        val path = when {
            file != null -> file.path
            source is String -> source
            else -> null
        } ?: return null
        return try {
            val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(path, opts)
            if (opts.outWidth > 0 && opts.outHeight > 0) opts.outWidth to opts.outHeight else null
        } catch (e: Exception) {
            Timber.e(e, "read image bounds fail: $path")
            null
        }
    }
}
