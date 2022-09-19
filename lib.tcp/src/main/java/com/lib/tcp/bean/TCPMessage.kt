package com.lib.tcp.bean

import java.io.Serializable

/**
 * 作者：kelingqiu on 17/11/6 10:26
 * 邮箱：42747487@qq.com
 */
class TCPMessage: Serializable {
    private val serialVersionUID = 4666326960908432088L
    // 开始标识位，一个字节
    private var startTag: Byte? = null

    // 消息id，两个字节
    private var messageId: Int? = null

    // 消息体属性保留位
    private var messageCata: String? = null

    // 消息体长度(14BIT)
    private var messageLength: Int? = null

    // 平台统一分配的唯一编号，注册时终端随机数(10BYTE)
    private var deviceId: Long? = null

    // 消息体内容
    private var messagesBody: String? = null

    // 消息校验码
    private var messageCode: Byte? = null

    // 结束标识位，最后一个字节
    private var overTag: Byte = 0

    fun getStartTag(): Byte? {
        return startTag
    }

    fun setStartTag(startTag: Byte?) {
        this.startTag = startTag
    }

    fun getMessageId(): Int? {
        return messageId
    }

    fun setMessageId(messageId: Int?) {
        this.messageId = messageId
    }


    fun getMessageCata(): String? {
        return messageCata
    }

    fun setMessageCata(messageCata: String?) {
        this.messageCata = messageCata
    }


    fun getMessageLength(): Int? {
        return messageLength
    }

    fun setMessageLength(messageLength: Int?) {
        this.messageLength = messageLength
    }

    fun getDeviceId(): Long? {
        return deviceId
    }

    fun setDeviceId(deviceId: Long?) {
        this.deviceId = deviceId
    }


    fun getMessageBody(): String? {
        return messagesBody
    }

    fun setMessageBody(messageBody: String?) {
        this.messagesBody = messageBody
    }

    fun getMessageCode(): Byte? {
        return messageCode
    }

    fun setMessageCode(messageCode: Byte?) {
        this.messageCode = messageCode
    }

    fun setOverTag(overTag: Byte) {
        this.overTag = overTag
    }

    fun getOverTag(): Byte? {
        return overTag
    }

    fun setOverTag(overTag: Byte?) {
        this.overTag = overTag!!
    }
}