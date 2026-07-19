package com.sc.tmp_cw.bean

/**
 * 武汉通信协议 (FlavorB) 数据模型
 *
 * 帧格式: 0x75AA + Length(2) + Command(1) + SrcID(1) + Data + CRC16(2) + 0x5F
 *
 * @author tsc
 * @date 2025/1/13
 */

// ============== 帧常量 ==============
object WuhanProtocolConst {
    const val FRAME_HEADER_HIGH = 0x75.toByte()
    const val FRAME_HEADER_LOW = 0xAA.toByte()
    const val FRAME_TAIL = 0x5F.toByte()

    // 命令类型
    const val CMD_HEARTBEAT_A1 = 0xA1.toByte()     // 设备心跳/注册 (设备→PISC)
    const val CMD_ROUTE_INFO_B1 = 0xB1.toByte()     // 线路/站点信息 (PISC→设备)
    const val CMD_STATION_DATA_B2 = 0xB2.toByte()   // 站点拥挤度数据 (PISC→设备)
    const val CMD_DOOR_DATA_B3 = 0xB3.toByte()      // 车门7-16数据 (PISC→设备)
    const val CMD_DOOR_STATUS_B4 = 0xB4.toByte()    // 车门/站点状态 (PISC→设备)
    const val CMD_TEXT_BROADCAST_B5 = 0xB5.toByte() // 文本广播 (PISC→设备)

    // PISC ID
    const val PISC_ID_11 = 0x11.toByte()
    const val PISC_ID_21 = 0x21.toByte()
    const val PISC_ID_31 = 0x31.toByte()
    const val PISC_ID_41 = 0x41.toByte()

    // 设备类型ID (DATA7-8 in 0xA1)
    const val DEV_TYPE_DRM = 16
    const val DEV_TYPE_IDU = 18
    const val DEV_TYPE_DPLCD = 20
    const val DEV_TYPE_LCD = 27
    const val DEV_TYPE_MPS = 255
}

/**
 * 0xA1 心跳/注册包 (设备→PISC)
 */
data class WuhanHeartbeatA1(
    var deviceId: Int = -1,           // DATA0: 设备ID
    var carriagePosition: Int = -1,   // DATA1: 车厢位置 (B1=1)
    var loadRate: Int = -1,           // DATA2: 负载率 (1=1%)
    var swVersionMajor: Int = -1,     // DATA3: 主版本号
    var swVersionMinor: Int = -1,     // DATA4: 次版本号
    var flags: Int = -1,              // DATA5: Bit0=在线/离线, Bit1=UDP/TCP
    var cpuUsage: Int = -1,           // DATA6: CPU使用率 0-100
    var deviceType: Int = -1,         // DATA7-8: 设备类型ID
    var reserved: Int = -1,           // DATA9: 保留
    var cpuTemp: Int = -1,            // DATA10: CPU温度
    var lineId: Int = -1,             // DATA11: 线路ID
    var routeId: Int = -1,            // DATA12: 路线ID
    var direction: Int = -1,          // DATA13: 方向 (1.2)
    var startStationId: Int = -1,     // DATA14-15: 起始站ID
    var endStationId: Int = -1,       // DATA16-17: 终点站ID
    var currentStationId: Int = -1,   // DATA18-19: 当前站ID
    var nextStationId: Int = -1,      // DATA20-21: 下一站ID
)

/**
 * 0xB1 线路/站点信息 (PISC→设备) — 核心数据包
 */
data class WuhanRouteInfoB1(
    var piscId: Int = -1,             // DATA0: PISC ID (0x11/0x21/0x31/0x41)
    var emergencyFlag: Int = -1,      // DATA1: Bit0=紧急广播, Bit1=清客
    var serviceStatus: Int = -1,      // DATA2: 服务状态
    var tcStatus: Int = -1,           // DATA3: Bit0=TC1在位, Bit1=TC2在位
    var lineId: Int = -1,             // DATA4: 线路ID
    var routeId: Int = -1,            // DATA5: 路线ID
    var startStationId: Int = -1,     // DATA6-7: 起始站ID
    var endStationId: Int = -1,       // DATA8-9: 终点站ID
    var currentStationId: Int = -1,   // DATA10-11: 当前站ID
    var nextStationId: Int = -1,      // DATA12-13: 下一站ID
    var destStationId: Int = -1,      // DATA14-15: 目的站ID
    var trainStatus: Int = -1,        // DATA16: Bit0-1: 00=停车,01=运行,10=即将到站
    var doorStatus: Int = -1,         // DATA17: Bit0-1: 00=关闭,01=打开,10=故障
    var direction: Int = -1,          // DATA17 Bit4-7: 方向
    var temperature: Int = -1,        // DATA18: 温度
    var crowding: List<Int> = emptyList(),  // DATA19-20+: 每节车厢拥挤度 (Bit=1表示拥挤)
    var stationNames: String = "",    // DATA21-52: 站名 UTF-8
)

/**
 * 0xB2 站点数据 (PISC→设备) — 含拥挤度
 */
data class WuhanStationDataB2(
    var piscId: Int = -1,
    var carriageId: Int = -1,         // DATA0: Bit0-3=车厢号, Bit7-4=编组号
    var currentStationId: Int = -1,   // DATA2-3
    var nextStationId: Int = -1,      // DATA4-5
    var stationNames: String = "",    // DATA6-8+
    var direction: Int = -1,
    var crowdingRate: Int = -1,       // 0.01t
    var weight: Int = -1,             // 0.1t
    var temperature: Int = -1,        // 0.01°C
    var crowdingPerDoor: List<Int> = emptyList(),  // 每门拥挤度
)

/**
 * 0xB3 车门7-16数据 (PISC→设备)
 */
data class WuhanDoorDataB3(
    var piscId: Int = -1,
    var carriageId: Int = -1,
    var currentStationId: Int = -1,
    var nextStationId: Int = -1,
    var direction: Int = -1,
    var crowdingRate: Int = -1,
    var weight: Int = -1,
    var temperature: Int = -1,
    var crowdingPerDoor: List<Int> = emptyList(),  // 门7-16
)

/**
 * 0xB4 车门/站点状态 (PISC→设备)
 */
data class WuhanDoorStatusB4(
    var piscId: Int = -1,
    var tcStatus: Int = -1,           // Bit0:TC1, Bit1:TC2
    var doorStatus: List<Int> = emptyList(),  // 每门开关状态
    var stationInfo: List<Int> = emptyList(), // 站点特殊状态
)

/**
 * 0xB5 文本广播 (PISC→设备)
 */
data class WuhanTextBroadcastB5(
    var piscId: Int = -1,
    var broadcastType: Int = -1,      // DATA2: 0x00=正常, 0x01=紧急, 0x02=清客, 0x11=自定义
    var priority: Int = -1,           // DATA5: 优先级
    var message: String = "",         // DATA6-260: UTF-8 文本
)

/**
 * 统一的武汉协议解析结果
 */
data class WuhanParsedMessage(
    val command: Byte,
    val sourceId: Byte,
    val heartbeat: WuhanHeartbeatA1? = null,
    val routeInfo: WuhanRouteInfoB1? = null,
    val stationData: WuhanStationDataB2? = null,
    val doorData: WuhanDoorDataB3? = null,
    val doorStatus: WuhanDoorStatusB4? = null,
    val textBroadcast: WuhanTextBroadcastB5? = null,
)
