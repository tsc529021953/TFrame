package com.sc.tmp_translate.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import java.util.*

class TTSHelper(var context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isReady = false

    fun init() {
        tts = TextToSpeech(context.applicationContext, this)
        System.out.println("TTSHelper init $tts")
    }

    override fun onInit(status: Int) {
        System.out.println("TTSHelper onInit $status")
        if (status == TextToSpeech.SUCCESS) {
            // 设置语言，这里默认使用中文
            val result = tts?.setLanguage(Locale.CHINA)
            isReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
            if (!isReady) {
                System.out.println("TTSHelper 不支持的语言或缺少语言数据")
            } else {
                System.out.println("TTSHelper 初始化成功")
//                tts?.setVoice(Voice.QUALITY_HIGH)
//                tts?.vo
//                speak("今天天气不错")
            }
        } else {
            System.out.println("TTSHelper 初始化失败: $status")
        }
    }

    fun speak(text: String) {
        if (isReady) {
            try {
                // , "TTS_UTTERANCE_ID"
                val res = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null)
                System.out.println("TTSHelper 播报结果: $res")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            System.out.println("TTSHelper TTS尚未准备好，无法播报")
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.shutdown()
    }
}
