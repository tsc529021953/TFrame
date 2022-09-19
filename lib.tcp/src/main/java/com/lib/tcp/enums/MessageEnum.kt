package com.lib.tcp.enums

import com.lib.tcp.enums.BussinessDefinition


/**
 * 作者：kelingqiu on 17/11/6 15:49
 * 邮箱：42747487@qq.com
 */
enum class MessageEnum private constructor(val code: Int?, val message: String?) : BussinessDefinition {
    MSG_SUCC(0x0000, "成功！"),
    MSG_NEED(0x0001, "消息内容不完整，标识位+消息头+校验码+标识位小于最小长度！"),
    MSG_EORR(0x0002, "消息格式错误，缺少开始或结束标识位！"),
    MSG_MARK(0x0003, "消息格式错误，开始标识位或结束标识位异常！"),
    MSG_XORE(0x0004, "消息校验失败，请检查消息指令中校验码的正确性！"),
    MSG_HBDE(0x0005, "消息内容不完整，消息头+消息体+校验码小于最小长度！"),
    MSG_IDER(0x0006, "消息内容有误，消息编号为空或不合法！"),
    MSG_DEER(0x0007, "消息内容有误，设备编号为空或不合法！");

    override fun getBCode(): Int? {
        return this.code
    }

    override fun getBMessage(): String? {
        return this.message
    }
}