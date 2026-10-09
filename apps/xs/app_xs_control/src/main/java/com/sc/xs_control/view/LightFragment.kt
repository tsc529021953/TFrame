package com.sc.xs_control.view

import com.nbhope.lib_frame.base.BaseBindingFragment
import com.sc.xs_control.R
import com.sc.xs_control.databinding.FragmentLightBinding
import com.sc.xs_control.vm.LightViewModel
import timber.log.Timber
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2026/10/8
 * @version 0.0.0-1
 * @description 灯光控制页Fragment
 * 依据效果图生成：3列（顶面照明/墙面照明/空间区域灯光）x 4行（一键控制+区域1~3），底部平面效果图占位
 * 按钮与指令发射器绑定发送UDP指令，由 MainActivity 通过左侧菜单 menu_device / menu_light 切换显示
 */
class LightFragment : BaseBindingFragment<FragmentLightBinding, LightViewModel>() {

    @Inject
    override lateinit var viewModel: LightViewModel

    override var layoutId: Int = R.layout.fragment_light

    override fun linkViewModel() {
        binding.vm = viewModel
    }

    override fun subscribeUi() {

    }

    override fun initData() {
        Timber.i("LightFragment initData")
        viewModel.initData()
    }
}
