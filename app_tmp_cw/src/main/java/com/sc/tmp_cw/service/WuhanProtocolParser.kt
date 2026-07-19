package com.sc.tmp_cw.service

import com.nbhope.lib_frame.utils.DataUtil
import com.sc.tmp_cw.bean.*
import com.sc.tmp_cw.utils.Crc16X25
import timber.log.Timber

/**
 * 武汉通信协议 (FlavorB) 解析器
 *
 * 帧格式: 0x75 0xAA + Length(2,从字节2计) + Command(1) + SrcID(1) + Data + CRC16(2) + 0x5F
 *
 * 兼容策略:
 * - 自动检测协议: 检查字节0-1是否为 0x75 0xAA
 * - 检测到武汉协议→解析新协议
 * - 检测到旧协议("5041")→走原有的 MessageHandler.handleMessage()
 *
 * @author tsc
 * @date 2025/1/13
 */
object WuhanProtocolParser {

    private const val TAG = "WuhanProtocol"

    // 帧结构常量
    private const val HEADER_SIZE = 2      // 0x75 0xAA
    private const val LENGTH_SIZE = 2      // UINT16
    private const val CMD_SIZE = 1         // 命令号
    private const val SRC_ID_SIZE = 1      // 源/目的ID
    private const val CRC_SIZE = 2         // CRC16
    private const val TAIL_SIZE = 1        // 0x5F
    private const val MIN_FRAME_SIZE = HEADER_SIZE + LENGTH_SIZE + CMD_SIZE + SRC_ID_SIZE + CRC_SIZE + TAIL_SIZE // 9

    /**
     * 判断是否为武汉协议帧
     */
    fun isWuhanProtocol(data: ByteArray): Boolean {
        return data.size >= 2 &&
                data[0] == WuhanProtocolConst.FRAME_HEADER_HIGH &&
                data[1] == WuhanProtocolConst.FRAME_HEADER_LOW
    }

    /**
     * 判断 hex 字符串是否为武汉协议
     */
    fun isWuhanProtocol(hexMsg: String): Boolean {
        if (hexMsg.length < 4) return false
        return hexMsg.startsWith("75AA", ignoreCase = true) ||
                hexMsg.startsWith("75aa", ignoreCase = true)
    }

    /**
     * 解析原始字节数据为 WuhanParsedMessage
     * @param data 原始字节数组
     * @return 解析结果，解析失败返回 null
     */
    fun parse(data: ByteArray): WuhanParsedMessage? {
        return try {
            if (!isWuhanProtocol(data)) {
                Timber.e("$TAG 不是武汉协议帧")
                return null
            }

            if (data.size < MIN_FRAME_SIZE) {
                Timber.e("$TAG 帧太短: ${data.size} < $MIN_FRAME_SIZE")
                return null
            }

            // 1. 验证帧尾
            if (data[data.size - 1] != WuhanProtocolConst.FRAME_TAIL) {
                Timber.e("$TAG 帧尾错误")
                return null
            }

            // 2. 读取长度 (字节2-3, UINT16 big-endian)
            val dataLength = ((data[2].toInt() and 0xFF) shl 8) or (data[3].toInt() and 0xFF)
            val expectedFrameSize = HEADER_SIZE + LENGTH_SIZE + dataLength + CRC_SIZE + TAIL_SIZE
            if (data.size != expectedFrameSize && data.size < expectedFrameSize) {
                Timber.e("$TAG 帧长度不匹配: 实际=${data.size}, 期望=$expectedFrameSize")
                // 宽松模式: 实际>=期望时也接受(可能有多余数据)
                if (data.size < expectedFrameSize) return null
            }

            // 3. 验证CRC (CRC从字节2开始, 计算到 CRC之前, 共 2+LENGTH 字节)
            val crcDataLength = LENGTH_SIZE + dataLength  // 字节2到数据结束
            val crcOffset = HEADER_SIZE + LENGTH_SIZE + dataLength
            if (!Crc16X25.verify(data, crcOffset, 2, crcDataLength)) {
                Timber.e("$TAG CRC校验失败")
                return null
            }

            // 4. 提取命令和源ID
            val cmdOffset = HEADER_SIZE + LENGTH_SIZE
            val command = data[cmdOffset]
            val sourceId = data[cmdOffset + CMD_SIZE]
            val dataStart = cmdOffset + CMD_SIZE + SRC_ID_SIZE

            Timber.d("$TAG 解析命令: 0x${String.format("%02X", command)}, 源ID: 0x${String.format("%02X", sourceId)}, 数据长度: $dataLength")

            // 5. 按命令类型解析
            val parsed = when (command) {
                WuhanProtocolConst.CMD_HEARTBEAT_A1 -> {
                    WuhanParsedMessage(command, sourceId,
                        heartbeat = parseHeartbeatA1(data, dataStart))
                }
                WuhanProtocolConst.CMD_ROUTE_INFO_B1 -> {
                    WuhanParsedMessage(command, sourceId,
                        routeInfo = parseRouteInfoB1(data, dataStart))
                }
                WuhanProtocolConst.CMD_STATION_DATA_B2 -> {
                    WuhanParsedMessage(command, sourceId,
                        stationData = parseStationDataB2(data, dataStart))
                }
                WuhanProtocolConst.CMD_DOOR_DATA_B3 -> {
                    WuhanParsedMessage(command, sourceId,
                        doorData = parseDoorDataB3(data, dataStart))
                }
                WuhanProtocolConst.CMD_DOOR_STATUS_B4 -> {
                    WuhanParsedMessage(command, sourceId,
                        doorStatus = parseDoorStatusB4(data, dataStart))
                }
                WuhanProtocolConst.CMD_TEXT_BROADCAST_B5 -> {
                    WuhanParsedMessage(command, sourceId,
                        textBroadcast = parseTextBroadcastB5(data, dataStart, dataLength))
                }
                else -> {
                    Timber.w("$TAG 未知命令: 0x${String.format("%02X", command)}")
                    null
                }
            }

            parsed
        } catch (e: Exception) {
            Timber.e(e, "$TAG 解析异常: ${e.message}")
            null
        }
    }

    /**
     * 从 hex 字符串解析
     */
    fun parseFromHex(hexMsg: String): WuhanParsedMessage? {
        return try {
            val data = DataUtil.hexStringToBytes(hexMsg) ?: return null
            parse(data)
        } catch (e: Exception) {
            Timber.e(e, "$TAG hex解析异常")
            null
        }
    }

    // ============== 各命令解析 ==============

    /**
     * 解析 0xA1 心跳/注册包
     *
     * 字段布局:
     * DATA0:       设备ID
     * DATA1:       车厢位置 (Bit3-0=车厢号, Bit7-4=位置)
     * DATA2:       负载率 (1=1%)
     * DATA3-4:     软件版本
     * DATA5:       标志位 (Bit0=在线, Bit1=UDP, Bit2=TCP)
     * DATA6:       CPU使用率 (0-100)
     * DATA7-8:     设备类型ID
     * DATA9:       保留
     * DATA10:      CPU温度
     * DATA11:      线路ID
     * DATA12:      路线ID
     * DATA13:      方向
     * DATA14-15:   起始站ID
     * DATA16-17:   终点站ID
     * DATA18-19:   当前站ID
     * DATA20-21:   下一站ID
     */
    private fun parseHeartbeatA1(data: ByteArray, offset: Int): WuhanHeartbeatA1 {
        return WuhanHeartbeatA1(
            deviceId = data[offset].toInt() and 0xFF,
            carriagePosition = data[offset + 1].toInt() and 0xFF,
            loadRate = data[offset + 2].toInt() and 0xFF,
            swVersionMajor = data[offset + 3].toInt() and 0xFF,
            swVersionMinor = data[offset + 4].toInt() and 0xFF,
            flags = data[offset + 5].toInt() and 0xFF,
            cpuUsage = data[offset + 6].toInt() and 0xFF,
            deviceType = readUInt16(data, offset + 7),
            reserved = data[offset + 9].toInt() and 0xFF,
            cpuTemp = data[offset + 10].toInt() and 0xFF,
            lineId = data[offset + 11].toInt() and 0xFF,
            routeId = data[offset + 12].toInt() and 0xFF,
            direction = data[offset + 13].toInt() and 0xFF,
            startStationId = readUInt16(data, offset + 14),
            endStationId = readUInt16(data, offset + 16),
            currentStationId = readUInt16(data, offset + 18),
            nextStationId = readUInt16(data, offset + 20),
        )
    }

    /**
     * 解析 0xB1 线路/站点信息 (核心数据包)
     */
    private fun parseRouteInfoB1(data: ByteArray, offset: Int): WuhanRouteInfoB1 {
        val piscId = data[offset].toInt() and 0xFF
        val emergencyFlag = data[offset + 1].toInt() and 0xFF
        val serviceStatus = data[offset + 2].toInt() and 0xFF
        val tcStatus = data[offset + 3].toInt() and 0xFF
        val lineId = data[offset + 4].toInt() and 0xFF
        val routeId = data[offset + 5].toInt() and 0xFF
        val startStationId = readUInt16(data, offset + 6)
        val endStationId = readUInt16(data, offset + 8)
        val currentStationId = readUInt16(data, offset + 10)
        val nextStationId = readUInt16(data, offset + 12)
        val destStationId = readUInt16(data, offset + 14)
        val trainStatus = data[offset + 16].toInt() and 0xFF
        val doorStatus = data[offset + 17].toInt() and 0xFF
        val direction = (data[offset + 17].toInt() shr 4) and 0x0F
        val temperature = data[offset + 18].toInt() and 0xFF

        // 拥挤度: DATA19-20 (UINT8每bit表示一个车厢)
        val crowding = mutableListOf<Int>()
        val crowding1 = data[offset + 19].toInt() and 0xFF
        val crowding2 = data[offset + 20].toInt() and 0xFF
        for (i in 0 until 8) {
            crowding.add(if (((crowding1 shr i) and 0x01) != 0) 1 else 0)
        }
        for (i in 0 until 8) {
            crowding.add(if (((crowding2 shr i) and 0x01) != 0) 1 else 0)
        }

        // 站名: DATA21-52 (最多32字节, UTF-8)
        val nameStart = offset + 21
        val nameMaxLen = minOf(32, data.size - nameStart)
        val stationNames = try {
            String(data, nameStart, nameMaxLen, Charsets.UTF_8).trimEnd(' ')
        } catch (e: Exception) {
            ""
        }

        return WuhanRouteInfoB1(
            piscId = piscId,
            emergencyFlag = emergencyFlag,
            serviceStatus = serviceStatus,
            tcStatus = tcStatus,
            lineId = lineId,
            routeId = routeId,
            startStationId = startStationId,
            endStationId = endStationId,
            currentStationId = currentStationId,
            nextStationId = nextStationId,
            destStationId = destStationId,
            trainStatus = trainStatus,
            doorStatus = doorStatus,
            direction = direction,
            temperature = temperature,
            crowding = crowding,
            stationNames = stationNames,
        )
    }

    /**
     * 解析 0xB2 站点数据 (含拥挤度)
     */
    private fun parseStationDataB2(data: ByteArray, offset: Int): WuhanStationDataB2 {
        val carriageId = data[offset].toInt() and 0xFF
        val currentStationId = readUInt16(data, offset + 2)
        val nextStationId = readUInt16(data, offset + 4)
        val direction = data[offset + 9].toInt() and 0xFF
        val crowdingRate = readUInt16(data, offset + 10)  // 0.01t
        val weight = data[offset + 12].toInt() and 0xFF    // 0.1t
        val temperature = data[offset + 13].toInt() and 0xFF

        val crowdingPerDoor = mutableListOf<Int>()
        for (i in 0 until 15) {
            crowdingPerDoor.add(data[offset + 14 + i].toInt() and 0xFF)
        }

        return WuhanStationDataB2(
            piscId = -1,
            carriageId = carriageId,
            currentStationId = currentStationId,
            nextStationId = nextStationId,
            direction = direction,
            crowdingRate = crowdingRate,
            weight = weight,
            temperature = temperature,
            crowdingPerDoor = crowdingPerDoor,
        )
    }

    /**
     * 解析 0xB3 车门7-16数据
     */
    private fun parseDoorDataB3(data: ByteArray, offset: Int): WuhanDoorDataB3 {
        val carriageId = data[offset].toInt() and 0xFF
        val currentStationId = readUInt16(data, offset + 2)
        val nextStationId = readUInt16(data, offset + 4)
        val direction = data[offset + 9].toInt() and 0xFF
        val crowdingRate = readUInt16(data, offset + 10)
        val weight = data[offset + 12].toInt() and 0xFF
        val temperature = data[offset + 13].toInt() and 0xFF

        val crowdingPerDoor = mutableListOf<Int>()
        for (i in 0 until 10) {
            crowdingPerDoor.add(data[offset + 14 + i].toInt() and 0xFF)
        }

        return WuhanDoorDataB3(
            piscId = -1,
            carriageId = carriageId,
            currentStationId = currentStationId,
            nextStationId = nextStationId,
            direction = direction,
            crowdingRate = crowdingRate,
            weight = weight,
            temperature = temperature,
            crowdingPerDoor = crowdingPerDoor,
        )
    }

    /**
     * 解析 0xB4 车门/站点状态
     */
    private fun parseDoorStatusB4(data: ByteArray, offset: Int): WuhanDoorStatusB4 {
        val tcStatus = data[offset].toInt() and 0xFF

        val doorStatus = mutableListOf<Int>()
        for (i in 0 until 8) {
            doorStatus.add(data[offset + 1 + i].toInt() and 0xFF)
        }

        val stationInfo = mutableListOf<Int>()
        for (i in 0 until 7) {
            stationInfo.add(data[offset + 9 + i].toInt() and 0xFF)
        }

        return WuhanDoorStatusB4(
            piscId = -1,
            tcStatus = tcStatus,
            doorStatus = doorStatus,
            stationInfo = stationInfo,
        )
    }

    /**
     * 解析 0xB5 文本广播
     */
    private fun parseTextBroadcastB5(data: ByteArray, offset: Int, dataLength: Int): WuhanTextBroadcastB5 {
        val broadcastType = data[offset + 2].toInt() and 0xFF
        val priority = data[offset + 5].toInt() and 0xFF

        val msgStart = offset + 6
        val msgLen = minOf(dataLength - (CMD_SIZE + SRC_ID_SIZE + 6), 255)
        val message = if (msgLen > 0) {
            try {
                String(data, msgStart, msgLen, Charsets.UTF_8).trimEnd(' ')
            } catch (e: Exception) {
                ""
            }
        } else ""

        return WuhanTextBroadcastB5(
            broadcastType = broadcastType,
            priority = priority,
            message = message,
        )
    }

    // ============== 工具方法 ==============

    /**
     * 读取 UINT16 (big-endian)
     */
    private fun readUInt16(data: ByteArray, offset: Int): Int {
        return ((data[offset].toInt() and 0xFF) shl 8) or
                (data[offset + 1].toInt() and 0xFF)
    }

    /**
     * 构建武汉协议帧 (用于发送)
     * @param command 命令号
     * @param sourceId 源/目的ID
     * @param payload 数据载荷
     * @return 完整的帧字节数组
     */
    fun buildFrame(command: Byte, sourceId: Byte, payload: ByteArray): ByteArray {
        val dataLength = CMD_SIZE + SRC_ID_SIZE + payload.size
        val totalSize = HEADER_SIZE + LENGTH_SIZE + dataLength + CRC_SIZE + TAIL_SIZE

        val frame = ByteArray(totalSize)

        // 帧头
        frame[0] = WuhanProtocolConst.FRAME_HEADER_HIGH
        frame[1] = WuhanProtocolConst.FRAME_HEADER_LOW

        // 长度 (从字节4开始的数据长度)
        frame[2] = ((dataLength shr 8) and 0xFF).toByte()
        frame[3] = (dataLength and 0xFF).toByte()

        // 命令
        frame[4] = command

        // 源ID
        frame[5] = sourceId

        // 数据
        System.arraycopy(payload, 0, frame, 6, payload.size)

        // CRC (从字节2开始计算)
        val crcDataLen = LENGTH_SIZE + dataLength
        val crc = Crc16X25.calculate(frame, 2, crcDataLen)
        val crcOffset = HEADER_SIZE + LENGTH_SIZE + dataLength
        frame[crcOffset] = ((crc shr 8) and 0xFF).toByte()
        frame[crcOffset + 1] = (crc and 0xFF).toByte()

        // 帧尾
        frame[totalSize - 1] = WuhanProtocolConst.FRAME_TAIL

        return frame
    }

    /**
     * 构建设备心跳帧 (0xA1)
     */
    fun buildHeartbeatA1(
        deviceId: Int,
        carriagePosition: Int,
        loadRate: Int,
        swVerMajor: Int,
        swVerMinor: Int,
        flags: Int,
        cpuUsage: Int,
        deviceType: Int,
        cpuTemp: Int,
        lineId: Int,
        routeId: Int,
        direction: Int,
        startStationId: Int,
        endStationId: Int,
        currentStationId: Int,
        nextStationId: Int,
        sourceId: Byte = 0x0C  // DPLCD
    ): ByteArray {
        val payload = ByteArray(22)
        payload[0] = deviceId.toByte()
        payload[1] = carriagePosition.toByte()
        payload[2] = loadRate.toByte()
        payload[3] = swVerMajor.toByte()
        payload[4] = swVerMinor.toByte()
        payload[5] = flags.toByte()
        payload[6] = cpuUsage.toByte()
        payload[7] = ((deviceType shr 8) and 0xFF).toByte()
        payload[8] = (deviceType and 0xFF).toByte()
        payload[9] = 0  // reserved
        payload[10] = cpuTemp.toByte()
        payload[11] = lineId.toByte()
        payload[12] = routeId.toByte()
        payload[13] = direction.toByte()
        payload[14] = ((startStationId shr 8) and 0xFF).toByte()
        payload[15] = (startStationId and 0xFF).toByte()
        payload[16] = ((endStationId shr 8) and 0xFF).toByte()
        payload[17] = (endStationId and 0xFF).toByte()
        payload[18] = ((currentStationId shr 8) and 0xFF).toByte()
        payload[19] = (currentStationId and 0xFF).toByte()
        payload[20] = ((nextStationId shr 8) and 0xFF).toByte()
        payload[21] = (nextStationId and 0xFF).toByte()

        return buildFrame(WuhanProtocolConst.CMD_HEARTBEAT_A1, sourceId, payload)
    }
}
