package com.sc.xs_cc.activity

import android.Manifest
import android.os.Build
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.PermissionUtils
import com.lib.network.api.ApiResponse
import com.lib.network.api.ApiService
import com.lib.network.api.ApiSuccessResponse
import com.lib.network.api.ParamsCreator
import com.lib.network.vo.MusicFiltered
import com.nbhope.lib_frame.arouterpath.RouterPath
import com.nbhope.lib_frame.base.BaseActivity
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.base.TBaseActivity
import com.nbhope.lib_frame.utils.ViewUtil.Companion.immersionTitle
import com.nbhope.lib_frame.widget.WrapGridLayoutManager
import com.sc.xs_cc.R
import com.sc.xs_cc.bean.FileImgBean
import com.sc.xs_cc.databinding.ActivityMainBinding
import com.sc.xs_cc.vm.MainViewModel
import com.sc.xs_cc.weight.GridSpaceItemDecoration
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject

/**
 * author: sc
 * date: 2023/9/10
 */
@Route(path = RouterPath.activity_nft_main)
class MainActivity : TBaseActivity<ActivityMainBinding, BaseViewModel>() {

    companion object {
        var PERMISSIONS = arrayOf<String>(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )

        const val REQUEST_CODE = 10086
    }

    override var layoutId: Int = R.layout.activity_main

    @Inject
    lateinit var apiService: ApiService

    override fun subscribeUi() {
        super.subscribeUi()
        immersionTitle(this)
        if (!hasPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(PERMISSIONS, REQUEST_CODE)
            } else {
                init()
            }
        } else init()
//        val layoutManager: LinearLayoutManager = WrapGridLayoutManager(this, 2, LinearLayoutManager.VERTICAL
//            , true)
//        binding.imgIv.layoutManager = layoutManager
//        binding.imgIv.addItemDecoration(GridSpaceItemDecoration(2 , 0, 0))
    }

    fun navigation(item: FileImgBean) {
        ARouter.getInstance().build(RouterPath.activity_nft_file)
            .withString(MainViewModel.FILENAME, item.filename)
            .withString(MainViewModel.FILE, item.file)
            .withString(MainViewModel.NAME, item.name)
            .withInt(MainViewModel.TYPE, MainViewModel.TYPE_FIRST)
//            .withInt(MainViewModel.INDEX, MainViewModel.TYPE_FIRST)
            .navigation(this@MainActivity)
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
//                adapter?.setNewInstance(MainViewModel.getInstance().fileImg1List)
            }
        }
    }

    override fun initData() {
        viewModel.launchIO {
            val response = try {
                ApiResponse.create(apiService.test())
            } catch (throwable: Throwable) {
                ApiResponse.create<String>(throwable)
            }
            Timber.i("C_TAG response $response")
            var lastData = if (response is ApiSuccessResponse) {
                Timber.i("C_TAG success ${response.body}")
            } else {

            }
        }
    }

    override fun linkViewModel() {

    }

    override var viewModel: BaseViewModel = BaseViewModel()

}
