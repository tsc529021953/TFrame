package com.nbhope.phmusic.player

import android.content.Context
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import androidx.annotation.RequiresApi

/**
 *Created by ywr on 2021/11/11 17:09
 */
class AudioFocusManager(val context: Context) : AudioManager.OnAudioFocusChangeListener {
    private val mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val mAudioFocusChangeListener: ArrayList<OnAudioFocusChangeListener?> = ArrayList()

    override fun onAudioFocusChange(focusChange: Int) {
        mAudioFocusChangeListener?.forEach { item ->
            item?.onAudioFocusChange(focusChange)
        }
    }
    fun getAudioManager() : AudioManager{
        return mAudioManager
    }

    /**
     * 请求焦点
     */
    fun requestFocus(): Int {
        return mAudioManager.requestAudioFocus(
            this,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
    }

    /**
     * 请求临时焦点
     */
    fun requestFocusTemporary(): Int {
        return mAudioManager.requestAudioFocus(
            this,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
        )
    }
    fun requestFocusTemporary(mode: Int): Int {
        return mAudioManager.requestAudioFocus(
            this,
            mode,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
        )
    }

    fun releaseAudioFocus() {
        mAudioManager.abandonAudioFocus(this)
    }

    interface OnAudioFocusChangeListener {
        fun onAudioFocusChange(focusChange: Int)
    }

    fun setOnAudioFocusChangeListener(listener: OnAudioFocusChangeListener?) {
        this.mAudioFocusChangeListener.add(listener)
    }
}
