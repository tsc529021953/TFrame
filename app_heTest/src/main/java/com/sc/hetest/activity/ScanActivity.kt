package com.sc.hetest.activity

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.SPUtils
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.sc.hetest.R
import com.sc.hetest.bean.InfoItem
import com.sc.hetest.common.HEPath
import com.sc.hetest.databinding.ActivityInfoEtBinding
import com.sc.hetest.vm.InfoViewModel
import javax.inject.Inject

/**
 * author: sc
 * date: 2022/12/10
 */
@Route(path = HEPath.SCAN_PATH)
class ScanActivity : BaseBindingActivity<ActivityInfoEtBinding, InfoViewModel>(){

    companion object{

    }

    override var layoutId: Int = R.layout.activity_info_et

    override fun subscribeUi() {
        binding.failTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.SCAN_PATH, InfoItem.STATE_FAIL)
            finish()
        }
        binding.successTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.SCAN_PATH, InfoItem.STATE_SUCCESS)
            finish()
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//        binding.scanEt.inputType = InputType.TYPE_NULL;
        binding.scanEt.showSoftInputOnFocus = false
    }

    override fun initData() {
        val info = StringBuilder()
        info.append("请将二维码或条形码对准扫码口\n")
        viewModel.info.set(info.toString())
        viewModel.initData()
    }

    @Inject
    override lateinit var viewModel: InfoViewModel
    override fun linkViewModel() {
        binding.vm = viewModel
    }
}