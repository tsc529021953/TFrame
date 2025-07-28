package com.sc.tmp_translate.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Network
import android.net.NetworkCapabilities
import android.os.*
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableFloat
import androidx.lifecycle.MutableLiveData
import com.afollestad.materialdialogs.utils.MDUtil.getStringArray
import com.google.gson.Gson
import com.nbhope.lib_frame.app.HopeBaseApp
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.network.NetworkCallback
import com.nbhope.lib_frame.network.NetworkCallbackModule
import com.nbhope.lib_frame.utils.HopeUtils
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.nbhope.lib_frame.utils.TimerHandler
import com.sc.tmp_translate.MainActivity.Companion.TRANSLATE_TO
import com.sc.tmp_translate.R
import com.sc.tmp_translate.bean.DataRepository
import com.sc.tmp_translate.bean.TransTextBean
import com.sc.tmp_translate.bean.TranslateBean
import com.sc.tmp_translate.constant.MessageConstant
import com.sc.tmp_translate.inter.ITmpService
import com.sc.tmp_translate.inter.ITransRecord
import com.sc.tmp_translate.utils.hs.HSTranslateUtil
import com.sc.tmp_translate.utils.hs.TranslateConfig
import com.sc.tmp_translate.utils.record.TransAudioRecord
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.File
import java.lang.ref.WeakReference
import javax.inject.Inject


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
    private var transStateObb: ObservableBoolean = ObservableBoolean(false)
    private var textPlayObb: ObservableBoolean = ObservableBoolean(true)

    /*record*/
    private var transAudioRecord: TransAudioRecord? = null

    /*trans*/
    private var hsTranslateUtil: HSTranslateUtil? = null
    var sourceSB = StringBuilder()
    var targetSB = StringBuilder()
    var tempSB = StringBuilder()
    var curTransTextBean1 = TransTextBean()
    var curTransTextBean2 = TransTextBean()

    override fun onCreate() {
        super.onCreate()
        initNotice()
        init(application)
        mScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        mainHandler = MainHandler(Looper.getMainLooper())
        networkCallback = (application as HopeBaseApp).getAppComponent().networkCallback
        spManager = (application as HopeBaseApp).spManager
        networkCallback.registNetworkCallback(networkCallbackModule)

        fontSizeObf.set(spManager.getFloat(MessageConstant.SP_RECORD_TEXT_SIZE, 1f))
        val languages = getStringArray(R.array.lang_an_array)
        languageObs.set(spManager.getString(MessageConstant.SP_RECORD_LANGUAGE, languages[0]))
        moreDisplayObb.set(spManager.getBoolean(MessageConstant.SP_MORE_DISPLAY, false))
        textPlayObb.set(spManager.getBoolean(MessageConstant.SP_TEXT_PLAY, true))
        reBuild()


        Timber.i("XTAG service Create ${HopeUtils.getIP()} ${languageObs?.get()}")
//        testData()
        initTrans()

        // 初始化翻译组件
        hsTranslateUtil = HSTranslateUtil()
        hsTranslateUtil?.init(
            TranslateConfig.accessKey,
            TranslateConfig.secretKey
        )
    }

    override fun onDestroy() {
        super.onDestroy()
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

    override fun notifyTransPage(trans: Boolean) {
        LiveEBUtil.post(RemoteMessageEvent(MessageConstant.CMD_TRANSLATING, trans.toString()))
    }

    override fun getTransStateObs(): ObservableBoolean? {
        return transStateObb
    }

    override fun setTransState(play: Boolean) {
        transStateObb.set(play)
        if (play) transAudioRecord?.start()
        else transAudioRecord?.stop()
    }

    override fun setTransState() {
        setTransState(!transStateObb.get())
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

    private fun initTrans() {
        transAudioRecord = TransAudioRecord(this, object : ITransRecord{
            override fun onRecordEnd(isMaster: Boolean, path: String) {
                // 执行翻译
                mScope.launch {
                    sourceSB.clear()
                    targetSB.clear()
                    tempSB.clear()
                    if (isMaster) {
                        curTransTextBean1 = TransTextBean()
                        curTransTextBean1.isMaster = true
                    }

                    System.out.println("onRecordEnd2 $isMaster $path")
                    val ex = getExStr()
                    val source = if (isMaster) "zh" else ex
                    val target = if (!isMaster) "zh" else ex
                    hsTranslateUtil?.translate(path, source, target) { resList ->
//                        isTranslating = false
                        var isTemp = false
                        var isSource = true
                        val list = resList.map { res ->
                            try {
                                val data = gson.fromJson<TranslateBean>(res, TranslateBean::class.java)
                                if (data.Subtitle?.Definite == true) {
                                    if (data.Subtitle?.Language == target) {
                                        isSource = false
                                    }
                                } else {
                                    isTemp = true
                                }
                                data?.Subtitle?.Text ?: "解析失败"
                            } catch (e: Exception) {
                                res
                            }
                        }
                        System.out.println("Trans ${gson.toJson(list)}")
                        notifyInfo(isMaster, path, list, isSource, isTemp)
                    }
                }
            }
        })
        transAudioRecord?.init()
    }

    private fun notifyInfo(isMaster: Boolean, path: String, list: List<String>, isSource: Boolean = true, isTemp: Boolean = false) {
        if (isSource) {
            tempSB.clear()
            list.forEach { res ->
                if (isTemp) tempSB.append(res)
                else sourceSB.append(res)
            }
            if (isMaster) {
                curTransTextBean1.text = sourceSB.toString() + tempSB.toString()
            }
        } else {
            list.forEach { res ->
                targetSB.append(res)
            }
            if (isMaster) {
                curTransTextBean1.transText = targetSB.toString()
                DataRepository.addItem(curTransTextBean1.copy())
            }
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
//            delay(5000)
//            val arr: ArrayList<TransTextBean> = arrayListOf()
            val b1 = TransTextBean()
            b1.text = "你好"
            b1.transText = "Hello"
            val b2 = TransTextBean()
            b2.text = "give me some money"
            b2.transText = "给我点钱花花"
            b2.isMaster = false

            DataRepository.addItem(b1)
//            translatingData.value?.add(b1)
            delay(2000)
            DataRepository.addItem(b2)
            for (i in 0 until 10) {
                delay(2000)
                val b3 = TransTextBean()
                b3.text = "正文 $i"
                b3.transText = "译文 $i"
                b3.isMaster = i % 2 == 0
                DataRepository.addItem(b3)
            }
//            translatingData.value?.add(b2)
//            arr.add(b1)
//            arr.add(b2)
        }
    }

}
