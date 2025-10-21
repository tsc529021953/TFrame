package com.sc.tmp_translate.utils.record

import android.app.NotificationManager
import android.app.Service
import android.os.SystemClock
import android.util.Log
import com.bk.webrtc.Apm
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder

class TinyCapRecord {

    private val TAG = "TinyCapManager"

    var outputPath: String? = null
    var newPath: String? = null
    private var sampleRate = 44100
    private var channels = 2
    private var bits = 16
    var card = 4
    private var device = 0

    private var recordProcess: Process? = null

    @Volatile
    var recordRunning = false
    private var lastPosition = 0L

    var apm: Apm? = null

    var cmd: String = ""

    constructor() {
        initWebRTC()
    }

    fun setParams(sampleRate: Int, channels: Int, bits: Int, card: Int, device: Int) {
        this.sampleRate = sampleRate
        this.channels = channels
        this.bits = bits
        this.card = card
        this.device = device
    }

    fun startRecording(outputPath: String?): Boolean {
        this.outputPath = outputPath
        if (recordProcess != null) {
            Log.w(TAG, "Already recording")
            return false
        }
        cmd = String.format("tinycap %s -r %d -c %d -b %d -D %d -d %d",
                outputPath, sampleRate, channels, bits, card, device)
        return try {
            Log.d(TAG, "Executing: $cmd")
            recordProcess = Runtime.getRuntime().exec(arrayOf("sh", "-c", cmd))
            recordRunning = true

            Thread(Runnable {
                Timber.i("TinyCapRecord 接收线程开始")
                // 延迟1s

                val outputFile = File(outputPath)
                RandomAccessFile(outputFile, "rw").use { raf ->
                    Thread.sleep(500)
                    while (recordRunning) {
                        val fileLen = outputFile.length()
                        if (fileLen > lastPosition) {
                            val newSize = (fileLen - lastPosition).toInt()
                            val buffer = ByteArray(newSize)
                            raf.seek(lastPosition)
                            raf.readFully(buffer)
                            lastPosition = fileLen

                            // 解析看看是否有声音

//                            onData(buffer)
                            println("TinyCapRecord 解析数据 ${buffer.size} ${if(buffer.isNotEmpty()) buffer[0] else "empty"}")

                            // ⚠️ 如果要避免文件无限增长，可以定期清空
                            if (lastPosition > 2 * 1024 * 1024) { // >2MB
                                truncateFile(outputFile, raf)
                            }
                        }
                        Thread.sleep(100)
                    }
                }

                Timber.i("TinyCapRecord 解析线程结束")
            }).start()
            Thread({
                Timber.i("TinyCapRecord 解析线程开始")
                while (recordRunning) {

                }
                Timber.i("TinyCapRecord 解析线程结束")
            }).start()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            recordProcess = null
            false
        }
    }

    fun stopRecording() {
        recordRunning = false
        if (recordProcess != null) {
            recordProcess!!.destroy() // 终止录音进程
            recordProcess = null
        } else {
            Log.w(TAG, "Recording process is null")
        }
    }

    fun isRecording(): Boolean {
        return recordProcess != null
    }

    fun isTinyCapAvailable(): Boolean {
        val tinycap = File("/system/bin/tinycap")
        return tinycap.exists() && tinycap.canExecute()
    }

    private fun truncateFile(file: File, raf: RandomAccessFile) {
        // 将当前内容截断 (重新写header或清空)
//        if (recordProcess != null) {
//            recordProcess!!.destroy() // 终止录音进程
//            recordProcess = null
//            recordProcess = Runtime.getRuntime().exec(arrayOf("sh", "-c", cmd))
//        }
//        raf.seek(0)
        raf.setLength(0)
        lastPosition = 0L
    }

    private fun initWebRTC() {
//        apm.AECMSetSuppressionLevel(Apm.AECM_RoutingMode.LoudSpeakerphone);
//        apm.AECM(true);
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
        Timber.i("apm $apm")

    }

    private fun processWebRTC(data: ByteArray): ByteArray {
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

            /*val res =*/ apm?.ProcessRenderStream(inputData, 0)
            /*val res2 = */apm?.ProcessCaptureStream(inputData, 0)
//            val res3 = apm?.ProcessCaptureStream()
//            nsUtils!!.nsxProcess(nsxId, inputData, 1, outNsData)
//            val res = agcUtils!!.agcProcess(
//                agcId, inputData, 1, 160, outData,
//                0, 0, 0, false
//            )
//            nsUtils!!.nsxProcess(nsxId, outAgcData, 1, outData)
//            log("APM process $res $res2")
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

}