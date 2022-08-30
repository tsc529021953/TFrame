package com.sc.app_xsg.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.sc.app_xsg.R
import com.sc.app_xsg.databinding.ActivityHomeBinding
import com.sc.app_xsg.vm.HomeViewModel
import com.sc.lib_frame.base.BaseBindingActivity
import com.sc.lib_frame.common.BasePath

@Route(path = BasePath.HOME_PATH)
class HomeActivity : BaseBindingActivity<ActivityHomeBinding, HomeViewModel>() {

    override var layoutId: Int = R.layout.activity_home

    override fun subscribeUi() {

    }

    override fun initData() {

    }

    override fun linkViewModel() {
        binding.vm = viewModel
    }

    override lateinit var viewModel: HomeViewModel

}