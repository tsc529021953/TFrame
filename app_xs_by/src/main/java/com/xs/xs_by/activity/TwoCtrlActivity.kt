package com.xs.xs_by.activity

import android.content.pm.ActivityInfo
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.CompoundButton
import com.xs.xs_by.databinding.ActivityMainBinding
import com.xs.xs_by.vm.MainViewModel
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.network.NetworkCallback
import com.nbhope.lib_frame.network.NetworkCallbackModule
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import androidx.databinding.Observable
import com.xs.xs_by.R
import com.xs.xs_by.databinding.ActivityOneCtrlBinding
import com.xs.xs_by.databinding.ActivityTwoCtrlBinding
import com.xs.xs_by.dialog.BYTipDialog
import com.xs.xs_by.vm.OneCtrlViewModel
import com.xs.xs_by.vm.TwoCtrlViewModel

/**
 * @author  tsc
 * @date  2024/4/25 17:47
 * @version 0.0.0-1
 * @description
 * 1. wifi/line
 * 2. timer
 * 3. applist
 */
class TwoCtrlActivity : BaseBindingActivity<ActivityTwoCtrlBinding, TwoCtrlViewModel>() {

    override var layoutId: Int = R.layout.activity_two_ctrl

    @Inject
    override lateinit var viewModel: TwoCtrlViewModel

    @Inject
    lateinit var networkCallback: NetworkCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    override fun subscribeUi() {
        binding.vm = viewModel

    }

    override fun initData() {
        viewModel.initData()

    }

    override fun linkViewModel() {

    }

    override fun onDestroy() {
        super.onDestroy()
//        viewModel.stopAnimation(binding.textTv)
    }
}
