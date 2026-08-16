package com.sc.tmp_cw.utils

/**
 * CRC16/X25 校验工具
 * 用于武汉通信协议的帧校验
 *
 * 多项式: 0x1021
 * 初始值: 0xFFFF
 * 异或值: 0xFFFF
 * 输入反转: true
 * 输出反转: true
 *
 * @author tsc
 * @date 2025/1/13
 */
object Crc16X25 {

    // CRC16/X25 查找表
    private val crcTable: IntArray by lazy {
        IntArray(256) { i ->
            var crc = i shl 8
            for (j in 0 until 8) {
                crc = if ((crc and 0x8000) != 0) {
                    (crc shl 1) xor 0x1021
                } else {
                    crc shl 1
                }
            }
            crc and 0xFFFF
        }
    }

    /**
     * 计算 CRC16/X25
     * @param data 输入数据
     * @param offset 起始偏移
     * @param length 长度
     * @return CRC16 值
     */
    fun calculate(data: ByteArray, offset: Int, length: Int): Int {
        var crc = 0xFFFF
        for (i in offset until offset + length) {
            val index = ((crc shr 8) xor (data[i].toInt() and 0xFF)) and 0xFF
            crc = ((crc shl 8) xor crcTable[index]) and 0xFFFF
        }
        // 异或输出
        crc = crc xor 0xFFFF
        // 输出反转（字节交换）
        return ((crc shl 8) and 0xFF00) or ((crc shr 8) and 0x00FF)
    }

    /**
     * 计算 CRC16/X25 (全部数据)
     */
    fun calculate(data: ByteArray): Int {
        return calculate(data, 0, data.size)
    }

    /**
     * 验证 CRC16/X25
     * @param data 包含CRC的完整数据
     * @param crcOffset CRC 在数据中的起始偏移
     * @return true 表示校验通过
     */
    fun verify(data: ByteArray, crcOffset: Int, dataStart: Int, dataLength: Int): Boolean {
        if (crcOffset + 2 > data.size) return false
        val calculated = calculate(data, dataStart, dataLength)
        val received = ((data[crcOffset].toInt() and 0xFF) shl 8) or
                (data[crcOffset + 1].toInt() and 0xFF)
        return calculated == received
    }
}
