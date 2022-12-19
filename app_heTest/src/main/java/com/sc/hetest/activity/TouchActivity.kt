package com.sc.hetest.activity

import android.bluetooth.BluetoothAdapter
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.SPUtils
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.common.BasePath
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.nbhope.phmusic.player.AudioFocusManager
import com.nbhope.phmusic.player.SilentPlayer
import com.sc.hetest.R
import com.sc.hetest.bean.InfoItem
import com.sc.hetest.common.HECommon
import com.sc.hetest.common.HEPath
import com.sc.hetest.databinding.*
import com.sc.hetest.vm.HornViewModel
import com.sc.hetest.vm.InfoViewModel
import com.sc.hetest.vm.MainViewModel
import com.sc.hetest.vm.VerInfoViewModel
import javax.inject.Inject

/**
 * author: sc
 * date: 2022/12/10
 */
@Route(path = HEPath.TOUCH_PATH)
class TouchActivity : BaseBindingActivity<ActivityTouchBinding, InfoViewModel>(){

    companion object{

    }

    override var layoutId: Int = R.layout.activity_touch

    var bluetoothAdapter: BluetoothAdapter? = null

    override fun subscribeUi() {
        binding.failTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.TOUCH_PATH, InfoItem.STATE_FAIL)
            finish()
        }
        binding.successTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.TOUCH_PATH, InfoItem.STATE_SUCCESS)
            finish()
        }
        binding.btnLy.visibility = View.GONE
        binding.touchSetting.setOnClickListener {
            if (binding.btnLy.visibility == View.VISIBLE)
                binding.btnLy.visibility = View.GONE
            else binding.btnLy.visibility = View.VISIBLE
        }
    }

    override fun initData() {
        viewModel.initData()
    }

    @Inject
    override lateinit var viewModel: InfoViewModel
    override fun linkViewModel() {
        binding.vm = viewModel
    }

    private fun releaseMediaPlayer() {

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}