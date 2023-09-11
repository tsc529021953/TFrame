package com.sc.nft.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.nbhope.lib_frame.arouterpath.RouterPath
import com.nbhope.lib_frame.base.BaseViewModel
import com.sc.nft.R
import com.sc.nft.adapter.FileImgAdapter
import com.sc.nft.databinding.ActivityFileImg2Binding
import com.sc.nft.databinding.ActivityFileImg3Binding
import com.sc.nft.vm.MainViewModel

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
//        binding.titleTv.text = MainViewModel.getInstance().fileImg2
    }

    override var viewModel: BaseViewModel = BaseViewModel()

}