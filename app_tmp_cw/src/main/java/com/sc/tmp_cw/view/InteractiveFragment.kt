package com.sc.tmp_cw.view

import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.alibaba.android.arouter.launcher.ARouter
import com.nbhope.lib_frame.base.BaseBindingFragment
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.utils.HandleClick
import com.sc.tmp_cw.MainActivity
import com.sc.tmp_cw.R
import com.sc.tmp_cw.base.CWBaseBindingFragment
import com.sc.tmp_cw.constant.MessageConstant
import com.sc.tmp_cw.databinding.FragmentInteractiveBinding
import com.sc.tmp_cw.inter.IFragment
import com.sc.tmp_cw.service.MessageHandler
import com.sc.tmp_cw.weight.KeepStateNavigator

/**
 * @author  tsc
 * @date  2025/1/10 11:26
 * @version 0.0.0-1
 * @description
 */
class InteractiveFragment: CWBaseBindingFragment<FragmentInteractiveBinding, BaseViewModel>(), IFragment {

    override var layoutId: Int = R.layout.fragment_interactive

    private lateinit var mNavHostFragment: NavHostFragment

    companion object {

        var iFragment: IFragment? = null
    }

    var navController: NavController? = null

    override fun linkViewModel() {

    }

    override fun subscribeUi() {

        /*初始导航*/
        mNavHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        // setup custom navigator
        val navigator = KeepStateNavigator(requireContext(), mNavHostFragment.childFragmentManager, R.id.nav_host_fragment)
        navController = mNavHostFragment.navController
        navController?.navigatorProvider?.addNavigator(navigator)
        navController?.setGraph(R.navigation.interactive_navigation)
        MainActivity.iMain?.show(arrayListOf(MainActivity.TAG_HOME))
        super.rootLy = binding.bgLy
        super.finish = {
            hide()
            MainActivity.iMain?.home()
        }
        iFragment = this
        super.subscribeUi()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            MainActivity.iMain?.show(arrayListOf(MainActivity.TAG_HOME))
        } else {
            hide()
        }
    }

    private fun hide() {
        navController?.navigate(R.id.navigation_guide, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        navController = null
    }

    override fun initData() {

    }

    override fun back() {
        reset()
        hide()
    }

    override fun navigate(resId: Int) {
        navController?.navigate(resId, null)
    }

    override fun clicked() {
        reset()
    }

    override fun stop2() {
        stop()
    }
}
