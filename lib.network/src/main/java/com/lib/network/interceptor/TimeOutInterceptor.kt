package com.lib.network.interceptor

import android.text.TextUtils
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

/**
 *Created by ywr on 2021/8/23 17:09
 */
class TimeOutInterceptor : Interceptor {
    companion object {
        val CONNECT_TIMEOUT = "CONNECT_TIMEOUT"
        val READ_TIMEOUT="READ_TIMEOUT"
        val WRITE_TIMEOUT="WRITE_TIMEOUT"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var connectTimeOut = chain.connectTimeoutMillis()
        var readtimeout=chain.readTimeoutMillis()
        var writetiemout=chain.writeTimeoutMillis()

        val request = chain.request()
        var connectTimeoutHeader = request.header(CONNECT_TIMEOUT)
        var readtimeoutHeader=request.header(READ_TIMEOUT)
        var writetimeoutHeader=request.header(WRITE_TIMEOUT)
        if (!TextUtils.isEmpty(connectTimeoutHeader)) {
            connectTimeOut = connectTimeoutHeader!!.toInt()
        }
        if (!TextUtils.isEmpty(readtimeoutHeader)) {
            readtimeout = readtimeoutHeader!!.toInt()
        }
        if (!TextUtils.isEmpty(writetimeoutHeader)) {
            writetiemout = writetimeoutHeader!!.toInt()
        }
        return chain
                .withConnectTimeout(connectTimeOut, TimeUnit.SECONDS)
                .withReadTimeout(readtimeout, TimeUnit.SECONDS)
                .withWriteTimeout(writetiemout, TimeUnit.SECONDS)
                .proceed(request)
    }
}