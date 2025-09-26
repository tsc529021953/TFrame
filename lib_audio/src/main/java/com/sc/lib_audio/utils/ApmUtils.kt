package com.sc.lib_audio.utils

import com.bk.webrtc.Apm
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ApmUtils {

    private var apm: Apm? = null

    fun init() {
        apm = Apm(false, true, true, false, false, false, false)
        apm?.AEC(false)
        apm?.AECM(false)
        apm?.NSSetLevel(Apm.NS_Level.VeryHigh);
        apm?.NS(true);
        apm?.AGC(true)
        apm?.AGCSetMode(Apm.AGC_Mode.FixedDigital)
        apm?.HighPassFilter(true)
        apm?.VAD(true)
        apm?.VADSetLikeHood(Apm.VAD_Likelihood.ModerateLikelihood)
        log("apm $apm")
    }

    fun release() {
        try {
            apm?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun process(data: ByteArray): ByteArray {
//        log("datas ${data.size}")
//        return data
        // 转short
        val end = data.size % 320
        val endCount = if (end == 0) 320 else end
        val count = if (data.size % 320 == 0) data.size / 320 else (data.size / 320) + 1
        var data2 = ByteArray(count * 320)
        val tempData = ByteArray(320)
        val inputData = ShortArray(160)
        val outNsData = ShortArray(160)
        val outAgcData = ShortArray(160)
        val outData = ShortArray(160)
        val bufferCvt = ByteBuffer.allocate(2)
        bufferCvt.order(ByteOrder.LITTLE_ENDIAN);
        for (i in 0 until count) {
            val index = i * 320
            System.arraycopy(data, index, tempData, 0, if (i == count -1) endCount else 320);
            ByteBuffer.wrap(tempData).order(ByteOrder.LITTLE_ENDIAN)
                .asShortBuffer()
                .get(inputData)

            val res = apm?.ProcessRenderStream(inputData, 0)
            val res2 = apm?.ProcessCaptureStream(inputData, 0)
//            val res3 = apm?.ProcessCaptureStream()
//            nsUtils!!.nsxProcess(nsxId, inputData, 1, outNsData)
//            val res = agcUtils!!.agcProcess(
//                agcId, inputData, 1, 160, outData,
//                0, 0, 0, false
//            )
//            nsUtils!!.nsxProcess(nsxId, outAgcData, 1, outData)
            log("APM process $res $res2")
            for (j in 0 until inputData.size) {
                bufferCvt.clear()
                bufferCvt.putShort(inputData[j])
                data2[index + j * 2] = bufferCvt[0]
                data2[index + j * 2 + 1] = bufferCvt[1]
            }
        }
        // agc
//        log("datas ${data2.size}")
        return data2
    }

    fun log(msg: Any?) {
        System.out.println("ApmUtils ${msg ?: ""}")
    }
}