package com.sc.tmp_translate.bean

import java.text.SimpleDateFormat
import java.util.*

/**
 * @author  tsc
 * @date  2025/7/25 14:20
 * @version 0.0.0-1
 * @description
 */
class TransTextBean {

    var timer = 0L
    var time = ""
    var text = ""
    var transText = ""
    var isMaster = true

    constructor(): this("", "", true)
    constructor(text: String, transText: String): this(text, transText, true)
    constructor(text: String, transText: String, isMaster: Boolean) {
        this.text = text
        this.transText = transText
        this.isMaster = isMaster
        timer = System.currentTimeMillis()
        time = getTimeStr(timer)
    }

    fun updateTime() {
        timer = System.currentTimeMillis()
        time = getTimeStr(timer)
    }

    fun copy(): TransTextBean {
        val bean = TransTextBean()
        bean.updateTime()
        bean.text = this.text
        bean.transText = this.transText
        bean.isMaster = this.isMaster
        return bean
    }

    companion object {

        var format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        fun getTimeStr(time: Long): String {
            val d1 = Date(time)
            return format.format(d1)
        }

        fun getCurTimeStr(): String {
            val time = System.currentTimeMillis()
            val d1 = Date(time)
            return format.format(d1)
        }

        fun formatSecondsToHMS(mill: Long): String {
            val seconds = mill / 1000
            val h = seconds / 3600
            val m = (seconds % 3600) / 60
            val s = seconds % 60
            return String.format("%02d:%02d:%02d", h, m, s)
        }

    }

}
