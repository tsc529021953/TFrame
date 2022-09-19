package com.lib.tcp.consts

/**
 * 作者：kelingqiu on 17/11/6 10:39
 * 邮箱：42747487@qq.com
 */
class MessageConst private constructor(){

    companion object {
        /********************************************************************
         * 转义：0x7e <————> 0x7d 后紧跟一个 0x02
         * 还原：0x7d <————> 0x7d 后紧跟一个 0x01
         */
        // 消息标识位
        val MESSAGE_FLAG: Byte = 0x7E

        // 消息指令流水号最大值
        val MESSAGE_MAXSERIAL = 0xFFFF

        // 一个消息最小的长度定义，标识位+消息头+校验码+标识位
        val MESSAGE_MINLEN = 18

        // 一个消息最小的消息头+消息体+校验码长度定义，消息体可以为空
        val MESSAGE_HBCODE = 16

        // 消息指令进制定义
        val MESSAGE_RADIX = 16

        // 消息体属性位长度
        val ATTRIBUTE_LEN = 16

        // 消息转义码
        val CONVERT_FLAG: Byte = 0x7D

        // 0x7E转义附加码
        val CONVERT_SUFFIX: Byte = 0x02

        // 0x7D转义附加码
        val CONVERT_PREFIX: Byte = 0x01

        // 补位符号
        val REPAIR_BIT = "0"

        // 明文消息属性加密标志二进制位
        val MESSAGE_EXPRESS = "0"

        // 密文消息属性加密标志二进制位
        val MESSAGE_ENCRYPT = "1"

        // 消息分包标志二进制位
        val MESSAGE_PACKAGE = "1"

        // 消息未分包标志二进制位
        val MESSAGE_UNEPACK = "0"

        // 消息体属性保留位
        val MESSAGE_ATTRREP = "00"

        // 默认的终端设备编号
        val MESSAGE_DEVICE = 236523015018623455L

        // 将整数转化为两位十六进制字符串的正则定义
        val TWO_HEX_FORMAT = "%02x"

        // 将整数转化为四位十六进制字符串的正则定义
        val FOUR_HEX_FORMAT = "%04x"

        // 将整数转化为八位十六进制字符串的正则定义
        val EIGHT_HEX_FORMAT = "%08x"

        //DeviceId长度
        val DEVICEID_LENGTH = 18

        //设备编号长度
        val PRIMARY_LENGTH = 20

        // 设备编号正数补位
        val PLUS_REPAIR = "A"

        // 设备编号负数补位
        val MINU_REPIAR = "F"
    }
}