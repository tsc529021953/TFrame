package com.sc.tmp_translate.utils.record

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.*
import android.os.Build
import androidx.core.app.ActivityCompat
import timber.log.Timber

/**
 * @author  tsc
 * @date  2025/4/27 15:17
 * @version 0.0.0-1
 * @description
 */
class MultipleAudioRecord constructor(var context: Context) {

    companion object {
        const val TAG = "MAR"

        /**
         * 采样率，现在能够保证在所有设备上使用的采样率是44100Hz, 但是其他的采样率（22050, 16000, 11025）在一些设备上也可以使用。
         */
        const val SAMPLE_RATE_IN_HZ = 16000

        /**
         * 返回的音频数据的格式。 ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, and ENCODING_PCM_FLOAT.
         * 16 代表采样位数 也称采样精度
         */
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        // Record
        const val AUDIO_SOURCE = MediaRecorder.AudioSource.VOICE_COMMUNICATION
        /**
         * 声道数。CHANNEL_IN_MONO and CHANNEL_IN_STEREO. 其中CHANNEL_IN_MONO是可以保证在所有设备能够使用的。
         */
        const val RECORD_CHANNEL_CONFIG: Int = AudioFormat.CHANNEL_IN_STEREO
        /*取出的单个包的大小，用处主要是webRtc做处理的时候，数组大小限制*/
        const val BYTE_SIZE = 160 * SAMPLE_RATE_IN_HZ / 8000
    }


    private var audioRecord: AudioRecord? = null

    private var minSize = 0

    private var recordThread: AudioRecordThread? = null

    private var isStop = true
    private var isRecording = false

    private lateinit var mBuffer: ByteArray

    fun init() {
        recordThread = AudioRecordThread()
        recordThread?.start()
    }

    fun initRecord(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val devices = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS)
            log("devices size ${devices.size}")
            for (device in devices) {
                when (device.type) {
                    AudioDeviceInfo.TYPE_BUILTIN_MIC -> {
                        // 找到内置麦克风
                        log("device mic ${device.id}")
                    }
                    AudioDeviceInfo.TYPE_USB_DEVICE -> {
                        // 找到USB麦克风
                        log("device usb${device.id}")
                    }
                    else -> {
                        log("device other ${device.type} ${device.id}")
                    }
                }
            }
        } else return false

        minSize = AudioRecord.getMinBufferSize(
                SAMPLE_RATE_IN_HZ,
                RECORD_CHANNEL_CONFIG,
                AUDIO_FORMAT
        )
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            log("暂无录音权限")
            return false
        }
        audioRecord = AudioRecord(
                AUDIO_SOURCE,
                SAMPLE_RATE_IN_HZ,
                RECORD_CHANNEL_CONFIG,
                AUDIO_FORMAT,
                minSize
        )
        mBuffer = ByteArray(minSize)
        if (audioRecord?.state == AudioRecord.STATE_INITIALIZED) {
            log("初始化成功 $minSize")
        } else {
            log("初始化失败")
            return false
        }

//        // 构建AudioRecord时指定 device
//        val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            AudioRecord.Builder()
//                    .setAudioSource(MediaRecorder.AudioSource.MIC)
//                    .setAudioFormat(AudioFormat.Builder()
//                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
//                            .setSampleRate(16000)
//                            .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
//                            .build())
//                    .setBufferSizeInBytes(bufferSize)
//                    .setAudioDevice(selectedDevice)
//                    .build()
//        } else {
////            TODO("VERSION.SDK_INT < M")
//        }
        return true
    }

    fun start() {
        if (!isInit()) return
    }

    fun stop() {
        if (!isInit()) return
    }

    fun isInit(): Boolean {
        return audioRecord != null && audioRecord?.state == AudioRecord.STATE_INITIALIZED
    }

    fun startOrStop() {
        if (!isInit()) return
        if (isRecording) {
            stop()
        } else {
            start()
        }
    }

    fun release() {
        releaseIn()
    }

    private fun releaseIn() {
        isStop = false
    }

    inner class AudioRecordThread: Thread() {

        override fun run() {
            super.run()
            val initRes = initRecord()
            if (initRes) {
                audioRecord?.startRecording()
                var read = -1
                while (!isStop) {
                    read = audioRecord!!.read(mBuffer, 0, mBuffer.size)
                }

                audioRecord?.stop()
                audioRecord?.release()
                audioRecord = null
            }

            log("Record End")
        }

    }

    fun log(msg: String?) {
        Timber.i("$TAG ${msg ?: "null"}")
    }
}
