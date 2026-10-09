package com.sc.xs_control.view

import com.nbhope.lib_frame.base.BaseBindingFragment
import com.sc.xs_control.R
import com.sc.xs_control.databinding.FragmentDeviceBinding
import com.sc.xs_control.vm.DeviceViewModel
import timber.log.Timber
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2026/10/8
 * @version 0.0.0-1
 * @description 设备控制页Fragment
 * 依据效果图生成：PC/一体机/电视1~6 共8张卡片，按钮与指令发射器绑定发送UDP指令
 * 由 MainActivity 通过左侧菜单 menu_device / menu_light 切换显示
 */
class DeviceFragment : BaseBindingFragment<FragmentDeviceBinding, DeviceViewModel>() {

    @Inject
    override lateinit var viewModel: DeviceViewModel

    override var layoutId: Int = R.layout.fragment_device

    override fun linkViewModel() {
        binding.vm = viewModel
    }

    override fun subscribeUi() {

    }

    override fun initData() {
        Timber.i("DeviceFragment initData")
        viewModel.initData()
    }
}
