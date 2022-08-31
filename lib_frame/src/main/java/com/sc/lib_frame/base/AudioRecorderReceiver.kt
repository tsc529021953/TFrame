package com.sc.lib_frame.base

/**
 * @Author qiukeling
 * @Date 2020/5/6-1:21 PM
 * @Email qiukeling@nbhope.cn
 */
interface AudioRecorderReceiver {
    fun onVolume(data:ByteArray)

    fun onData(data:ByteArray)
}