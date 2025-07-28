package com.sc.tmp_translate.utils.record

import android.R.attr.streamType
import android.content.Context
import android.media.*
import android.os.Environment
import com.sc.tmp_translate.inter.ITransRecord
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


/**
 * TODO
 * 1.读取两个摄像头
 * 2.打开并录音
 * 3.录音并播放
 */
class TransAudioRecord(var context: Context, var iTransRecord: ITransRecord) {

    companion object {
        const val SAMPLE_RATE_IN_HZ = 16000
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        const val RECORD_CHANNEL_CONFIG: Int = AudioFormat.CHANNEL_IN_MONO
        const val AUDIO_SOURCE = MediaRecorder.AudioSource.MIC
    }

    private val am: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private var audioRecord1: AudioRecord? = null
    private var audioRecord2: AudioRecord? = null
    private var isRecord1 = false
    private var isRecord2 = false

    private var isRecordEnd = true
    private var isRelease = false

    private var audioTrack1: AudioTrack? = null
    private var audioTrack2: AudioTrack? = null

    private lateinit var recordBuffer1: ByteArray
    private lateinit var recordBuffer2: ByteArray

    private var outputStream1: BufferedOutputStream? = null
    private var outputStream2: BufferedOutputStream? = null

    private var minSize = 0
    private var minTrackSize = 0

    fun init() {
        val devices = am.getDevices(AudioManager.GET_DEVICES_INPUTS)
        log("devices size ${devices.size}")
        devices.forEach {
            log("device ${it.id} ${it.productName} ${it.address} ${it.type} ${it.type == AudioDeviceInfo.TYPE_USB_DEVICE}")
            if (it.type == AudioDeviceInfo.TYPE_USB_DEVICE) {
                log("USB MIC ${it.id} ${it.productName}")
            }
        }
        minSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE_IN_HZ,
            RECORD_CHANNEL_CONFIG,
            AUDIO_FORMAT
        )
        recordBuffer1 = ByteArray(minSize)
        recordBuffer2 = ByteArray(minSize)
        log("minSize $minSize")
        audioRecord1 = AudioRecord(
            AUDIO_SOURCE,
            SAMPLE_RATE_IN_HZ,
            RECORD_CHANNEL_CONFIG,
            AUDIO_FORMAT,
            minSize
        )
        audioRecord2 = AudioRecord(
            MediaRecorder.AudioSource.VOICE_UPLINK,
            SAMPLE_RATE_IN_HZ,
            RECORD_CHANNEL_CONFIG,
            AUDIO_FORMAT,
            minSize
        )
        log("audioRecord1 初始化 ${if (audioRecord1?.state == AudioRecord.STATE_INITIALIZED) "成功" else "失败" } ")
        log("audioRecord2 初始化 ${if (audioRecord2?.state == AudioRecord.STATE_INITIALIZED) "成功" else "失败" } ")
//        minTrackSize = AudioTrack.getMinBufferSize(SAMPLE_RATE_IN_HZ,
//            RECORD_CHANNEL_CONFIG,
//            AUDIO_FORMAT);
//        audioTrack1 =
//            AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE_IN_HZ, RECORD_CHANNEL_CONFIG, AUDIO_FORMAT, minTrackSize
//                , AudioTrack.MODE_STREAM)
//        audioTrack2 =
//            AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE_IN_HZ, RECORD_CHANNEL_CONFIG, AUDIO_FORMAT, minTrackSize
//                , AudioTrack.MODE_STREAM)
        log("audioTrack1 初始化 ${if (audioTrack1?.state == AudioTrack.STATE_INITIALIZED) "成功" else "失败" } ")
        log("audioTrack2 初始化 ${if (audioTrack2?.state == AudioTrack.STATE_INITIALIZED) "成功" else "失败" } ")
    }

    fun start() {
        isRecordEnd = false
        if (!isRecord1 && audioRecord1?.state == AudioRecord.STATE_INITIALIZED) {
            isRecord1 = true
            val outputFile = createOutputFile(1)
            try {
                outputStream1 = BufferedOutputStream(FileOutputStream(outputFile))
            } catch (e: Exception) {
                log("文件1创建失败: ${e.message}")
                outputStream1 = null
            }
            audioRecord1?.startRecording()
            //发数据的线程
            Thread {
                var read = -1
                while (!isRecordEnd) {
                    read = audioRecord1!!.read(recordBuffer1, 0, recordBuffer1.size)
                    log("read1 $read")
                    audioTrack1?.write(recordBuffer1, 0, read)
                    if (read > 0) {
                        outputStream1?.write(recordBuffer1, 0, read)
                    }
                }
                try {
                    outputStream2?.flush()
                    outputStream2?.close()
                } catch (e: Exception) {
                    log( "关闭文件1失败: ${e.message}")
                }
                iTransRecord?.onRecordEnd(true, outputFile.absolutePath)
                audioRecord1?.stop()
                audioTrack1?.stop()
                if (isRelease) {
                    audioRecord1?.release()
                    audioRecord1 = null

                    audioTrack1?.release()
                    audioTrack1 = null
                }
                isRecord1 = false
                log("录音1结束 $isRelease")
            }.start()
        }
        if (!isRecord2  && audioRecord2?.state == AudioRecord.STATE_INITIALIZED) {
            isRecord2 = true
            val outputFile = createOutputFile(2)
            try {
                outputStream2 = BufferedOutputStream(FileOutputStream(outputFile))
            } catch (e: Exception) {
                log("文件2创建失败: ${e.message}")
                outputStream2 = null
            }
            audioRecord2?.startRecording()
            //发数据的线程
            Thread {
                var read = -1
                while (!isRecordEnd) {
                    read = audioRecord2!!.read(recordBuffer2, 0, recordBuffer2.size)
                    log("read2 $read")
                    audioTrack2?.write(recordBuffer2, 0, read)
                    if (read > 0) {
                        outputStream2?.write(recordBuffer2, 0, read)
                    }
                }
                try {
                    outputStream2?.flush()
                    outputStream2?.close()
                } catch (e: Exception) {
                    log( "关闭文件2失败: ${e.message}")
                }
                audioRecord2?.stop()
                audioTrack2?.stop()
                if (isRelease) {
                    audioRecord2?.release()
                    audioRecord2 = null

                    audioTrack2?.release()
                    audioTrack2 = null
                }
                isRecord2 = false
                log("录音2结束 $isRelease")
            }.start()
        }
    }

    fun stop() {
        if (isRecordEnd) {
            audioRecord1?.release()
            audioRecord1 = null

            audioRecord2?.release()
            audioRecord2 = null
        }
        isRecordEnd = true
    }

    fun release() {
        isRelease = true
        stop()
    }

    private fun createOutputFile(index: Int): File {
        val dir = File(Environment.getExternalStorageDirectory(), "AudioRecordings")
        if (!dir.exists()) dir.mkdirs()
        val fileName = "${index}_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date()) + ".pcm"
        return File(dir, fileName)
    }

    private fun log(msg: Any?) {
        System.out.println("TARecord ${msg ?: "null"}")
    }

}