package com.lib.frame.codec

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.AudioTrack.MODE_STREAM
import android.media.audiofx.AcousticEchoCanceler
import android.util.Log
import timber.log.Timber

class HopeAudioTrack {
    private var mAudioTrack: AudioTrack? = null
    private var isCreate = false
    private var mSessionId: Int = -1

    private var acousticEchoCanceler: AcousticEchoCanceler? = null


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

        val bufferSize = AudioTrack.getMinBufferSize(48000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)
        Timber.i("bufferSize:$bufferSize")
        var audioFormat = AudioFormat.Builder()
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(48000)
                .build()
        var audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setLegacyStreamType(AudioManager.STREAM_VOICE_CALL)
                .build()

        if (mSessionId != -1) {
            mAudioTrack = AudioTrack(audioAttributes, audioFormat, bufferSize * 2, MODE_STREAM, mSessionId)
            initAEC(mSessionId)
        } else {
            mAudioTrack = AudioTrack(audioAttributes, audioFormat, bufferSize * 2, MODE_STREAM, AudioManager.AUDIO_SESSION_ID_GENERATE)
            initAEC(mAudioTrack!!.audioSessionId)
        }
//        mAudioTrack = AudioTrack(AudioManager.MODE_IN_COMMUNICATION, 48000, AudioFormat.CHANNEL_OUT_MONO,
//                AudioFormat.ENCODING_PCM_16BIT, bufferSize*5, AudioTrack.MODE_STREAM,mSessionId)
//        initAEC(mSessionId)

        mAudioTrack!!.play()

        //        try {
        //            outputStream = new FileOutputStream(file, true);
        //            bufferedOutputStream = new BufferedOutputStream(outputStream, 4096);
        //        } catch (FileNotFoundException e) {
        //            e.printStackTrace();
        //        }
    }

    fun writeData(data: ByteArray, offset: Int, dataLen: Int) {
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
        mAudioTrack!!.stop()
        mAudioTrack!!.release()
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


}
