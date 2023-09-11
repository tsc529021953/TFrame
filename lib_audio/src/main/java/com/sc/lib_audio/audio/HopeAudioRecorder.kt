package com.sc.lib_audio.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Process
import timber.log.Timber

/**
 *Created by ywr on 2021/11/10 15:37
 *麦克风录音管理类，内部类AudioIn 是实际的录音线程，将录音数据通过接口
 * AudioRecorderReceiver 穿出
 */
class HopeAudioRecorder {

    var TAG = "AudioRecordTAG"

    //存放采样频率，取值只能为8000、16000、32000、48000。
    val sampleRate_interphone = 8000
    val audioChannel_interphone = AudioFormat.CHANNEL_IN_MONO
    val audioChannelNum_interphone = 1
    var audioSource =
//        MediaRecorder.AudioSource.MIC
        MediaRecorder.AudioSource.VOICE_COMMUNICATION

    val audioEncoding = AudioFormat.ENCODING_PCM_16BIT


    private var receiver: AudioRecorderReceiver? = null

    //录音线程
    private lateinit var audioIn: AudioIn

    var byteSize = 160

    init {

//        LiveEBUtil.registForever(AudioControlEvent::class.java, Observer {
//            it as AudioControlEvent
//            Timber.i("AudioControlEvent:${it.message}")
//            when (it.message) {
//                AudioControlEvent.START_SPEEK -> {
//                    startSpeak()
//                }
//                AudioControlEvent.STOP_SPEEK -> {
//                    stopSpeak()
//                }
//
//                AudioControlEvent.STATR_INTERPHONE -> {
//                    stopRecoder()
//                    startRecoder()
//                }
//                AudioControlEvent.STOP_INTERPHONE -> {
//                    stopRecoder()
//                }
//
//            }
//        })
    }


    fun startRecoder() {
        Timber.e("$TAG start AudioRecoder ")
        audioIn = AudioIn(true)
    }

    fun stopRecoder() {
        if (audioIn == null) return
        if (this::audioIn.isInitialized) {
            audioIn.stopRecord()
        }
    }

    fun startSpeak() {
        if (audioIn != null)
        audioIn.startSpeak()
    }

    fun stopSpeak() {
        if (audioIn != null)
        audioIn.stopSpeak()
    }

    fun registReceiver(audioRecorderReceiver: AudioRecorderReceiver) {
        receiver = audioRecorderReceiver
    }


    inner class AudioIn(private var stopped: Boolean) : Thread() {
        private var mIsRecording = true
        private var recorder: AudioRecord? = null

        init {
            start()
        }


        @SuppressLint("MissingPermission")
        override fun run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO)
            val buffers = Array(256) { ByteArray(byteSize) }
            var ix = 0
            try { // ... initialise
                var N = 0//PCM编码AAC
                //process is what you will do with the data...not defined here

                HopeAudioTrack.instance.release()
                N = AudioRecord.getMinBufferSize(
                    sampleRate_interphone,
                    audioChannel_interphone,
                    audioEncoding
                )
                if (recorder?.state == AudioRecord.STATE_INITIALIZED) {
                    recorder?.stop()
                    recorder?.release()
                    recorder = null
                }
                recorder = AudioRecord(
                    audioSource,
                    sampleRate_interphone,
                    audioChannel_interphone,
                    audioEncoding,
                    N * 2
                )
                if (recorder!!.state == AudioRecord.STATE_INITIALIZED) {
                    Timber.i("$TAG AudioRecord_initr_phone 初始化成功")
                }
                Timber.i("$TAG AudioRecord 初始化成功${recorder!!.state == AudioRecord.STATE_INITIALIZED}")
                HopeAudioTrack.instance.create(recorder!!.audioSessionId)
                // ... loop


                recorder!!.startRecording()
                // ... loop
                while (mIsRecording) {
                    if (stopped) {
                        continue
                    }
                    val buffer = buffers[ix++ % buffers.size]
                    N = recorder!!.read(buffer, 0, buffer.size)
                    //process is what you will do with the data...not defined here


//                    com.sc.nft.receiver?.onVolume(buffer)


                    if (stopped) {
                        continue
                    } else {
                        val bytes = ByteArray(N)
                        System.arraycopy(buffer, 0, bytes, 0, N)
                        receiver?.onData(bytes)
                    }
                }
            } catch (x: Throwable) {
                Timber.e("Error reading voice audio", x)
                Timber.e(x)
                Timber.e("reStart AudioRecoder")
                stopRecord()
                sleep(1000)
                audioIn = AudioIn(stopped)
            } finally {
                Timber.e("AudioRecoder End")
            }
        }

        fun stopSpeak() {
            stopped = true

        }

        fun startSpeak() {
            stopped = false
        }

        fun stopRecord() {
            stopped = true
            if (recorder?.state != AudioRecord.STATE_INITIALIZED) {
                return
            }
            recorder?.run {
                release()
                recorder = null
            }

            mIsRecording = false
            Timber.e("stop AudioRecoder")
        }
    }
}