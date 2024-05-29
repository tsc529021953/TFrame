package com.sc.lib_float.inter

import android.content.Context
import android.view.View

/**
 * @author  tsc
 * @date  2024/4/1 14:24
 * @version 0.0.0-1
 * @description
 */
interface IPaint {

    fun showLine3()
    fun hideLine3()
    fun showLine2()
    fun hideLine2()
    fun showLine()
    fun hideLine()
    fun showDraw()
    fun hideDraw()
    fun initView(root: View?)

}
