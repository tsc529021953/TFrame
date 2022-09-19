package com.lib.tcp

import android.os.HandlerThread
import android.util.Log
import com.lib.tcp.bean.TCPMessage
import com.lib.tcp.core.MinaClient
import com.lib.tcp.core.MinaConfig
import org.apache.mina.core.session.IoSession
import timber.log.Timber

class MinaThread constructor(name: String, private val config: MinaConfig, private val delay: Long = 0L) : HandlerThread(name, 10) {
    private var minaClient: MinaClient? = null
    private var reconnectTimes = 0
    private var stop = false

    private var isConnect = false

    override fun onLooperPrepared() {
        super.onLooperPrepared()
        if (minaClient == null) {
            minaClient = MinaClient(config)
            if (this.delay > 0) {
                try {
                    Thread.sleep(delay)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                    Timber.e(e)
                }
            }
            while (true) {
                isConnect = minaClient?.connection() ?: false
                Timber.d("stop $stop")
                if (stop){
                    minaClient?.disConnect()
                    break
                }
                Timber.d("isConnect $isConnect")
                if (isConnect) {
                    reconnectTimes = 0
                    break
                }
                try {
                    if (reconnectTimes < 12) {
                        reconnectTimes++
                    }
                    Thread.sleep(reconnectTimes * 10000L)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                    Timber.e(e)
                }
                minaClient?.disConnect()
                minaClient = MinaClient(config)
            }
        }
    }

    override fun quitSafely(): Boolean {
        stop = true
        minaClient?.disConnect()
        return super.quitSafely()
    }

    override fun quit(): Boolean {
        stop = true
        minaClient?.disConnect()
        return super.quit()
    }

    fun write(tcpMessage: TCPMessage) {
        try {
            if (minaClient?.session != null && minaClient?.session?.isActive == true) {
                minaClient?.session!!.write(tcpMessage)
            } else {
               Timber.e( "session is error session is null ${minaClient?.session != null} isActive ${minaClient?.session?.isActive}")
            }
        } catch (e: Throwable) {
           Timber.e( e.localizedMessage)
        }
    }

    fun write(tcpMessage: Any) {
        try {
            if (minaClient?.session != null && minaClient?.session?.isActive == true) {
                minaClient?.session!!.write(tcpMessage)
            } else {
                Timber.e( "session is error session is null ${minaClient?.session != null} isActive ${minaClient?.session?.isActive}")
            }
        } catch (e: Throwable) {
            Timber.e( e.localizedMessage)
        }
    }

    fun isConnect(): Boolean {
        return minaClient?.isConnect() ?: false
    }

    fun session(): IoSession? {
        return minaClient?.session
    }
}