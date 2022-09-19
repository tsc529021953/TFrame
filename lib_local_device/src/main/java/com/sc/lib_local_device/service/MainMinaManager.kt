package com.sc.lib_local_device.service

import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.lib.tcp.MinaThread
import com.lib.tcp.bean.TCPMsgType
import com.lib.tcp.codec.ByteArrayCodecFactory
import com.lib.tcp.core.MinaConfig
import com.lib.tcp.enums.MinaEventEnum
import com.lib.tcp.event.MinaHandlerEvent
import org.apache.mina.filter.codec.textline.TextLineCodecFactory
import org.json.JSONObject
import timber.log.Timber
import java.nio.charset.Charset

/**
 * @author  tsc
 * @date  2022/9/2 16:36
 * @version 0.0.0-1
 * @description
 */
class MainMinaManager {
    private var minaThread: MinaThread? = null
    private var config: MinaConfig? = null
//    var port: Int = 8091
//    ip: String? =
    private var auth = false

    fun init(port: Int, ip: String) {
        // TODO 心跳添加
        if (config == null) {
//            val keepAliveMessageFactory =
//                    KeepAliveMessageFactoryImpl(MessageBuilder.getMessage(0x0002, deviceId, TCPMsgType.DEVICE_MSG, ""))
            config = MinaConfig.Builder()
//                    .ip(HopeDynConfig.instence.socketIp()!!)
//                    .port(HopeDynConfig.instence.socketPort()!!)
                .ip(ip)
//                    .ip("192.168.110.153")
                    .port(port)
                // Charset.forName("UTF-8"
                    .codecFactory(TextLineCodecFactory())
//                    .keepAliveMessageFactory(keepAliveMessageFactory)
//                    .keepAliveRequestTimeoutHandler(keepAliveMessageFactory)
                    .readBufferSize(1024)
                    .connectionTimeout(30)
                    .build()
        } else {
//            (config!!.keepAliveMessageFactory as KeepAliveMessageFactoryImpl).liveMessage = MessageBuilder.getMessage(0x0002, deviceId, TCPMsgType.DEVICE_MSG, "")
        }
    }

    fun isConnect(): Boolean {
        return minaThread?.isConnect() ?: false
    }

    fun startMina(delay: Long = 0L) {
        if (minaThread == null && config != null) {
            minaThread = MinaThread("minaThread", config!!, delay)
            minaThread?.start()
        }
    }

    val minaObserver =
            Observer<Any> {
                it as MinaHandlerEvent
                Timber.d("LTAG ${it.enum} ${Gson().toJson(it)}")
                when (it.enum) {
                    MinaEventEnum.MESSAGE_AUTH -> {
                        auth = true
                    }
                    MinaEventEnum.SESSION_OPEN -> {
                        // 连接成功！在此处发送数据出去
//                        Timber.i("LTAG SESSION_OPEN 信息 ${InSonaManager.getDeviceInfo()}")
//                        UHomeLocalReceiverHelper.invokeTcpMessage(it)
                    }
                    MinaEventEnum.SESSION_CLOSE -> {
//                        UHomeLocalReceiverHelper.invokeTcpMessage(it)
                    }
                    MinaEventEnum.MESSAGE_RECEIVE_OTHER -> {
//                        Timber.i("LTAG ${it.message?.getMessageBody()}")
//                        UHomeLocalReceiverHelper.invokeTcpMessage(it)
                    }
                    MinaEventEnum.MESSAGE_SEND -> {
//                        Timber.d("${it.enum.name} 原始消息: ${MessageParser.toHexString(it.message)}")
//                        Timber.d("${it.enum.name} ${String.format("%04x", it.message?.getMessageId()).toUpperCase()} ${ByteUtils.hexStringToStr(it.message?.getMessageBody()
//                                ?: "")}")
                    }
                    MinaEventEnum.MESSAGE_WRITE -> {
//                        if (auth) {
//                            it.message?.also {
//                                minaThread?.write(it)
//                            }
//                        }
                    }
                }
            }

    fun write(msg: String) {
        if (!isConnect()) return
        minaThread?.write(msg)
    }

    fun stopMina() {
        minaThread?.quitSafely()
        config = null
        minaThread = null
    }
}