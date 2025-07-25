package com.sc.tframe.utils

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession
import com.arthenica.ffmpegkit.FFmpegSessionCompleteCallback
import com.arthenica.ffmpegkit.ReturnCode
import com.nbhope.lib_frame.utils.DateUtil
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException


/**
 * @author  tsc
 * @date  2025/4/16 14:57
 * @version 0.0.0-1
 * @description
 */
class DTSPlayer {

    companion object {
        const val TAG = "DTSPlayer"
        const val BUFFER_SIZE = 24576

        const val RATE_IN_HZ = 44100 // 192000 // 44100 // 48000
        const val CHANNEL = AudioFormat.CHANNEL_OUT_STEREO // AudioFormat.CHANNEL_OUT_STEREO // AudioFormat.CHANNEL_OUT_FRONT_LEFT; // AudioFormat.CHANNEL_OUT_5POINT1
        const val ENCODING = AudioFormat.ENCODING_IEC61937 // AudioFormat.ENCODING_PCM_16BIT // AudioFormat.ENCODING_DTS // AudioFormat.ENCODING_IEC61937
        const val CONTENT_TYPE = AudioAttributes.CONTENT_TYPE_MUSIC // CONTENT_TYPE_MOVIE
    }

    var mAudioTrack: AudioTrack? = null

    var audioFormat: AudioFormat? = null

    private var tmpBuf: ByteArray = ByteArray(BUFFER_SIZE) //临时存储buffer

    private var mInputStream: DataInputStream? = null

    fun init() {
//        if (mAudioTrack == null) {
//            mAudioTrack
//        }
        var attributes: AudioAttributes? = null
        audioFormat = AudioFormat.Builder().build()
        var bufSize = AudioTrack.getMinBufferSize(192000, AudioFormat.CHANNEL_OUT_5POINT1, AudioFormat.ENCODING_IEC61937);
//        var bufSize = AudioTrack.getMinBufferSize(44100,AudioFormat.CHANNEL_IN_STEREO,AudioFormat.ENCODING_PCM_16BIT);//获取最小buffer的大小
        bufSize *= 2;//对最小buff乘2
        println("$TAG getMinBufferSize*2 $bufSize bytes");
        mAudioTrack = AudioTrack(AudioManager.STREAM_MUSIC, 192000,AudioFormat.CHANNEL_OUT_5POINT1, AudioFormat.ENCODING_IEC61937,bufSize, AudioTrack.MODE_STREAM);
//        创建一个44100hz，双通道，16位有符号格式的audiotrack
//        mAudioTrack = new AudioTrack (AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, bufSize, AudioTrack.MODE_STREAM);
        mAudioTrack?.play();//启动AudioTrack
//        tmpBuf = new byte [24576];
    }

    fun getDuration(path: String): Long {
        var duration = 0L
        val session = FFmpegKit.execute("-i $path")
        var isDuration = false
        var durationStr = ""
        session!!.getAllLogs().forEach {
            System.out.println("$TAG log ${it.message}")
            if (isDuration && durationStr == "") {
                durationStr = it.message
                return@forEach
            }
            if (it.message.contains("Duration:")) {
                isDuration = true
            }
        }
        if (durationStr != "") {
            if (durationStr.split(".").size == 2) durationStr = durationStr.split(".")[0]
            duration = DateUtil.timeToDurationMillis(durationStr)
            System.out.println("$TAG durationStr $durationStr $duration ${DateUtil.getDateToString(duration, "HH:mm:ss")}")
        }
        return duration
    }

    fun getInfo(path: String) {
//        val session = FFmpegKit.execute(
////                "-i $path -ar $RATE_IN_HZ -ac $CHANNEL -f spdif sdcard/output.pcm -y"
////                 "-i $path -ar $RATE_IN_HZ -ac 2 -f s24le sdcard/output.pcm -y"
//                "-i $path"
//        )
        FFmpegKit.execute("-v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 $path")
    }

    fun play(path: String) {
        println("$TAG play start $path")
        // "-i $path -ar 44100 -ac 2 -f s16le sdcard/output.pcm -y"
        // val session =
        FFmpegKit.executeAsync(
//                "-i $path -ar $RATE_IN_HZ -ac $CHANNEL -f spdif sdcard/output.pcm -y"
//                 "-i $path -ar $RATE_IN_HZ -ac 2 -f s24le sdcard/output.pcm -y"
//                "-i $path -map 0:1 -vn -acodec copy -f spdif sdcard/output2.bin -y"
                "-i $path -map 0:0 -vn -acodec copy -f spdif sdcard/output2.bin -y",
                object : FFmpegSessionCompleteCallback {
                    override fun apply(session: FFmpegSession?) {
                        if (session == null) return
                        session!!.getAllLogs().forEach {
                            System.out.println("$TAG log ${it.message}")
                        }
                        when {
                            ReturnCode.isSuccess(session!!.returnCode) -> {
                                // SUCCESS
                                println("$TAG SUCCESS ${session!!.getAllLogs().size}")
//                                //
//                                val file = File("/sdcard/output.pcm") //从文件读取pcm数据
//                                try {
//                                    mInputStream = DataInputStream(FileInputStream(file))
//                                } catch (e: FileNotFoundException) {
//                                    e.printStackTrace()
//                                    println("$TAG ${e.message}")
//                                    return
//                                }
//                                println("$TAG audioTrack state " + mAudioTrack?.state)
//                                var bufLen = 0
//                                bufLen = mInputStream!!.read(tmpBuf)
//                                println("$TAG bufLen $bufLen")
//                                mAudioTrack?.write(tmpBuf, 0, BUFFER_SIZE)
                            }
                            ReturnCode.isCancel(session!!.returnCode) -> {
                                // CANCEL
                                println("$TAG CANCEL")
                            }
                            else -> {
                                // FAILURE
                                println(String.format("$TAG Command failed with state %s and rc %s.%s", session!!.state, session!!.returnCode, session!!.failStackTrace))
                            }
                        }
                    }
                }
        )
        // -f iec61937  -c:v pcm_s24le  -f s16le
        println("$TAG play start $path")

    }

    fun play() {

    }

    fun pause() {

    }

    fun release() {

    }

}
