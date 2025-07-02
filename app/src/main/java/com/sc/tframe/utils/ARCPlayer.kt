package com.sc.tframe.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession
import com.arthenica.ffmpegkit.FFmpegSessionCompleteCallback
import com.arthenica.ffmpegkit.ReturnCode
import kotlinx.coroutines.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import kotlin.math.roundToLong

/**
 * @author  tsc
 * @date  2025/4/22 16:31
 * @version 0.0.0-1
 * @description
 */
class ARCPlayer(var iPlayer: IPlayListen) : IPlayer {

    companion object {
        const val BUFFER_SIZE = 24576

        const val RATE_IN_HZ = 44100 // 192000 // 44100 // 48000
        const val CHANNEL = AudioFormat.CHANNEL_OUT_STEREO // AudioFormat.CHANNEL_OUT_STEREO // AudioFormat.CHANNEL_OUT_FRONT_LEFT; // AudioFormat.CHANNEL_OUT_5POINT1
        const val ENCODING = AudioFormat.ENCODING_IEC61937 // AudioFormat.ENCODING_PCM_16BIT // AudioFormat.ENCODING_DTS // AudioFormat.ENCODING_IEC61937
        const val CONTENT_TYPE = AudioAttributes.CONTENT_TYPE_MUSIC // CONTENT_TYPE_MOVIE
        const val BYTE_RATE = 176.4  // 44100 × 2 × 16 / 8 / 1000 以毫秒来算

        const val DECODE_FILE_PATH = "/sdcard/"
        const val DECODE_FILE_NAME = "output2.bin"
    }

    private var mAudioTrack: AudioTrack? = null
    private var mMiniBufferSize = 0
    private lateinit var mBuffer: ByteArray

    private var context: Context? = null

    var fis: FileInputStream? = null

    private var isStop = true
    private var isPaused = false
    private var pauseLock = Object()

    private var duration = 0L
    private var position = 0L
    private var positionRecord = 0L
    private var positionPercent = 0L
    private var total = 0L

    private var mScope: CoroutineScope? = null
    private var arcPlayer: ARCThread? = null

    /**
     * Decode res 是否解码成功
     */
    private var decodeRes = false

    private var endCallBackPath: String? = null

    init {
        mScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    }

    override fun init() {
        try {
            /**
             * 参数1：采样率    根据录音的采样率自定义
             * 参数2：声道     {@link AudioFormat.CHANNEL_OUT_MONO}单声道  {@link AudioFormat.CHANNEL_OUT_STEREO} 双声道
             * 参数3：比特率  {@link AudioFormat.ENCODING_PCM_16BIT} {@link AudioFormat.ENCODING_PCM_8BIT}
             * */
            /**
             * 参数1：采样率    根据录音的采样率自定义
             * 参数2：声道     [AudioFormat.CHANNEL_OUT_MONO]单声道  [AudioFormat.CHANNEL_OUT_STEREO] 双声道
             * 参数3：比特率  [AudioFormat.ENCODING_PCM_16BIT] [AudioFormat.ENCODING_PCM_8BIT]
             */
            mMiniBufferSize = AudioTrack.getMinBufferSize(RATE_IN_HZ,
                    CHANNEL,  // CHANNEL_CONFIGURATION_MONO,
                    ENCODING)
            val attr = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(CONTENT_TYPE) // CONTENT_TYPE_MOVIE
                    .build()
            val format = AudioFormat.Builder()
                    .setSampleRate(RATE_IN_HZ)
                    .setChannelMask(CHANNEL)
                    .setEncoding(ENCODING)
                    .build()
            mAudioTrack = AudioTrack.Builder()
                    .setAudioAttributes(attr)
                    .setAudioFormat(format)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .setBufferSizeInBytes(mMiniBufferSize)
                    .build()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                log("PCMPlayer isDirectPlaybackSupported " + AudioTrack.isDirectPlaybackSupported(format, attr) + " " + mMiniBufferSize)
            }
            mBuffer = ByteArray(mMiniBufferSize) // mMiniBufferSize
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun release() {
        endCallBackPath = null
        releaseFun()
    }

    private fun releaseFun() {
        play()
        isStop = true
    }

    override fun play(path: String) {
        // 先转码到本地
        if (!isStop) {
            releaseFun()
            endCallBackPath = path
            return
        }
        isStop = false
        log("播放 $path")
        ffmpegDecode(path) { res: Int, msg: String? ->
            decodeRes = res == 0
            log("解码结束 res $res")
            if (res == 0) {
                val cb = {
                    total = fis?.available()?.toLong() ?: 0
                    duration = (total / BYTE_RATE).roundToLong()
                    // 39919616 225740/226,816 176.838 // 55922688 316900/317,742 176.467  // 51560448 291760/292,957  176.72
                    log("总长 $total $duration")
                    iPlayer?.onDuration(duration)
                }
                if (fis != null) {
                    cb.invoke()
                } else {
                    mScope?.launch {
                        delay(500)
                        cb.invoke()
                    }
                }
            }
        }
        arcPlayer = ARCThread()
        arcPlayer?.start()
    }

    private fun ffmpegDecode(path: String, onDecoded: (res: Int, msg: String?) -> Unit) {
        FFmpegKit.executeAsync(
                "-i $path -map 0:0 -vn -acodec copy -f spdif sdcard/output2.bin -y",
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
                                val msg = String.format("${DTSPlayer.TAG} Command failed with state %s and rc %s.%s", session!!.state, session!!.returnCode, session!!.failStackTrace)
                                onDecoded.invoke(2, msg)
                            }
                        }
                    }
                }
        )
    }

    override fun play() {
        if (!isPaused) return
        synchronized(pauseLock) {
            isPaused = false
            pauseLock.notifyAll()
        }
        iPlayer.onPlay()
    }

    override fun pause() {
        if (isPaused) return
        isPaused = true
        iPlayer.onPause()
    }

    override fun playPause() {
        if (isPaused) {
            play()
        } else {
            pause()
        }
    }

    override fun getPosition(): Int {
        return (getPositionMs() / 1000).toInt()
    }

    override fun getPositionMs(): Long {
        var position = 0L
        if (mAudioTrack != null && mAudioTrack!!.state == AudioTrack.STATE_INITIALIZED) {
            position = if (fis != null && fis!!.channel.isOpen) {
                try {
                    (fis!!.channel.position() / BYTE_RATE).toLong()
                } catch (e: Exception) {
                    0
                }
            } else {
                mAudioTrack!!.playbackHeadPosition * 1000L / mAudioTrack!!.sampleRate
            }
        }
        return if (position > duration) duration else position;
    }

    override fun seek(position: Long) {
        if (!decodeRes || fis == null || !fis!!.channel.isOpen || mAudioTrack == null) return
        mAudioTrack?.flush()
        fis?.channel?.position(position)
        positionRecord = 0
        positionPercent = total * position / duration
    }

    override fun getDuration(): Int {
        return (duration / 1000).toInt()
    }

    override fun getDurationMs(): Long {
        return duration
    }

    inner class ARCThread: Thread() {

        override fun run() {
            super.run()
            init()
            sleep(500)
            log("state ${mAudioTrack?.state}")
            val file = File(DECODE_FILE_PATH, DECODE_FILE_NAME)
            fis = FileInputStream(file)
            if (mAudioTrack != null && mAudioTrack!!.state == AudioTrack.STATE_INITIALIZED) {
                mAudioTrack?.play()
                var read = -1
                try {
                    iPlayer?.onPlay()
                    while (!isStop) {
                        read = fis!!.read(mBuffer)
//                        log("read $read")
                        if (read != -1) {
                            synchronized(pauseLock) {
                                while (isPaused) {
                                    pauseLock.wait()
                                }
                            }
                            try {
                                positionRecord += read
                                if (positionRecord >= positionPercent)
                                    mAudioTrack!!.write(mBuffer, 0, read)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            isStop = true
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                mAudioTrack!!.stop()
                mAudioTrack!!.release()
                mAudioTrack = null
            }
            try {
                fis?.close()
                fis = null
                iPlayer!!.onRelease()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            decodeRes = false
            isStop = true
            if (endCallBackPath != null)
                play(endCallBackPath!!)
            log("Thread End")
        }
    }

    fun log(msg: Any?) {
        System.out.println("ARCPlayer: ${msg ?: "null"}")
    }

}
