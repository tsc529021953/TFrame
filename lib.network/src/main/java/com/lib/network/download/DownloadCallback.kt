package com.lib.network.download

import java.io.File


/**
 * @Author qiukeling
 * @Date 2020/7/9-10:28 AM
 * @Email qiukeling@nbhope.cn
 */
interface DownloadCallback {
    fun onStart()

    fun onProgress(totalByte: Long, currentByte: Long, progress: Int)

    fun onFinish(file: File)

    fun onError(msg: String?)
}