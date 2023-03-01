package com.sc.hetest.utils

import android.view.Surface

/**
 * author: sc
 * date: 2023/3/2
 */
interface IAspectRatio {
    fun setAspectRatio(width: Int, height: Int)
    fun getSurfaceWidth(): Int
    fun getSurfaceHeight(): Int
    fun getSurface(): Surface?
    fun postUITask(task: ()->Unit)
}