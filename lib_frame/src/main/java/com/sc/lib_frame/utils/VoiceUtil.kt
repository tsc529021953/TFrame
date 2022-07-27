package com.nbhope.phfame.utils

import android.content.Context
import android.media.AudioManager
import androidx.annotation.IntRange

object VoiceUtil {
    /* ************************************************** 操作声音 ******************************************************* */
    private var volumCrr: Int = -1

    /**
     * 增加声音
     *
     * @param context
     */
    fun incVolume(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
    }

    /**
     * 降低声音
     *
     * @param context
     */
    fun decVolume(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI)
    }

    /**
     * 设置声音
     *
     * @param context
     * @param volume  音量 0-15
     */
    fun setVolume(context: Context, @IntRange(from = 0, to = 15) volume: Int) {
        val mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI)
    }

    fun showVolumeUI(context: Context) {
        val mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, getVolume(context), AudioManager.FLAG_SHOW_UI)
    }

    //获取当前音量
    fun getVolume(context: Context): Int {
        val mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    }


    //设置静音
    fun setScience(context: Context) {
//        if (getVolume(context) > 0) {
//            volumCrr = getVolume(context)
//        }
//       setVolume(context, 0)
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, AudioManager.FLAG_SHOW_UI)
    }

    //恢复静音
    fun recoverVoice(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_SHOW_UI)
//        if (getVolume(context)>0){
//            setVolume(context, getVolume(context))
//        }else{
//            if (volumCrr!=-1){
//                setVolume(context, volumCrr)
//            }else{
//                incVolume(context)
//                volumCrr = getVolume(context)
//            }
//        }
    }

    /**
     * 打开或者关闭扬声器
     * @param open true 扬声器开启  false 扬声器关闭
     */
    fun openOrCloseAudio(context: Context,open:Boolean) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.isSpeakerphoneOn = open
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 0,
                AudioManager.STREAM_VOICE_CALL)
        audioManager.mode = AudioManager.MODE_IN_CALL
    }


    /**
     * 计算分区音量
     */
    @Synchronized
    fun calcInvalidVolume(volume: Int, add: Boolean, index: Int = 10): Int {
        var vol = volume
        vol = if (add) {
            if (vol >= 100) {
                100
            } else {
                val result = vol + index
                if (result > 100) {
                    100
                } else {
                    result
                }
            }
        } else {
            if (vol > 0) {
                val result = vol - index
                if (result < 0) {
                    0
                } else {
                    result
                }
            } else {
                0
            }
        }
        return vol
    }

    /* ************************************************** 操作声音 End ******************************************************* */
}