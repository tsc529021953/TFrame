package com.lib.network.utils

import okhttp3.*
import timber.log.Timber
import java.lang.Exception
import java.net.URLDecoder

/**
 * @Author qiukeling
 * @Date 2022/1/12-9:05 AM
 * @Email qiukeling@nbhope.cn
 */
class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        //这个chain里面包含了request和response，所以你要什么都可以从这里拿
        val request: Request = chain.request()
        val t1 = System.nanoTime() //请求发起的时间
        Timber.tag("OkHttp").i(String.format("发送请求 %s", request.url()))
        val body = request.body()
        if (body is FormBody){
            for(i in 0..body.size()){
                if (body.encodedName(i) == "dat"){
                    val decodeBody = try {
                        URLDecoder.decode(body.encodedValue(i),"UTF-8")
                    }catch (e: Exception){
                        "解析异常"
                    }
                    Timber.tag("OkHttp").i(String.format("请求体:%n %s", decodeBody))
                    break
                }
            }
        }
        val response: Response
        response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            Timber.tag("OkHttp").e("<-- HTTP FAILED: $e")
            throw e
        }
        val t2 = System.nanoTime() //收到响应的时间

        //这里不能直接使用response.body().string()的方式输出日志
        //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
        //个新的response给应用层处理
        Timber.tag("OkHttp").d(String.format("接收响应: %s %n 耗时:%.1fms %n",
                response.request().url(),
                (t2 - t1) / 1e6))
        val responseBody: ResponseBody = response.peekBody(1024 * 1024.toLong())
        var strBody = responseBody.string()
        val p = 2048
        val length = strBody.length
        Timber.tag("OkHttp").d("返回json:")
        if (length < p || length == p) Timber.tag("OkHttp").d(strBody) else {
            while (strBody.length > p) {
                val logContent = strBody.substring(0, p)
                strBody = strBody.replace(logContent, "")
                Timber.tag("OkHttp").d(logContent)
            }
            Timber.tag("OkHttp").d(strBody)
        }
        return response
    }
}