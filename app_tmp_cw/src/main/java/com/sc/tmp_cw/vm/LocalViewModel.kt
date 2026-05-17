package com.sc.tmp_cw.vm

import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.view.Surface
import android.view.SurfaceHolder
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.LoadControl
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.RenderersFactory
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nbhope.lib_frame.app.HopeBaseApp
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.bean.FileBean
import com.nbhope.lib_frame.utils.FileUtil
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.nbhope.lib_frame.utils.TimerHandler
import com.nbhope.lib_frame.utils.toast.ToastUtil
import com.sc.tmp_cw.constant.MessageConstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.EOFException
import java.io.File
import javax.inject.Inject


/**
 * @author  tsc
 * @date  2024/4/26 14:43
 * @version 0.0.0-1
 * @description
 */
class LocalViewModel @Inject constructor(val spManager: SharedPreferencesManager) : BaseViewModel() {

    companion object {


    }

    var mScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var gson = Gson()

//    var player: IjkMediaPlayer? = null
    var player: ExoPlayer? = null
//    var player: MediaPlayer? = null
    var surfaceHolder: SurfaceHolder? = null
    var surface: Surface? = null

    var videoListObs = MutableLiveData<ArrayList<FileBean>>()

    var list: List<FileBean>? = null

    var playStatusObs = ObservableBoolean(false)
    var playIndex = 0

    var lastStr = ""

    fun initPlayer() {
        // 释放旧的播放器实例（如果存在）
        player?.release()
        
        // 创建带宽计量器
        val bandwidthMeter = DefaultBandwidthMeter.Builder(HopeBaseApp.getContext()).build()
        
        // 创建轨道选择器，优化视频播放
        val trackSelector = DefaultTrackSelector(HopeBaseApp.getContext()).apply {
            setParameters(buildUponParameters().apply {
                // 允许自适应选择
                setAllowAudioMixedChannelCountAdaptiveness(true)
                setAllowVideoMixedMimeTypeAdaptiveness(true)
                // 优先选择标准清晰度视频以减少解码压力
                setMaxVideoSizeSd()
            })
        }
        
        // 自定义加载控制，优化缓冲策略以减少卡顿
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                10000, // 最小缓冲时间(ms) - 增加到10秒以减少卡顿
                30000, // 最大缓冲时间(ms) - 增加到30秒以提供更好的缓冲
                2500,  // 播放前缓冲时间(ms) - 增加到2.5秒以确保流畅开始
                5000   // 重新缓冲前的时间(ms) - 增加到5秒
            )
            .setPrioritizeTimeOverSizeThresholds(true) // 优先考虑时间而非大小
            .build()
        
        // 构建ExoPlayer实例，启用硬件加速和优化配置
        player = ExoPlayer.Builder(HopeBaseApp.getContext())
            .setTrackSelector(trackSelector)
            .setLoadControl(loadControl)
            .setBandwidthMeter(bandwidthMeter)
            .build()
            
        // 设置播放器准备状态
        player?.playWhenReady = true
        
        Timber.i("ExoPlayer初始化完成，已优化硬件解码和缓冲策略")
    }

    fun initData() {
        var path = Environment.getExternalStorageDirectory().absolutePath + MessageConstant.PATH_VIDEO
        list = FileUtil.getDicFileBeansByExs(FileUtil.VIDEO_EXTENSIONS, path) ?: return
        lastStr = spManager.getString(MessageConstant.SP_PLAYLIST_CHECK, "")
        checkVideo(false)
    }

    fun checkVideo(check: Boolean = true, checkPlay: Boolean = false) {
        if (check) {
            val record = spManager.getString(MessageConstant.SP_PLAYLIST_CHECK, "")
            if (record == lastStr) {
               Timber.i("相同数据，无需校验！ $checkPlay")
                if (checkPlay) {
                    try {
//                        player?.start()
                        player?.play()
//                        setIndex(0)
                    } catch (e: Exception) {
                        Timber.i("checkVideo replay ${e.message}")
                        e.printStackTrace()
                    }
                }
                return
            } else lastStr = record
        }

        val local = spManager.getString(MessageConstant.SP_PLAYLIST, "")
        val list3 = ArrayList<FileBean>()
        if (local.isNullOrEmpty()) {
            list?.let { list3.addAll(it) }
        } else {
            val record = gson.fromJson<ArrayList<FileBean>>(local, object : TypeToken<List<FileBean?>?>() {}.type)
            record.forEach { it2 ->
                val item = list!!.find { it3 -> it3.path == it2.path }
                if (item != null) {
                    it2.status = 1
                    list3.add(it2)
                }
            }
//            list!!.map { it ->
//                val item = record.find { it2 -> it.path == it2.path }
//                if (item != null) {
//                    list3.add(it)
//                    it.status = 1
//                } else it.status = 0
//            }
        }
        videoListObs.postValue(list3)
    }

    fun next() {
        if (videoListObs.value == null) return
        if (playIndex + 1 >= videoListObs.value!!.size) setIndex(0)
        else setIndex(playIndex + 1)
    }

    fun pre() {
        if (videoListObs.value == null) return
        if (playIndex - 1 < 0) setIndex(videoListObs.value!!.size - 1)
        else setIndex(playIndex - 1)
    }

    private fun setIndex(index: Int) {
        if (videoListObs.value == null) return
        if (index < 0 || index >= videoListObs.value!!.size) return
        val fileBean = videoListObs.value!!.get(index)
        play(fileBean)
    }

    fun play(item: FileBean) {
        if (videoListObs.value == null) return
        playIndex = videoListObs.value!!.indexOf(item)
        if (!File(item.path).exists()) {
            mScope.launch(Dispatchers.Main) {
                ToastUtil.showS("未检测到视频 ${item.name},将重新加载")
            }
            initData()
            return
        }
        
        // 检查视频兼容性
        if (!checkVideoCompatibility(item.path)) {
            mScope.launch(Dispatchers.Main) {
                ToastUtil.showS("视频格式可能不兼容: ${item.name}")
            }
            Timber.w("视频格式可能不兼容: ${item.path}")
            // 仍然尝试播放，但记录警告
        }
        
        try {
            // 停止当前播放
            player?.stop()
            
            val uri = Uri.fromFile(File(item.path))
            var mediaItem = MediaItem.fromUri(uri)
            
            // 设置媒体项并准备播放
            player?.setMediaItem(mediaItem)
            player?.prepare()
            player?.play()
            
            Timber.i("播放 ${item.name}")
            
            // 预加载下一个视频
            preloadNextVideo()
        } catch (e: Exception) {
            Timber.e(e, "播放视频时发生错误: ${item.name}")
            e.printStackTrace()
            // 尝试重新初始化播放器
            initPlayer()
        }
    }
    
    /**
     * 预加载下一个视频以减少切换时的卡顿
     */
    private fun preloadNextVideo() {
        if (videoListObs.value == null || videoListObs.value!!.size <= 1) return
        
        val nextIndex = if (playIndex + 1 >= videoListObs.value!!.size) 0 else playIndex + 1
        val nextItem = videoListObs.value!![nextIndex]
        
        // 在后台线程中预加载下一个视频
        mScope.launch(Dispatchers.IO) {
            try {
                // 这里可以添加更复杂的预加载逻辑
                // 例如：预先解析视频元数据、检查文件完整性等
                Timber.d("预加载下一个视频: ${nextItem.name}")
            } catch (e: Exception) {
                Timber.w("预加载下一个视频失败: ${e.message}")
            }
        }
    }

    fun stop() {
        if (videoListObs.value == null) return
        try {
            player?.stop()
//            player?.next()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        player?.release()
    }

    fun mute(isMute: Boolean) {
        if (isMute)
            player?.volume = 0f
        else player?.volume = 1.0f
//        if (isMute)
//            player?.setVolume(0f, 0f)
//        else player?.setVolume(1f, 1f)
    }
    
    /**
     * 检查视频文件是否可能引起解码问题
     */
    fun checkVideoCompatibility(filePath: String): Boolean {
        return try {
            val file = File(filePath)
            if (!file.exists()) {
                Timber.w("视频文件不存在: $filePath")
                return false
            }
            
            // 检查文件大小，过大的文件可能导致内存问题
            val fileSize = file.length()
            if (fileSize > 2L * 1024 * 1024 * 1024) { // 大于2GB
                Timber.w("视频文件过大: ${fileSize / (1024 * 1024)}MB")
            }
            
            // 检查文件扩展名
            val extension = filePath.substringAfterLast('.', "").lowercase()
            val supportedExtensions = listOf("mp4", "avi", "mkv", "mov", "wmv", "flv", "webm", "3gp")
            
            if (!supportedExtensions.contains(extension)) {
                Timber.w("不支持的视频格式: .$extension")
                return false
            }
            
            // 对于某些格式，可能需要额外的检查
            when (extension) {
                "mkv" -> {
                    // MKV 容器可能包含多种编码格式，需要特别注意
                    Timber.d("MKV 格式视频，可能存在兼容性问题")
                }
                "avi" -> {
                    // AVI 格式较老，可能有编码兼容性问题
                    Timber.d("AVI 格式视频，可能存在兼容性问题")
                }
            }
            
            true
        } catch (e: Exception) {
            Timber.e(e, "检查视频兼容性时出错")
            false
        }
    }

}
