package com.sc.tmp_translate.utils.record

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import io.reactivex.internal.operators.observable.ObservableFilter
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.concurrent.Executors

class PcmAudioPlayer {

    enum class State {
        STOPPED, PLAYING, PAUSED
    }

    private var audioTrack: AudioTrack? = null
    private var playThread = Executors.newSingleThreadExecutor()
    var stateObs = ObservableField<State>(State.STOPPED)
    var currentPathObs: ObservableField<String> = ObservableField<String>("")
    private var currentName: String? = null
    private var stopFlag = false

    private val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_OUT_MONO // 双通道
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT

//    fun getState(): State = state

    fun getCurrentFileName(): String? = currentName

    fun playPause(path: String) {
        if (path != currentPathObs.get()) {
            play(path)
        } else {
            when (stateObs.get()) {
                State.PLAYING -> { pause() }
                State.PAUSED -> { resume() }
                else -> play(path)
            }
        }
    }

    fun play(path: String) {
        if (stateObs.get() == State.PLAYING) stop()

        val file = File(path)
        if (!file.exists()) {
            Timber.i("PcmAudioPlayer File not found: $path")
            return
        }

        currentPathObs.set(path)
        currentName = file.name
        stopFlag = false

        val bufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat)

        audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize,
            AudioTrack.MODE_STREAM
        )

        audioTrack?.play()
        stateObs.set(State.PLAYING)

        playThread.execute {
            Timber.i("PcmAudioPlayer start")
            try {
                val input = FileInputStream(file)
                val buffer = ByteArray(bufferSize)
                while (!stopFlag && input.read(buffer).also { bytesRead -> 
                        if (bytesRead > 0) {
                            audioTrack?.write(buffer, 0, bytesRead)
                        }
                    } > 0) {
                    // 播放中
                }
                input.close()
            } catch (e: IOException) {
                Timber.e("PcmAudioPlayer Playback error: ${e.message}")
            } finally {
                stopInternal()
            }
            Timber.i("PcmAudioPlayer end")
        }
    }

    fun pause() {
        if (stateObs.get() == State.PLAYING) {
            audioTrack?.pause()
            stateObs.set(State.PAUSED)
        }
    }

    fun resume() {
        if (stateObs.get() == State.PAUSED) {
            audioTrack?.play()
            stateObs.set(State.PLAYING)
        }
    }

    fun stop() {
        if (stateObs.get() == State.PLAYING || stateObs.get() == State.PAUSED) {
            stopFlag = true
        }
    }

    private fun stopInternal() {
        Timber.i("PcmAudioPlayer stopInternal")
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
        stateObs.set(State.STOPPED)
    }

    fun release() {
        stop()
        playThread.shutdownNow()
    }
}
