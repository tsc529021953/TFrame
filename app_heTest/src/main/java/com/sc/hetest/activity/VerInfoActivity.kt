package com.sc.hetest.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.SPUtils
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.common.BasePath
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.sc.hetest.R
import com.sc.hetest.bean.InfoItem
import com.sc.hetest.common.HECommon
import com.sc.hetest.common.HEPath
import com.sc.hetest.databinding.ActivityMainBinding
import com.sc.hetest.databinding.ActivityVerInfoBinding
import com.sc.hetest.vm.MainViewModel
import com.sc.hetest.vm.VerInfoViewModel
import javax.inject.Inject

/**
 * author: sc
 * date: 2022/12/10
 */
@Route(path = HEPath.VER_INFO_PATH)
class VerInfoActivity : BaseBindingActivity<ActivityVerInfoBinding, VerInfoViewModel>(){
    override var layoutId: Int = R.layout.activity_ver_info
    override fun subscribeUi() {
        binding.failTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.VER_INFO_PATH, InfoItem.STATE_FAIL)
//            ARouter.getInstance().build(BasePath.MAIN_PATH)
//                .withString(HECommon.COM_PATH, HEPath.VER_INFO_PATH)
//                .withInt(HECommon.COM_STATE, InfoItem.STATE_FAIL)
//                .navigation(this )
            finish()
        }
        binding.successTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.VER_INFO_PATH, InfoItem.STATE_SUCCESS)
//            ARouter.getInstance().build(BasePath.MAIN_PATH)
//                .withString(HECommon.COM_PATH, HEPath.VER_INFO_PATH)
//                .withInt(HECommon.COM_STATE, InfoItem.STATE_SUCCESS)
//                .navigation(this )
            finish()
        }
    }

    override fun initData() {
        viewModel.initData()
    }

    @Inject
    lateinit var spManager: SharedPreferencesManager

    @Inject
    override lateinit var viewModel: VerInfoViewModel
    override fun linkViewModel() {
        binding.vm = viewModel
    }
}