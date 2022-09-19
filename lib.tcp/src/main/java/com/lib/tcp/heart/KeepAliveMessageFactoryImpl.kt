package com.lib.tcp.heart

import com.jeremyliao.liveeventbus.LiveEventBus
import com.lib.tcp.bean.TCPMessage
import com.lib.tcp.enums.MinaEventEnum
import com.lib.tcp.event.MinaHandlerEvent
import com.lib.tcp.utils.MessageBuilder
import com.lib.tcp.utils.MessageParser
import org.apache.mina.core.buffer.IoBuffer
import org.apache.mina.core.session.IoSession
import org.apache.mina.filter.keepalive.KeepAliveFilter
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler
import timber.log.Timber

class KeepAliveMessageFactoryImpl(var liveMessage: TCPMessage) : KeepAliveMessageFactory,
        KeepAliveRequestTimeoutHandler {
    private var timeOutCount = 0

    override fun keepAliveRequestTimedOut(filter: KeepAliveFilter?, session: IoSession?) {
        if (timeOutCount < 3) {
            timeOutCount++
            Timber.e("HEART time out $timeOutCount")
        } else {
            Timber.e("HEART time out reset $timeOutCount")
            timeOutCount = 0
            session?.closeNow()
        }
    }

    private var keepAliveEnable = false
    override fun getResponse(session: IoSession?, request: Any): Any? {
        Timber.e("HEART is getResponse")
        return null
    }

    //检测是否是心跳请求
    override fun isRequest(session: IoSession?, message: Any): Boolean {
        if (message is TCPMessage && message.getMessageId() == 0x0002) {
            Timber.e("HEART is request")
            return true
        }
        return false
    }

    //检测是否是回复请求
    override fun isResponse(session: IoSession?, message: Any): Boolean {
        var isResponse = false
        if (message is TCPMessage && message.getMessageId() == 0x8001 && message.getMessageBody() != null) {
            val msg = MessageParser.decodeBody(message.getMessageBody()!!)
            if (msg.messageId == 0x0002) {
                Timber.e("HEART RECEIVE")
//                Timber.d(MessageParser.toHexString(message))
                isResponse = true
                timeOutCount = 0
            } else if (msg.messageId == 0x0004) {
                LiveEventBus.get().with(MinaHandlerEvent::class.java.simpleName).post(MinaHandlerEvent(MinaEventEnum.MESSAGE_AUTH))
                keepAliveEnable = msg.result == 50000
            }
        }
//        timeOutCount = 0
//        Timber.e("HEART time out reset $message")
        return isResponse
    }

    //发送心跳
    override fun getRequest(session: IoSession?): Any? {
        if (!keepAliveEnable) {
            Timber.e("keepAliveEnable false")
            return null
        }
        Timber.e("HEART SEND")
//        Timber.d(MessageParser.toHexString(liveMessage))
        return IoBuffer.wrap(MessageBuilder.encode(liveMessage))
    }

    fun resetKeepAliveEnable(){
        keepAliveEnable = false
    }
}