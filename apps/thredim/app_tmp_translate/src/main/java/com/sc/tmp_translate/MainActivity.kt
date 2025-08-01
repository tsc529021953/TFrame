package com.sc.tmp_translate

//import com.volcengine.JSON
//import com.volcengine.model.request.translate.LangDetectRequest
//import com.volcengine.model.request.translate.TranslateTextRequest
//import com.volcengine.model.response.translate.LangDetectResponse
//import com.volcengine.model.response.translate.TranslateTextResponse
import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import com.google.gson.Gson
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.network.NetworkCallback
import com.sc.tmp_translate.bean.TranslateBean
import com.sc.tmp_translate.databinding.ActivityMainBinding
import com.sc.tmp_translate.utils.AudioRecorder
import com.sc.tmp_translate.utils.hs.HSTranslateUtil
import com.sc.tmp_translate.utils.hs.TranslateConfig
import com.sc.tmp_translate.utils.record.MultipleAudioRecord
import com.sc.tmp_translate.vm.MainViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import kotlin.random.Random


/**
 * @author  tsc
 * @date  2024/4/25 17:47
 * @version 0.0.0-1
 * @description
   1.开启两个麦克风的录音，并做降噪处理
   2.录音上传给火山进行翻译
 */
class MainActivity : BaseBindingActivity<ActivityMainBinding, MainViewModel>(), AudioRecorder.Callback {

    companion object {

        const val REQUEST_CODE = 10085

        var PERMISSIONS = arrayListOf<String>(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
        )

        const val PLAY_IMAGE_TIME = 15000L
        const val CTRL_LAYOUT_VIEW_TIME = 10000L

        var TRANSLATE_TO = "en"
    }

    override var layoutId: Int = R.layout.activity_main

    @Inject
    override lateinit var viewModel: MainViewModel

    @Inject
    lateinit var networkCallback: NetworkCallback

    var hsTranslateUtil: HSTranslateUtil? = null

    var sourceSB = StringBuilder()
    var targetSB = StringBuilder()
    var tempSB = StringBuilder()

    var gson = Gson()

    var textList = arrayListOf<String>(
        "今天天气怎么样",
        "中国最大的公司是什么",
        "特朗普富豪朋友在股市1天赚25亿美元",
        "聚焦构建周边命运共同体",
        "中国将适度减少美国影片进口数量热",
        "中方对美加征84%关税正式生效",
        "这次“历史罕见”大风有多极端",
        "金价暴涨创5年来最大涨幅"
    )

    var random :Random? = null

    private var isTranslating = false

    private var listener = androidx.lifecycle.Observer<Any> {
        it as RemoteMessageEvent
        when (it.cmd) {

        }
    }

    private var audioRecorder: AudioRecorder? = null

    private var mar: MultipleAudioRecord? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        System.out.println("onCreate ??? $requestedOrientation ${requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE}")
        if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

    }

    override fun subscribeUi() {
        binding.vm = viewModel
        if (checkPermissions(true)) {
            init()
        }
        random = Random(System.currentTimeMillis())
        binding.micIv.setOnClickListener {
            mar?.startOrStop()
        }
        binding.textIv.setOnClickListener {
            dealText()
        }
        binding.audioIv.setOnClickListener {
            dealAudio()
        }
        binding.recordIv.setOnClickListener {
            recordAndTrans()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && checkPermissions(false)) {
            System.out.println("onRequestPermissionsResult ")
            init()
        }
    }

    override fun initData() {

    }

    override fun linkViewModel() {

    }

    override fun onDestroy() {
        super.onDestroy()
        hsTranslateUtil?.release()
        mar?.release()
    }

    private fun init() {
        viewModel.initData()
        viewModel?.mScope?.launch {
            initTranslate()

            if (mar == null) mar = MultipleAudioRecord(this@MainActivity)
            mar?.init()
        }
//        for (i in 0 until 100) {
//            val list = arrayListOf("开心") // textList[index]
//            notifyInfo(list)
//        }
        audioRecorder = AudioRecorder(this, this)
    }

    private fun initTranslate() {
        logT("initTranslate")
        hsTranslateUtil = HSTranslateUtil()
        hsTranslateUtil?.init(
            TranslateConfig.accessKey,
            TranslateConfig.secretKey
        )
    }

    private fun notifyInfo(list: List<String>, isSource: Boolean = true, isTemp: Boolean = false) {
        this.runOnUiThread {
            if (isSource) {
                tempSB.clear()
                list.forEach { res ->
                    if (isTemp) tempSB.append(res + "\n")
                    else sourceSB.append(res + "\n")
                }
                binding.srcTv.text = sourceSB.toString() + tempSB.toString()
                binding.srcSv.post {
                    binding.srcSv.fullScroll(View.FOCUS_DOWN)
                }
            } else {
                list.forEach { res ->
                    targetSB.append(res + "\n")
                }
                binding.targetTv.text = targetSB.toString()
                binding.targetSv.post {
                    binding.targetSv.fullScroll(View.FOCUS_DOWN)
                }
            }
        }
    }

    private fun checkPermissions(request: Boolean = false): Boolean {
        var hasPermission = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (i in 0 until PERMISSIONS.size) {
                val permission = PERMISSIONS[i]
                logT("per ${checkSelfPermission(permission)} $permission")
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {

                    hasPermission = false
                }
            }
            if (!hasPermission && request) {
//                requestPermissions(PERMISSIONS.toMutableList().toTypedArray(), 10086)
                //
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // android 11  且 不是已经被拒绝
                    // 先判断有没有权限
                    if (!Environment.isExternalStorageManager()) {
                        val intent: Intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        intent.setData(Uri.parse("package:$packageName"))
                        startActivityForResult(intent, 1024)
                    }
                }
                this.requestPermissions(PERMISSIONS.toTypedArray(), REQUEST_CODE)
            }
        }
        logT("hasPermission $hasPermission")
        return hasPermission
    }

    fun recordAndTrans() {
        if (audioRecorder?.isRecording != true) {
            audioRecorder?.startRecording()
        } else {
            audioRecorder?.stopRecording()
        }
    }

    private fun dealText() {
        if (isTranslating) return
        isTranslating = true
        val index = random!!.nextInt(textList.size)
        viewModel.mScope.launch {
            val list = arrayListOf(textList[index])
            notifyInfo(list)
            hsTranslateUtil?.translate2EN(list) { resList ->
                isTranslating = false
                notifyInfo(resList, false)
            }
        }
    }

    private fun dealAudio() {
        if (isTranslating) return
        isTranslating = true
        viewModel.mScope.launch {

            val path =
//                    "https://file.upfile.live/uploads/20250413/a21d54bff8710ebafcd4f25a0256a5db.mp4"
//                    "https://stream7.iqilu.com/10339/upload_transcode/202002/09/20200209104902N3v5Vpxuvb.mp4"
//                    "http://121.43.187.15:9005/default/audio.wav"
                Environment.getExternalStorageDirectory().absolutePath + File.separator + "THREDIM_MEDIA" + File.separator  + "audio.wav"
//                + "audio.wav"

//            Environment.getExternalStorageDirectory().absolutePath
//                    + File.separator + "THREDIM_MEDIA" + File.separator + "audio.wav"
            notifyInfo(arrayListOf("开始翻译音频 $path"))
            val file = File(path)
            logT("file ${file.exists()} ${file.absolutePath}")
            if (!file.exists()) {
                isTranslating = false
                notifyInfo(arrayListOf("未找到相关路径"), false)
            } else
//                    hsTranslateUtil?.translate(path, 10000) { resList ->
//                        isTranslating = false
//                        notifyInfo(resList, false)
//                    }
                hsTranslateUtil?.translate(path) { resList ->
                    isTranslating = false
                    var isTemp = false
                    var isSource = true
                    val list = resList.map { res ->
                        try {
                            val data = gson.fromJson<TranslateBean>(res, TranslateBean::class.java)
                            if (data.Subtitle?.Definite == true) {
                                if (data.Subtitle?.Language == TRANSLATE_TO) {
                                    isSource = false
                                }
                            } else {
                                isTemp = true
                            }
                            data?.Subtitle?.Text ?: "解析失败"
                        } catch (e:Exception) {
                            res
                        }
                    }
                    notifyInfo(list, isSource, isTemp)
                }
        }
    }

    private fun logT(msg: Any?) {
        Timber.i("TransTAG ${msg ?: ""}")
    }

    override fun onRecordingStarted() {
        isTranslating = true
    }

    override fun onRecordingStopped() {
        isTranslating = false
    }

    override fun onAudioData(data: ByteArray, size: Int) {

    }
}
