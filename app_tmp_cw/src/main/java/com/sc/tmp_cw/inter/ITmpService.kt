package com.sc.tmp_cw.inter

import android.content.Context
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.alibaba.android.arouter.facade.template.IProvider
import com.sc.tmp_cw.bean.CWInfo

/**
 * @author  tsc
 * @date  2024/4/12 13:28
 * @version 0.0.0-1
 * @description
 */
interface  ITmpService: IProvider {

    var stationStatusObs: ObservableField<String>
    var stationObs: ObservableField<String>
    var stationNotifyObs: ObservableInt
    var timeObs: ObservableField<String>
    var titleObs: ObservableField<String>
    var rtspUrlObs: ObservableField<String>
    var urgentNotifyMsgObs: ObservableField<String>

    fun test(msg: String)
    fun write(msg: String)
    fun reBuild()

    /*悬浮窗*/
    fun showFloat()
    fun hideFloat(delayMillis: Long)
    fun getCWInfo(): CWInfo

}
