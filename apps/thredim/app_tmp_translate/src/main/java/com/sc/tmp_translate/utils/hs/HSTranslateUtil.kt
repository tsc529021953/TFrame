package com.sc.tmp_translate.utils.hs

import android.os.Build
import android.os.Environment
import com.alibaba.fastjson.JSON
import com.google.gson.Gson
import com.sc.tmp_translate.bean.TranslateText
import com.volcengine.model.request.LangDetectRequest
import com.volcengine.model.request.TranslateAudioQueryRequest
import com.volcengine.model.request.TranslateAudioSubmitRequest
import com.volcengine.model.request.TranslateTextRequest
import com.volcengine.model.response.LangDetectResponse
import com.volcengine.model.response.TranslateTextResponse
import com.volcengine.service.translate.ITranslateService
import com.volcengine.service.translate.impl.TranslateServiceImpl
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileInputStream
import java.net.URI
import java.util.*
import kotlin.math.min


class HSTranslateUtil {

    var translateService: ITranslateService? = null

    var translateTextRequest: TranslateTextRequest? = null

    var gson = Gson()

    fun init(ak: String, sk: String) {
        translateService = TranslateServiceImpl.getInstance()
        // call below method if you dont set ak and sk in ～/.volc/config
        translateService?.accessKey = ak
        translateService?.secretKey = sk
        // lang detect
        try {
            val langDetectRequest: LangDetectRequest = LangDetectRequest()
            langDetectRequest.setTextList(mutableListOf("hello world", "how are you"))
            val langDetectResponse: LangDetectResponse? = translateService?.langDetect(langDetectRequest)
            println(JSON.toJSONString(langDetectResponse))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun changeLanguage(lan: String) {
        println("TTAG changeLanguage $translateService")
        try {
            if (translateService == null) return
            if (translateTextRequest == null) {
                translateTextRequest = TranslateTextRequest()
                // translateTextRequest.setSourceLanguage("en"); // 不设置表示自动检测
            }
            translateTextRequest?.targetLanguage = lan
        } catch (e: Exception) {
            println("TTAG changeLanguage err ${e.message}")
        }
    }

    fun translate2CH(textList: List<String>, cb: (resList: List<String>) -> Unit) {
        changeLanguage("ch")
        translate(textList, cb)
    }

    fun translate2EN(textList: List<String>, cb: (resList: List<String>) -> Unit) {
        changeLanguage("en")
        translate(textList, cb)
    }

    fun translate(textList: List<String>, cb: (resList: List<String>) -> Unit) {
        try {
            translateTextRequest?.setTextList(textList)
            val translateText: TranslateTextResponse? = translateService?.translateText(translateTextRequest)
            if (translateText != null) {
                val res = gson.fromJson(JSON.toJSONString(translateText), TranslateText::class.java)
                cb.invoke(res.TranslationList.map { it.Translation })
            }
        } catch (e: Exception) {
            println("TTAG err ${e.message}")
            e.printStackTrace()
            cb.invoke(arrayListOf(e.message ?: ""))
        }
    }

    suspend fun translate(uri: String, time: Long, cb: (resList: List<String>) -> Unit) {
        // 提交任务，获取任务ID
        try {
            val translateAudioSubmitRequest = TranslateAudioSubmitRequest()
            translateAudioSubmitRequest.sourceLanguage = "en"
            translateAudioSubmitRequest.targetLanguage = "zh"
            translateAudioSubmitRequest.uri = uri // "http://xx/xx.mp4"
            val translateAudioSubmitResponse = translateService!!.translateAudioSubmit(translateAudioSubmitRequest)
            println(JSON.toJSONString(translateAudioSubmitResponse))

            // 等待任务完成
//        Thread.sleep(8000) // 视频时长*0.3
            delay((time * 0.3).toLong())

            // 查询结果
            val translateAudioQueryRequest = TranslateAudioQueryRequest()
            translateAudioQueryRequest.taskId = translateAudioSubmitResponse.taskId
            val response = translateService!!.translateAudioQuery(translateAudioQueryRequest)
            println("res: ${JSON.toJSONString(response)}")
            println("res: ${response.status} ${response.responseMetadata?.error} ${gson.toJson(response.subtitles)}")

            cb.invoke(arrayListOf(JSON.toJSONString(translateAudioSubmitResponse), JSON.toJSONString(response)))
        } catch (e: Exception) {
            cb.invoke(arrayListOf(e.message ?: ""))
        }
    }

    suspend fun translate(uri: String, cb: (resList: List<String>) -> Unit) {
        translate(uri, "zh", "en", cb)
    }

    fun translate(uri: String, source: String, target: String, cb: (resList: List<String>) -> Unit) {
//        val input: File = File("audio.wav")
        val input = File(
            uri
        )
        val translateService = TranslateService(TranslateConfig.serviceInfo, TranslateConfig.apiInfoList)
        translateService.accessKey = TranslateConfig.accessKey
        translateService.secretKey = TranslateConfig.secretKey

        val signUrl = translateService.getSignUrl(TranslateConfig.api, null)
        val url = URI("wss://" + TranslateConfig.host + TranslateConfig.path + "?" + signUrl)
        println(url)
        // open websocket
        val client = Client(url)
        client.setClientListener(object : IClientListener {
            override fun onMessage(s: String?) {
                cb.invoke(arrayListOf(s ?: ""))
            }
        })
        client.connectBlocking()
        client.send(Client.getConfig(source, target))
        val buffer = ByteArray(200 * 32)
        var bytesLeft = 100 * 1024 * 1024
        try {
            FileInputStream(input).use { fis ->
                while (bytesLeft > 0) {
                    val read: Int = fis.read(buffer, 0, min(bytesLeft.toDouble(), buffer.size.toDouble()).toInt())
                    if (read == -1) {
                        break
                    }
                    client.send(Client.bytesToMessage(buffer))
                    Thread.sleep(200)
                    bytesLeft -= read
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                client.send(Client.getEnd())
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun release() {

    }

}