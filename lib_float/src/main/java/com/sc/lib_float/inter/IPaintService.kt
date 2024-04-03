package com.sc.lib_float.inter

import android.content.Context

/**
 * @author  tsc
 * @date  2024/4/1 14:24
 * @version 0.0.0-1
 * @description
 */
interface IPaintService {

    fun init(context: Context)
    fun showFloat()
    fun hideFloat(delayMillis: Long)
    fun showLine()
    fun hideLine()
    fun showDraw()
    fun hideDraw()

}
