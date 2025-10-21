package com.sc.tmp_translate.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.*
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableFloat
import com.afollestad.materialdialogs.utils.MDUtil.getStringArray
import com.bytedance.speech.speechengine.SpeechEngine
import com.bytedance.speech.speechengine.SpeechEngineDefines
import com.bytedance.speech.speechengine.SpeechEngineGenerator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nbhope.lib_frame.app.HopeBaseApp
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.network.NetworkCallback
import com.nbhope.lib_frame.network.NetworkCallbackModule
import com.nbhope.lib_frame.utils.*
import com.nbhope.lib_frame.utils.toast.ToastUtil
import com.sc.tmp_translate.R
import com.sc.tmp_translate.bean.TransRecordBean
import com.sc.tmp_translate.bean.TransTextBean
import com.sc.tmp_translate.bean.TranslateBean
import com.sc.tmp_translate.constant.MessageConstant
import com.sc.tmp_translate.da.RecordRepository
import com.sc.tmp_translate.da.TransRepository
import com.sc.tmp_translate.inter.ITmpService
import com.sc.tmp_translate.inter.ITmpTTS
import com.sc.tmp_translate.inter.ITransRecord
import com.sc.tmp_translate.utils.HSTTSHelper
import com.sc.tmp_translate.utils.TTSHelper
import com.sc.tmp_translate.utils.hs.HSTranslateUtil
import com.sc.tmp_translate.utils.hs.TranslateConfig
import com.sc.tmp_translate.utils.record.PcmAudioPlayer
import com.sc.tmp_translate.utils.record.TransAudioRecord
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.File
import java.lang.ref.WeakReference


/**
 * @author  tsc
 * @date  2024/4/12 13:28
 * @version 0.0.0-1
 * @description
 * record
 * filter
 * zh or en
 * view
 */
class TmpServiceImpl : ITmpService, Service() {

    companion object {
        const val TAG = "TTAG"

        var CHANNEL_ONE_NAME = "TmpServiceImpl"
        var CHANNEL_ONE_ID = TmpServiceImpl::class.java.simpleName
        const val SERVICE_NOTICE_ID = 101

        const val BASE_FILE = "/XS_MEDIA/"
        const val CONFIG_FILE = "AppInfo.json"

        const val SERVER_PORT = 60000
    }

    private val mBinder: IBinder = BaseBinder()

    private lateinit var mScope: CoroutineScope

    /*view*/
    private var rootView: View? = null

    lateinit var mainHandler: MainHandler

    private var timerHandler: TimerHandler? = null

    lateinit var networkCallback: NetworkCallback

    private var gson = Gson()

    lateinit var spManager: SharedPreferencesManager

    private val fontSizeObf: ObservableFloat = ObservableFloat(1.0f)

    private val languageObs: ObservableField<String> = ObservableField<String>("")

    private var moreDisplayObb: ObservableBoolean = ObservableBoolean(false)
    private var translatingObb: ObservableBoolean = ObservableBoolean(false)
    private var transStateObb1: ObservableBoolean = ObservableBoolean(false)
    private var transStateObb2: ObservableBoolean = ObservableBoolean(false)
    private var transRecognitionObb1: ObservableBoolean = ObservableBoolean(false)
    private var transRecognitionObb2: ObservableBoolean = ObservableBoolean(false)
    private var textPlayObb: ObservableBoolean = ObservableBoolean(true)
    private var transRecordObb: ObservableBoolean = ObservableBoolean(false)

    /*record*/
    private var transAudioRecord: TransAudioRecord? = null

    /*trans*/
    private var hsTranslateUtil: HSTranslateUtil? = null
    var curTransTextBean1 = TransTextBean()
    var curTransTextBean2 = TransTextBean()

    private var transTextMap = LinkedHashMap<String, String>()

    var sourceSB1 = StringBuilder()
    var targetSB1 = StringBuilder()
    var tempSB1 = StringBuilder()
    var sourceSB2 = StringBuilder()
    var targetSB2 = StringBuilder()
    var tempSB2 = StringBuilder()

    private var pcmAudioPlayer: PcmAudioPlayer? = null

    var transRecordBean: TransRecordBean? = null // 用于记录一次当前的对话

    lateinit var audioManager: AudioManager

    var ttsHelper: ITmpTTS? = null

    override fun onCreate() {
        super.onCreate()
        initNotice()
        init(application)
        mScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        mainHandler = MainHandler(Looper.getMainLooper())
        networkCallback = (application as HopeBaseApp).getAppComponent().networkCallback
        spManager = (application as HopeBaseApp).spManager
        networkCallback.registNetworkCallback(networkCallbackModule)
        audioManager = application.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
        initSpeech()

        // sp数据加载
        initData()
//        RecordRepository.updateItems()
        reBuild()


        Timber.i("XTAG service Create ${HopeUtils.getIP()} ${languageObs?.get()}")
        initTrans()

        // 初始化翻译组件
        hsTranslateUtil = HSTranslateUtil()
        hsTranslateUtil?.init(
            TranslateConfig.accessKey,
            TranslateConfig.secretKey
        )

        // pcm 初始化
//        pcmAudioPlayer = PcmAudioPlayer()
    }

    private fun initSpeech() {
        ttsHelper = HSTTSHelper(application)
        ttsHelper?.init()
//        mScope.launch {
//            delay(5000)
//            ttsHelper?.speak("你好")
//            delay(1000)
//            ttsHelper?.speak("今天天气怎么样")
//        }
    }

    private fun initData() {
        mScope.launch {
//            fontSizeObf.set(spManager.getFloat(MessageConstant.SP_RECORD_TEXT_SIZE, 1f))
            fontSizeObf.set(0.5f) // TODO 还原
            val languages = getStringArray(R.array.lang_an_array)
            languageObs.set(spManager.getString(MessageConstant.SP_RECORD_LANGUAGE, languages[0]))
            moreDisplayObb.set(spManager.getBoolean(MessageConstant.SP_MORE_DISPLAY, false))
            textPlayObb.set(spManager.getBoolean(MessageConstant.SP_TEXT_PLAY, true))

            try {
                val record = spManager.getString(MessageConstant.SP_TRANS_RECORD, "")
                if (!record.isNullOrEmpty()) {
                    val list = gson.fromJson<ArrayList<TransRecordBean>>(record, object : TypeToken<List<TransRecordBean?>?>() {}.type)
                    RecordRepository.updateItems(list)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
//        testData()
    }

    override fun onDestroy() {
        super.onDestroy()
        ttsHelper?.release()
        pcmAudioPlayer?.release()
        timerHandler?.stop()
        transAudioRecord?.release()
        hsTranslateUtil?.release()
    }

    private fun initNotice() {
        var builder: Notification.Builder? = Notification.Builder(this)
        builder!!.setSmallIcon(R.drawable.logo).setWhen(System.currentTimeMillis())
        builder.setContentTitle("KeepAppAlive")
        builder.setContentText(resources.getString(R.string.app_name) + "服务 is running...")
//        startForeground(SPEECH_NOTICE_ID, builder.build())
//        // 如果觉得常驻通知栏体验不好
//        // 可以通过启动CancelNoticeService，将通知移除，oom_adj值不变
//        val intent = Intent(this, CancelNoticeService::class.java)
//        startService(intent)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //修改安卓8.1以上系统报错
            var notificationChannel: NotificationChannel? = NotificationChannel(
                CHANNEL_ONE_ID,
                CHANNEL_ONE_NAME,
                NotificationManager.IMPORTANCE_MIN
            )
            notificationChannel!!.enableLights(false)//如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
            notificationChannel.setShowBadge(false)//是否显示角标
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
            var manager: NotificationManager? =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager?.createNotificationChannel(notificationChannel)
            builder.setChannelId(CHANNEL_ONE_ID)
            notificationChannel = null
            manager = null
        }
        startForeground(SERVICE_NOTICE_ID, builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun initFloat() {
    }

    override fun init(context: Context) {

    }

    override fun showFloat() {
        mainHandler.removeMessages(MSG_FLOAT_HIDE)
        mainHandler.sendEmptyMessage(MSG_FLOAT_SHOW)
    }

    override fun hideFloat(delayMillis: Long) {
        mainHandler.sendEmptyMessage(MSG_FLOAT_HIDE)
    }

    override fun write(msg: String) {

    }


    override fun getFontSizeObs(): ObservableFloat? {
        return fontSizeObf
    }

    override fun setFontSize(size: Float) {
        fontSizeObf?.set(size)
        spManager.setFloat(MessageConstant.SP_RECORD_TEXT_SIZE, size)
    }

    override fun getTransLangObs(): ObservableField<String>? {
        return languageObs
    }

    override fun setTransLang(lang: String) {
        languageObs?.set(lang)
        spManager.setString(MessageConstant.SP_RECORD_LANGUAGE, lang)
    }

    override fun getMoreDisplayObs(): ObservableBoolean? {
        return moreDisplayObb
    }

    override fun setMoreDisplay(more: Boolean) {
        moreDisplayObb.set(more)
        spManager.setBoolean(MessageConstant.SP_MORE_DISPLAY, more)
    }

    override fun getTextPlayObs(): ObservableBoolean? {
        return textPlayObb
    }

    override fun setTextPlay(play: Boolean) {
        textPlayObb.set(play)
        spManager.setBoolean(MessageConstant.SP_TEXT_PLAY, play)
    }

    override fun getTranslatingObs(): ObservableBoolean? {
        return translatingObb
    }

    override fun setTranslating(play: Boolean) {
        translatingObb.set(play)
    }

    override fun notifyTransPage(trans: Boolean, reset: Boolean) {
        LiveEBUtil.post(RemoteMessageEvent(MessageConstant.CMD_TRANSLATING, trans.toString()))
//        if (reset) {
//            transRecordBean = TransRecordBean(languageObs.get() ?: "")
//        }
    }

    override fun changeTranslatingState(open: Boolean) {
        if (open) {
            transRecordBean = TransRecordBean(languageObs?.get() ?: "")
        } else {
            if (transRecordBean?.beans.isNullOrEmpty()) return
            TransRepository.clear()
            transRecordBean?.end(spManager)
        }
    }

    override fun getTransStateObs(index: Int): ObservableBoolean? {
        return if (index == 1) return transStateObb1 else transStateObb2
    }

    override fun setTransState(play: Boolean, index: Int) {
        if (play && (transRecognitionObb1?.get() || transRecognitionObb2?.get())) {
            ToastUtil.showS("识别翻译中，请稍候！")
            return
        }
        val cb = {
            ToastUtil.showS("识别翻译中，请稍候！")
        }
        if (!moreDisplayObb.get() || index == 0) {
            if (play) {
                if (transRecognitionObb1?.get() || transRecognitionObb2?.get()) {
                    cb.invoke()
                    return
                }
                transRecognitionObb1.set(true)
                transRecognitionObb2.set(true)
            }
            transStateObb1.set(play)
            transStateObb2.set(play)
        } else if (index == 1) {
            if (play) {
                if (transRecognitionObb1?.get()) {
                    cb.invoke()
                    return
                }
                transRecognitionObb1.set(true)
            }
            transStateObb1.set(play)
        }
        else {
            if (play) {
                if (transRecognitionObb2?.get()) {
                    cb.invoke()
                    return
                }
                transRecognitionObb2.set(true)
            }
            transStateObb2.set(play)
        }

        if (play) transAudioRecord?.start(if (!moreDisplayObb.get()) 0 else index)
        else transAudioRecord?.stop(if (!moreDisplayObb.get()) 0 else index)
    }

    override fun setTransState(index: Int) {
//        setTransState(if (index == 2 && moreDisplayObb.get()) !transStateObb2.get() else !transStateObb1.get(), index)
        setTransState(!transStateObb1.get(), 0)
    }

    override fun getTransRecognition(index: Int): ObservableBoolean? {
        return if (index == 1) transRecognitionObb1 else transRecognitionObb2
    }

    override fun getTransPlayObs(): ObservableField<String>? {
        return pcmAudioPlayer?.currentPathObs
    }

    override fun getPlayStatusObs(): ObservableField<PcmAudioPlayer.State>? {
        return pcmAudioPlayer?.stateObs
    }

    override fun setTransPlay(bean: TransRecordBean) {
        if (bean.path.isNullOrEmpty()) {
            Timber.i("未找到播放路径")
            ToastUtil.showS("未找到播放路径！")
            return
        }
        val exist = File(bean.path).exists()
        Timber.i("setTransPlay $exist ${bean.path}")
        if (!exist) ToastUtil.showS("文件已丢失，可移除此记录！")
        pcmAudioPlayer?.playPause(bean.path)
    }

    override fun getTransRecordObs(): ObservableBoolean? {
        return transRecordObb
    }

    override fun setTransRecord(play: Boolean) {
        transRecordObb.set(play)
    }

    override fun showVolume() {
        audioManager.adjustVolume(AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI)
    }

    private val MSG_FLOAT_SHOW = 100
    private val MSG_FLOAT_UPDATE = 101
    private val MSG_FLOAT_HIDE = 102

    inner class MainHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_FLOAT_HIDE -> {
                    if (rootView?.visibility != View.GONE)
                        rootView?.visibility = View.GONE
                    mainHandler.removeMessages(MSG_FLOAT_HIDE)
                }

                MSG_FLOAT_SHOW -> {
                    if (rootView?.visibility != View.VISIBLE)
                        rootView?.visibility = View.VISIBLE
                    mainHandler.removeMessages(MSG_FLOAT_SHOW)
                }
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return this.mBinder
    }

    inner class BaseBinder : Binder() {
        fun getPaintService(): TmpServiceImpl {
            return WeakReference(this@TmpServiceImpl).get()!!
        }
    }

    override fun reBuild() {

    }

    fun tipError(msg: String = MessageConstant.TIP_ANA_FAIL) {
        log(msg)
//        try {
//            ToastUtil.showS(msg)
//        } catch (e: Exception) {}
    }

    private fun initTrans() {
        transAudioRecord = TransAudioRecord(this, object : ITransRecord{
            override fun onRecordEnd(isMaster: Boolean, path: String?) {
                // 录音完成
                if (isMaster) transRecognitionObb1.set(false)
                else transRecognitionObb2.set(false)
                if (path == null) return
//                // 执行翻译
//                if (isMaster) {
//                    sourceSB1.clear()
//                    targetSB1.clear()
//                    tempSB1.clear()
//                } else {
//                    sourceSB2.clear()
//                    targetSB2.clear()
//                    tempSB2.clear()
//                }
//                if (isMaster) {
//                    curTransTextBean1 = TransTextBean()
//                    curTransTextBean1.isMaster = true
//                } else {
//                    curTransTextBean2 = TransTextBean()
//                    curTransTextBean2.isMaster = false
//                }

                log("onRecordEnd2 $isMaster $path")
                val ex = getExStr()
                val source = if (isMaster) "zh" else ex
                val target = if (!isMaster) "zh" else ex
                hsTranslateUtil?.translate(path, source, target) { resList ->
                    if (resList.isNotEmpty()) {
                        try {
                            val res = resList[0]
                            val data = gson.fromJson<TranslateBean>(res, TranslateBean::class.java)
                            val res2 = data?.Subtitle?.Text ?: MessageConstant.TIP_ANA_FAIL
                            val res3 = data?.ResponseMetaData?.RequestId ?: MessageConstant.TIP_NO_REQUEST_ID
                            if (res2 == MessageConstant.TIP_NO_REQUEST_ID) {
                                tipError(res3)
                                return@translate
                            }
                            if (res2 != MessageConstant.TIP_ANA_FAIL) {
                                if (data.Subtitle?.Definite == true) {
                                    // 可能是原文或者译文
                                    val key = data.ResponseMetaData!!.RequestId
                                    if (data.Subtitle?.Language == target) {
                                        log("译文 $res2")
                                        if (transTextMap.containsKey(key)) {
                                            if (isMaster && !TextPinyinUtil.containsChinese(transTextMap[key]!!)) {
                                                log("应该是串音了 ${transTextMap[key]}")
                                                return@translate
                                            }
                                            notifyInfo(res2, transTextMap[key]!!, isMaster, path)
                                        } else {
                                            log("未找到原文 $key")
                                        }
                                    } else {
                                        log("原文 $res2")
                                        transTextMap[key] = res2
                                    }
                                } else {
                                    log("解析中 $res2")
                                }
                            } else {
                                tipError()
                            }
                        } catch (e: Exception) {

                        }
                    }
                }
            }
        })
        transAudioRecord?.init()
    }

    private fun notifyInfo(transText: String, text: String, isMaster: Boolean, path: String) {
        var bean = TransTextBean()
        bean.text = text
        bean.transText = transText
        bean.isMaster = isMaster
        val lang = languageObs.get() ?: ""
        TransRepository.addItem(bean.copy(), lang, path, this)
        playText(transText)
    }

    private fun playText(tarnsText: String?) {
        log("playText ${textPlayObb.get()} $tarnsText")
        if (!tarnsText.isNullOrEmpty() && textPlayObb.get()) {
            try {
                ttsHelper?.speak(tarnsText)
            } catch (e: Exception) {
                log("playText err ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun notifyInfo(isMaster: Boolean, path: String, list: List<String>, isSource: Boolean = true, isTemp: Boolean = false) {
        if (isSource) {
            if (isMaster) tempSB1.clear() else tempSB2.clear()
            list.forEach { res ->
                if (isTemp) if (isMaster) tempSB1.append(res) else tempSB2.append(res)
                else if (isMaster) sourceSB1.append(res) else sourceSB2.append(res)
            }
            if (isMaster) {
                curTransTextBean1.text = sourceSB1.toString() + tempSB1.toString()
            } else {
                curTransTextBean2.text = sourceSB2.toString() + tempSB2.toString()
            }
        } else {
            list.forEach { res ->
                if (isMaster) targetSB1.append(res) else targetSB2.append(res)
            }
            val lang = languageObs.get() ?: ""
            var tarnsText = ""
            if (isMaster) {
                curTransTextBean1.transText = targetSB1.toString()
                tarnsText = curTransTextBean1.transText
                // 添加一份记录
                TransRepository.addItem(curTransTextBean1.copy(), lang, path, this)
                // TODO 数量到达一定大小之后，删除前面的一半数据，避免卡顿
            } else {
                tarnsText = curTransTextBean2.transText
                curTransTextBean2.transText = targetSB2.toString()
                TransRepository.addItem(curTransTextBean2.copy(), lang, path, this)
            }
            playText(tarnsText)
        }
    }


    fun getExStr(): String {
        val index = getStringArray(R.array.lang_an_array).indexOf(languageObs.get())
        if (index < 0) return "en"
        val exLanguages = getStringArray(R.array.lang_ex_array)
        return exLanguages[index]
    }

    var networkCallbackModule: NetworkCallbackModule = object : NetworkCallbackModule {
        override fun onAvailable(network: Network?) {
            reBuild()
        }

        override fun onLost(network: Network?) {

        }

        override fun onCapabilitiesChanged(network: Network?, networkCapabilities: NetworkCapabilities) {
            if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                when {
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        Timber.i("onCapabilitiesChanged: 网络类型为wifi")
                    }

                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        Timber.i("onCapabilitiesChanged: 网络类型为以太网")
                    }

                    else -> {
                        Timber.i("onCapabilitiesChanged: 其他网络")
                    }
                }
            }
        }
    }

    private fun testData() {
        mScope.launch {
            delay(500)
            transRecordBean = TransRecordBean(languageObs.get() ?: "")
//            val arr: ArrayList<TransTextBean> = arrayListOf()
            val b1 = TransTextBean()
            b1.text = "你好 老弟"
            b1.transText = "Hello Bro"
            val b2 = TransTextBean()
            b2.text = "give me some money"
            b2.transText = "给我点钱花花"
            b2.isMaster = false
            val dir = File(Environment.getExternalStorageDirectory(), "AudioRecordings")
            val lang = languageObs.get() ?: ""
            val path1 = File(dir, "1_20250729_231124.pcm").absolutePath
            val path2 = File(dir, "2_20250729_231124.pcm").absolutePath
            TransRepository.addItem(b1, lang, path1, this@TmpServiceImpl)
//            translatingData.value?.add(b1)
            delay(500)
            TransRepository.addItem(b2, lang, path2, this@TmpServiceImpl)
            for (i in 0 until 10) {
                delay(2000)
                val b3 = TransTextBean()
                b3.text = "正文3 $i"
                b3.transText = "译文3 $i"
                b3.isMaster = i % 2 == 0
                TransRepository.addItem(b3, lang, if (b3.isMaster) path1 else path2, this@TmpServiceImpl)
            }
            transRecordBean?.end(spManager)
            transRecordBean = null
        }
    }

    private fun log(msg: Any?) {
        Timber.i("TMPService ${msg ?: "null"}")
    }
}
