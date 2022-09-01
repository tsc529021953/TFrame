package com.sc.app_xsg.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.sc.app_xsg.R
import com.sc.app_xsg.databinding.ActivityHomeBinding
import com.sc.app_xsg.vm.HomeViewModel
import com.sc.lib_frame.base.BaseBindingActivity
import com.sc.lib_frame.common.BasePath
import com.sc.lib_frame.utils.SharedPreferencesManager
import com.sc.lib_local_device.common.DeviceCommon
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2022/8/1 13:12
 * @version 0.0.0-1
 * @description
 * 主页 显示一张主页图片
 * 到此处
 */
@Route(path = BasePath.HOME_PATH)
class HomeActivity : BaseBindingActivity<ActivityHomeBinding, HomeViewModel>() {

    override var layoutId: Int = R.layout.activity_home

    override fun subscribeUi() {
        //
        binding.deviceIdTv.visibility = if (DeviceCommon.deviceType.equals(DeviceCommon.DeviceType.Ctrl))
            View.GONE else View.VISIBLE
    }

    override fun initData() {

    }

    override fun linkViewModel() {
        binding.vm = viewModel
        viewModel.initData()
    }

    @Inject
    override lateinit var viewModel: HomeViewModel

}