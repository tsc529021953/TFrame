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
import com.sc.nft.bean.FileImgBean
import com.sc.nft.databinding.ActivityFileImg2Binding
import com.sc.nft.databinding.ActivityFileImg3Binding
import com.sc.nft.databinding.ActivityFileImgBinding
import com.sc.nft.vm.MainViewModel
import kotlinx.coroutines.delay
import pl.droidsonroids.gif.GifDrawable
import timber.log.Timber

/**
 * author: sc
 * date: 2023/9/10
 */
@Route(path = RouterPath.activity_nft_file)
class FileImgActivity : NFTBaseActivity<ActivityFileImgBinding, BaseViewModel>() {

    override var layoutId: Int = R.layout.activity_file_img

    private var adapter: FileImgAdapter? = null

    private var type = -1
    private var item: FileImgBean? = null

    override fun subscribeUi() {
        super.subscribeUi()

        type = MainViewModel.getInstance().fileImgType.get()
        item = MainViewModel.getInstance().fileImgItem

        binding.vm = MainViewModel.getInstance()
        binding.homeIv.setOnClickListener {
            Timber.i("NTAG homeIv home")
            AppManager.appManager?.killAll("com.sc.nft.activity.MainActivity")
        }
        binding.backIv.setOnClickListener {
            finish()
        }
//        MainViewModel.getInstance().fileImg3Img.addOnPropertyChangedCallback(callBack)
//        imgLoad()
    }

    override fun onResume() {
        super.onResume()
//        MainViewModel.getInstance().clickFileImg(item, type = type)
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
//        MainViewModel.getInstance().fileImg3Img.removeOnPropertyChangedCallback(callBack)
    }

}
