package com.lib.network.utils

import timber.log.Timber
import java.io.UnsupportedEncodingException
import kotlin.experimental.xor

object HopeDesUtil {

    fun calc(dat: String, key: String, cid: String, sid: String, ver: String): String {
        //拼接字符串
        val X = splicingString(dat, key, cid, sid, ver)
        //计算MD5
        val md5 = calcMD5(X)
        //转为大写
        val Y = generateY(md5)
        //生成byte数组
        val yArray = getYArray(Y)
        //异或运算
        val result = xorOperation(yArray!!)
        //转16进制
        val hex = toHexString(result)
        //转为大写
        val hexUpper = hexUpperCase(hex)
//        println("hexUpper:$hexUpper")
        return hexUpper
    }

    private fun hexUpperCase(hex: String): String {
        return hex.toUpperCase()
    }

    private fun toHexString(result: Byte): String {
        var hex = Integer.toHexString(result.toInt() and 0xFF)
        if (hex.length == 1) {
            hex = "0$hex"
        }
        return hex
    }

    private fun xorOperation(yArray: ByteArray): Byte {
        var result = yArray[0]
        for (i in 1 until yArray.size) {
            result = (result xor yArray[i])
        }
        return result
    }

    private fun getYArray(y: String?): ByteArray? {
        var yArray: ByteArray? = null
        try {
            yArray = y?.toByteArray(charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            Timber.e(e)
        }

        return yArray
    }

    private fun generateY(md5: String?): String? {
        val Y = md5?.toUpperCase()
//        println("Y:$Y")
//        println("Y.length:" + Y?.length)
        return Y
    }

    private fun calcMD5(x: String): String? {
        val md5 = MD5Util.md5(x)
//        println("md5:$md5")
        return md5
    }

    private fun splicingString(dat: String, key: String, cid: String, sid: String, ver: String): String {
        val len = dat.length
//        println("len:$len")
        return len.toString() + key + cid + sid + ver
    }

    fun println(str: String) {
        println("Z-Test: $str")
    }
}