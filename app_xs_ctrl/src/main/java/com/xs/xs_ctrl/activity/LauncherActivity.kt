package com.xs.xs_ctrl.activity

import com.alibaba.android.arouter.launcher.ARouter
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.xs.xs_ctrl.R
import com.xs.xs_ctrl.constant.MessageConstant
import com.xs.xs_ctrl.databinding.ActivityLauncherBinding
import com.xs.xs_ctrl.vm.LauncherViewModel

import javax.inject.Inject

class LauncherActivity : BaseBindingActivity<ActivityLauncherBinding, LauncherViewModel>() {

    @Inject
    override lateinit var viewModel: LauncherViewModel

    override var layoutId: Int = R.layout.activity_launcher

    override fun linkViewModel() {

    }

    override fun subscribeUi() {
        binding.bgLy.setOnClickListener {
            ARouter.getInstance().build(MessageConstant.PATH_MAIN).navigation(this)
        }
    }

    override fun initData() {

    }
}