package com.sc.nft.activity

import android.Manifest
import android.os.Build
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.PermissionUtils
import com.nbhope.lib_frame.arouterpath.RouterPath
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.utils.ViewUtil.Companion.immersionTitle
import com.nbhope.lib_frame.widget.WrapGridLayoutManager
import com.sc.nft.R
import com.sc.nft.adapter.FileImgAdapter
import com.sc.nft.bean.FileImgBean
import com.sc.nft.databinding.ActivityMainBinding
import com.sc.nft.vm.MainViewModel
import com.sc.nft.weight.GridSpaceItemDecoration
import timber.log.Timber

/**
 * author: sc
 * date: 2023/9/10
 */
@Route(path = RouterPath.activity_nft_main)
class MainActivity : NFTBaseActivity<ActivityMainBinding, BaseViewModel>() {

    companion object {
        var PERMISSIONS = arrayOf<String>(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )

        const val REQUEST_CODE = 10086
    }

    override var layoutId: Int = R.layout.activity_main

    private var adapter: FileImgAdapter? = null

    override fun subscribeUi() {
        immersionTitle(this)
        if (!hasPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(PERMISSIONS, REQUEST_CODE)
            } else {
                init()
            }
        } else init()
        val layoutManager: LinearLayoutManager = WrapGridLayoutManager(this, 2, LinearLayoutManager.VERTICAL
            , true)
        binding.imgIv.layoutManager = layoutManager
        binding.imgIv.addItemDecoration(GridSpaceItemDecoration(2 , 0, 0))
        adapter = FileImgAdapter(MainViewModel.getInstance().fileImg1List, object : FileImgAdapter.FileImgCallback{
            override fun onItemClick(item: FileImgBean) {
//                MainViewModel.getInstance().clickFileImg2(item)
//                ARouter.getInstance().build(RouterPath.activity_nft_file2).navigation()
                MainViewModel.getInstance().fileImgType.set(1)
                MainViewModel.getInstance().fileImgItem = item
                MainViewModel.getInstance().clickFileImg(item)
                ARouter.getInstance().build(RouterPath.activity_nft_file2).navigation()
            }
        })
        binding.imgIv.adapter = adapter
        super.subscribeUi()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (hasPermission())
            init()
    }

    private fun hasPermission(): Boolean {
        var has = true
        PERMISSIONS.forEach {
            if (!PermissionUtils.isGranted(it)) {
                has = false
            }
        }
        return has
    }

    private fun init() {
        Timber.i("NTAG init 初始化数据")
        MainViewModel.getInstance().getDirs {
            if (it == 0) {
                adapter?.setNewInstance(MainViewModel.getInstance().fileImg1List)
            }
        }
    }

    override fun initData() {

    }

    override fun linkViewModel() {

    }

    override var viewModel: BaseViewModel = BaseViewModel()

}
