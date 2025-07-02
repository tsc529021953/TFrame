package com.sc.tframe.utils

/**
 * @author  tsc
 * @date  2025/4/22 16:31
 * @version 0.0.0-1
 * @description
 */
interface IPlayer {

    fun init()

    fun release()

    fun play(path: String)

    fun play()

    fun pause()

    fun playPause()

    fun getPosition(): Int
    fun getPositionMs(): Long

    fun seek(position: Long)

    fun getDuration(): Int
    fun getDurationMs(): Long

}
