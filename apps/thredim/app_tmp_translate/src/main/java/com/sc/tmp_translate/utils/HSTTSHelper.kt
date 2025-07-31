package com.sc.tmp_translate.utils

import android.app.Application
import android.os.Environment
import com.bytedance.speech.speechengine.SpeechEngine
import com.bytedance.speech.speechengine.SpeechEngineDefines
import com.bytedance.speech.speechengine.SpeechEngineGenerator
import com.nbhope.lib_frame.utils.HopeUtils
import com.sc.tmp_translate.inter.ITmpTTS
import com.sc.tmp_translate.utils.hs.TranslateConfig
import java.io.File

/**
 * @author  tsc
 * @date  2025/7/31 10:04
 * @version 0.0.0-1
 * @description
 */
class HSTTSHelper(var application: Application) : ITmpTTS, SpeechEngine.SpeechListener {

    lateinit var engine: SpeechEngine
    var engineHandler: Long = 0L

    override fun init() {
        SpeechEngineGenerator.PrepareEnvironment(application, application);
        engine = SpeechEngineGenerator.getInstance()
        engineHandler = engine.createEngine()
        engine.setContext(application)
        // 语音合成引擎
        engine.setOptionString(engineHandler, SpeechEngineDefines.PARAMS_KEY_ENGINE_NAME_STRING, SpeechEngineDefines.TTS_ENGINE);
//        engine.setOptionString(engineHandler, SpeechEngineDefines.PARAMS_KEY_LOG_LEVEL_STRING, SpeechEngineDefines.LOG_LEVEL_WARN);
//        engine.setOptionString(engineHandler, SpeechEngineDefines.PARAMS_KEY_DEBUG_PATH_STRING, File(Environment.getExternalStorageDirectory(), "AudioRecordings").absolutePath);

        engine.setOptionString(engineHandler, SpeechEngineDefines.PARAMS_KEY_UID_STRING, HopeUtils.getSN());
        engine.setOptionString(engineHandler, SpeechEngineDefines.PARAMS_KEY_DEVICE_ID_STRING, HopeUtils.getSN());

        // 在线授权
        engine.setOptionString(engineHandler, SpeechEngineDefines.PARAMS_KEY_APP_ID_STRING, TranslateConfig.ttsAppId);
        engine.setOptionString(engineHandler, SpeechEngineDefines.PARAMS_KEY_APP_TOKEN_STRING, "Bearer;${TranslateConfig.ttsAccessToken}");

        // 合成场景：连续合成场景
        engine.setOptionString(engineHandler, SpeechEngineDefines.PARAMS_KEY_TTS_SCENARIO_STRING, SpeechEngineDefines.TTS_SCENARIO_TYPE_NORMAL);

        // 合成策略：在线合成
        engine.setOptionInt(engineHandler, SpeechEngineDefines.PARAMS_KEY_TTS_WORK_MODE_INT, SpeechEngineDefines.TTS_WORK_MODE_ONLINE);
        // 建连超时，默认12000ms
        engine.setOptionInt(engineHandler, SpeechEngineDefines.PARAMS_KEY_TTS_CONN_TIMEOUT_INT, 12000);
        // 接收超时，默认8000ms
        engine.setOptionInt(engineHandler, SpeechEngineDefines.PARAMS_KEY_TTS_RECV_TIMEOUT_INT, 8000);

        // 在线合成使用的“发音人”
//        engine.setOptionString(engineHandler, SpeechEngineDefines.PARAMS_KEY_TTS_VOICE_ONLINE_STRING, "通用女声");
//        // 在线合成使用的“演绎风格”
        engine.setOptionString(engineHandler, SpeechEngineDefines.PARAMS_KEY_TTS_VOICE_TYPE_ONLINE_STRING, "zh_female_cancan_mars_bigtts")

        // 音色对应音高
        engine.setOptionInt(engineHandler, SpeechEngineDefines.PARAMS_KEY_TTS_PITCH_INT, 10);
        // 音色对应音量
        engine.setOptionInt(engineHandler, SpeechEngineDefines.PARAMS_KEY_TTS_VOLUME_INT, 10);
        // 音色对应语速
        engine.setOptionInt(engineHandler, SpeechEngineDefines.PARAMS_KEY_TTS_SPEED_INT, 10);

        // 在线请求资源配置
        engine.setOptionString(engineHandler, SpeechEngineDefines.PARAMS_KEY_TTS_ADDRESS_STRING, "wss://openspeech.bytedance.com");
        engine.setOptionString(engineHandler, SpeechEngineDefines.PARAMS_KEY_TTS_URI_STRING, "/api/v1/tts/ws_binary");
        engine.setOptionString(engineHandler, SpeechEngineDefines.PARAMS_KEY_TTS_CLUSTER_STRING, "volcano_tts");
        engine.setOptionInt(engineHandler, SpeechEngineDefines.PARAMS_KEY_AUDIO_STREAM_TYPE_INT, SpeechEngineDefines.AUDIO_STREAM_TYPE_MEDIA);

        val ret = engine.initEngine(engineHandler);
        log("ret $ret")
        if (ret != SpeechEngineDefines.ERR_NO_ERROR) {
            val errMessage = "Init Engine Faile: " + ret;
            log(errMessage);
        } else log("初始化成功")
        engine.setListener(this);

//        //注意这里先调用同步停止，避免SDK内部异步线程带来的问题
//        engine.sendDirective(engineHandler, SpeechEngineDefines.DIRECTIVE_CREATE_CONNECTION, "")
//
//        engine.sendDirective(engineHandler, SpeechEngineDefines.DIRECTIVE_SYNC_STOP_ENGINE, "")
//        engine.sendDirective(engineHandler, SpeechEngineDefines.DIRECTIVE_START_ENGINE, "")
//        speak("今天天气怎么样")
    }

    override fun release() {
        if (this::engine.isInitialized) {
            engine.destroyEngine(engineHandler);
            engineHandler = -1;
//            engine = null;
        }
    }

    override fun speak(msg: String) {
        if (!this::engine.isInitialized) return
        // 要合成的文本
        log("speak $msg")
        engine.setOptionString(engineHandler, SpeechEngineDefines.PARAMS_KEY_TTS_TEXT_STRING, msg);

        engine.sendDirective(engineHandler, SpeechEngineDefines.DIRECTIVE_CREATE_CONNECTION, "")
        engine.sendDirective(engineHandler, SpeechEngineDefines.DIRECTIVE_SYNC_STOP_ENGINE, "")
        engine.sendDirective(engineHandler, SpeechEngineDefines.DIRECTIVE_START_ENGINE, "")
    }

    override fun onSpeechMessage(type: Int, data: ByteArray?, len: Int) {
        var stdData = if (data == null) "" else String(data)
        when (type) {
            SpeechEngineDefines.MESSAGE_TYPE_ENGINE_START -> {
                // Callback: 引擎启动成功回调
                log("Callback: 引擎启动成功: data: $stdData")
//                speechStart(stdData)
                // “合成”指令必须要在收到 MESSAGE_TYPE_ENGINE_START 后发送
                engine.sendDirective(engineHandler, SpeechEngineDefines.DIRECTIVE_SYNTHESIS, "");
            }
            SpeechEngineDefines.MESSAGE_TYPE_ENGINE_STOP -> {
                // Callback: 引擎关闭回调
                log("Callback: 引擎关闭: data: $stdData")
//                speechStop(stdData)
            }
            SpeechEngineDefines.MESSAGE_TYPE_ENGINE_ERROR -> {
                // Callback: 错误信息回调
                log("Callback: 错误信息: $stdData")
//                speechError(stdData)
            }
            SpeechEngineDefines.MESSAGE_TYPE_TTS_SYNTHESIS_BEGIN -> {
                // Callback: 合成开始回调
                log("Callback: 合成开始: $stdData")
//                speechStartSynthesis(stdData)
            }
            SpeechEngineDefines.MESSAGE_TYPE_TTS_SYNTHESIS_END -> {
                // Callback: 合成结束回调
                log("Callback: 合成结束: $stdData")
//                speechFinishSynthesis(stdData)
            }
            SpeechEngineDefines.MESSAGE_TYPE_TTS_START_PLAYING -> {
                // Callback: 播放开始回调
                log("Callback: 播放开始: $stdData")
//                speechStartPlaying(stdData)
            }
            SpeechEngineDefines.MESSAGE_TYPE_TTS_PLAYBACK_PROGRESS -> {
                // Callback: 播放进度回调
                log("Callback: 播放进度")
//                speechPlayingProgress(stdData)
            }
            SpeechEngineDefines.MESSAGE_TYPE_TTS_FINISH_PLAYING -> {
                // Callback: 播放结束回调
                log("Callback: 播放结束: $stdData")
//                speechFinishPlaying(stdData)
            }
            SpeechEngineDefines.MESSAGE_TYPE_TTS_AUDIO_DATA -> {
                // Callback: 音频数据回调
                log(String.format("Callback: 音频数据，长度 %d 字节", stdData.length))
//                speechTtsAudioData(android.R.attr.data, false)
            }
            SpeechEngineDefines.MESSAGE_TYPE_TTS_AUDIO_DATA_END -> {
                // Callback: 音频数据回调
                log(String.format("Callback: 音频数据，长度 %d 字节", stdData.length))
//                speechTtsAudioData(ByteArray(0), true)
            }
            else -> {
            }
        }
    }

    private fun log(msg: Any?) {
        System.out.println("HSTTS ${msg ?: "null"}")
    }

    private fun start() {

    }

    private fun stop() {

    }

}
