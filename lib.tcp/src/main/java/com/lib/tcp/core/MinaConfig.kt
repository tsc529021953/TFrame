package com.lib.tcp.core

import org.apache.mina.filter.codec.ProtocolCodecFactory
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler
import java.lang.IllegalArgumentException

class MinaConfig internal constructor(build: Builder) {
    @get:JvmName("ip")
    val ip:String = build.ip!!

    @get:JvmName("port")
    val port:Int = build.port!!

    @get:JvmName("readBufferSize")
    val readBufferSize = build.readBufferSize

    @get:JvmName("connectionTimeout")
    val connectionTimeout = build.connectionTimeout

    @get:JvmName("coderFactory")
    val coderFactory = build.codecFactory

    @get:JvmName("keepAliveMessageFactory")
    val keepAliveMessageFactory = build.keepAliveMessageFactory

    @get:JvmName("keepAliveRequestTimeoutHandler")
    val keepAliveRequestTimeoutHandler = build.keepAliveRequestTimeoutHandler

    class Builder constructor() {
        internal var ip: String? = null

        internal var port: Int? = null

        internal var readBufferSize: Int = 1024

        internal var connectionTimeout: Int = 10000

        internal var codecFactory: ProtocolCodecFactory = ObjectSerializationCodecFactory()

        internal var keepAliveMessageFactory: KeepAliveMessageFactory? = null

        internal var keepAliveRequestTimeoutHandler: KeepAliveRequestTimeoutHandler? = null

        fun ip(ip: String) = apply {
            this.ip = ip
        }

        fun port(port: Int) = apply {
            this.port = port
        }

        fun readBufferSize(readBufferSize: Int) = apply {
            this.readBufferSize = readBufferSize
        }

        fun connectionTimeout(connectionTimeout: Int) = apply {
            this.connectionTimeout = connectionTimeout
        }

        fun codecFactory(codecFactory: ProtocolCodecFactory) = apply {
            this.codecFactory = codecFactory
        }

        fun keepAliveMessageFactory(keepAliveMessageFactory: KeepAliveMessageFactory) = apply {
            this.keepAliveMessageFactory = keepAliveMessageFactory
        }

        fun keepAliveRequestTimeoutHandler(keepAliveRequestTimeoutHandler: KeepAliveRequestTimeoutHandler) = apply {
            this.keepAliveRequestTimeoutHandler = keepAliveRequestTimeoutHandler
        }

        @Throws(IllegalArgumentException::class)
        fun build(): MinaConfig {
            if (this.ip == null || this.port == null)
                throw IllegalArgumentException("ip or port must not be null")
            return MinaConfig(this)
        }
    }
}