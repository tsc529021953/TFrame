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

    override fun linkViewModel() {

    }


    override fun subscribeUi() {
        binding.backBtn.setOnClickListener {
            InteractiveFragment.iFragment?.back()
        }

        val requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .override(com.bumptech.glide.request.target.Target.SIZE_ORIGINAL, com.bumptech.glide.request.target.Target.SIZE_ORIGINAL)
            .format(DecodeFormat.PREFER_ARGB_8888)
//            .placeholder(R.drawable.ic_launcher_foreground)
//        .format(DecodeFormat.PREFER_RGB_565)　　//设置为这种格式去掉透明度通道，可以减少内存占有
//        .placeholder(R.drawable.img_error)
//            .error(R.drawable.img_error)

        var path = Environment.getExternalStorageDirectory().absolutePath + MessageConstant.PATH_LINE
                
        // 判断路径下的图片是否存在
        val imageFile = File(path)
        Timber.i("imageFile: ${imageFile.path} ${imageFile.exists()} ${imageFile.isFile}")
        val loadImage = if (imageFile.exists() && imageFile.isFile) {
            Timber.i("文件加载")
            // 文件存在，加载外部存储的图片
            path
        } else {
            // 文件不存在，使用本地图片
            R.mipmap.ic_line_ly
        }
        
        Glide.with(requireContext())
//            .setDefaultRequestOptions(requestOptions)
            .load(loadImage)
            .override(3840, 2160) // 限制最大尺寸，避免加载过大的图片
            .centerInside() // 保持比例居中显示
            .into(binding!!.lineIv)
        binding.zoom.setOnTouchListener { view, motionEvent ->
            InteractiveFragment.iFragment?.clicked()
            return@setOnTouchListener false
        }
    }

    override fun initData() {

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            (binding.zoom as ZoomView).reset()
        }
    }

}
