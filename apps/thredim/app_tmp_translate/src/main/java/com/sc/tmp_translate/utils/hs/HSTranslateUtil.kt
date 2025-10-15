package com.sc.tmp_translate.utils.hs

//import com.alibaba.fastjson.JSON
//import com.volcengine.model.request.LangDetectRequest
//import com.volcengine.model.request.TranslateAudioQueryRequest
//import com.volcengine.model.request.TranslateAudioSubmitRequest
//import com.volcengine.model.request.TranslateTextRequest
//import com.volcengine.model.request.translate.LangDetectRequest
//import com.volcengine.model.request.translate.TranslateAudioQueryRequest
//import com.volcengine.model.request.translate.TranslateAudioSubmitRequest
//import com.volcengine.model.request.translate.TranslateTextRequest
//import com.volcengine.model.response.LangDetectResponse
//import com.volcengine.model.response.TranslateTextResponse
//import com.volcengine.model.response.translate.LangDetectResponse
//import com.volcengine.model.response.translate.TranslateTextResponse
//import com.volcengine.service.translate.ITranslateService
//import com.volcengine.service.translate.impl.TranslateServiceImpl
import com.google.gson.Gson
import com.volcengine.ApiClient
import com.volcengine.auth.ISignerV4
import com.volcengine.auth.impl.SignerV4Impl
import com.volcengine.helper.Const
import com.volcengine.model.ApiInfo
import com.volcengine.service.SignableRequest
import com.volcengine.sign.Credentials
import com.volcengine.translate20250301.Translate20250301Api
import com.volcengine.translate20250301.model.SubmitAudioRequest
import com.volcengine.translate20250301.model.TranslateTextRequest
import org.apache.http.NameValuePair
import java.io.File
import java.io.FileInputStream
import java.net.URI


class HSTranslateUtil {

//    var translateService: ITranslateService? = null
//
//    var translateTextRequest: TranslateTextRequest? = null

    var gson = Gson()

    var apiClient: ApiClient? = null

    var api: Translate20250301Api? = null

    var submitAudio: SubmitAudioRequest? = null

    protected var credentials: com.volcengine.model.Credentials? = null

    protected var iSigner: ISignerV4? = null

    fun init(ak: String, sk: String) {
//        translateService = TranslateServiceImpl.getInstance()
//        // call below method if you dont set ak and sk in ～/.volc/config
//        translateService?.accessKey = ak
//        translateService?.secretKey = sk
//        // lang detect
//        try {
//            val langDetectRequest: LangDetectRequest = LangDetectRequest()
//            langDetectRequest.setTextList(mutableListOf("hello world", "how are you"))
//            val langDetectResponse: LangDetectResponse? = translateService?.langDetect(langDetectRequest)
//            println(JSON.toJSONString(langDetectResponse))
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
        apiClient = ApiClient()
            .setCredentials(Credentials.getCredentials(ak, sk))
            .setRegion(TranslateConfig.region)
        api = Translate20250301Api(apiClient)

        credentials = com.volcengine.model.Credentials()
        credentials!!.service = TranslateConfig.serviceInfo.credentials.service
        credentials!!.region = TranslateConfig.serviceInfo.credentials.region
        credentials!!.accessKeyID = TranslateConfig.serviceInfo.credentials.accessKeyID
        credentials!!.secretAccessKey = TranslateConfig.serviceInfo.credentials.secretAccessKey
        credentials!!.sessionToken = TranslateConfig.serviceInfo.credentials.sessionToken
        credentials!!.accessKeyID = ak
        credentials!!.secretAccessKey = sk

        iSigner = SignerV4Impl()
    }

    private fun changeLanguage(lan: String) {
        if (submitAudio == null) {
            submitAudio = SubmitAudioRequest()
        }
        submitAudio?.targetLanguage = lan
//        println("TTAG changeLanguage $translateService")
//        try {
//            if (translateService == null) return
//            if (translateTextRequest == null) {
//                translateTextRequest = TranslateTextRequest()
//                // translateTextRequest.setSourceLanguage("en"); // 不设置表示自动检测
//            }
//            translateTextRequest?.targetLanguage = lan
//        } catch (e: Exception) {
//            println("TTAG changeLanguage err ${e.message}")
//        }
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
        val transText = TranslateTextRequest()
        transText.textList = textList
        transText.targetLanguage = "en"
        val response = api?.translateText(transText)
        println("translate text ${response?.translationList} ${response?.responseMetadata?.error} ${response?.responseMetadata?.action} ${response?.responseMetadata?.service}")
        if (response?.translationList?.size ?: 0 > 0) {
            cb.invoke(response!!.translationList!!.map { it.translation })
        } else {
            cb.invoke(arrayListOf(response?.responseMetadata?.error?.message ?: ""))
        }
    }

    suspend fun translate(uri: String, time: Long, cb: (resList: List<String>) -> Unit) {
        // 提交任务，获取任务ID
//        try {
//            val translateAudioSubmitRequest = TranslateAudioSubmitRequest()
//            translateAudioSubmitRequest.sourceLanguage = "en"
//            translateAudioSubmitRequest.targetLanguage = "zh"
//            translateAudioSubmitRequest.uri = uri // "http://xx/xx.mp4"
//            val translateAudioSubmitResponse = translateService!!.translateAudioSubmit(translateAudioSubmitRequest)
//            println(JSON.toJSONString(translateAudioSubmitResponse))
//
//            // 等待任务完成
////        Thread.sleep(8000) // 视频时长*0.3
//            delay((time * 0.3).toLong())
//
//            // 查询结果
//            val translateAudioQueryRequest = TranslateAudioQueryRequest()
//            translateAudioQueryRequest.taskId = translateAudioSubmitResponse.taskId
//            val response = translateService!!.translateAudioQuery(translateAudioQueryRequest)
//            println("res: ${JSON.toJSONString(response)}")
//            println("res: ${response.status} ${response.responseMetadata?.error} ${gson.toJson(response.subtitles)}")
//
//            cb.invoke(arrayListOf(JSON.toJSONString(translateAudioSubmitResponse), JSON.toJSONString(response)))
//        } catch (e: Exception) {
//            cb.invoke(arrayListOf(e.message ?: ""))
//        }
    }

    suspend fun translate(uri: String, cb: (resList: List<String>) -> Unit) {
        translate(uri, "zh", "en", cb)
    }

    fun translate(uri: String, source: String, target: String, cb: (resList: List<String>) -> Unit) {
//        val submitAudio = SubmitAudioRequest()
//        submitAudio.targetLanguage = target
//        submitAudio.sourceLanguage = source
//        submitAudio.uri = uri // URI("wss://" + TranslateConfig.host + TranslateConfig.path + "?" + signUrl)
//        val response = api?.submitAudio(submitAudio)
//        println("translate submitAudio ${response?.taskId}")
//        if (response?.taskId != null) {
//            val queryAudioRequest = QueryAudioRequest()
//            queryAudioRequest.taskId = response!!.taskId
//            val response2 = api?.queryAudio(queryAudioRequest)
//            println("translate queryAudio ${response2?.subtitles} ${response2?.status}")
//        }

////        val input: File = File("audio.wav")
        val input = File(
            uri
        )
//        val translateService = TranslateService(TranslateConfig.serviceInfo, TranslateConfig.apiInfoList)
//        translateService.accessKey = TranslateConfig.accessKey
//        translateService.secretKey = TranslateConfig.secretKey

        val signUrl = getSignUrl(TranslateConfig.api, null)
        var urlStr = "wss://" + TranslateConfig.host + TranslateConfig.path + "?" + signUrl
        println("urlStr $urlStr ${System.getenv(Const.ACCESS_KEY)}")
//        urlStr = "wss://translate.volces.com/api/translate/speech/v1/?Action=SpeechTranslate&Version=2020-06-01&X-Date=20251013T150557Z&X-NotSignBody=&X-Credential=AKLTMmQ2YjI5MTJiZGI0NGVmZGI5MzM3NWM3MTdiZDRhMTE/20251013/cn-north-1/translate/request&X-Algorithm=HMAC-SHA256&X-SignedHeaders=&X-SignedQueries=Action;Version;X-Algorithm;X-Credential;X-Date;X-NotSignBody;X-SignedHeaders;X-SignedQueries&X-Signature=80ab01ee2c49a339251fcc26c8faac2ecf8202deaa3c1deb6c319261251aef7a"
        val url = URI(urlStr)
        println(url)
        // open websocket
        val client = Client(url)
        client.setClientListener(object : IClientListener {
            override fun onMessage(s: String?) {
                print("onMessage ${s ?: "null"}")
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
                    val read: Int = fis.read(buffer, 0, Math.min(bytesLeft.toDouble(), buffer.size.toDouble()).toInt())
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


    fun getSignUrl(api: String?, params: List<NameValuePair>?): String? {
        val apiInfo: ApiInfo = TranslateConfig.apiInfoList.get(api) ?: return null // "SpeechTranslate"
        val mergedNV: List<NameValuePair> = mergeQuery(params, apiInfo.query)
        val request = SignableRequest()
        val builder = request.uriBuilder
        request.method = apiInfo.method.toUpperCase()
        builder.scheme = TranslateConfig.serviceInfo.scheme
        builder.host = TranslateConfig.serviceInfo.host
        builder.path = apiInfo.path
        builder.setParameters(mergedNV)
        return iSigner?.signUrl(request, this.credentials)
    }

    private fun mergeQuery(query1: List<NameValuePair>?, query2: List<NameValuePair>?): List<NameValuePair> {
        val res: MutableList<NameValuePair> = ArrayList()
        if (query1 != null) {
            res.addAll(query1)
        }
        if (query2 != null) {
            res.addAll(query2)
        }
        return res
    }

    fun release() {

    }

}