package com.lib.tcp.core

import com.jeremyliao.liveeventbus.LiveEventBus
import com.lib.tcp.bean.TCPMessage
import com.lib.tcp.enums.MinaEventEnum
import com.lib.tcp.event.MinaHandlerEvent
import org.apache.mina.core.service.IoHandlerAdapter
import org.apache.mina.core.session.IdleStatus
import org.apache.mina.core.session.IoSession
import org.apache.mina.filter.codec.ProtocolCodecFilter
import org.apache.mina.filter.keepalive.KeepAliveFilter
import org.apache.mina.filter.logging.LoggingFilter
import org.apache.mina.transport.socket.nio.NioSocketConnector
import timber.log.Timber
import java.net.InetSocketAddress


class MinaClient constructor(config: MinaConfig) {
    private val TAG = MinaClient::class.java.simpleName
    private var mConnection: NioSocketConnector? = NioSocketConnector()
    private var mAddress: InetSocketAddress? = InetSocketAddress(config.ip, config.port)

    @get:JvmName("session")
    var session: IoSession? = null

    init {
        Timber.d("LTAG ${config.ip}, ${config.port} ${config.coderFactory}")
        mConnection!!.setDefaultRemoteAddress(mAddress)
        mConnection!!.sessionConfig.readBufferSize = config.readBufferSize
        mConnection!!.filterChain.addLast("Logger", LoggingFilter("mina"))
        mConnection!!.filterChain.addLast("codec", ProtocolCodecFilter(config.coderFactory))
        if (config.keepAliveMessageFactory != null && config.keepAliveRequestTimeoutHandler != null) {
            val kal = KeepAliveFilter(config.keepAliveMessageFactory, IdleStatus.WRITER_IDLE, config.keepAliveRequestTimeoutHandler)
            kal.isForwardEvent = true
            kal.requestInterval = 30
            kal.requestTimeout = 5
            mConnection!!.filterChain.addLast("heart", kal)
        }
        mConnection!!.handler = DefaultHandler()
        mConnection!!.sessionConfig.readBufferSize = 1024
        mConnection!!.sessionConfig.setIdleTime(IdleStatus.READER_IDLE, 60)
    }

    fun connection(): Boolean {
        Timber.d("Mina connection")
        return try {
            val futrue = mConnection?.connect()
            futrue?.awaitUninterruptibly()
            futrue?.session != null
        } catch (e: Exception) {
            Timber.e("connection error:${e.message}")
            //不关闭的话会运行一段时间后抛出，too many open files异常，导致无法连接
            mConnection?.dispose()
            false
        }
    }

    fun isConnect(): Boolean {
        return mConnection?.isActive ?: false
    }

    fun disConnect() {
        mConnection?.dispose()
        mConnection = null
        session?.closeNow()
        session = null
        mAddress = null
    }

    inner class DefaultHandler : IoHandlerAdapter() {
        private val TAG = DefaultHandler::class.java.simpleName

        override fun messageSent(session: IoSession?, message: Any) {
            if (message is TCPMessage) {
                LiveEventBus.get().with(MinaHandlerEvent::class.java.simpleName).post(MinaHandlerEvent(MinaEventEnum.MESSAGE_SEND, message))
            }
            super.messageSent(session, message)
        }

        override fun messageReceived(session: IoSession?, message: Any) {
            Timber.i("LTAG messageReceived $message")
            if (message is TCPMessage) {
                LiveEventBus.get().with(MinaHandlerEvent::class.java.simpleName).post(MinaHandlerEvent(MinaEventEnum.MESSAGE_RECEIVE, message))
            } else {
                // 当用于其他连接时，其传输的数据可能不为TCPMessage,需要另外转发
                Timber.i("LTAG MESSAGE_RECEIVE_OTHER ${message}")
                var msg = TCPMessage()
                msg.setMessageBody(message.toString())
                LiveEventBus.get().with(MinaHandlerEvent::class.java.simpleName).post(MinaHandlerEvent(MinaEventEnum.MESSAGE_RECEIVE_OTHER, msg))
            }
            super.messageReceived(session, message)
        }

        override fun sessionCreated(session: IoSession?) {
            super.sessionCreated(session)
            Timber.d("sessionCreated")
        }

        override fun sessionClosed(session: IoSession?) {
            super.sessionClosed(session)
            Timber.d("sessionClosed")
            LiveEventBus.get().with(MinaHandlerEvent::class.java.simpleName).post(MinaHandlerEvent(MinaEventEnum.SESSION_CLOSE))
        }

        override fun sessionOpened(session: IoSession?) {
            super.sessionOpened(session)
            this@MinaClient.session = session
            Timber.d("sessionOpened")
            LiveEventBus.get().with(MinaHandlerEvent::class.java.simpleName).post(MinaHandlerEvent(MinaEventEnum.SESSION_OPEN))
        }

        override fun sessionIdle(session: IoSession?, status: IdleStatus?) {
            super.sessionIdle(session, status)
            Timber.d("sessionIdle")
        }

        override fun exceptionCaught(session: IoSession?, cause: Throwable?) {
            super.exceptionCaught(session, cause)
            Timber.d("exceptionCaught:${cause?.message}")
        }
    }
}