package com.lib.network.download

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * @Author qiukeling
 * @Date 2020/7/9-9:55 AM
 * @Email qiukeling@nbhope.cn
 */
class DownloadInterceptor constructor(var listener: DownloadListener? = null): Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse: Response = chain.proceed(chain.request())
        return originalResponse.newBuilder()
                .body(DownloadResponseBody(originalResponse.body()!!, listener))
                .build()
    }
}