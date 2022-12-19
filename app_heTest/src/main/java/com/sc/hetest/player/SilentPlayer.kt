package com.nbhope.phmusic.player

import android.content.res.AssetFileDescriptor
import android.media.AudioManager
import android.media.MediaPlayer
import timber.log.Timber
import java.io.File
import java.io.IOException

/**
 * 静默播放器，
 * 用于播放不在前台显示的歌曲
 */
object SilentPlayer : AudioFocusManager.OnAudioFocusChangeListener {
    private val TAG = SilentPlayer::class.java.simpleName
    private var mediaPlayer: MediaPlayer? = null
    private var audioFocusManager: AudioFocusManager? = null
    private var loop = false

    private fun inspectInit() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setOnPreparedListener {
                play()
            }
            mediaPlayer?.setOnCompletionListener {
                if (loop){
                    mediaPlayer?.start()
                }else{
                    destroyMedia()
                }
            }
        }
    }

    private fun play(): Boolean {
        audioFocusManager?.requestFocusTemporary()
        mediaPlayer?.start()
        return true
    }

    /**
     * @param filePath
     * @return
     */
    fun play(filePath: String, audioFocusManager: AudioFocusManager): Boolean {
        return play(File(filePath), audioFocusManager)
    }

    fun play(file: File, audioFocusManager: AudioFocusManager, loop: Boolean = false,focusListener:AudioFocusManager.OnAudioFocusChangeListener? = null): Boolean {
        if (!file.exists()) {
            return false
        }
        if (!isSupportGenre(file)) {
            return false
        }
        destroyMedia()
        this.loop = loop
        this.audioFocusManager = audioFocusManager
        if (focusListener != null){
            this.audioFocusManager?.setOnAudioFocusChangeListener(focusListener)
        }else{
            this.audioFocusManager?.setOnAudioFocusChangeListener(this)
        }
        inspectInit()
        val filePath = file.path
        try {
            mediaPlayer?.setDataSource(filePath)
            mediaPlayer?.prepareAsync()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e(e)
        }
        return false
    }
    fun play(fd: AssetFileDescriptor, audioFocusManager: AudioFocusManager, loop: Boolean = false, focusListener:AudioFocusManager.OnAudioFocusChangeListener? = null): Boolean {
        if (fd===null) {
            return false
        }
        destroyMedia()
        this.loop = loop
        this.audioFocusManager = audioFocusManager
        if (focusListener != null){
            this.audioFocusManager?.setOnAudioFocusChangeListener(focusListener)
        }else{
            this.audioFocusManager?.setOnAudioFocusChangeListener(this)
        }
        inspectInit()
        try {
            mediaPlayer?.setDataSource(fd.fileDescriptor,fd.startOffset,fd.length)
            mediaPlayer?.prepareAsync()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e(e)
        }
        return false
    }

    fun stopMedia(){
        mediaPlayer?.stop()
        this.loop = false
        destroyMedia()
    }
    fun stopMediaNow(){
        Timber.i("stopMediaNow")
        this.loop = false
        destroyMedia()
    }

    private fun destroyMedia() {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer?.release()
        Timber.i("destroyMedia")
        audioFocusManager?.releaseAudioFocus()
        audioFocusManager?.setOnAudioFocusChangeListener(null)
        audioFocusManager = null
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.setOnPreparedListener(null)
        mediaPlayer = null
    }

    private fun isSupportGenre(file: File): Boolean {
        val fileName = file.name.toLowerCase()
        return fileName.endsWith(".mp3") ||
                fileName.endsWith(".wma") ||
                fileName.endsWith(".wav") ||
                fileName.endsWith(".flac") ||
                fileName.endsWith(".aac") ||
                fileName.endsWith(".amr") ||
                fileName.endsWith(".m4a") ||
                fileName.endsWith(".ape") ||
                fileName.endsWith(".ac3") ||
                fileName.endsWith(".mmf") ||
                fileName.endsWith(".m4r") ||
                fileName.endsWith(".wavpack") ||
                fileName.endsWith(".mp2") ||
                fileName.endsWith(".ogg")
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
//                mediaPlayer?.pause()
//                mediaPlayer?.release()
                destroyMedia()
            }
            AudioManager.AUDIOFOCUS_GAIN -> {

            }
            AudioManager.AUDIOFOCUS_LOSS -> {
//                mediaPlayer?.pause()
//                mediaPlayer?.release()
                destroyMedia()
            }
        }
    }
}