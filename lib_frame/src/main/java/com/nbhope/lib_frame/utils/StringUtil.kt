package com.nbhope.lib_frame.utils

import java.util.regex.Matcher
import java.util.regex.Pattern


object StringUtil {

    /**
     * 判断一个字符串是否是一个合法的ip地址：
     * 1 首先检查字符串的长度 最短应该是0.0.0.0 7位 最长 000.000.000.000 15位
     * 2 按.符号进行拆分，拆分结果应该是4段
     * 3 检查每个字符串是不是都是数字
     */
    fun isIP(str: String): Boolean {
        // 1、首先检查字符串的长度 最短应该是0.0.0.0 7位 最长 000.000.000.000 15位
        if (str.length < 7 || str.length > 15) return false
        // 2、按.符号进行拆分，拆分结果应该是4段，"."、"|"、"^"等特殊字符必须用 \ 来进行转义
        // 而在java字符串中，\ 也是个已经被使用的特殊符号，也需要使用 \ 来转义
        val arr = str.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (arr.size != 4) return false
        // 3、检查每个字符串是不是都是数字,ip地址每一段都是0-255的范围
        for (i in 0..3) {
            if (!isNUM(arr[i]) || arr[i].length == 0 || arr[i].toInt() > 255 || arr[i].toInt() < 0) {
                return false
            }
        }
        return true
    }

    /**
     * 判断一个字符串是否是数字
     */
    fun isNUM(str: String?): Boolean {
        val p: Pattern = Pattern.compile("[0-9]*")
        val m: Matcher = p.matcher(str)
        return m.matches()
    }

}