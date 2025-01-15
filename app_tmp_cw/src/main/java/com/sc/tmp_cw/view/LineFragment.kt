package com.sc.tmp_cw.view

import com.nbhope.lib_frame.base.BaseBindingFragment
import com.nbhope.lib_frame.base.BaseViewModel
import com.sc.tmp_cw.R
import com.sc.tmp_cw.databinding.FragmentLineBinding
import com.sc.tmp_cw.databinding.FragmentTravelBinding

/**
 * @author  tsc
 * @date  2025/1/6 19:38
 * @version 0.0.0-1
 * @description
 */
class LineFragment: BaseBindingFragment<FragmentLineBinding, BaseViewModel>() {

    override var layoutId: Int = R.layout.fragment_line

    override fun linkViewModel() {

    }


    override fun subscribeUi() {
        binding.backBtn.setOnClickListener {
            InteractiveFragment.navCallBack?.invoke()
        }
    }

    override fun initData() {

    }


}
