package com.sc.tmp_cw.service

import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.utils.DataUtil
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.sc.tmp_cw.bean.PISBean
import timber.log.Timber
import java.lang.StringBuilder
import com.sc.tmp_cw.R
import com.sc.tmp_cw.constant.MessageConstant
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author  tsc
 * @date  2025/1/10 16:46
 * @version 0.0.0-1
 * @description
 */
object MessageHandler {

    val testData1 = "50 41 00 00 00 00 01 00 00 00 00 1D 00 01 00 6f 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 19 01 0D 08 34 32"
    val testData2 = "50 41 00 00 00 00 01 00 00 00 00 1D 00 01 00 70 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 19 01 0D 09 34 32"
    val testData3 = "50 41 00 00 00 00 01 00 00 00 00 1D 00 01 00 FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 19 01 0D 08 34 34"

    val IS_JUDGE_LENGTH = false

    /**
     *
     * @param msg
     * @param service
     */
    fun test(msg: String, service: TmpServiceImpl) {
//        var data = msg
//        if (data.isNullOrEmpty()) {
//            val sb = StringBuilder()
//            sb.append("ff00ff00") // 框架版本
//            val end = sb.toString()
//            data = "${PISBean.START_MESSAGE}${DataUtil.intToHex2(PISBean.START_MESSAGE.length + 4 + end.length) }$end"
//            System.out.println("message:$data")
//        }
        service.mScope.launch {
            handleMessage(testData1.replace(" ", ""), service)
//            delay(5000)
//            handleMessage(testData2.replace(" ", ""), service)
            delay(5000)
            handleMessage(testData3.replace(" ", ""), service)
        }

    }

    fun handleMessage(msg: String, service: TmpServiceImpl) {
        if (!msg.startsWith(PISBean.START_MESSAGE)) {
            Timber.e("消息头错误 $msg")
        } else {
            val len = msg.length
            // 获取数据长度
            if (len <= 8) {
                Timber.e("消息长度不够")
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


                pisBean.startCode = getInfoByIndex(msg, 20, 22)
                pisBean.endCode = getInfoByIndex(msg, 22, 24)
                pisBean.currentCode = getInfoByIndex(msg, 24, 26)
                pisBean.nextCode = getInfoByIndex(msg, 26, 28)
                if (pisBean.boardStatus != -1) {
                    when (pisBean.boardStatus) {
                        0 -> {
                            service.stationStatusObs.set(service.getString(R.string.nextStation))
                            service.stationObs.set(service.getStationStr(pisBean.nextCode))
                        }
                        1 -> {
                            service.stationStatusObs.set(service.getString(R.string.arrive))
                            service.stationObs.set(service.getStationStr(pisBean.currentCode))
                        }
                        2 -> {
                            service.stationStatusObs.set(service.getString(R.string.arrived))
                            service.stationObs.set(service.getStationStr(pisBean.currentCode))
                        }
                    }
                }


                pisBean.urgentNotifyCode = getInfoByIndex(msg, 30, 32)
                // 判断当前界面或者判断上一个记录

                val str = service.getUrgentNotifyStr(pisBean.urgentNotifyCode)
//                if (str.isNullOrEmpty())
//                    LiveEBUtil.post(RemoteMessageEvent(MessageConstant.CMD_URGENT_NOTICE, ""))
                service.urgentNotifyMsgObs.set(str)

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

                System.out.println(pisBean)
            }
        }
    }

    private fun getInfoByIndex(msg: String, start: Int, end: Int): Int {
        if (end > msg.length) return -1
        return DataUtil.hexStringToDecimal(msg.substring(start, end))
    }

    fun getTime(time: Int): String {
        return if (time < 10) return "0$time"
        else time.toString()
    }

}
