package com.sc.tframe.utils

/**
 * @author  tsc
 * @date  2025/4/22 16:38
 * @version 0.0.0-1
 * @description
 */
interface IPlayListen {

    fun onPlay()

    fun onPause()

    fun onRelease()

    fun onDuration(duration: Long)
}
