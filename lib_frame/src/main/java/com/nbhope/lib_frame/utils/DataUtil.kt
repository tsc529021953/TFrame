package com.nbhope.lib_frame.utils

import java.io.UnsupportedEncodingException
import java.util.*

object DataUtil {

    fun byteToString(data: ByteArray): String {
        var index = data.size
        for (i in data.indices) {
            if (data[i].toInt() == 0) {
                index = i
                break
            }
        }
        val temp = ByteArray(index)
        Arrays.fill(temp, 0.toByte())
        System.arraycopy(data, 0, temp, 0, index)
        val str: String
        try {
            str = String(temp, charset("GBK"))
        } catch (e: UnsupportedEncodingException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            return ""
        }
        return str
    }

    fun hex2String(hex: String): String {
        val charArray = hex.toCharArray()
        val length = charArray.size
        val times = length / 2
        var c1i = 0
        while (c1i < times) {
            val c2i = c1i + 1
            val c1 = charArray[c1i]
            val c2 = charArray[c2i]
            val c3i = length - c1i - 2
            val c4i = length - c1i - 1
            charArray[c1i] = charArray[c3i]
            charArray[c2i] = charArray[c4i]
            charArray[c3i] = c1
            charArray[c4i] = c2
            c1i += 2
        }
        return String(charArray)
    }

    fun hexStringToBytes(hexString: String?): ByteArray? {
        var hexString = hexString
        if (hexString == null || hexString == "") {
            return null
        }
        hexString = hexString.trim { it <= ' ' }
        hexString = hexString.uppercase(Locale.getDefault())
        val length = hexString.length / 2
        val hexChars = hexString.toCharArray()
        val d = ByteArray(length)
        for (i in 0 until length) {
            val pos = i * 2
            d[i] = (charToByte(hexChars[pos]).toInt() shl 4 or charToByte(hexChars[pos + 1]).toInt()).toByte()
        }
        return d
    }

    fun charToByte(c: Char): Byte {
        return "0123456789ABCDEF".indexOf(c).toByte()
    }

}