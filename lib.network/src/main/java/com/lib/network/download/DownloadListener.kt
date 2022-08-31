package com.lib.network.download

import okhttp3.ResponseBody




/**
 * @Author qiukeling
 * @Date 2020/7/9-9:42 AM
 * @Email qiukeling@nbhope.cn
 */
interface DownloadListener {
    fun onStart(responseBody: ResponseBody)
}