package com.illusory.paint

import android.app.Application

import com.illusory.paint.vm.MainViewModel
import com.nbhope.lib_frame.app.HopeBaseApp
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.di.BaseAppComponent
import com.nbhope.lib_frame.network.NetworkCallback
import com.sc.lib_float.databinding.ActivityMainBinding
import com.sc.lib_float.module.LineManager
import com.sc.lib_float.module.NormalLineManager
import com.sc.lib_float.service.PaintServiceDelegate
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2024/4/30 15:13
 * @version 0.0.0-1
 * @description
 */
class MainActivity: BaseBindingActivity<ActivityMainBinding, MainViewModel>() {

    override var layoutId: Int = R.layout.activity_main

    @Inject
    override lateinit var viewModel: MainViewModel

    @Inject
    lateinit var networkCallback: NetworkCallback

    lateinit var lineManager: NormalLineManager

    override fun subscribeUi() {
        lineManager = NormalLineManager(viewModel.mScope, HopeBaseApp.getContext() as Application, false)
        lineManager.drawView = binding.draw1
        lineManager.paintView = binding.drawParent
        binding.dv = binding.draw1
        lineManager.initView(binding.root)
    }

    override fun initData() {

    }

    override fun linkViewModel() {

    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
