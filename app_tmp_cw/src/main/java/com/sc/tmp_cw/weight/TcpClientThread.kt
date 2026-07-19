package com.sc.tmp_cw.weight

import com.nbhope.lib_frame.utils.DataUtil
import timber.log.Timber
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket

/**
 * TCP 客户端线程 — 武汉通信协议 (FlavorB)
 *
 * 用于连接 PISC 服务器 (默认 10.254.21.7:5000)，
 * 发送设备心跳 0xA1，接收 PISC 下发的数据。
 *
 * @author tsc
 * @date 2025/1/13
 */
class TcpClientThread(
    private val serverIp: String,
    private val serverPort: Int,
    private val onDataReceived: (ByteArray) -> Unit,
    private val onConnected: (() -> Unit)? = null,
    private val onDisconnected: (() -> Unit)? = null,
    private val onError: ((String) -> Unit)? = null,
) : Thread() {

    companion object {
        private const val TAG = "TcpClient"
        private const val RECONNECT_DELAY_MS = 5000L
        private const val CONNECT_TIMEOUT_MS = 5000
        private const val READ_BUFFER_SIZE = 4096
    }

    @Volatile
    private var isRunning = false

    private var socket: Socket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    /** 缓冲区，用于拼接不完整的帧 */
    private val frameBuffer = java.io.ByteArrayOutputStream()

    override fun run() {
        super.run()
        isRunning = true
        Timber.i("$TAG TCP客户端启动 $serverIp:$serverPort")

        while (isRunning) {
            try {
                connect()
                onConnected?.invoke()
                Timber.i("$TAG TCP已连接到 $serverIp:$serverPort")
                readLoop()
            } catch (e: InterruptedException) {
                Timber.i("$TAG 线程被中断")
                break
            } catch (e: Exception) {
                Timber.e(e, "$TAG 连接/读取异常: ${e.message}")
                onError?.invoke(e.message ?: "unknown error")
            } finally {
                disconnect()
                onDisconnected?.invoke()
            }

            // 重连等待
            if (isRunning) {
                Timber.i("$TAG ${RECONNECT_DELAY_MS}ms后重连...")
                try {
                    sleep(RECONNECT_DELAY_MS)
                } catch (e: InterruptedException) {
                    break
                }
            }
        }
        Timber.i("$TAG TCP客户端已停止")
    }

    private fun connect() {
        socket = Socket()
        socket?.connect(InetSocketAddress(serverIp, serverPort), CONNECT_TIMEOUT_MS)
        socket?.soTimeout = 0 // 阻塞读取，不超时
        socket?.tcpNoDelay = true
        socket?.keepAlive = true
        inputStream = socket?.getInputStream()
        outputStream = socket?.getOutputStream()
    }

    private fun readLoop() {
        val buffer = ByteArray(READ_BUFFER_SIZE)
        val input = inputStream ?: return

        while (isRunning && socket?.isConnected == true) {
            val bytesRead = input.read(buffer)
            if (bytesRead == -1) {
                Timber.w("$TAG 服务端断开连接")
                break
            }

            // 将数据加入缓冲区
            frameBuffer.write(buffer, 0, bytesRead)
            // 尝试从缓冲区提取完整帧
            processBuffer()
        }
    }

    /**
     * 从缓冲区提取完整的武汉协议帧 (0x75AA...0x5F)
     */
    private fun processBuffer() {
        val rawData = frameBuffer.toByteArray()
        if (rawData.size < 9) return // 最小帧长度

        var searchPos = 0
        val extractedFrames = mutableListOf<ByteArray>()

        while (searchPos <= rawData.size - 2) {
            // 查找帧头 0x75 0xAA
            if (rawData[searchPos] == 0x75.toByte() && rawData[searchPos + 1] == 0xAA.toByte()) {
                // 读取长度字段 (字节2-3)
                if (searchPos + 4 > rawData.size) break

                val dataLength = ((rawData[searchPos + 2].toInt() and 0xFF) shl 8) or
                        (rawData[searchPos + 3].toInt() and 0xFF)
                val frameSize = 2 + 2 + dataLength + 2 + 1 // header + length + data + crc + tail

                if (searchPos + frameSize <= rawData.size) {
                    // 验证帧尾
                    if (rawData[searchPos + frameSize - 1] == 0x5F.toByte()) {
                        val frame = rawData.copyOfRange(searchPos, searchPos + frameSize)
                        extractedFrames.add(frame)
                        searchPos += frameSize
                    } else {
                        // 帧尾不匹配，跳过这个假帧头
                        Timber.w("$TAG 帧尾不匹配, 跳过1字节")
                        searchPos++
                    }
                } else {
                    // 数据不完整，等待更多数据
                    break
                }
            } else {
                searchPos++
            }
        }

        // 重建缓冲区：保留未处理的数据
        frameBuffer.reset()
        if (searchPos < rawData.size) {
            frameBuffer.write(rawData, searchPos, rawData.size - searchPos)
        }

        // 回调已提取的帧
        for (frame in extractedFrames) {
            try {
                val hex = DataUtil.byteArray2HexString(frame)
                Timber.d("$TAG 收到TCP帧: ${frame.size}字节 $hex")
                onDataReceived(frame)
            } catch (e: Exception) {
                Timber.e(e, "$TAG 回调异常")
            }
        }
    }

    /**
     * 发送数据
     */
    @Synchronized
    fun send(data: ByteArray): Boolean {
        return try {
            outputStream?.write(data)
            outputStream?.flush()
            Timber.d("$TAG 发送: ${data.size}字节")
            true
        } catch (e: Exception) {
            Timber.e(e, "$TAG 发送失败")
            false
        }
    }

    /**
     * 发送 hex 字符串数据
     */
    fun sendHex(hex: String): Boolean {
        val data = DataUtil.hexStringToBytes(hex) ?: return false
        return send(data)
    }

    private fun disconnect() {
        try {
            inputStream?.close()
        } catch (e: Exception) { /* ignore */ }
        try {
            outputStream?.close()
        } catch (e: Exception) { /* ignore */ }
        try {
            socket?.close()
        } catch (e: Exception) { /* ignore */ }
        inputStream = null
        outputStream = null
        socket = null
        frameBuffer.reset()
    }

    fun stopClient() {
        isRunning = false
        disconnect()
        interrupt()
    }

    fun isConnected(): Boolean {
        return socket?.isConnected == true && !socket?.isClosed!! && isRunning
    }
}
