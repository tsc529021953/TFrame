package com.lib.network.utils

import java.io.File

object DownloadUtils {
    fun getTempFile(url: String, filePath: String, checkMd5: Boolean? = true): File {
        return if (checkMd5!!) {
            val parentFile: File = File(filePath).parentFile
            val md5 = bytes2HexString(url.toByteArray())
            File(parentFile.absolutePath, "$md5.temp")
        } else {
            File(filePath)
        }

    }

    private val HEX_DIGITS = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

    private fun bytes2HexString(bytes: ByteArray?): String {
        if (bytes == null) {
            return ""
        }
        val len = bytes.size
        if (len <= 0) {
            return ""
        }
        val ret = CharArray(len shl 1)
        var i = 0
        var j = 0
        while (i < len) {
            ret[j++] = HEX_DIGITS[bytes[i].toInt().shr(4) and 0x0f]
            ret[j++] = HEX_DIGITS[bytes[i].toInt() and 0x0f]
            i++
        }
        return String(ret)
    }
}