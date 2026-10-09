package com.sc.xs_control.weight

import timber.log.Timber
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * UDP 指令发射/监听线程
 *
 * 统一使用 UDP：
 * 1. 发送：向指定 ip:port 发送指令字节（DatagramSocket send）。
 * 2. 接收：绑定本地端口监听，收到数据后回调（当前无接收协议，仅打印日志）。
 *
 * @author  tsc
 * @date  2026/10/8
 * @version 0.0.0-1
 * @description
 */
class UdpThread constructor(
    /** 本地监听端口 */
    private val listenPort: Int
) : Thread() {

    companion object {
        private const val TAG = "XCTAG"
        private const val BUFFER_SIZE = 1024
    }

    /** 运行标记位 */
    @Volatile
    private var isRun = false

    private var socket: DatagramSocket? = null

    /** 接收回调：(来源ip, 来源port, 数据) */
    var onReceive: ((String, Int, ByteArray) -> Unit)? = null

    override fun run() {
        try {
            socket = DatagramSocket(listenPort)
            socket?.broadcast = true
        } catch (e: Exception) {
            Timber.e("$TAG UDP 监听端口 $listenPort 启动失败 ${e.message}")
            e.printStackTrace()
            return
        }
        isRun = true
        Timber.i("$TAG UDP 监听启动成功 port=$listenPort")
        val buffer = ByteArray(BUFFER_SIZE)
        while (isRun) {
            try {
                val packet = DatagramPacket(buffer, buffer.size)
                socket?.receive(packet) ?: break
                val address = packet.address.hostAddress ?: packet.address.toString()
                val port = packet.port
                val data = packet.data.copyOfRange(0, packet.length)
                Timber.i("$TAG UDP 收到数据 来自 $address:$port hex=[${toHexString(data)}] ascii=[${toAsciiString(data)}] len=${data.size}")
                onReceive?.invoke(address, port, data)
            } catch (e: Exception) {
                if (isRun) {
                    Timber.e("$TAG UDP 接收异常 ${e.message}")
                    e.printStackTrace()
                }
            }
        }
        closeSocket()
        Timber.i("$TAG UDP 监听线程退出")
    }

    /**
     * 发送数据到指定地址
     */
    fun send(ip: String, port: Int, data: ByteArray) {
        Thread {
            try {
                val packet = DatagramPacket(data, data.size, InetAddress.getByName(ip), port)
                // 发送复用监听socket，若未启动则临时创建
                val sendSocket = if (socket != null && !socket!!.isClosed) socket else DatagramSocket()
                sendSocket?.send(packet)
                Timber.i("$TAG UDP 发送数据 到 $ip:$port hex=[${toHexString(data)}] ascii=[${toAsciiString(data)}]")
                if (sendSocket != null && sendSocket !== socket) sendSocket.close()
            } catch (e: Exception) {
                Timber.e("$TAG UDP 发送失败 $ip:$port ${e.message}")
                e.printStackTrace()
            }
        }.start()
    }

    fun close() {
        isRun = false
        closeSocket()
    }

    private fun closeSocket() {
        try {
            socket?.disconnect()
            socket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun toHexString(data: ByteArray): String =
        data.joinToString(" ") { String.format("%02x", it) }

    private fun toAsciiString(data: ByteArray): String {
        val sb = StringBuilder()
        for (b in data) {
            val c = b.toInt() and 0xFF
            if (c in 32..126) sb.append(c.toChar()) else sb.append('.')
        }
        return sb.toString()
    }
}
