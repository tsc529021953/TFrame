package com.sc.xs_control.constant

/**
 * 指令常量表 —— 依据《指令代码2.xlsx》(2026/10/15更新版) 整理
 *
 * 说明：
 * 1. 指令内容为 HEX 字节串，实际内容为 ASCII 字符串 “setr=XXXXXXXX”，
 *    例如 “73 65 74 72 3d 31 31 31 31 31 31 31 31” = “setr=11111111”。
 * 2. 设备控制(8位掩码，发192.168.1.102)：bit1=大厅电子屏(PC)，
 *    bit2~bit7 对应 电视1~电视6，bit8 未使用，
 *    未控制位直接发字符 'x'（设备端不改变）。
 *    注意：旧版《指令代码.xlsx》为 bit1~bit6=电视1~6，已按新表调整。
 * 3. 灯光控制为 16 位掩码：bit1~bit3 顶面照明区域1~3，
 *    bit4~bit6 墙面照明区域1~3，bit7~bit9 空间区域灯光区域1~3，
 *    bit10~bit16 未使用，未控制位直接发字符 'x'。
 * 4. 大厅电子屏(PC) 和 换装体验(一体机) 为字符串指令：开="0"、关="1"，
 *    各发往对应IP（.100/.101）；PC 开/关同时还需发 bit1 掩码到 .102。
 * 5. 表格中设备控制 IP 段写作 192.169.1.x，确认为 192.168.1.x 笔误，已按 192.168 配置。
 *
 * @author tsc
 * @date 2026/10/8
 */
object CommandConstant {

    /** 统一使用 UDP，目标端口 9128 */
    const val UDP_PORT = 9128

    /** 本机监听端口（当前无接收协议，仅监听 9128 并打印日志） */
    const val LOCAL_LISTEN_PORT = 9128

    // ==================== 设备控制 ====================

    /** 大厅电子屏(PC) 目标 */
    const val DEVICE_IP_PC = "192.168.1.100"

    /** 换装体验(一体机) 目标 */
    const val DEVICE_IP_AIO = "192.168.1.101"

    /** 电视1~6 目标 */
    const val DEVICE_IP_TV = "192.168.1.102"

    // ==================== 字符串指令（大厅电子屏/换装体验） ====================

    /** 大厅电子屏/换装体验 开 */
    const val CMD_STR_ON = "0"

    /** 大厅电子屏/换装体验 关 */
    const val CMD_STR_OFF = "1"

    // ==================== 播放/暂停指令（大厅电子屏 + 电视1~6） ====================

    /** 播放/暂停 目标端口（与开关指令的9128不同） */
    const val MEDIA_PORT = 2368

    /** 播放/暂停 目标IP：下标0=大厅电子屏(PC)，1~6=电视1~6，依次为192.168.1.90~96 */
    val MEDIA_IPS = Array(7) { "192.168.1.${90 + it}" }

    /** 播放 */
    const val CMD_MEDIA_PLAY = "play"

    /** 暂停 */
    const val CMD_MEDIA_PAUSE = "pause"

    /** 内容关（新表新增的PC指令字，当前界面无对应按钮，预留） */
    const val CMD_MEDIA_STOP = "stop"

    /** 静音（格式与 xs_mediaplay 的 MessageConstant.CMD_VOICE 协议一致，"voice:0"） */
    const val CMD_VOICE_MUTE = "voice:0"

    /** 取消静音（"voice:100"，恢复音量100） */
    const val CMD_VOICE_UNMUTE = "voice:100"

    // ==================== 灯光控制 ====================

    /** 灯光控制 目标 */
    const val LIGHT_IP = "192.168.1.103"

    // ==================== 掩码指令 ====================
    // 掩码中 'x' 为不改变位，发送时原样发字符 'x'
    // 8位掩码：bit1=大厅电子屏(PC)，bit2~bit7=电视1~6，bit8未使用

    /** 设备控制 一键 全开（8位） */
    const val MASK_DEVICE_ALL_ON = "setr=11111111"

    /** 设备控制 一键 全关（8位） */
    const val MASK_DEVICE_ALL_OFF = "setr=00000000"

    /** 大厅电子屏(PC) 开（bit1掩码，发.102） */
    const val MASK_PC_ON = "setr=1xxxxxxx"

    /** 大厅电子屏(PC) 关（bit1掩码，发.102） */
    const val MASK_PC_OFF = "setr=0xxxxxxx"

    /** 灯光控制 一键 全开（16位） */
    const val MASK_LIGHT_ALL_ON = "setr=1111111111111111"

    /** 灯光控制 一键 全关（16位） */
    const val MASK_LIGHT_ALL_OFF = "setr=0000000000000000"

}
