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
    const val CMD_ROUTE_INFO_B1 = 0xB1.toByte()     // 报站信息 (PISC→设备, 含紧急广播)
    const val CMD_STATION_DATA_B2 = 0xB2.toByte()   // 列车信息/拥挤度 (PISC→设备)
    const val CMD_DOOR_DATA_B3 = 0xB3.toByte()      // 列车信息2 车厢7-16 (PISC→设备)
    const val CMD_DOOR_STATUS_B4 = 0xB4.toByte()    // 车门防夹信息 (PISC→设备)
    const val CMD_TEXT_BROADCAST_B5 = 0xB5.toByte() // 自定义文字显示信息 (PISC→设备, 脉冲连发3次)

    // 0xC4 状态控制 (PTU→设备): 屏幕开关/心跳模式/背光/高低压/复位/TCP服务器IP端口配置。
    // 现场由 10.254.21.6:15050 以约3.5帧/秒下发, 目标设备类型 0x0C(动态地图)/0x0D/0x16 轮播,
    // payload固定 11 FF 01 + 13字节0 (DATA2=0x01 → 屏幕开, 服务器IP/端口全0无效),
    // 对显示无影响, 目前仅识别不处理
    const val CMD_STATUS_CONTROL_C4 = 0xC4.toByte()

    // ===== 广播类型 (协议1.2, B1 DATA16 Bit4-7) =====
    const val BC_TYPE_NONE = 0          // 无操作
    const val BC_TYPE_DEPART = 1        // 离站
    const val BC_TYPE_APPROACH = 2      // 预到站
    const val BC_TYPE_ARRIVE = 3        // 到站
    const val BC_TYPE_SKIP = 4          // 越站
    const val BC_TYPE_URGENT = 5        // 紧急广播
    const val BC_TYPE_SAFE = 6          // 安全广播
    const val BC_TYPE_DOOR_OPEN = 7     // 开门声广播
    const val BC_TYPE_DOOR_CLOSE = 8    // 关门声广播

    // ===== 广播控制 (B1 DATA16 Bit0-1) =====
    const val BC_CTRL_NONE = 0          // 无操作
    const val BC_CTRL_START = 1         // 开始广播
    const val BC_CTRL_STOP = 2          // 停止广播

    // ===== B5 操作命令 (协议5.6, DATA2) =====
    const val B5_ACTION_NONE = 0x00     // 无操作
    const val B5_ACTION_SHOW = 0x01     // 显示 (广播可打断)
    const val B5_ACTION_HIDE = 0x02     // 停止显示
    const val B5_ACTION_SHOW_HOLD = 0x11 // 持续显示 (广播不打断)

    // PISC ID
    const val PISC_ID_11 = 0x11.toByte()
    const val PISC_ID_21 = 0x21.toByte()
    const val PISC_ID_31 = 0x31.toByte()
    const val PISC_ID_41 = 0x41.toByte()

    // 设备类型ID — 协议V2.12 §1.1 (10进制): LCD动态地图DRM=12, LCD贯通道显示器IDU=13,
    // 数码海报屏/OLED/电视机DPLCD=16, PIDS电视机LCD=18, MPS=20, OLED=27, 所有设备ALL=255。
    // 注意: 下面这组是旧版取值, 与V2.12不一致, 目前仅被旧布局心跳(A1)引用, 待心跳重构时一并修正
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
 * 0xB1 报站信息 (PISC→设备) — 核心数据包
 * PISC定时2s发送; 触发广播报站时无间隔连发3条 [udp:224.100.1.8:6008]
 */
data class WuhanRouteInfoB1(
    var piscId: Int = -1,             // DATA0: PISC ID (0x11/0x21/0x31/0x41)
    var validFlags: Int = -1,         // DATA1: Bit0=开门信息有效, Bit1=站点信息有效
    var reserved2: Int = -1,          // DATA2: 保留
    var tcStatus: Int = -1,           // DATA3: Bit0=TC1激活, Bit1=TC2激活, Bit2=紧急广播播放中
    var lineId: Int = -1,             // DATA4: 线ID
    var routeId: Int = -1,            // DATA5: 线路ID (奇数上行/偶数下行; 3,4=环线)
    var startStationId: Int = -1,     // DATA6-7: 起始站ID
    var endStationId: Int = -1,       // DATA8-9: 终点站ID
    var currentStationId: Int = -1,   // DATA10-11: 当前站ID
    var nextStationId: Int = -1,      // DATA12-13: 下一站ID
    var urgentNotifyId: Int = -1,     // DATA14-15: 紧急广播ID (触发紧急广播时有效)
    var broadcastControl: Int = -1,   // DATA16 Bit0-1: 0=无操作, 1=开始广播, 2=停止广播
    var broadcastType: Int = -1,      // DATA16 Bit4-7: 广播类型(见BC_TYPE_*), 5=紧急广播
    var doorInfo: Int = -1,           // DATA17: 开门信息 (Bit0-1下一站开左/右/双, Bit3-2本站开, Bit4-5本站关)
    var stopStationCount: Int = -1,   // DATA18: 停靠站总数, 0=默认全线停靠
    var stopMask1: Int = -1,          // DATA19: 第1-8站停靠位图 (Bit0=第1站, 1=停靠)
    var stopMask2: Int = -1,          // DATA20: 第9-16站停靠位图
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
 * 0xB5 自定义文字显示信息 (PISC→设备) — 脉冲连发3次
 * byte5: 0xFF=所有设备, 0x0C=动态地图
 * 备注: 收到"显示"命令时, 无论当前处于什么界面, 应马上显示文字内容
 */
data class WuhanTextBroadcastB5(
    var piscId: Int = -1,
    var action: Int = -1,             // DATA2: 操作命令 0x00=无操作, 0x01=显示(广播可打断), 0x02=停止显示, 0x11=持续显示(广播不打断)
    var contentLength: Int = -1,      // DATA5: 内容长度
    var message: String = "",         // DATA6-260: 文字内容 (UTF-8)
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
