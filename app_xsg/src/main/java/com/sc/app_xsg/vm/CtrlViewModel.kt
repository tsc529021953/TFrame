package com.sc.app_xsg.vm

import androidx.databinding.ObservableBoolean
import com.sc.lib_frame.base.BaseViewModel
import com.sc.lib_frame.utils.SharedPreferencesManager
import com.sc.lib_local_device.common.DeviceCommon
import com.sc.lib_local_device.vm.BaseCtrlViewModel
import com.sc.lib_local_device.vm.BaseLDViewModel
import io.reactivex.Observable
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2022/8/30 16:31
 * @version 0.0.0-1
 * @description
 */
class CtrlViewModel @Inject constructor(val spManager: SharedPreferencesManager) : BaseCtrlViewModel() {

    override fun initData() {
       super.initData()
    }

    override fun onClicked() {
        super.onClicked()
        //
    }

}