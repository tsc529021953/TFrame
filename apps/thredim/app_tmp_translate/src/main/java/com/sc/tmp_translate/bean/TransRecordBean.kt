package com.sc.tmp_translate.bean

import com.nbhope.lib_frame.utils.FileSpManager
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.sc.tmp_translate.da.RecordRepository

/**
 * @author  tsc
 * @date  2025/7/25 14:22
 * @version 0.0.0-1
 * @description
 */
class TransRecordBean {

    var beans: ArrayList<TransTextBean> = arrayListOf()

    var lang: String = "en"
    var path: String = ""

    var useTimerStr: String = "" // 耗时
    var startTimeStr: String = "" // 开始时间字符串
    var startTime: Long = 0L // 开始时间戳

    constructor(lang: String) {
        // TODO 初始化的时候，记录开始时间戳和字符串，记录lang
        this.lang = lang
        this.startTime = System.currentTimeMillis()
        this.startTimeStr = TransTextBean.getTimeStr(this.startTime)
    }

    fun end(spManager: SharedPreferencesManager) {
        // TODO 结束后,记录时长,添加到库中去，存储到sp
        this.useTimerStr = TransTextBean.formatSecondsToHMS(System.currentTimeMillis() - this.startTime)
        RecordRepository.addItem(this, spManager)
    }
}
