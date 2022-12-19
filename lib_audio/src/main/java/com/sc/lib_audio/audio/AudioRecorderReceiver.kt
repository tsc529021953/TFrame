package com.sc.lib_audio.audio

/**
 *Created by ywr on 2021/11/10 17:01
 */
interface AudioRecorderReceiver {
    fun onData(data: ByteArray)
}