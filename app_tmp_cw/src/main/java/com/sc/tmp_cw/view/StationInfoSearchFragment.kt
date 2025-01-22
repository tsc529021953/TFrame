package com.sc.tmp_cw.view

import com.alibaba.android.arouter.launcher.ARouter
import com.nbhope.lib_frame.base.BaseBindingFragment
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.utils.HandleClick
import com.sc.tmp_cw.R
import com.sc.tmp_cw.constant.MessageConstant
import com.sc.tmp_cw.databinding.FragmentStationInfoSearchBinding
import com.sc.tmp_cw.service.MessageHandler

/**
 * @author  tsc
 * @date  2025/1/10 11:26
 * @version 0.0.0-1
 * @description
 */
class StationInfoSearchFragment: BaseBindingFragment<FragmentStationInfoSearchBinding, BaseViewModel>() {

    override var layoutId: Int = R.layout.fragment_station_info_search

    override fun linkViewModel() {

    }

    override fun subscribeUi() {
        binding.backBtn.setOnClickListener {
            InteractiveFragment.iFragment?.back()
        }
    }

    override fun initData() {

    }
}
