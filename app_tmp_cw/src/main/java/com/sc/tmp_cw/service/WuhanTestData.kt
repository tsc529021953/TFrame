package com.sc.tmp_cw.service

import com.nbhope.lib_frame.utils.DataUtil
import com.sc.tmp_cw.bean.WuhanProtocolConst
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * 武汉协议 (FlavorB) 测试数据 — 协议V2.12
 *
 * 测试帧通过 WuhanProtocolParser.buildFrame() 在运行时构建 (自动计算CRC16/X25),
 * DATA字段布局见 WuhanProtocolParser 中各 parse 方法注释。
 *
 * 使用方法: 在 TmpServiceImpl.test() 中调用 MessageHandler.handleMessage(XXX, this)
 * 或调用 runAll(this) 按顺序播放全部场景
 */
object WuhanTestData {

    // 站点ID (与配置stations.id对应): 32=石桥, 33=中一路, 34=后湖四路, 35=兴业路
    private const val ST_SHI_QIAO = 0x20
    private const val ST_ZHONG_YI_LU = 0x21
    private const val ST_HOU_HU_SI_LU = 0x22
    private const val ST_XING_YE_LU = 0x23

    // ============================================================
    // B1 报站信息测试帧 (PISC→设备, DATA16 = Bit4-7广播类型 + Bit0-1控制)
    // ============================================================

    /** 预到站: 石桥(32)→中一路(33), 类型2+开始 → boardStatus=1 (即将到站) */
    val NEXT_STATION_1 = buildB1(
        current = ST_SHI_QIAO, next = ST_ZHONG_YI_LU,
        data16 = (WuhanProtocolConst.BC_TYPE_APPROACH shl 4) or WuhanProtocolConst.BC_CTRL_START)

    /** 到站: 中一路(33), 类型3+开始 → boardStatus=2 (已到站) */
    val ARRIVED_1 = buildB1(
        current = ST_ZHONG_YI_LU, next = ST_ZHONG_YI_LU,
        data16 = (WuhanProtocolConst.BC_TYPE_ARRIVE shl 4) or WuhanProtocolConst.BC_CTRL_START)

    /** 离站: 中一路(33)→后湖四路(34), 类型1+开始 → boardStatus=0 (下一站) */
    val DEPART_1 = buildB1(
        current = ST_ZHONG_YI_LU, next = ST_HOU_HU_SI_LU,
        data16 = (WuhanProtocolConst.BC_TYPE_DEPART shl 4) or WuhanProtocolConst.BC_CTRL_START)

    /** 无广播: 类型0+无操作 → boardStatus=-1, 站点信息不更新(用于模拟穿插无效帧后信号重新触发) */
    val IDLE_NONE = buildB1(
        current = ST_ZHONG_YI_LU, next = ST_HOU_HU_SI_LU,
        data16 = (WuhanProtocolConst.BC_TYPE_NONE shl 4) or WuhanProtocolConst.BC_CTRL_NONE)

    /** 预到站: 后湖四路(34)→兴业路(35), 类型2+开始 → boardStatus=1 (即将到站) */
    val NEXT_STATION_2 = buildB1(
        current = ST_HOU_HU_SI_LU, next = ST_XING_YE_LU,
        data16 = (WuhanProtocolConst.BC_TYPE_APPROACH shl 4) or WuhanProtocolConst.BC_CTRL_START)

    /** 离站: 后湖四路(34)→兴业路(35), 类型1+开始 → boardStatus=0 (下一站) */
    val DEPART_2 = buildB1(
        current = ST_HOU_HU_SI_LU, next = ST_XING_YE_LU,
        data16 = (WuhanProtocolConst.BC_TYPE_DEPART shl 4) or WuhanProtocolConst.BC_CTRL_START)

    /** 到站: 兴业路(35), 类型3+开始 → boardStatus=2 (已到站) */
    val ARRIVED_2 = buildB1(
        current = ST_XING_YE_LU, next = ST_XING_YE_LU,
        data16 = (WuhanProtocolConst.BC_TYPE_ARRIVE shl 4) or WuhanProtocolConst.BC_CTRL_START)

    /** 紧急广播开始: 火情(紧急广播ID=6), 类型5+开始, DATA3 Bit2=播放中 → 拉起UrgentNotifyActivity */
    val URGENT_FIRE_START = buildB1(
        current = ST_XING_YE_LU, next = ST_XING_YE_LU,
        urgentId = 6, tcStatus = 0x05,
        data16 = (WuhanProtocolConst.BC_TYPE_URGENT shl 4) or WuhanProtocolConst.BC_CTRL_START)

    /** 紧急广播停止: 类型5+停止 → 关闭UrgentNotifyActivity */
    val URGENT_FIRE_STOP = buildB1(
        current = ST_XING_YE_LU, next = ST_XING_YE_LU,
        urgentId = 6,
        data16 = (WuhanProtocolConst.BC_TYPE_URGENT shl 4) or WuhanProtocolConst.BC_CTRL_STOP)

    // ============================================================
    // B5 自定义文字显示测试帧 (PISC→设备, DATA2=操作命令, DATA5=内容长度)
    // ============================================================

    /** 临时停车通知 (显示, 广播可打断) */
    val TEXT_TEMP_STOP = buildB5(WuhanProtocolConst.B5_ACTION_SHOW,
        "各位乘客请注意，列车现在是临时停车，请您耐心等待，不要触摸列车设备。给您造成的不便敬请谅解，谢谢合作！")

    /** 停止显示 */
    val TEXT_HIDE = buildB5(WuhanProtocolConst.B5_ACTION_HIDE, "")

    // ============================================================
    // 帧构建
    // ============================================================

    /**
     * 构建 0xB1 报站信息帧 (44字节DATA, 参考现场帧: PISC=0x11, 线ID=12, 线路=2下行, 起始31/终点67)
     */
    private fun buildB1(
        current: Int, next: Int,
        data16: Int,
        urgentId: Int = 0,
        tcStatus: Int = 0x01,
        piscId: Int = 0x11,
        lineId: Int = 0x0C,
        routeId: Int = 0x02,
        start: Int = 0x1F,
        end: Int = 0x43,
        doorInfo: Int = 0x00,
    ): String {
        val payload = ByteArray(44)
        payload[0] = piscId.toByte()
        payload[1] = 0xFF.toByte()          // Bit0开门信息有效 + Bit1站点信息有效
        payload[2] = 0x00                   // 保留
        payload[3] = tcStatus.toByte()      // Bit0=TC1激活, Bit1=TC2激活, Bit2=紧急广播播放中
        payload[4] = lineId.toByte()
        payload[5] = routeId.toByte()
        putUInt16(payload, 6, start)
        putUInt16(payload, 8, end)
        putUInt16(payload, 10, current)
        putUInt16(payload, 12, next)
        putUInt16(payload, 14, urgentId)    // 紧急广播ID (触发时有效)
        payload[16] = data16.toByte()       // Bit0-1广播控制 + Bit4-7广播类型
        payload[17] = doorInfo.toByte()
        payload[18] = 0x00                  // 停靠站总数 0=全线停靠
        // DATA19-20 停靠位图 / DATA21-43 保留, 保持0
        return DataUtil.byteArray2HexString(
            WuhanProtocolParser.buildFrame(WuhanProtocolConst.CMD_ROUTE_INFO_B1, 0xFF.toByte(), payload))
    }

    /**
     * 构建 0xB5 自定义文字显示帧 (DATA0-1=PISC/保留, DATA2=命令, DATA3-4=保留, DATA5=内容长度, DATA6+=UTF-8)
     */
    private fun buildB5(action: Int, message: String): String {
        val msgBytes = message.toByteArray(Charsets.UTF_8)
        val payload = ByteArray(6 + msgBytes.size)
        payload[0] = 0x11                   // PISC ID
        payload[1] = 0xFF.toByte()
        payload[2] = action.toByte()        // 操作命令
        payload[3] = 0x00                   // 保留
        payload[4] = 0x00                   // 保留
        payload[5] = msgBytes.size.toByte() // 内容长度
        System.arraycopy(msgBytes, 0, payload, 6, msgBytes.size)
        return DataUtil.byteArray2HexString(
            WuhanProtocolParser.buildFrame(WuhanProtocolConst.CMD_TEXT_BROADCAST_B5, 0xFF.toByte(), payload))
    }

    private fun putUInt16(dst: ByteArray, offset: Int, value: Int) {
        dst[offset] = ((value shr 8) and 0xFF).toByte()
        dst[offset + 1] = (value and 0xFF).toByte()
    }

    /**
     * 测试入口：在 MessageHandler.test() 中按顺序播放所有测试场景
     */
    fun runAll(service: TmpServiceImpl) {
        val messages = listOf(
            "预到站(石桥→中一路)" to NEXT_STATION_1,
            "已到站(中一路)" to ARRIVED_1,
            "离站(后湖四路→兴业路)" to DEPART_2,
            "已到站(兴业路)" to ARRIVED_2,
            "紧急广播开始(火情 ID=6)" to URGENT_FIRE_START,
            "紧急广播停止(火情)" to URGENT_FIRE_STOP,
            "B5文字显示(临时停车)" to TEXT_TEMP_STOP,
            "B5停止显示" to TEXT_HIDE,
        )

        var index = 0
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                if (index >= messages.size) return
                val (name, hex) = messages[index]
                Timber.i("=== 测试: $name ===")
                MessageHandler.handleMessage(hex, service)
                index++
                handler.postDelayed(this, 4000) // 每4秒发一条
            }
        }
        handler.post(runnable)
    }

    /**
     * 站点播报视频测试场景 (MessageHandler.test() flavorB默认入口):
     *
     * 完整模拟一轮行车: 预到站(只改文案) → 到站(进站视频+持续到站信号去重)
     * → 出站(出站视频) → 出站信号持续下发(视频播放中由top拦截,播完退出后由状态+站点去重拦截)
     * → 穿插无效帧让出站信号重新触发(站点信息不变,仍不拉起)
     * → 新一轮: 预到站(只改文案) → 到站(站点变化应重新拉起进站视频)
     */
    fun runStationNotifyScene(service: TmpServiceImpl) {
        service.mScope.launch {
            val videoWait = 15_000L // 等视频播完自动finish(视频均为13s,+2s缓冲)

            // ① 预到站: 只更新顶部文案,不拉起界面
            Timber.i("=== 站点播报测试①: 预到站(石桥→中一路) → 只改文案不切界面 ===")
            MessageHandler.handleMessage(NEXT_STATION_1, service)
            delay(3000)

            // ② 到站: 拉起界面播放进站视频
            Timber.i("=== 站点播报测试②: 到站(中一路) → 进站视频 ===")
            MessageHandler.handleMessage(ARRIVED_1, service)

            // ③ 到站信号持续下发+无效帧(开门声等映射-1)穿插重新触发: 均不应重复拉起
            delay(3000)
            Timber.i("=== 站点播报测试③: 持续到站信号+无效帧穿插(站点不变,均不应拉起) ===")
            repeat(3) {
                MessageHandler.handleMessage(ARRIVED_1, service)
                delay(1000)
                MessageHandler.handleMessage(IDLE_NONE, service)
                delay(1000)
            }

            // ④ 出站: 进站视频播完界面退出后,拉起界面播出站视频
            delay(videoWait)
            Timber.i("=== 站点播报测试④: 出站(中一路→后湖四路) → 出站视频 ===")
            MessageHandler.handleMessage(DEPART_1, service)

            // ⑤ 出站后PISC持续下发出站信号(站点不变):
            //    视频播放中 → top为StationNotifyActivity,不重复拉起
            //    视频播完退出后 → 同值不重发回调/状态+站点去重,不重复拉起
            delay(3000)
            Timber.i("=== 站点播报测试⑤: 持续下发出站信号x6(站点不变,均不应拉起) ===")
            repeat(6) {
                MessageHandler.handleMessage(DEPART_1, service)
                delay(2000)
            }

            // ⑥ 穿插无效帧(无广播)使出站信号重新触发回调: 站点信息不变,去重后仍不拉起
            Timber.i("=== 站点播报测试⑥: 无效帧穿插+出站信号重新触发(站点不变,不应拉起) ===")
            repeat(2) {
                MessageHandler.handleMessage(IDLE_NONE, service)
                delay(1000)
                MessageHandler.handleMessage(DEPART_1, service)
                delay(2000)
            }

            // ⑦ 新一轮: 预到站(只改文案) → 到站(站点信息变化)应重新拉起进站视频
            Timber.i("=== 站点播报测试⑦: 新一轮 预到站(后湖四路→兴业路) → 只改文案 ===")
            MessageHandler.handleMessage(NEXT_STATION_2, service)
            delay(3000)
            Timber.i("=== 站点播报测试⑦: 到站(兴业路) → 站点变化应重新拉起进站视频 ===")
            MessageHandler.handleMessage(ARRIVED_2, service)

            Timber.i("=== 站点播报测试完成 ===")
        }
    }
}
