package com.sc.nft.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.Observable
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.nbhope.lib_frame.app.AppManager
import com.nbhope.lib_frame.arouterpath.RouterPath
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.constants.GlobalScopeUtils
import com.sc.nft.R
import com.sc.nft.adapter.FileImgAdapter
import com.sc.nft.databinding.ActivityFileImg2Binding
import com.sc.nft.databinding.ActivityFileImg3Binding
import com.sc.nft.vm.MainViewModel
import kotlinx.coroutines.delay
import pl.droidsonroids.gif.GifDrawable
import timber.log.Timber

/**
 * author: sc
 * date: 2023/9/10
 */
@Route(path = RouterPath.activity_nft_file3)
class FileImg3Activity : NFTBaseActivity<ActivityFileImg3Binding, BaseViewModel>() {

    override var layoutId: Int = R.layout.activity_file_img_3

    private var adapter: FileImgAdapter? = null

    override fun subscribeUi() {
        super.subscribeUi()

        binding.vm = MainViewModel.getInstance()
        binding.homeIv.setOnClickListener {
            Timber.i("NTAG homeIv home")
            finish()
            GlobalScopeUtils.launchSafety {
                var close = false
                val callback = {
                    if (AppManager.appManager?.topActivity?.javaClass?.simpleName == "FileImg2Activity") {
                        AppManager.appManager?.topActivity?.finish()
                        close = true
                    }
                }
                delay(300)
                callback.invoke()
                if (close) return@launchSafety
                delay(100)
                callback.invoke()
                if (close) return@launchSafety
                delay(200)
                callback.invoke()
            }
        }
        binding.backIv.setOnClickListener {
            finish()
        }
        MainViewModel.getInstance().fileImg3Img.addOnPropertyChangedCallback(callBack)
        imgLoad()
    }

    fun imgLoad() {
        Timber.i("NTAG fileImg3Img ${MainViewModel.getInstance().fileImg3Img.get()}")
        val img = MainViewModel.getInstance().fileImg3Img.get() ?: return
        if (img.isNullOrEmpty()) return
        // 图片加载
        if (img.endsWith(".gif")) {
            try {
                val gifFromAssets = GifDrawable(img)
                binding.imgIv.setImageDrawable(gifFromAssets)
            } catch (e: Exception) {
                e.printStackTrace()
                Timber.e("NTAG err $e")
            }
        } else {
            Glide.with(binding.imgIv)
                .load(img)
                .error(R.mipmap.logo_move)
//                            .placeholder(R.mipmap.uhome_ic_device_default)
                .into(binding.imgIv)
        }
    }

    private val callBack = object :
        Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            imgLoad()
        }
    }

    override var viewModel: BaseViewModel = BaseViewModel()

    override fun onDestroy() {
        super.onDestroy()
        MainViewModel.getInstance().fileImg3Img.removeOnPropertyChangedCallback(callBack)
    }

}
