package com.sc.tmp_cw.utils

/**
 * CRC16/X25 校验工具
 * 用于武汉通信协议的帧校验
 *
 * 标准参数 (check: "123456789" => 0x906E):
 * 多项式: 0x1021 (反射后右移异或 0x8408)
 * 初始值: 0xFFFF
 * 异或值: 0xFFFF
 * 输入反转: true
 * 输出反转: true
 *
 * 注意: 输入/输出必须是按位反射, 不能用字节交换代替位反转,
 * 否则与PISC设备的校验结果不一致。
 *
 * @author tsc
 * @date 2025/1/13
 */
object Crc16X25 {

    /**
     * 计算 CRC16/X25 (标准反射实现)
     * @param data 输入数据
     * @param offset 起始偏移
     * @param length 长度
     * @return CRC16 值
     */
    fun calculate(data: ByteArray, offset: Int, length: Int): Int {
        var crc = 0xFFFF
        for (i in offset until offset + length) {
            crc = crc xor (data[i].toInt() and 0xFF)
            for (j in 0 until 8) {
                crc = if ((crc and 0x0001) != 0) {
                    (crc shr 1) xor 0x8408
                } else {
                    crc shr 1
                }
            }
        }
        // 异或输出
        return (crc xor 0xFFFF) and 0xFFFF
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
