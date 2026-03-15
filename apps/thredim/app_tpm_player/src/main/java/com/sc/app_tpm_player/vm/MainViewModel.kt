package com.sc.app_tpm_player.vm

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.gson.Gson
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.utils.FileUtil
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.nbhope.lib_frame.utils.TimerHandler
import com.nbhope.lib_frame.utils.toast.ToastUtil
import com.sc.app_tpm_player.bean.AppInfoBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject


/**
 * @author  tsc
 * @date  2024/4/26 14:43
 * @version 0.0.0-1
 * @description
 */
class MainViewModel @Inject constructor(val spManager: SharedPreferencesManager) : BaseViewModel() {

    companion object {

        const val BASE_FILE = "/THREDIM_MEDIA/"
        const val CONFIG_FILE = "AppInfo.json"
    }

    var mScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var gson = Gson()

    var playStatusObs = ObservableBoolean(false)

    var filesObs = MutableLiveData<List<File>>()

    private var videoDirPath = "${Environment.getExternalStorageDirectory().absolutePath}/Thredim/Videos/"

    var isImage: Boolean? = null

    var playIndex = 0

    lateinit var timerHandler: TimerHandler

    var url = ""

    var type = 0

    var player: ExoPlayer? = null

    fun initData() {
        loadVideoFiles { videoFiles ->
            filesObs.postValue(videoFiles)
            if (videoFiles.isEmpty()) {
                Timber.i("未找到路径${videoDirPath}下的视频文件！")
                ToastUtil.showS("未找到路径${videoDirPath}下的视频文件！")
            }
        }
    }

    fun play(index: Int) {
        if (filesObs.value == null || index < 0 || index >= filesObs.value!!.size) {
            ToastUtil.showS("未找到对应视频文件！")
            return
        }
        play(filesObs.value!!.get(index))
    }

    fun play(file: File) {
        val uri = Uri.fromFile(file)
        var mediaItem = MediaItem.fromUri(uri)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
    }

    fun play() {
        player?.play()
    }

    fun pause() {
        player?.pause()
    }

    fun stop() {
        player?.stop()
    }

    fun release() {
        player?.release()
    }

    fun prev() {

    }

    fun next() {

    }

    /**
     * 遍历 SD 卡下 Thredim/Videos/ 目录中的所有视频文件
     * @param callback 回调函数，返回视频文件列表
     */
    private fun loadVideoFiles(callback: (List<File>) -> Unit) {
        mScope.launch {
            try {
                val videoDir = File(videoDirPath)

                Timber.i("TTAG 视频目录路径：$videoDirPath")

                if (!videoDir.exists()) {
                    Timber.w("TTAG 视频目录不存在：$videoDirPath")
                    callback.invoke(emptyList())
                    return@launch
                }

                if (!videoDir.isDirectory) {
                    Timber.w("TTAG 路径不是目录：$videoDirPath")
                    callback.invoke(emptyList())
                    return@launch
                }

                // 使用 FileUtil 的工具方法获取视频文件
                val videoFiles = FileUtil.getDicFilesByExs(FileUtil.VIDEO_EXTENSIONS, videoDirPath)

                Timber.i("TTAG 找到 ${videoFiles.size} 个视频文件")
                videoFiles.forEach { file ->
                    Timber.d("TTAG 视频文件：${file.name}")
                }

                filesObs.postValue(videoFiles)
                callback.invoke(videoFiles)
            } catch (e: Exception) {
                Timber.e(e, "TTAG 加载视频文件失败")
                callback.invoke(emptyList())
            }
        }
    }

    fun initAppData(context: Context, copy: Boolean = true, callback: () -> Unit) {
        mScope.launch {
            var path = Environment.getExternalStorageDirectory().absolutePath + BASE_FILE
            var file = File(path)
            val copyFun = {
                val res = FileUtil.copyAssetFile(context, CONFIG_FILE, path)
                Timber.i("TTAG copyFun $res")
                if (copy)
                    initAppData(context, false, callback)
            }
            path += CONFIG_FILE
            if (!file.exists()) {
                file.mkdirs()
                Timber.i("TTAG 路径不存在 $path")
                // copy
                copyFun.invoke()
            } else {
                file = File(path)
                if (file.exists()) {
                    val json = FileUtil.readFile(path)
                    val netBean = gson.fromJson(json, AppInfoBean::class.java)
                    url = netBean.url ?: url
                    type = netBean.type
                    callback.invoke()
                } else {
                    copyFun.invoke()
                }
            }
        }
    }


}
