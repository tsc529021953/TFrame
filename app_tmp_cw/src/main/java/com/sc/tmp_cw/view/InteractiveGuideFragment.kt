package com.sc.tmp_cw.view

import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.alibaba.android.arouter.launcher.ARouter
import com.nbhope.lib_frame.base.BaseBindingFragment
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.dialog.PersonalTipDialog
import com.nbhope.lib_frame.utils.HandleClick
import com.sc.tmp_cw.R
import com.sc.tmp_cw.constant.MessageConstant
import com.sc.tmp_cw.databinding.FragmentInteractiveBinding
import com.sc.tmp_cw.databinding.FragmentInteractiveGuideBinding
import com.sc.tmp_cw.service.MessageHandler
import com.sc.tmp_cw.weight.KeepStateNavigator

/**
 * @author  tsc
 * @date  2025/1/10 11:26
 * @version 0.0.0-1
 * @description
 */
class InteractiveGuideFragment: BaseBindingFragment<FragmentInteractiveGuideBinding, BaseViewModel>() {

    override var layoutId: Int = R.layout.fragment_interactive_guide

    private var handleClick: HandleClick? = null

    override fun linkViewModel() {

    }

    override fun subscribeUi() {
        if (handleClick == null)
            handleClick = HandleClick()
        binding.settingIv.setOnClickListener {
            handleClick?.handle {
                // TODO 弹出密码框
                PersonalTipDialog.inputNameDialog(context!!.resources.getString(R.string.setting_please_enter_password)) {
                    if (it == "8888") {
                        ARouter.getInstance().build(MessageConstant.ROUTH_SETTING).navigation(this.context)
                        return@inputNameDialog null
                    } else return@inputNameDialog context!!.resources.getString(R.string.setting_password_error)
                }.show(
                        activity!!.supportFragmentManager,
                        "personalTipDialog"
                )
            }
        }

        binding.travelLy.setOnClickListener {
            navigate(R.id.navigation_travel)
        }
        binding.siLy.setOnClickListener {
            navigate(R.id.navigation_station_search)
        }
        binding.lineLy.setOnClickListener {
            navigate(R.id.navigation_line)
        }
        binding.cateLy.setOnClickListener {
            navigate(R.id.navigation_cate)
        }

        binding.travelIv.setOnClickListener {
            navigate(R.id.navigation_travel)
        }
        binding.siIv.setOnClickListener {
            navigate(R.id.navigation_station_search)
        }
        binding.lineIv.setOnClickListener {
            navigate(R.id.navigation_line)
        }
        binding.cateIv.setOnClickListener {
            navigate(R.id.navigation_cate)
        }
    }

    private fun navigate(resId: Int) {
        InteractiveFragment.navController?.navigate(resId, null)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun initData() {

    }
}
