package com.xs.xs_ctrl.inter

import android.content.Context

/**
 * @author  tsc
 * @date  2024/4/12 13:28
 * @version 0.0.0-1
 * @description
 */
interface ITmpService {

    fun init(context: Context)
    fun showFloat()
    fun hideFloat(delayMillis: Long)
    fun write(msg: String)
    fun write2(msg: String)
    fun writeMedia(ip: String, msg: String)
    fun reBuild()

}
