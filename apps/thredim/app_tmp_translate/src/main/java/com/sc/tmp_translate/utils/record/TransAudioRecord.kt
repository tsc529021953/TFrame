package com.sc.tmp_translate.utils.record

import android.content.Context
import android.media.*
import android.os.Environment
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession
import com.arthenica.ffmpegkit.FFmpegSessionCompleteCallback
import com.arthenica.ffmpegkit.ReturnCode
import com.sc.lib_audio.utils.PcmDiffProcessor
//import com.sc.audio.DualRecorderJNI
import com.sc.tmp_translate.inter.ITransRecord
//import com.signway.aec.AEC
import timber.log.Timber
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


/**
 *  * 1.读取两个摄像头
 *  * 2.打开并录音
 *  * 3.录音并播放
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
    private var card1 = -1
    private var card2 = -1

    private var isRecordEnd = true
    private var isRelease = false

    private var audioTrack1: AudioTrack? = null
    private var audioTrack2: AudioTrack? = null

    private lateinit var recordBuffer1: ByteArray
    private lateinit var recordBuffer2: ByteArray

    private var outputStream1: BufferedOutputStream? = null
    private var outputStream2: BufferedOutputStream? = null

    private var recorder1Ptr: Long = 0
    private var recorder2Ptr: Long = 0

    private var tinyCapManager1: TinyCapManager? = null
    private var tinyCapManager2: TinyCapManager? = null

    private var tinyCapRecord1: TinyCapRecord? = null
    private var tinyCapRecord2: TinyCapRecord? = null

//    private var pcmRecord1: AEC? = null
//    private var pcmRecord2: AEC? = null

    private var minSize = 0
    private var minTrackSize = 0

    fun init() {
        // 此处通过系统API来读取设备数据
//        val devices = am.getDevices(AudioManager.GET_DEVICES_INPUTS)
//        log("devices size ${devices.size}")
//        devices.forEach {
//            log("device ${it.id} ${it.productName} ${it.address} ${it.type} ${it.type == AudioDeviceInfo.TYPE_USB_DEVICE}")
//            if (it.type == AudioDeviceInfo.TYPE_USB_DEVICE) {
//                log("USB MIC ${it.id} ${it.productName}")
//                val list1 = it.address.split(";")
//                if (list1.size > 1 && list1[0].contains("card=")) {
//                    card1 = list1[0].replace("card=", "").toIntOrNull() ?: -1
//                    if (card1 == -1) {
//                        log("未能读到主麦克相关信息 ${it.address}")
//                    } else log("麦克风1的Id为 $card1")
//                } else {
//                    log("未能读到主麦克相关信息 ${it.address}")
//                }
//            }
//        }

        minSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE_IN_HZ,
            RECORD_CHANNEL_CONFIG,
            AUDIO_FORMAT
        )
        recordBuffer1 = ByteArray(minSize)
        recordBuffer2 = ByteArray(minSize)
        log("minSize $minSize")

        val device1 = "/dev/snd/pcmC4D0c" // 第一个USB声卡
        val device2 = "/dev/snd/pcmC5D0c" // 第二个USB声卡
        // 初始化录音器

//        tinyCapManager1 = TinyCapManager()
//        tinyCapManager2 = TinyCapManager()

        tinyCapRecord1 = TinyCapRecord()
//        tinyCapRecord2 = TinyCapRecord()
//        pcmRecord1 = AEC()
//        pcmRecord2 = AEC()
        log("初始化结果 tinyCapManager ${TinyCapManager.isTinyCapAvailable()}")
//        recorder1Ptr = DualRecorderJNI.initRecorder(device1, SAMPLE_RATE_IN_HZ, 1);
//        recorder2Ptr = DualRecorderJNI.initRecorder(device2, SAMPLE_RATE_IN_HZ, 1);
//        log("初始化结果： recorder1Ptr $recorder1Ptr  recorder2Ptr $recorder2Ptr ")
//        audioRecord1 = AudioRecord(
//            AUDIO_SOURCE,
//            SAMPLE_RATE_IN_HZ,
//            RECORD_CHANNEL_CONFIG,
//            AUDIO_FORMAT,
//            minSize
//        )
//        audioRecord1.setPreferredDevice()
//        audioRecord2 = AudioRecord(
//            MediaRecorder.AudioSource.VOICE_UPLINK,
//            SAMPLE_RATE_IN_HZ,
//            RECORD_CHANNEL_CONFIG,
//            AUDIO_FORMAT,
//            minSize
//        )
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
        AudioCardUtils.readSoundCards(object : AudioCardUtils.OnCardInfoReadListener{
            override fun onReadCompleted(
                allCards: List<AudioCardUtils.SoundCardInfo>,
                usbCards: List<AudioCardUtils.SoundCardInfo>
            ) {
                log("读取的USB麦克风数${usbCards.size} ${allCards.size}")
                for (i in usbCards.indices) {
                    log(usbCards[i].toString())
                    if (card1 == -1) { card1 = usbCards[i].index }
                    else if (usbCards[i].index != card1) card2 = usbCards[i].index
                }
                if (card1 == -1) {
                    log("读取麦克风信息失败 $card1 $card2")
                } else {
                    tinyCapManager1?.card = card1
                    tinyCapManager2?.card = card2
                    tinyCapRecord1?.card = card1
                    tinyCapRecord2?.card = card2
                    log("麦克风1的Id为 $card1")
                    log("麦克风2的Id为 $card2")
                }

            }

            override fun onReadFailed(error: String) {
                log("读取麦克风信息失败，请确定相关设备信息")
            }

        })
    }

    fun start(index: Int = 0) {
        isRecordEnd = false
        recordState = 0
        if ((index == 1 || index == 0) && !isRecord1 && audioRecord1?.state == AudioRecord.STATE_INITIALIZED) {
            isRecord1 = true
            val outputFile = createOutputFile(1)
            try {
                outputStream1 = BufferedOutputStream(FileOutputStream(outputFile))
            } catch (e: Exception) {
                log("文件1创建失败: ${e.message}")
                outputStream1 = null
            }

            log("开始录制 $index")
            audioRecord1?.startRecording()
            //发数据的线程
            Thread {
                var read = -1
                while (!isRecordEnd) {
                    read = audioRecord1!!.read(recordBuffer1, 0, recordBuffer1.size)
                    audioTrack1?.write(recordBuffer1, 0, read)
                    if (read > 0) {
                        outputStream1?.write(recordBuffer1, 0, read)
                    }
                }
                try {
                    outputStream1?.flush()
                    outputStream1?.close()
                } catch (e: Exception) {
                    log( "关闭文件1失败: ${e.message}")
                }
                onRecordEnd(true, outputFile.absolutePath)
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
        if ((index == 2 || index == 0) && !isRecord2 && audioRecord2?.state == AudioRecord.STATE_INITIALIZED) {
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
        if ((index == 1 || index == 0) && tinyCapManager1?.isRecording == false && card1 > 0) {
            val outputFile = createOutputFile(1)
            tinyCapManager1?.newPath = outputFile.absolutePath
            val res = tinyCapManager1?.startRecording(getTempPath(1).absolutePath)
            log("开始录制1 $index $res ${outputFile.absolutePath}")
        }
        if ((index == 2 || index == 0) && tinyCapManager2?.isRecording == false && card2 > 0) {
            val outputFile = createOutputFile(2)
            tinyCapManager2?.newPath = outputFile.absolutePath
            val res = tinyCapManager2?.startRecording(getTempPath(2).absolutePath)
            log("开始录制2 $index $res ${outputFile.absolutePath}")
        }
        if ((index == 1 || index == 0) && tinyCapRecord1?.isRecording() == false/* && card1 > 0*/) {
            val outputFile = createOutputFile(1)
            tinyCapRecord1?.newPath = outputFile.absolutePath
            val res = tinyCapRecord1?.startRecording(getTempPath(1).absolutePath)
            log("开始录制3 $index $res ${outputFile.absolutePath}")
        }
        if ((index == 2 || index == 0) && tinyCapRecord2?.isRecording() == false /*&& card2 > 0*/) {
            val outputFile = createOutputFile(2)
            tinyCapRecord1?.newPath = outputFile.absolutePath
            val res = tinyCapRecord1?.startRecording(getTempPath(2).absolutePath)
            log("开始录制4 $index $res ${outputFile.absolutePath}")
        }
//        if ((index == 1 || index == 0) && !isRecord1 /*&& pcmRecord1 != null && card1 > 0*/) {
//            isRecord1 = true
//            val outputFile = createOutputFile(1)
//            try {
//                outputStream1 = BufferedOutputStream(FileOutputStream(outputFile))
//            } catch (e: Exception) {
//                log("文件1创建失败: ${e.message}")
//                outputStream1 = null
//            }
//
//            log("开始录制 $index")
////            pcmRecord1?.open(card1, 0, 16000, 1)
//            // 线程读取数据
//            Thread {
//                var read = -1
//                while (!isRecordEnd) {
//
////                    read = pcmRecord1!!.read(recordBuffer1, recordBuffer1.size)
////                    if (read > 0) {
////                        outputStream1?.write(recordBuffer1, 0, read)
////                    }
//                }
//                try {
//                    outputStream2?.flush()
//                    outputStream2?.close()
//                } catch (e: Exception) {
//                    log( "关闭文件1失败: ${e.message}")
//                }
//                onRecordEnd(true, outputFile.absolutePath)
////                pcmRecord1?.close()
//                if (isRelease) {
////                    pcmRecord1 = null
//                }
//                isRecord1 = false
//                log("录音1结束 $isRelease")
//            }.start()
//        }
    }

    fun stop(index: Int = 0) {
        if (index == 1 || index == 0) {
            if (isRecordEnd) {
                audioRecord1?.release()
                audioRecord1 = null

                audioRecord2?.release()
                audioRecord2 = null
            }
            isRecordEnd = true
        }
        if ((index == 1 || index == 0) && tinyCapManager1?.isRecording == true && card1 > 0) {
            tinyCapManager1?.stopRecording()
            ffmpegDecode(tinyCapManager1!!.outputPath, tinyCapManager1!!.newPath) { res ,msg ->
                if (res != 0) {
                    log("1 转码失败 $msg")
                    onRecordEnd(true, null)
                } else {
                    log("1 转码成功")
                    // 调用翻译
                    onRecordEnd(true, tinyCapManager1!!.newPath)
                }
                tinyCapManager1?.recordRunning = false
            }
        }
        if ((index == 2 || index == 0) && tinyCapManager2?.isRecording == true && card2 > 0) {
            tinyCapManager2?.stopRecording()
            ffmpegDecode(tinyCapManager2!!.outputPath, tinyCapManager2!!.newPath) { res ,msg ->
                if (res != 0) {
                    log("2 转码失败 $msg")
                    onRecordEnd(false, null)
                } else {
                    log("2 转码成功")
                    // 调用翻译
                    onRecordEnd(false, tinyCapManager2!!.newPath)
                }
                tinyCapManager2?.recordRunning = false
            }
        }
        if ((index == 1 || index == 0) && tinyCapRecord1?.isRecording() == true /*&& card1 > 0*/) {
            tinyCapRecord1?.stopRecording()
//            ffmpegDecode(tinyCapRecord1!!.outputPath, tinyCapRecord1!!.newPath) { res ,msg ->
//                if (res != 0) {
//                    log("1 转码失败 $msg")
//                    onRecordEnd(true, null)
//                } else {
//                    log("1 转码成功")
//                    // 调用翻译
//                    onRecordEnd(true, tinyCapRecord1!!.newPath)
//                }
//                tinyCapRecord1?.recordRunning = false
//            }
        }
        if ((index == 2 || index == 0) && tinyCapRecord2?.isRecording() == true && card2 > 0) {
            tinyCapRecord2?.stopRecording()
//            ffmpegDecode(tinyCapRecord2!!.outputPath, tinyCapRecord2!!.newPath) { res ,msg ->
//                if (res != 0) {
//                    log("2 转码失败 $msg")
//                    onRecordEnd(false, null)
//                } else {
//                    log("2 转码成功")
//                    // 调用翻译
//                    onRecordEnd(false, tinyCapRecord2!!.newPath)
//                }
//                tinyCapRecord2?.recordRunning = false
//            }
        }
    }

    private var recordState = 0

    private fun onRecordEnd(isMaster: Boolean, path: String?) {
        log("onRecordEnd $isMaster $recordState $path")
        iTransRecord?.onRecordEnd(isMaster, path)
//        if (recordState == 0) {
//            // 第一个进来的,等待后面那一个
//            if (isMaster) {
//                if (path != null) {
//                    recordState = 1
//                } else {
//                    recordState = -1
//                    iTransRecord?.onRecordEnd(isMaster, null)
//                }
//            } else {
//                if (path != null) {
//                    recordState = 2
//                } else {
//                    recordState = -2
//                    iTransRecord?.onRecordEnd(isMaster, null)
//                }
//            }
//        } else {
//            if (recordState == 1 || recordState == 2) {
//                // 对比生成
//                if (path != null) {
//                    val out1 = getPathBy2(tinyCapManager1!!.newPath)
//                    val out2 = getPathBy2(tinyCapManager2!!.newPath)
//                    PcmDiffProcessor.alignAndSplit(
//                        pcmPath1 = tinyCapManager1!!.newPath,
//                        pcmPath2 = tinyCapManager2!!.newPath,
//                        output1 = out1,
//                        output2 = out2,
//                        sampleRate = 16000,
//                        channels = 1
//                    )
//                    log("两个做同步处理 $out1 $out2")
//                    iTransRecord?.onRecordEnd(true, out1)
//                    iTransRecord?.onRecordEnd(false, out2)
//                } else {
//                    // TODO 降噪与VAD
//                    iTransRecord?.onRecordEnd(!isMaster, if (!isMaster) tinyCapManager1!!.newPath else tinyCapManager2!!.newPath)
//                }
//            } else {
//                if (path != null) {
//                    // TODO 降噪与VAD
//                }
//                iTransRecord?.onRecordEnd(isMaster, path)
//            }
//        }
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

    fun getPathBy2(path: String): String {
        return path.replace(".pcm", "_2.pcm")
    }

    fun getTempPath(index: Int): File {
        val dir = File(Environment.getExternalStorageDirectory(), "AudioRecordings")
        if (!dir.exists()) dir.mkdirs()
        val fileName = "${index}_temp" + ".pcm"
        return File(dir, fileName)
    }

    private fun ffmpegDecode(source: String, target: String, onDecoded: (res: Int, msg: String?) -> Unit) {
        val cmd = "-f s16le -ar 44100 -ac 2 -i $source -af pan=mono|c0=0.5*c0+0.5*c1,aresample=16000 -f s16le -ar 16000 -ac 1 $target"
            // "-y -i $source -acodec pcm_s16le -ar 16000 -ac 1 $target"
        FFmpegKit.executeAsync(
            cmd,
            object : FFmpegSessionCompleteCallback {
                override fun apply(session: FFmpegSession?) {
                    if (session == null) return
//                        session!!.getAllLogs().forEach {
//                            System.out.println("${DTSPlayer.TAG} log ${it.message}")
//                        }
                    when {
                        ReturnCode.isSuccess(session!!.returnCode) -> {
                            // SUCCESS
                            onDecoded.invoke(0, null)
                        }
                        ReturnCode.isCancel(session!!.returnCode) -> {
                            // CANCEL
                            onDecoded.invoke(1, null)
                        }
                        else -> {
                            // FAILURE
                            val msg = String.format("Command failed with state %s and rc %s.%s", session!!.state, session!!.returnCode, session!!.failStackTrace)
                            onDecoded.invoke(2, msg)
                        }
                    }
                }
            }
        )
    }

    private fun log(msg: Any?) {
        Timber.i("TARecord ${msg ?: "null"}")
    }

}
