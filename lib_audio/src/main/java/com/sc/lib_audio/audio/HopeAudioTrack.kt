package com.sc.lib_audio.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.audiofx.AcousticEchoCanceler
import android.media.audiofx.AutomaticGainControl
import android.media.audiofx.NoiseSuppressor
import timber.log.Timber


/**
 *Created by ywr on 2021/11/10 17:28
 * jrtplib aac音频流播放器
 */
class HopeAudioTrack {

    var TAG = "AudioRecordTAG"

    private var mAudioTrack: AudioTrack? = null
    private var isCreate = false
    private var mSessionId: Int = -1



    private var acousticEchoCanceler: AcousticEchoCanceler? = null

    val audioChannel_interphone = AudioFormat.CHANNEL_OUT_MONO
    val sampleRate_interphone = 8000
    val audioEncoding = AudioFormat.ENCODING_PCM_16BIT
    var usage =
//        AudioAttributes.USAGE_MEDIA
        AudioAttributes.USAGE_VOICE_COMMUNICATION
    var streamType =
//        AudioManager.STREAM_MUSIC
        AudioManager.STREAM_VOICE_CALL
    var contentType = AudioAttributes
    .CONTENT_TYPE_SPEECH
//        .CONTENT_TYPE_MUSIC
    var mode = AudioTrack.MODE_STREAM

    companion object {

        private var INSTANCE: HopeAudioTrack? = null

        private val TAG = "AudioTrackUtil"


        @JvmStatic
        val instance: HopeAudioTrack
            get() {
                if (INSTANCE == null) {
                    INSTANCE = HopeAudioTrack()
                }
                return INSTANCE as HopeAudioTrack
            }
    }


    fun create(id: Int) {
        if (isCreate) {
            mSessionId = id
            return
        }
        isCreate = true
        mSessionId = id

        val bufferSize = AudioTrack.getMinBufferSize(sampleRate_interphone, audioChannel_interphone, audioEncoding)
        Timber.i("$TAG bufferSize:$bufferSize")
        var audioFormat = AudioFormat.Builder()
            .setChannelMask(audioChannel_interphone)
            .setEncoding(audioEncoding)
            .setSampleRate(sampleRate_interphone)
            .build()
        var audioAttributes = AudioAttributes.Builder()
            .setUsage(usage)
            .setContentType(contentType)
            .setLegacyStreamType(streamType)
            .build()

        // 初始化音频播放
//        mAudioTrack = AudioTrack(
//            streamType,
//            sampleRate_interphone, audioChannel_interphone, audioEncoding,
//            bufferSize, mode
//        )

        if (mSessionId != -1) {
            mAudioTrack = AudioTrack(audioAttributes, audioFormat, bufferSize * 2, mode, mSessionId)
//            initANA(mSessionId)
        } else {
            mAudioTrack = AudioTrack(audioAttributes, audioFormat, bufferSize * 2, mode, AudioManager.AUDIO_SESSION_ID_GENERATE)
//            initANA(mAudioTrack!!.audioSessionId)
        }

        Timber.i("$TAG AudioTrack state:${mAudioTrack?.state}")
//        mAudioTrack = AudioTrack(AudioManager.MODE_IN_COMMUNICATION, 48000, AudioFormat.CHANNEL_OUT_MONO,
//                AudioFormat.ENCODING_PCM_16BIT, bufferSize*5, AudioTrack.MODE_STREAM,mSessionId)
//        initAEC(mSessionId)
        if (mAudioTrack?.state == AudioTrack.STATE_INITIALIZED)
            mAudioTrack!!.play()

        //        try {
        //            outputStream = new FileOutputStream(file, true);
        //            bufferedOutputStream = new BufferedOutputStream(outputStream, 4096);
        //        } catch (FileNotFoundException e) {
        //            e.printStackTrace();
        //        }
    }

    fun writeData(data: ByteArray, offset: Int, dataLen: Int) {
//        Timber.i("$TAG HopeAudioTrack  writeData ${Thread.currentThread().id} $dataLen $isCreate")
        if (!isCreate) return
        //        try {
        //            bufferedOutputStream.write(data);
        //            bufferedOutputStream.flush();
        //        } catch (IOException e) {
        //            e.printStackTrace();
        //        }
        mAudioTrack?.write(data, offset, dataLen)
    }

    fun release() {
        if (!isCreate) return
        isCreate = false
        if (mAudioTrack?.state == AudioTrack.STATE_INITIALIZED) {
            mAudioTrack?.stop()
            mAudioTrack?.release()
        }
    }

    private fun initAEC(mySessionId: Int) {
        Timber.d("initAEC  isAvailable: ---->${AcousticEchoCanceler.isAvailable()}")
        if (mySessionId != -1) {
            if (acousticEchoCanceler == null) {
                acousticEchoCanceler = AcousticEchoCanceler.create(mySessionId)
                Timber.d("initAEC: ---->$acousticEchoCanceler\t$mySessionId")
                if (acousticEchoCanceler == null) {
                    Timber.w("initAEC: ----->AcousticEchoCanceler create fail.")
                } else {
                    acousticEchoCanceler!!.enabled = true
                }
            }
        }
    }

    private fun initANA(mySessionId: Int){
        Timber.i("$TAG initANA ${AutomaticGainControl.isAvailable()} ${NoiseSuppressor.isAvailable()} ${AcousticEchoCanceler.isAvailable()}")
        initAEC(mSessionId)
        if(AutomaticGainControl.isAvailable()){
            var automaticGainControl = AutomaticGainControl.create(mySessionId);
            if (automaticGainControl != null)
                automaticGainControl.enabled = true;
            else Timber.i("$TAG 增益为null")
        }
    }
}