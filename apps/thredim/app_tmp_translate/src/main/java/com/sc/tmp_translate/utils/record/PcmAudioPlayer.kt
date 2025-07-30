package com.sc.tmp_translate.utils.record

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import io.reactivex.internal.operators.observable.ObservableFilter
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
    private var state: State = State.STOPPED
    var currentPathObs: ObservableField<String> = ObservableField<String>("")
    private var currentName: String? = null
    private var stopFlag = false

    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_OUT_STEREO  // 双通道
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT

    fun getState(): State = state

    fun getCurrentFileName(): String? = currentName

    fun play(path: String) {
        if (state == State.PLAYING) stop()

        val file = File(path)
        if (!file.exists()) {
            Log.e("PcmAudioPlayer", "File not found: $path")
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
        state = State.PLAYING

        playThread.execute {
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
                Log.e("PcmAudioPlayer", "Playback error: ${e.message}")
            } finally {
                stopInternal()
            }
        }
    }

    fun pause() {
        if (state == State.PLAYING) {
            audioTrack?.pause()
            state = State.PAUSED
        }
    }

    fun resume() {
        if (state == State.PAUSED) {
            audioTrack?.play()
            state = State.PLAYING
        }
    }

    fun stop() {
        if (state == State.PLAYING || state == State.PAUSED) {
            stopFlag = true
        }
    }

    private fun stopInternal() {
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
        state = State.STOPPED
    }

    fun release() {
        stop()
        playThread.shutdownNow()
    }
}
