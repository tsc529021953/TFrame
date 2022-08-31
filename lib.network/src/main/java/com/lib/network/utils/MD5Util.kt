package com.lib.network.utils

import java.security.MessageDigest

object MD5Util {
    private val HEX_DIGITS = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

    fun md5(pwd: String): String? {
        try {
            //使用平台的默认字符集将此 String 编码为 byte序列，并将结果存储到一个新的 byte数组中
            val btInput = pwd.toByteArray()

            //信息摘要是安全的单向哈希函数，它接收任意大小的数据，并输出固定长度的哈希值。
            val mdInst = MessageDigest.getInstance("MD5")

            //MessageDigest对象通过使用 update方法处理数据， 使用指定的byte数组更新摘要
            mdInst.update(btInput)

            // 摘要更新之后，通过调用digest（）执行哈希计算，获得密文
            val md = mdInst.digest()

            // 把密文转换成十六进制的字符串形式
            val j = md.size
            val str = CharArray(j * 2)
            var k = 0
            for (i in 0 until j) {   //  i = 0
                val byte0 = md[i]  //95
                str[k++] = HEX_DIGITS[byte0.toInt().ushr(4) and 0xf]    //    5
                str[k++] = HEX_DIGITS[byte0.toInt() and 0xf]   //   F
            }

            //返回经过加密后的字符串
            return String(str)

        } catch (e: Exception) {
            return null
        }

    }
}