package com.sc.tmp_cw.service

import android.content.Context
import android.os.Build
import android.provider.Settings
import com.nbhope.lib_frame.utils.DataUtil
import com.sc.tmp_cw.bean.PISBean
import timber.log.Timber
import com.sc.tmp_cw.R
import com.sc.tmp_cw.utils.FlavorConfigUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * @author  tsc
 * @date  2025/1/10 16:46
 * @version 0.0.0-1
 * @description
 */
object MessageHandler {

    val testData1 = "504100f602008b0001ff001d020200ff0000007dffff000000000000000000000000000000000000ffffffff1907180e0007000000ffffff00fa00f000f000f000fa00e600000000000000000000004c000000ff820082018200820182008201820082218200822182008201820082018200820182008221820082218200820182208201820082018200822182008201820182008201820082018200820182008201820082018200820182008201820082018200820182008201820082018200820182008201820082018200000000000000000000000000000000000000000000000000000000000000000000000000000000007f03"
    val testData2 = "504100f602008c0002ff001d020200ff0000007dffff000000000000000000000000000000000000ffffffff1907180e0008000000ffffff00fa00f000f000f000fa00e60000000000000000000042ff000000ff820082018200820182008201820082218200822182008201820082018200820182008221820082218200820182208201820082018200822182008201820182008201820082018200820182008201820082018200820182008201820082018200820182008201820082018200820182008201820082018200000000000000000000000000000000000000000000000000000000000000000000000000000000009403"
    val testData3 = "504100f602008d0000ff001d020200ff0000007dffff000000000000000000000000000000000000ffffffff1907180e0009000000ffffff00fa00f000f000f000fa00e60000000000000000000042ff000000ff820082018200820182008201820082218200822182008201820082018200820182008221820082218200820182208201820082018200822182008201820182008201820082018200820182008201820082018200820182008201820082018200820182008201820082018200820182008201820082018200000000000000000000000000000000000000000000000000000000000000000000000000000000009403"
    // 紧报
    val testData4 = "5041d00f602008d0002ff001d020200320000007dffff000000000000000000000000000000000000ffffffff1907180e0009000000ffffff00fa00f000f000f000fa00e60000000000000000000042ff000000ff820082018200820182008201820082218200822182008201820082018200820182008221820082218200820182208201820082018200822182008201820182008201820082018200820182008201820082018200820182008201820082018200820182008201820082018200820182008201820082018200000000000000000000000000000000000000000000000000000000000000000000000000000000009403"

    // ===== FlavorA 测试数据：模拟日志中的三帧站点到达信号 =====
    // boardStatus=2（已到达）, currentCode=1（咸水沽西）, lifeSignal=10/11/12, second=28/29/30
    val testLogSignal1 = "504100f602000a0002ff001d000000ff00ff0022ffff000000000000000000000000000000000000ffffffff00000101001c000000fffffffefffefffefffefffefffeff00000000ffffffffffff020a000000ff020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200000000000000000000000000000000000000000000000000000000000000000000000000000000006802"
    val testLogSignal2 = "504100f602000b0002ff001d000000ff00ff0022ffff000000000000000000000000000000000000ffffffff00000101001d000000fffffffefffefffefffefffefffeff00000000ffffffffffff020a000000ff020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200000000000000000000000000000000000000000000000000000000000000000000000000000000000000"
    val testLogSignal3 = "504100f602000c0002ff001d000000ff00ff0022ffff000000000000000000000000000000000000ffffffff00000101001e000000fffffffefffefffefffefffefffeff00000000ffffffffffff0400000000ff020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000200000000000000000000000000000000000000000000000000000000000000000000000000000000000000"

    val IS_JUDGE_LENGTH = false

    val MAX_LENGTH = 100

    /**
     *
     * @param msg
     * @param service
     */
    fun test(msg: String, service: TmpServiceImpl) {
        // 优先使用传入的测试数据
        val data = if (msg.isNotEmpty()) {
            msg.replace(" ", "")
        } else {
            null
        }
        service.mScope.launch {
            if (data != null) {
                // 使用传入的测试指令
                Timber.i("使用自定义测试数据: ${data.length}字符")
                handleMessage(data, service)
            } else if (FlavorConfigUtil.isFlavorA()) {
                // FlavorA: 模拟日志中的三帧站点到达信号，间隔1秒
                // boardStatus=2（已到达：咸水沽西），触发 StationNotifyActivity
                Timber.i("FlavorA 测试：模拟日志三帧站点到达信号")
                handleMessage(testLogSignal1, service)
                delay(1000)
                handleMessage(testLogSignal2, service)
                delay(1000)
                handleMessage(testLogSignal3, service)
            } else {
                // FlavorB: 跑武汉协议测试序列 + 旧协议测试
                WuhanTestData.runAll(service)
                // 延迟后跑旧协议
                delay(8000)
                handleMessage(testData2.replace(" ", ""), service)
                delay(1000)
                handleMessage(testData1.replace(" ", ""), service)
            }
        }
    }

    fun handleMessage(msg: String, service: TmpServiceImpl) {
        // ===== 协议自动检测 =====
        if (WuhanProtocolParser.isWuhanProtocol(msg)) {
            handleWuhanMessage(msg, service)
            return
        }

        if (!msg.startsWith(PISBean.START_MESSAGE)) {
            Timber.e("消息头错误 $msg")
        } else {
            val len = msg.length
            // 获取数据长度
            if (len <= 8 || len < 100) {
                Timber.e("消息长度不够:$len")
                return
            }
            val len2 = getInfoByIndex(msg, 4, 8)
            System.out.println("len $len2 $len $msg")
            if (IS_JUDGE_LENGTH && len != len2) {
                Timber.e("消息长度不够")
            } else {
                val pisBean = PISBean()
                pisBean.frameworkVersion = getInfoByIndex(msg, 8, 10)
                pisBean.modifyVersion = getInfoByIndex(msg, 10, 12)
                pisBean.lifeSignal = getInfoByIndex(msg, 12, 14)
                pisBean.checkSignal = getInfoByIndex(msg, 14, 16)
                pisBean.boardStatus = getInfoByIndex(msg, 16, 18)


                pisBean.startCode = getInfoByIndex(msg, 20, 22) + 1
                pisBean.endCode = getInfoByIndex(msg, 22, 24) + 1
                pisBean.currentCode = getInfoByIndex(msg, 24, 26) + 1
                pisBean.nextCode = getInfoByIndex(msg, 26, 28) + 1
                service.stationNotifyObs.set(pisBean.boardStatus)
                var tip = ""
                if (pisBean.boardStatus != -1) {
                    when (pisBean.boardStatus) {
                        0 -> {
                            tip = service.getString(R.string.nextStation)
                            service.stationStatusObs.set(tip)
                            service.stationObs.set("$tip：" + service.getStationStr(pisBean.nextCode))
                        }
                        1 -> {
                            tip = service.getString(R.string.arrive)
                            service.stationStatusObs.set(tip)
                            service.stationObs.set("$tip：" + service.getStationStr(pisBean.nextCode))
                        }
                        2 -> {
                            tip = service.getString(R.string.arrived)
                            service.stationStatusObs.set(tip)
                            service.stationObs.set("$tip：" + service.getStationStr(pisBean.currentCode))
                        }
                        else -> {
                            // 如果界面还打开着，关闭界面
                        }
                    }
                }


                pisBean.urgentNotifyCode = getInfoByIndex(msg, 30, 32)
                // 判断当前界面或者判断上一个记录

                val str = service.getUrgentNotifyStr(pisBean.urgentNotifyCode)
//                if (str.isNullOrEmpty())
//                    LiveEBUtil.post(RemoteMessageEvent(MessageConstant.CMD_URGENT_NOTICE, ""))
                service.urgentNotifyMsgObs.set(str)
                Timber.i("站点信息 ${pisBean.boardStatus} ${service.stationStatusObs.get()} ${service.stationObs.get()} 提示 ${pisBean.urgentNotifyCode} ${str ?: ""}")

                pisBean.runDirection = getInfoByIndex(msg, 34, 36)
                pisBean.carNumber = getInfoByIndex(msg, 36, 40)

                pisBean.jiaoLuNumber = getInfoByIndex(msg, 40, 42)
                pisBean.carCiNumber = getInfoByIndex(msg, 42, 44)
                pisBean.lineNumber = getInfoByIndex(msg, 44, 46)

                pisBean.ignoreStation = getInfoByIndex(msg, 46, 50)

                pisBean.year = getInfoByIndex(msg, 88, 90)
                pisBean.month = getInfoByIndex(msg, 90, 92)
                pisBean.day = getInfoByIndex(msg, 92, 94)
                pisBean.hour = getInfoByIndex(msg, 94, 96)
                pisBean.minute = getInfoByIndex(msg, 96, 98)
                pisBean.second = getInfoByIndex(msg, 98, 100)
                service.timeObs.set("20${pisBean.year}年${getTime(pisBean.month)}月${getTime(pisBean.day)}日 ${getTime(pisBean.hour)}:${getTime(pisBean.minute)}:${getTime(pisBean.second)}")
                                
                // 自动校时逻辑
                syncSystemTime(service, pisBean)
                                
                Timber.i("${service.timeObs.get()} ${pisBean.toString()}")
            }
        }
    }

    private fun getInfoByIndex(msg: String, start: Int, end: Int): Int {
        if (end > msg.length) return -1
        return DataUtil.hexStringToDecimal(msg.substring(start, end))
    }

    /**
     * 处理武汉协议 (FlavorB) 消息
     * 将 WuhanParsedMessage 映射到现有 PISBean/service 接口
     */
    private fun handleWuhanMessage(msg: String, service: TmpServiceImpl) {
        val parsed = WuhanProtocolParser.parseFromHex(msg)
        if (parsed == null) {
            Timber.e("武汉协议解析失败")
            return
        }

        when {
            // 0xB1 线路/站点信息 (核心数据包)
            parsed.routeInfo != null -> {
                val info = parsed.routeInfo!!
                val pisBean = PISBean()

                // 站点信息映射
                pisBean.startCode = info.startStationId
                pisBean.endCode = info.endStationId
                pisBean.currentCode = info.currentStationId
                pisBean.nextCode = info.nextStationId

                // boardStatus 映射: 0x00=停车→0(arrived), 0x01=运行→?, 0x02=即将到站→0(next)
                pisBean.boardStatus = when (info.trainStatus and 0x03) {
                    0 -> 2  // 停车 → arrived
                    1 -> -1 // 运行中 → 无站点提示
                    2 -> 0  // 即将到站 → next station
                    else -> -1
                }

                pisBean.runDirection = info.direction
                pisBean.lineNumber = info.lineId
                pisBean.carCiNumber = info.routeId
                pisBean.jiaoLuNumber = info.piscId
                pisBean.urgentNotifyCode = info.emergencyFlag

                // 更新服务状态
                service.stationNotifyObs.set(pisBean.boardStatus)

                var tip = ""
                if (pisBean.boardStatus != -1) {
                    when (pisBean.boardStatus) {
                        0 -> {
                            tip = service.getString(R.string.nextStation)
                            service.stationStatusObs.set(tip)
                            service.stationObs.set("$tip：" + service.getStationStr(pisBean.nextCode))
                        }
                        1 -> {
                            tip = service.getString(R.string.arrive)
                            service.stationStatusObs.set(tip)
                            service.stationObs.set("$tip：" + service.getStationStr(pisBean.nextCode))
                        }
                        2 -> {
                            tip = service.getString(R.string.arrived)
                            service.stationStatusObs.set(tip)
                            service.stationObs.set("$tip：" + service.getStationStr(pisBean.currentCode))
                        }
                        else -> { /* 无状态 */ }
                    }
                }

                Timber.i("武汉B1 站点信息: 当前站=${pisBean.currentCode}, 下一站=${pisBean.nextCode}, " +
                        "状态=${pisBean.boardStatus} 方向=${pisBean.runDirection} " +
                        "温度=${info.temperature} 拥挤度=${info.crowding}")
            }

            // 0xB5 文本广播 → 紧急通知
            parsed.textBroadcast != null -> {
                val broadcast = parsed.textBroadcast!!
                if (broadcast.message.isNotEmpty()) {
                    service.urgentNotifyMsgObs.set(broadcast.message)
                    Timber.i("武汉B5 文本广播: 类型=${broadcast.broadcastType}, " +
                            "优先级=${broadcast.priority}, 消息=${broadcast.message}")
                }
            }

            // 0xB2/B3/B4 站点/车门数据 (辅助信息)
            parsed.stationData != null || parsed.doorData != null || parsed.doorStatus != null -> {
                Timber.d("武汉协议 站点/车门数据: " +
                        "B2=${parsed.stationData != null}, " +
                        "B3=${parsed.doorData != null}, " +
                        "B4=${parsed.doorStatus != null}")
                // 这些数据主要用于拥挤度显示，目前先记录日志
            }

            // 0xA1 心跳 (通常是设备发送的，收到说明是PISC回显，可忽略)
            parsed.heartbeat != null -> {
                Timber.d("武汉A1 心跳回显: 设备=${parsed.heartbeat!!.deviceId}")
            }

            else -> {
                Timber.w("武汉协议 未识别的数据包")
            }
        }
    }

    fun getTime(time: Int): String {
        return if (time < 10) return "0$time"
        else time.toString()
    }

    /**
     * 同步系统时间
     * @param service TmpServiceImpl实例
     * @param pisBean 包含时间信息的PISBean
     */
    private fun syncSystemTime(service: TmpServiceImpl, pisBean: PISBean) {
        try {
            val context = service.applicationContext
            
            // 1. 检查并关闭自动校时
            if (isAutoTimeEnabled(context)) {
                disableAutoTime(context)
                Timber.i("已关闭自动校时功能")
            }
            
            // 2. 校验 PIS 时间数据有效性
            // year=0 或 month=0 表示 PIS 时间数据未初始化，应跳过校时
            if (pisBean.year <= 0 || pisBean.month <= 0 || pisBean.day <= 0) {
                Timber.w("PIS时间数据无效(year=${pisBean.year}, month=${pisBean.month}, day=${pisBean.day})，跳过校时")
                return
            }

            // 3. 计算时间差
            val receivedTimeMillis = convertToMillis(pisBean)
            val currentTimeMillis = System.currentTimeMillis()
            val timeDiff = Math.abs(receivedTimeMillis - currentTimeMillis)

            // Timber.i("时间对比 - 接收时间: $receivedTimeMillis, 当前时间: $currentTimeMillis, 差值: ${timeDiff}ms (${timeDiff / 1000 / 60}分钟)")

            // 4. 如果时间差大于1分钟(60000ms)，则设置系统时间
            if (timeDiff > 60000) {
                setSystemTime(receivedTimeMillis)
                Timber.i("时间差超过1分钟，已更新系统时间为: ${service.timeObs.get()}")
            }
//            else {
//                Timber.i("时间差在1分钟内，无需校时")
//            }
        } catch (e: Exception) {
            Timber.e("自动校时失败: ${e.message}")
            e.printStackTrace()
        }
    }
    
    /**
     * 检查是否开启了自动校时
     */
    private fun isAutoTimeEnabled(context: Context): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Settings.Global.getInt(context.contentResolver, Settings.Global.AUTO_TIME, 0) == 1
            } else {
                Settings.System.getInt(context.contentResolver, Settings.System.AUTO_TIME, 0) == 1
            }
        } catch (e: Exception) {
            Timber.e("检查自动校时状态失败: ${e.message}")
            false
        }
    }
    
    /**
     * 关闭自动校时
     */
    private fun disableAutoTime(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Settings.Global.putInt(context.contentResolver, Settings.Global.AUTO_TIME, 0)
            } else {
                Settings.System.putInt(context.contentResolver, Settings.System.AUTO_TIME, 0)
            }
        } catch (e: Exception) {
            Timber.e("关闭自动校时失败: ${e.message}")
        }
    }
    
    /**
     * 将PISBean中的时间信息转换为毫秒时间戳
     */
    private fun convertToMillis(pisBean: PISBean): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, 2000 + pisBean.year)
        calendar.set(Calendar.MONTH, pisBean.month - 1) // Calendar月份从0开始
        calendar.set(Calendar.DAY_OF_MONTH, pisBean.day)
        calendar.set(Calendar.HOUR_OF_DAY, pisBean.hour)
        calendar.set(Calendar.MINUTE, pisBean.minute)
        calendar.set(Calendar.SECOND, pisBean.second)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    /**
     * 设置系统时间
     * 注意：需要系统权限 android.permission.SET_TIME
     */
    private fun setSystemTime(timeMillis: Long) {
        try {
            // 使用 root 权限或系统应用权限设置时间
            val command = "date -s @$((timeMillis / 1000))"
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            process.waitFor()
            Timber.i("系统时间已设置为: $timeMillis")
        } catch (e: Exception) {
            Timber.e("设置系统时间失败: ${e.message}")
            // 尝试另一种方法
            try {
                val process = Runtime.getRuntime().exec("su")
                val outputStream = process.outputStream
                val writer = java.io.OutputStreamWriter(outputStream)
                writer.write("date -s @$((timeMillis / 1000))\n")
                writer.write("exit\n")
                writer.flush()
                writer.close()
                outputStream.close()
                process.waitFor()
                Timber.i("系统时间已通过备用方法设置")
            } catch (e2: Exception) {
                Timber.e("备用方法也失败: ${e2.message}")
            }
        }
    }

}
