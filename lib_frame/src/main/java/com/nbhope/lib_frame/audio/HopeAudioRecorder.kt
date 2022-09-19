package com.nbhope.lib_frame.audio

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Process
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.lib.frame.codec.HopeAudioTrack
import com.nbhope.lib_frame.base.AudioRecorderReceiver
import com.nbhope.lib_frame.bus.event.AudioControlEvent
import com.nbhope.lib_frame.utils.LiveEBUtil
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by zhouwentao on 2020-01-07.
 */
@Singleton
class HopeAudioRecorder @Inject constructor() {

    var audioRecoderType = SPEECH
    private val sampleRate_speech = 16000
    private val audioChannel_speech = AudioFormat.CHANNEL_IN_STEREO
    private val audioChannelNum_speech = 2

    private val sampleRate_tianmao = 16000
    private val audioChannel_tianmao = AudioFormat.CHANNEL_IN_MONO
    private val audioChannelNum_tianmao = 1

    private val sampleRate_interphone = 48000
    private val audioChannel_interphone = AudioFormat.CHANNEL_IN_MONO
    private val audioChannelNum_interphone = 1

    private val audioEncoding = AudioFormat.ENCODING_PCM_16BIT

    private var receiverList = HashMap<Int, AudioRecorderReceiver>()

    private var receiver: AudioRecorderReceiver? = null

    //录音线程
    private lateinit var audioIn: AudioIn

//    @Autowired
//    @JvmField
//    var speechService: SpeechService? = null


    companion object {

        @JvmStatic
        var SPEECH: Int = 0

        @JvmStatic
        var TIANMAO: Int = 1

        @JvmStatic
        var INTER_PHONE: Int = 2

    }

    init {

        LiveEBUtil.registForever(AudioControlEvent::class.java, Observer {
            it as AudioControlEvent
            when (it.message) {
                AudioControlEvent.START_SPEEK -> {
                    startSpeak()
                }
                AudioControlEvent.STOP_SPEEK -> {
                    stopSpeak()
                }
//                AudioControlEvent.CHANGE_SPEECH -> {
//                    stopRecoder()
//                    audioRecoderType = SPEECH
//                    startRecoder()
//                    startSpeak()
//                }
                AudioControlEvent.CHANGE_INTERPHONE -> {
                    stopRecoder()
                    audioRecoderType = INTER_PHONE
                    startRecoder()
                }

            }
        })
    }

    fun init(type: Int) {
        audioRecoderType = type

    }

    fun startRecoder() {
        Timber.e("start AudioRecoder $audioRecoderType")
        audioIn = AudioIn(true, audioRecoderType)
        receiver = receiverList[audioRecoderType]
    }

    fun stopRecoder() {
        if (this::audioIn.isInitialized) {
            audioIn.stopRecord()
        }
    }

    fun startSpeak() {
        audioIn.startSpeak()
    }

    fun stopSpeak() {
        audioIn.stopSpeak()
    }

    fun registReceiver(type: Int, audioRecorderReceiver: AudioRecorderReceiver) {
        receiverList[type] = audioRecorderReceiver
    }


    inner class AudioIn(private var stopped: Boolean, private var type: Int) : Thread() {
        private var mIsRecording = true
        private var recorder: AudioRecord? = null

        init {
            start()
        }


        @SuppressLint("MissingPermission")
        override fun run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO)
            val buffers = Array(256) { ByteArray(160) }
            var ix = 0
            try { // ... initialise
                var N = 0//PCM编码AAC
                //process is what you will do with the data...not defined here
                when (type) {
                    SPEECH -> {
                        N = AudioRecord.getMinBufferSize(sampleRate_speech, audioChannel_speech, audioEncoding)
                        recorder = AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION,
                                sampleRate_speech,
                                audioChannel_speech,
                                audioEncoding,
                                N * 10)

                        if (recorder!!.state == AudioRecord.STATE_INITIALIZED) {
                            Timber.i("AudioRecord 初始化成功")
                        }
                    }
                    INTER_PHONE -> {
                        N = AudioRecord.getMinBufferSize(sampleRate_interphone, audioChannel_interphone, audioEncoding)
                        if (recorder?.state == AudioRecord.STATE_INITIALIZED) {
                            recorder?.stop()
                            recorder?.release()
                            recorder = null
                        }

                        recorder = AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                                sampleRate_interphone,
                                audioChannel_interphone,
                                audioEncoding,
                                N * 2)
                        if (recorder!!.state == AudioRecord.STATE_INITIALIZED) {
                            Timber.i("AudioRecord_initr_phone 初始化成功")
                        }
                        Timber.i("AudioRecord 初始化成功${recorder!!.state == AudioRecord.STATE_INITIALIZED}")
                        HopeAudioTrack.instance.create(recorder!!.audioSessionId)

                    }
                    TIANMAO -> {
                        N = AudioRecord.getMinBufferSize(sampleRate_tianmao, audioChannel_tianmao, audioEncoding)
                        recorder = AudioRecord(MediaRecorder.AudioSource.MIC,
                                sampleRate_tianmao,
                                audioChannel_tianmao,
                                audioEncoding,
                                N * 10)

                    }
                    // ... loop
                }


                recorder!!.startRecording()
                // ... loop
                while (mIsRecording) {
                    if (stopped) {
                        continue
                    }
                    val buffer = buffers[ix++ % buffers.size]
                    N = recorder!!.read(buffer, 0, buffer.size)
                    //process is what you will do with the data...not defined here


//                    receiver?.onVolume(buffer)


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
                audioIn = AudioIn(stopped, type)
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
                stop()
                release()
                recorder = null
            }

            mIsRecording = false
            Timber.e("stop AudioRecoder")
        }
    }
}