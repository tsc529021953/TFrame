package com.sc.tmp_cw.view

import android.os.Environment
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.nbhope.lib_frame.base.BaseBindingFragment
import com.nbhope.lib_frame.base.BaseViewModel
import com.sc.tmp_cw.R
import com.sc.tmp_cw.constant.MessageConstant
import com.sc.tmp_cw.databinding.FragmentLineBinding
import timber.log.Timber
import java.io.File

/**
 * @author  tsc
 * @date  2025/1/6 19:38
 * @version 0.0.0-1
 * @description
 */
class LineFragment: BaseBindingFragment<FragmentLineBinding, BaseViewModel>() {

    override var layoutId: Int = R.layout.fragment_line

    // 保存外部图片路径，用于重新显示时重新加载
    private var imagePath: String? = null

    companion object {
        /** 初始显示比例：在完整显示的最小比例基础上放大 1.8 倍 */
        private const val INITIAL_SCALE_FACTOR = 1.8f
    }

    override fun linkViewModel() {

    }


    override fun subscribeUi() {
        binding.backBtn.setOnClickListener {
            InteractiveFragment.iFragment?.back()
        }

        // 双指缩放/拖动/双击放大由控件自身处理（瓦片式分块解码，任意倍率清晰），
        // 触摸事件只用来重置页面无操作定时器
        binding.lineIv.setOnTouchListener { view, motionEvent ->
            InteractiveFragment.iFragment?.clicked()
            return@setOnTouchListener false
        }
        // 放大上限与原 ZoomView 一致（5 倍）；最小比例默认 CENTER_INSIDE，完整显示、长边贴边
        binding.lineIv.maxScale = 5f

        val path = Environment.getExternalStorageDirectory().absolutePath + MessageConstant.PATH_LINE

        // 判断路径下的图片是否存在
        val imageFile = File(path)
        Timber.i("imageFile: ${imageFile.path} ${imageFile.exists()} ${imageFile.isFile}")
        imagePath = if (imageFile.exists() &&imageFile.isFile) imageFile.path else null

        loadImage()
    }

    /**
     * 加载大图：外部 8K 图走瓦片式分块解码（初始按整屏解码，放大时按当前缩放级别
     * 解码对应区域的高清块，任意倍率都清晰且不 OOM，与系统图库同原理）；
     * 文件不存在时回退内置线路图
     */
    private fun loadImage() {
        val path = imagePath
        // 图片就绪后才能拿到 minScale/center，此时设初始缩放为 1.8 倍
        binding.lineIv.setOnImageEventListener(object : SubsamplingScaleImageView.DefaultOnImageEventListener() {
            override fun onImageLoaded() {
                applyInitialScale()
            }
        })
        if (path != null) {
            binding.lineIv.setImage(ImageSource.uri(path))
        } else {
            binding.lineIv.setImage(ImageSource.resource(R.mipmap.ic_line_ly))
        }
    }

    /** 以完整显示的最小比例为基准放大 1.8 倍，中心点保持图片中心 */
    private fun applyInitialScale() {
        val view = binding.lineIv
        view.setScaleAndCenter(view.minScale * INITIAL_SCALE_FACTOR, view.center)
    }

    override fun initData() {

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            // Fragment 重新显示时重置缩放位置（图片数据仍在内存，无需重新解码）
            binding.lineIv.resetScaleAndCenter()
            applyInitialScale()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 位图随控件 onDetachedFromWindow 自动回收，无需手动清理
    }

}
