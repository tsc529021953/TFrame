package com.sc.tmp_cw.view

import android.os.Environment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.nbhope.lib_frame.base.BaseBindingFragment
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.view.ZoomView
import com.sc.tmp_cw.MainActivity
import com.sc.tmp_cw.R
import com.sc.tmp_cw.constant.MessageConstant
import com.sc.tmp_cw.databinding.FragmentLineBinding
import com.sc.tmp_cw.databinding.FragmentTravelBinding
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
    
    // 保存加载参数，用于重新显示时重新加载
    private var loadImageSource: Any? = null
    private lateinit var requestOptions: RequestOptions

    override fun linkViewModel() {

    }


    override fun subscribeUi() {
        binding.backBtn.setOnClickListener {
            InteractiveFragment.iFragment?.back()
        }

        requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(false) // 启用内存缓存
            .format(DecodeFormat.PREFER_RGB_565) // 使用 RGB_565 减少内存占用（不需要透明度）
//            .placeholder(R.drawable.ic_launcher_foreground)
//        .format(DecodeFormat.PREFER_RGB_565)  //设置为这种格式去掉透明度通道，可以减少内存占有
//        .placeholder(R.drawable.img_error)
//            .error(R.drawable.img_error)

        var path = Environment.getExternalStorageDirectory().absolutePath + MessageConstant.PATH_LINE
                
        // 判断路径下的图片是否存在
        val imageFile = File(path)
        Timber.i("imageFile: ${imageFile.path} ${imageFile.exists()} ${imageFile.isFile}")
        loadImageSource = if (imageFile.exists() && imageFile.isFile) {
            Timber.i("文件加载")
            // 文件存在，加载外部存储的图片
            path
        } else {
            // 文件不存在，使用本地图片
            R.mipmap.ic_line_ly
        }
        
        // 加载图片
        loadGlideImage()
        
        binding.zoom.setOnTouchListener { view, motionEvent ->
            InteractiveFragment.iFragment?.clicked()
            return@setOnTouchListener false
        }
    }
    
    /**
     * 加载 Glide 图片
     */
    private fun loadGlideImage() {
        loadImageSource?.let { source ->
            // 延迟加载，确保视图已准备好
            view?.post {
                if (!isDetached && isAdded) {
                    Glide.with(this)
                        .load(source)
                        .apply(requestOptions)
                        .override(3840, 2160) // 根据实际显示尺寸调整，避免加载过大的图片
                        .centerInside() // 保持比例居中显示
                        .placeholder(android.R.color.transparent) // 透明占位，避免闪烁
                        .skipMemoryCache(true) // 跳过内存缓存
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // 禁用磁盘缓存
                        .into(binding!!.lineIv)
                }
            }
        }
    }

    override fun initData() {

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            // Fragment 重新显示时，重置缩放并重新加载图片
            (binding.zoom as ZoomView).reset()
            // 由于使用了缓存，重新加载会很快从缓存读取
            loadGlideImage()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        // Fragment 销毁时清理图片资源
        Glide.with(this).clear(binding.lineIv)
    }

}
