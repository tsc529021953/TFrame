package com.lib.network.api

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * @Author qiukeling
 * @Date 2020-03-30-13:21
 * @Email qiukeling@nbhope.cn
 */

const val API_URL = "http://open.migu.cn:8100/"
interface LrcService {
    @Streaming
    @GET
    suspend fun download(@Url url: String): ResponseBody
}