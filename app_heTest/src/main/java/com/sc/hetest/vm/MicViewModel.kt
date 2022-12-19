package com.sc.hetest.vm

import androidx.databinding.ObservableField
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2022/8/30 16:31
 * @version 0.0.0-1
 * @description
 */
class MicViewModel @Inject constructor(val spManager: SharedPreferencesManager) : BaseViewModel() {

    var musicInfo = ObservableField<String>("")

    fun initData() {

    }

}