package com.sc.tmp_translate.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.*
import android.util.Log
import androidx.core.app.ActivityCompat

class AudioRecorder(var content: Context, private val callback: Callback) {

    interface Callback {
        fun onRecordingStarted()
        fun onRecordingStopped()
        fun onAudioData(data: ByteArray, size: Int)
    }

    private var audioRecord: AudioRecord? = null
    private var recordingThread: Thread? = null
    @Volatile
    var isRecording = false

    private val sampleRate = 16000  // 常用采样率
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO // 单声道
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    fun init() {
        val am = content.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val devices = am.getDevices(AudioManager.GET_DEVICES_INPUTS)
        devices.forEach {
            System.out.println("device ${it.id} ${it.productName} ${it.address} ${it.type} ${it.type == AudioDeviceInfo.TYPE_USB_DEVICE}")
        }
    }

    fun startRecording() {
        if (isRecording) return

        if (ActivityCompat.checkSelfPermission(
                content,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
            Log.e("AudioRecorder", "初始化失败")
            return
        }

        audioRecord?.startRecording()
        isRecording = true
        callback.onRecordingStarted()

        recordingThread = Thread {
            val buffer = ByteArray(bufferSize)
            while (isRecording) {
                val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (read > 0) {
                    callback.onAudioData(buffer, read)
                }
            }
        }.apply { start() }
    }

    fun stopRecording() {
        if (!isRecording) return
        isRecording = false

        try {
            recordingThread?.join()
        } catch (e: InterruptedException) {
            Log.e("AudioRecorder", "录音线程终止失败: ${e.message}")
        }

        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        recordingThread = null

        callback.onRecordingStopped()
    }
}
