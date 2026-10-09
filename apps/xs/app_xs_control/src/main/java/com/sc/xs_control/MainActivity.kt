package com.sc.xs_control

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.databinding.Observable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.sc.xs_control.databinding.ActivityMainBinding
import com.sc.xs_control.view.DeviceFragment
import com.sc.xs_control.view.LightFragment
import com.sc.xs_control.vm.MainViewModel
import timber.log.Timber
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2026/10/8
 * @version 0.0.0-1
 * @description 显示控制主界面
 * 左侧菜单（设备控制/灯光控制）点击切换 DeviceFragment / LightFragment
 * 依据效果图生成，按钮与指令发射器绑定发送UDP指令
 * 网络状态（侧栏底部联机状态）来自 TmpServiceImpl，见 MainViewModel.attachNetObs
 */
class MainActivity : BaseBindingActivity<ActivityMainBinding, MainViewModel>() {

    companion object {

        const val REQUEST_CODE = 10085

        private const val TAG_DEVICE = "device"
        private const val TAG_LIGHT = "light"
    }

    override var layoutId: Int = R.layout.activity_main

    @Inject
    override lateinit var viewModel: MainViewModel

    private var deviceFragment: DeviceFragment? = null
    private var lightFragment: LightFragment? = null

    /** 页签变化回调：切换Fragment显示 */
    private val tabCallback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            switchFragment(viewModel.tabObs.get())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    override fun onResume() {
        super.onResume()
        // service若在initData时还未绑定完成，这里重试挂接
        viewModel.attachNetObs()
    }

    override fun subscribeUi() {
        binding.vm = viewModel
        initFragments()
        viewModel.tabObs.addOnPropertyChangedCallback(tabCallback)
    }

    override fun initData() {
        Timber.i("MainActivity initData")
        viewModel.initData()
        // 从service获取网络状态（侧栏底部联机状态显示）
        viewModel.attachNetObs()
    }

    override fun linkViewModel() {

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.tabObs.removeOnPropertyChangedCallback(tabCallback)
        viewModel.detachNetObs()
    }

    /** 初始化两个Fragment（一次加载，后续show/hide切换，保留各自状态） */
    private fun initFragments() {
        val fm = supportFragmentManager
        deviceFragment = fm.findFragmentByTag(TAG_DEVICE) as? DeviceFragment
        lightFragment = fm.findFragmentByTag(TAG_LIGHT) as? LightFragment

        if (deviceFragment == null || lightFragment == null) {
            val tx: FragmentTransaction = fm.beginTransaction()
            if (deviceFragment == null) {
                deviceFragment = DeviceFragment()
                tx.add(R.id.fragment_container, deviceFragment as Fragment, TAG_DEVICE)
            }
            if (lightFragment == null) {
                lightFragment = LightFragment()
                tx.add(R.id.fragment_container, lightFragment as Fragment, TAG_LIGHT)
            }
            tx.commitAllowingStateLoss()
        }
        // 按当前页签显示
        switchFragment(viewModel.tabObs.get())
    }

    /** 切换Fragment：设备控制 0 / 灯光控制 1 */
    private fun switchFragment(tab: Int) {
        val fm = supportFragmentManager
        val tx: FragmentTransaction = fm.beginTransaction()
        if (tab == MainViewModel.TAB_DEVICE) {
            lightFragment?.let { if (!it.isHidden) tx.hide(it) }
            deviceFragment?.let { if (it.isHidden) tx.show(it) }
        } else {
            deviceFragment?.let { if (!it.isHidden) tx.hide(it) }
            lightFragment?.let { if (it.isHidden) tx.show(it) }
        }
        if (!tx.isEmpty) {
            tx.commitAllowingStateLoss()
        }
    }
}
