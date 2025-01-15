package com.nbhope.lib_frame.utils

import java.util.*

/**
 * @author  tsc
 * @date  2025/1/13 17:47
 * @version 0.0.0-1
 * @description
 */
class HandleClick constructor(var rangeTime: Int = 2000, var rangeCount: Int = 5) {

    private var clickCount = 0
    private var lastClickTime = 0L

    fun handle(callback: () -> Unit) {
        val currentTime = Date().time
        if (currentTime - lastClickTime < rangeTime) {
            clickCount++
            if (clickCount >= rangeCount) {
                callback.invoke()
                reset()
            }
        } else {
            reset()
            clickCount = 1
        }
        lastClickTime = currentTime
    }

    fun reset() {
        clickCount = 0
        lastClickTime = 0
    }


}
