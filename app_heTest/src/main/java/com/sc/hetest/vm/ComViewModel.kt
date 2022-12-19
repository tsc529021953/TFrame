package com.sc.hetest.vm

import androidx.databinding.ObservableField
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.sc.lib_system.sp.SerialPortUtil
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2022/8/30 16:31
 * @version 0.0.0-1
 * @description
 */
class ComViewModel @Inject constructor(val spManager: SharedPreferencesManager) : BaseViewModel() {

    var info = ObservableField<String>("")

    var open = ObservableField<String>("打开")

    var hex = ObservableField<String>("TEXT")

    var input = ObservableField<String>("")

    var spUtil : SerialPortUtil = SerialPortUtil()

    companion object{

    }

    fun initData( iDataReceived: SerialPortUtil.IDataReceived) {
        spUtil.setiDataReceived(iDataReceived)
    }

    fun getList(): ArrayList<String> {
        var list = ArrayList<String>()
        var arr = spUtil?.spList
        if (!arr.isNullOrEmpty()) {
            list.addAll(arr)
        }
        return list
    }

    fun dispose() {
        spUtil?.dispose()
    }
}