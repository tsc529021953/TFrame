package com.sc.xs_control.inter

import android.content.Context
import androidx.databinding.ObservableBoolean

/**
 * @author  tsc
 * @date  2026/10/8
 * @version 0.0.0-1
 * @description 指令服务接口
 */
interface ITmpService {

    fun init(context: Context)

    fun showFloat()

    fun hideFloat(delayMillis: Long)

    fun write(msg: String)

    fun reBuild()

    /** 网络是否正常（侧栏底部联机状态显示），service未绑定时返回null */
    fun getNetObs(): ObservableBoolean?

    // ==================== 指令发送 ====================

    /** 设备控制 一键开关 全开/全关（大厅电子屏/换装体验发字符串指令，电视发掩码） */
    fun sendDeviceAllCmd(on: Boolean)

    /** 大厅电子屏 开/关（字符串指令：开="0"、关="1"，并同步发 bit1 掩码到 .102） */
    fun sendPcCmd(on: Boolean)

    /** 换装体验(一体机) 开/关（字符串指令：开="0"、关="1"） */
    fun sendAioCmd(on: Boolean)

    /** 大厅电子屏 播放/暂停（字符串指令"play"/"pause"，发192.168.1.90:2368） */
    fun sendPcMediaCmd(isPlay: Boolean)

    /** 电视 index: 1~6 播放/暂停（字符串指令"play"/"pause"，发192.168.1.(89+index):2368） */
    fun sendTvMediaCmd(index: Int, isPlay: Boolean)

    /** 大厅电子屏 静音/取消静音（字符串指令"voice:0"/"voice:100"，发192.168.1.90:2368） */
    fun sendPcVoiceCmd(mute: Boolean)

    /** 电视 index: 1~6 静音/取消静音（字符串指令"voice:0"/"voice:100"，发192.168.1.(89+index):2368） */
    fun sendTvVoiceCmd(index: Int, mute: Boolean)

    /** 电视 index: 1~6 开/关（8位掩码：bit1=PC，bit2~bit7=电视1~6） */
    fun sendTvCmd(index: Int, on: Boolean)

    /** 灯光控制 一键开关 全开/全关 */
    fun sendLightAllCmd(on: Boolean)

    /** 顶面照明 一键 开/关 */
    fun sendLightCeilingAllCmd(on: Boolean)

    /** 顶面照明 区域 index: 1~3 开/关 */
    fun sendLightCeilingAreaCmd(index: Int, on: Boolean)

    /** 墙面照明 一键 开/关 */
    fun sendLightWallAllCmd(on: Boolean)

    /** 墙面照明 区域 index: 1~3 开/关 */
    fun sendLightWallAreaCmd(index: Int, on: Boolean)

    /** 空间区域灯光 一键 开/关 */
    fun sendLightSpaceAllCmd(on: Boolean)

    /** 空间区域灯光 区域 index: 1~3 开/关 */
    fun sendLightSpaceAreaCmd(index: Int, on: Boolean)

}
