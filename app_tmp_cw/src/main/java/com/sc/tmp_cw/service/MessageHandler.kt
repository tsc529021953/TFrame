package com.sc.tmp_cw.service

import com.nbhope.lib_frame.utils.DataUtil
import com.sc.tmp_cw.bean.PISBean
import timber.log.Timber
import java.lang.StringBuilder

/**
 * @author  tsc
 * @date  2025/1/10 16:46
 * @version 0.0.0-1
 * @description
 */
object MessageHandler {

    fun test(msg: String, service: TmpServiceImpl) {
        var data = msg
        if (data.isNullOrEmpty()) {
            val sb = StringBuilder()
            sb.append("ff00ff00") // 框架版本
            val end = sb.toString()
            data = "${PISBean.START_MESSAGE}${DataUtil.intToHex2(PISBean.START_MESSAGE.length + 4 + end.length) }$end"
            System.out.println("message:$data")
        }
        handleMessage(data, service)
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
            val len2 = DataUtil.hexStringToDecimal(msg.substring(PISBean.START_MESSAGE.length, PISBean.START_MESSAGE.length + 4))
            System.out.println("len $len2 $len")
        }
    }

}
