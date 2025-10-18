package com.sc.tmp_translate.utils.record

import android.app.NotificationManager
import android.app.Service
import android.os.SystemClock
import android.util.Log
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile

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

    fun TinyCapRecord() {}

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
        val cmd = String.format("tinycap %s -r %d -c %d -b %d -D %d -d %d",
                outputPath, sampleRate, channels, bits, card, device)
        return try {
            Log.d(TAG, "Executing: $cmd")
            recordProcess = Runtime.getRuntime().exec(arrayOf("sh", "-c", cmd))
            recordRunning = true

            Thread(Runnable {
                Timber.i("TinyCapRecord 解析线程开始")
                // 延迟1s
//                while (recordRunning) {
//
//                    Thread.sleep(100)
//                }
                val outputFile = File(outputPath)
                RandomAccessFile(outputFile, "r").use { raf ->
                    while (recordRunning) {
                        val fileLen = outputFile.length()
                        if (fileLen > lastPosition) {
                            val newSize = (fileLen - lastPosition).toInt()
                            val buffer = ByteArray(newSize)
                            raf.seek(lastPosition)
                            raf.readFully(buffer)
                            lastPosition = fileLen

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
        raf.setLength(0)
        lastPosition = 0L
    }

}