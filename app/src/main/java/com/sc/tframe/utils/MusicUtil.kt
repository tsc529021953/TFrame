package com.sc.tframe.utils

import android.media.MediaPlayer
import java.io.IOException

/**
 * @author  tsc
 * @date  2025/4/22 15:33
 * @version 0.0.0-1
 * @description
 */
class MusicUtil {


    companion object {
        /**
         * 获取音频文件的总时长大小
         *
         * @param filePath 音频文件路径
         * @return 返回时长大小
         */
        fun getAudioFileVoiceTime(filePath: String?): Long {
            var mediaPlayerDuration = 0L
            if (filePath == null || filePath.isEmpty()) {
                return 0
            }
            val mediaPlayer = MediaPlayer()
            try {
                mediaPlayer.setDataSource(filePath)
                mediaPlayer.prepare()
                mediaPlayerDuration = mediaPlayer.duration.toLong()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (mediaPlayer != null) {
                mediaPlayer.stop()
                mediaPlayer.reset()
                mediaPlayer.release()
            }
            return mediaPlayerDuration
        }
    }

}
