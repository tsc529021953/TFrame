package com.sc.tmp_cw.view

import android.os.Environment
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.alibaba.android.arouter.launcher.ARouter
import com.nbhope.lib_frame.base.BaseBindingFragment
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.utils.FileUtil
import com.nbhope.lib_frame.utils.HandleClick
import com.sc.tmp_cw.MainActivity
import com.sc.tmp_cw.R
import com.sc.tmp_cw.base.CWBaseBindingFragment
import com.sc.tmp_cw.constant.MessageConstant
import com.sc.tmp_cw.databinding.FragmentInteractiveBinding
import com.sc.tmp_cw.inter.IFragment
import com.sc.tmp_cw.inter.TimerIntercept
import com.sc.tmp_cw.service.MessageHandler
import com.sc.tmp_cw.utils.BigImageLoader
import com.sc.tmp_cw.utils.FlavorConfigUtil
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
            // 定时到达:当前子页面存在浮层等界面时优先关界面并重新计时,再次到达才退出页面
            if (interceptTimerByChild()) {
                reset()
            } else {
                hide()
                MainActivity.iMain?.home()
            }
        }
        iFragment = this
        super.subscribeUi()
        preloadBigImages()
    }

    /**
     * 提前在后台解码站点查询整图并写入缓存，
     * 用户点进对应页面时直接命中缓存，避免进页面后等待解码。
     * （线路图 LineFragment 已改用 SubsamplingScaleImageView 瓦片式解码，不走 Glide，无需预加载）
     */
    private fun preloadBigImages() {
        if (FlavorConfigUtil.isFlavorB()) {
            val root = Environment.getExternalStorageDirectory().absolutePath
            FileUtil.getPath(root + MessageConstant.PATH_STATION_SEARCH)?.let {
                BigImageLoader.preload(requireContext(), it)
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            MainActivity.iMain?.show(arrayListOf(MainActivity.TAG_HOME))
        } else {
            hide()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun hide() {
        try {
            navController?.navigate(R.id.navigation_guide, null)
            MainActivity.iMain?.show(arrayListOf(MainActivity.TAG_HOME))
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
        MainActivity.iMain?.show(arrayListOf())
        navController?.navigate(resId, null)
    }

    override fun clicked() {
        reset()
    }

    /** 定时器到达时,询问当前显示的子页面是否消费(如先关闭页内浮层) */
    private fun interceptTimerByChild(): Boolean {
        val child = mNavHostFragment.childFragmentManager.primaryNavigationFragment ?: return false
        return (child as? TimerIntercept)?.onTimerReach() == true
    }

    override fun stop2() {
        stop()
    }
}
