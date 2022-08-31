package com.lib.network.download

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.IOException


/**
 * @Author qiukeling
 * @Date 2020/7/9-9:58 AM
 * @Email qiukeling@nbhope.cn
 */
class DownloadResponseBody constructor(private val responseBody: ResponseBody, listener: DownloadListener?): ResponseBody() {
    private var bufferedSource: BufferedSource? = null

    init {
        listener?.onStart(responseBody)
    }

    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }


    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun source(): BufferedSource? {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(getSource(responseBody.source()))
        }
        return bufferedSource
    }

    private fun getSource(source: Source): Source {
        return object : ForwardingSource(source) {
            var downloadBytes = 0L

            @Throws(IOException::class)
            override fun read(buffer: Buffer, byteCount: Long): Long {
                val singleRead = super.read(buffer, byteCount)
                if (-1L != singleRead) {
                    downloadBytes += singleRead
                }
                return singleRead
            }
        }
    }
}