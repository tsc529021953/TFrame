package com.lib.network

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import com.lib.network.api.DownloadApi
import com.lib.network.download.DownloadCallback
import com.lib.network.download.DownloadInterceptor
import com.lib.network.download.DownloadListener
import com.lib.network.utils.DownloadUtils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.util.concurrent.TimeUnit


object DownloadFactory {
    private const val TIME_OUT_SECOND = 15L
    private var mBuilder: OkHttpClient.Builder? = null
    private var interceptor = DownloadInterceptor()

    private fun getDownloadRetrofit(): Retrofit {
        val headerInterceptor = Interceptor { chain ->
            val originalRequest: Request = chain.request()
            val requestBuilder: Request.Builder = originalRequest.newBuilder()
                    .addHeader("Accept-Encoding", "gzip")
                    .method(originalRequest.method(), originalRequest.body())
            val request: Request = requestBuilder.build()
            chain.proceed(request)
        }

        val logInterceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            logInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        }

        if (null == mBuilder) {
            mBuilder = OkHttpClient.Builder()
                    .connectTimeout(TIME_OUT_SECOND, TimeUnit.SECONDS)
                    .readTimeout(TIME_OUT_SECOND, TimeUnit.SECONDS)
                    .writeTimeout(TIME_OUT_SECOND, TimeUnit.SECONDS)
                    .addInterceptor(headerInterceptor)
                    .addInterceptor(logInterceptor)
                    .addInterceptor(interceptor)
        }

        return Retrofit.Builder()
//                .baseUrl("http://${BuildConfig.HTTP_IP}:${BuildConfig.HTTP_PORT}/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(mBuilder!!.build())
                .build()
    }

    suspend fun download(url: String, filePath: String, chekMD5:Boolean?=true,callback: DownloadCallback?) {
        interceptor.listener = object : DownloadListener {
            override fun onStart(responseBody: ResponseBody) {
                saveFile(responseBody, url, filePath,chekMD5, callback)
            }
        }

        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(filePath)) {
            callback?.onError("url or path empty")
            return
        }
        val oldFile = File(filePath)
        if (oldFile.exists()) {
            callback?.onFinish(oldFile)
            return
        }

        getDownloadRetrofit().create(DownloadApi::class.java).downloadFile("bytes=" + DownloadUtils.getTempFile(url, filePath, chekMD5).length() + "-", url)
    }

    private fun saveFile(responseBody: ResponseBody, url: String, filePath: String,chekMD5: Boolean?=true, callback: DownloadCallback?) {
        Handler(Looper.getMainLooper()).post {
            callback?.onStart()
        }
        var downloadSuccess = true
        val tempFile: File = DownloadUtils.getTempFile(url, filePath,chekMD5)
        try {
            writeFileToDisk(responseBody, tempFile.absolutePath, callback)
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e(e)
            downloadSuccess = false
        }
        if (downloadSuccess) {
            val renameSuccess = tempFile.renameTo(File(filePath))
            Handler(Looper.getMainLooper()).post {
                if (null != callback && renameSuccess) {
                    callback.onFinish(File(filePath))
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun writeFileToDisk(responseBody: ResponseBody, filePath: String, callback: DownloadCallback?) {
        val totalByte = responseBody.contentLength()
        var downloadByte: Long = 0
        val file = File(filePath)
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        val buffer = ByteArray(1024 * 4)
        val randomAccessFile = RandomAccessFile(file, "rwd")
        val tempFileLen = file.length()
        randomAccessFile.seek(tempFileLen)
        var lastProgress  = 0
        var progress = 0
        val allSize = tempFileLen + totalByte
        while (true) {
            val len = responseBody.byteStream().read(buffer)
            if (len == -1) {
                break
            }
            randomAccessFile.write(buffer, 0, len)
            downloadByte += len.toLong()
            lastProgress = progress
            progress = (downloadByte * 100 /allSize).toInt()
            if (progress > 0 && progress != lastProgress) {
                callbackProgress(tempFileLen + totalByte, tempFileLen + downloadByte,progress, callback)
            }
        }
        randomAccessFile.close()
    }

    private fun callbackProgress(totalByte: Long, downloadByte: Long,progress:Int, callback: DownloadCallback?) {
        Handler(Looper.getMainLooper()).post { callback?.onProgress(totalByte, downloadByte, progress) }
    }
}