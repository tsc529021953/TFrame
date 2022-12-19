package com.sc.hetest.activity

import android.bluetooth.BluetoothAdapter
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
import com.sc.hetest.databinding.ActivityHornBinding
import com.sc.hetest.databinding.ActivityInfoBinding
import com.sc.hetest.databinding.ActivityMainBinding
import com.sc.hetest.databinding.ActivityVerInfoBinding
import com.sc.hetest.vm.HornViewModel
import com.sc.hetest.vm.InfoViewModel
import com.sc.hetest.vm.MainViewModel
import com.sc.hetest.vm.VerInfoViewModel
import javax.inject.Inject

/**
 * author: sc
 * date: 2022/12/10
 */
@Route(path = HEPath.BT_PATH)
class BTActivity : BaseBindingActivity<ActivityInfoBinding, InfoViewModel>(){

    companion object{

    }

    override var layoutId: Int = R.layout.activity_info

    var bluetoothAdapter: BluetoothAdapter? = null

    override fun subscribeUi() {
        binding.failTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.BT_PATH, InfoItem.STATE_FAIL)
            finish()
        }
        binding.successTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.BT_PATH, InfoItem.STATE_SUCCESS)
            finish()
        }
    }

    override fun initData() {
        viewModel.initData()
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        getBTInfo()
    }

    private fun getBTInfo(){
        val info = StringBuilder()
        if (bluetoothAdapter == null) {
            info.append("当前设备不支持蓝牙！")
            return
        }
        info.append(if (bluetoothAdapter?.isEnabled == true) "蓝牙已打开" else "蓝牙尚未打开")
        info.append("\n")
        info.append("蓝牙名称：${bluetoothAdapter?.name} \n")
        info.append("MAC地址：${DeviceUtils.getMacAddress()} \n")
        viewModel.info.set(info.toString())
    }

    @Inject
    lateinit var spManager: SharedPreferencesManager

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