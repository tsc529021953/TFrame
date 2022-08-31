package com.lib.network.api

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Streaming
import retrofit2.http.Url


/**
 * @Author qiukeling
 * @Date 2020/7/9-10:13 AM
 * @Email qiukeling@nbhope.cn
 */
interface DownloadApi {
    /**
     * 下载文件
     */
    @Streaming
    @GET
    suspend fun downloadFile(@Header("Range") range: String, @Url url: String): ResponseBody
}