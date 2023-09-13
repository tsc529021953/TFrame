package com.sc.nft.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.nbhope.lib_frame.arouterpath.RouterPath
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.widget.WrapGridLayoutManager
import com.sc.nft.R
import com.sc.nft.adapter.FileImgAdapter
import com.sc.nft.bean.FileImgBean
import com.sc.nft.databinding.ActivityFileImg2Binding
import com.sc.nft.vm.MainViewModel
import com.sc.nft.weight.GridSpaceItemDecoration

/**
 * author: sc
 * date: 2023/9/10
 */
@Route(path = RouterPath.activity_nft_file2)
class FileImg2Activity : NFTBaseActivity<ActivityFileImg2Binding, BaseViewModel>() {

    override var layoutId: Int = R.layout.activity_file_img_2

    private var adapter: FileImgAdapter? = null

    override fun subscribeUi() {
        super.subscribeUi()
        binding.titleTv.text = MainViewModel.getInstance().fileImg2.name
        binding.vm = MainViewModel.getInstance()
        val size = MainViewModel.getInstance().fileImg2List.size
        var count = if (size > 2) {
            if (size % 3 == 0) 3 else 4
        } else 2
        count = 2
        val layoutManager: LinearLayoutManager = WrapGridLayoutManager(this, count, true)
        binding.imgIv.layoutManager = layoutManager
        binding.imgIv.addItemDecoration(GridSpaceItemDecoration(count , 0, 0))
        adapter = FileImgAdapter(MainViewModel.getInstance().fileImg2List, object : FileImgAdapter.FileImgCallback{
            override fun onItemClick(item: FileImgBean) {
                MainViewModel.getInstance().clickFileImg3(item, MainViewModel.getInstance().fileImg2List.indexOf(item))
                ARouter.getInstance().build(RouterPath.activity_nft_file3).navigation()
            }
        })
        binding.imgIv.adapter = adapter
        binding.backIv.setOnClickListener {
            finish()
        }
    }

    override var viewModel: BaseViewModel = BaseViewModel()

}
