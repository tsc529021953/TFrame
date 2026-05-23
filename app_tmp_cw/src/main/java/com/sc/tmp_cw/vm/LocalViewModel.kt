package com.sc.tmp_cw.vm

import android.net.Uri
import android.os.Environment
import android.view.Surface
import android.view.SurfaceHolder
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nbhope.lib_frame.app.HopeBaseApp
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.bean.FileBean
import com.nbhope.lib_frame.utils.FileUtil
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.nbhope.lib_frame.utils.toast.ToastUtil
import com.sc.tmp_cw.constant.MessageConstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2024/4/26 14:43
 * @version 0.0.0-1
 * @description 本地视频播放ViewModel - 双播放器无缝切换优化版
 */
class LocalViewModel @Inject constructor(val spManager: SharedPreferencesManager) : BaseViewModel() {

    var mScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    var gson = Gson()

    // 使用 ConcatenatingMediaSource 实现无缝播放
    var player: ExoPlayer? = null
    private var concatenatingMediaSource: ConcatenatingMediaSource? = null
    
    var surfaceHolder: SurfaceHolder? = null
    var surface: Surface? = null

    var videoListObs = MutableLiveData<ArrayList<FileBean>>()
    var list: List<FileBean>? = null

    var playStatusObs = ObservableBoolean(false)
    var playIndex = 0

    var lastStr = ""
    
    // 缓冲性能诊断
    var bufferStartTime: Long = 0
    
    // 防止缓冲死循环
    private var consecutiveBufferTimeouts = 0
    private val MAX_BUFFER_TIMEOUTS = 2
    
    // 频繁缓冲检测
    private var recentBufferCount = 0
    private var lastBufferTime = 0L
    private val BUFFER_WINDOW_MS = 30000
    
    // Fragment可见性状态管理
    var isFragmentVisible = false
    private var shouldResumePlay = false

    // 内部播放器监听器，用于重建播放器后重新绑定
    private var internalPlayerListener: Player.Listener? = null

    /**
     * 初始化播放器，使用 ConcatenatingMediaSource 实现无缝播放
     */
    fun initPlayer() {
        releasePlayer()
        
        // 带宽计量器优化
        val bandwidthMeter = DefaultBandwidthMeter.Builder(HopeBaseApp.getContext())
            .setInitialBitrateEstimate(10_000_000)
            .build()
        
        // 轨道选择器优化
        val trackSelector = DefaultTrackSelector(HopeBaseApp.getContext()).apply {
            setParameters(buildUponParameters().apply {
                setAllowAudioMixedChannelCountAdaptiveness(true)
                setAllowVideoMixedMimeTypeAdaptiveness(true)
            })
        }
        
        // 4G内存设备优化的缓冲策略 - 针对本地视频优化
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                2000,  // 最小缓冲：2秒
                10000, // 最大缓冲：10秒
                1000,  // 播放前缓冲：1秒
                2000   // 重新缓冲阈值：2秒
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()
        
        // 构建播放器
        player = ExoPlayer.Builder(HopeBaseApp.getContext())
            .setTrackSelector(trackSelector)
            .setLoadControl(loadControl)
            .setBandwidthMeter(bandwidthMeter)
            .build()
        player?.playWhenReady = false
        
        // 如果有外部监听器，重新绑定
        rebindPlayerListener()
        
        Timber.tag("LocalPlayer").i("ExoPlayer初始化完成，使用ConcatenatingMediaSource实现无缝播放")
    }
    
    /**
     * 设置播放器监听器（由Fragment调用），保存引用以便重建播放器后恢复
     */
    fun setPlayerListener(listener: Player.Listener) {
        internalPlayerListener = listener
        player?.addListener(listener)
    }
    
    /**
     * 重新绑定播放器监听器（initPlayer后自动调用）
     */
    private fun rebindPlayerListener() {
        internalPlayerListener?.let { listener ->
            player?.addListener(listener)
            Timber.tag("LocalPlayer").d("已重新绑定播放器监听器")
        }
    }
    
    /**
     * 同步playIndex为播放器当前实际索引
     */
    fun syncPlayIndex() {
        val currentIndex = player?.currentMediaItemIndex ?: 0
        if (currentIndex != playIndex) {
            Timber.tag("LocalPlayer").d("playIndex同步: $playIndex -> $currentIndex")
            playIndex = currentIndex
        }
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
                    // 根据播放器状态恢复播放，而非简单调play()
                    val state = player?.playbackState
                    if (state == Player.STATE_IDLE) {
                        // IDLE状态play()无效，需要重新加载视频列表
                        val currentList = videoListObs.value
                        if (currentList != null && currentList.isNotEmpty()) {
                            Timber.tag("LocalPlayer").w("checkVideo: 播放器IDLE，重新加载视频列表")
                            playVideoList(currentList)
                        }
                    } else {
                        try {
                            player?.play()
                        } catch (e: Exception) {
                            Timber.i("checkVideo replay ${e.message}")
                            e.printStackTrace()
                        }
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
        }
        Timber.tag("LocalPlayer").i("checkVideo ${list3.size}")
        videoListObs.postValue(list3)
        if (check)
            playVideoList(list3)
    }

    /**
     * 切换到下一个视频
     */
    fun next() {
        if (videoListObs.value.isNullOrEmpty()) {
            Timber.tag("LocalPlayer").i("没有更多视频了")
            return
        }
        
        val currentSize = videoListObs.value!!.size
        
        // 如果只有一个视频，重新播放
        if (currentSize == 1) {
            player?.seekTo(0, 0)
            player?.play()
            Timber.tag("LocalPlayer").d("只有一个视频，重新播放")
            return
        }
        
        // 先同步playIndex为播放器实际位置
        syncPlayIndex()
        
        // 计算下一个索引（自动循环）
        val nextIndex = (playIndex + 1) % currentSize
        
        // 检查播放器状态
        val currentPlayerState = player?.playbackState
        Timber.tag("LocalPlayer").d("next() 调用: 当前索引=$playIndex, 目标索引=$nextIndex, 播放器状态=$currentPlayerState")
        
        when (currentPlayerState) {
            Player.STATE_IDLE -> {
                // 播放器未准备，需要重新加载
                Timber.tag("LocalPlayer").w("播放器未准备(IDLE状态)，重新加载视频列表")
                playIndex = nextIndex
                playVideoList(videoListObs.value!!)
            }
            Player.STATE_BUFFERING -> {
                // 缓冲中，先停止再切换
                Timber.tag("LocalPlayer").w("播放器缓冲中，先停止再切换")
                player?.stop()
                playIndex = nextIndex
                // 使用协程延迟后重新加载，等待READY状态后再seek
                mScope.launch(Dispatchers.Main) {
                    delay(300)
                    playVideoList(videoListObs.value!!)
                    // 等待播放器进入READY状态后再seek，避免在BUFFERING时seek无效
                    var retryCount = 0
                    while (player?.playbackState != Player.STATE_READY && retryCount < 20) {
                        delay(200)
                        retryCount++
                    }
                    if (player?.playbackState == Player.STATE_READY) {
                        player?.seekToDefaultPosition(nextIndex)
                        player?.play()
                        Timber.tag("LocalPlayer").i("缓冲状态下切换完成，索引: $nextIndex")
                    } else {
                        Timber.tag("LocalPlayer").e("缓冲状态下切换超时，播放器未进入READY")
                    }
                }
            }
            else -> {
                // READY 或 ENDED 状态，直接切换
                player?.seekToDefaultPosition(nextIndex)
                player?.playWhenReady = true
                player?.play()
                playIndex = nextIndex
                Timber.tag("LocalPlayer").i("使用seekToDefaultPosition切换到索引: $playIndex")
            }
        }
    }

    /**
     * 切换到上一个视频
     */
    fun pre() {
        if (videoListObs.value == null || videoListObs.value!!.isEmpty()) return
        
        val currentSize = videoListObs.value!!.size
        
        // 如果只有一个视频，重新播放
        if (currentSize == 1) {
            player?.seekTo(0, 0)
            player?.play()
            Timber.tag("LocalPlayer").d("只有一个视频，重新播放")
            return
        }
        
        // 先同步playIndex为播放器实际位置
        syncPlayIndex()
        
        // 计算上一个索引（自动循环）
        val prevIndex = if (playIndex - 1 < 0) currentSize - 1 else playIndex - 1
        
        // 检查播放器状态
        val currentPlayerState = player?.playbackState
        Timber.tag("LocalPlayer").d("pre() 调用: 当前索引=$playIndex, 目标索引=$prevIndex, 播放器状态=$currentPlayerState")
        
        when (currentPlayerState) {
            Player.STATE_IDLE -> {
                Timber.tag("LocalPlayer").w("播放器未准备(IDLE状态)，重新加载视频列表")
                playIndex = prevIndex
                playVideoList(videoListObs.value!!)
            }
            Player.STATE_BUFFERING -> {
                Timber.tag("LocalPlayer").w("播放器缓冲中，先停止再切换")
                player?.stop()
                playIndex = prevIndex
                mScope.launch(Dispatchers.Main) {
                    delay(300)
                    playVideoList(videoListObs.value!!)
                    var retryCount = 0
                    while (player?.playbackState != Player.STATE_READY && retryCount < 20) {
                        delay(200)
                        retryCount++
                    }
                    if (player?.playbackState == Player.STATE_READY) {
                        player?.seekToDefaultPosition(prevIndex)
                        player?.play()
                        Timber.tag("LocalPlayer").i("缓冲状态下切换完成，索引: $prevIndex")
                    } else {
                        Timber.tag("LocalPlayer").e("缓冲状态下切换超时，播放器未进入READY")
                    }
                }
            }
            else -> {
                player?.seekToDefaultPosition(prevIndex)
                player?.playWhenReady = true
                player?.play()
                playIndex = prevIndex
                Timber.tag("LocalPlayer").i("使用seekToDefaultPosition切换到索引: $playIndex")
            }
        }
    }

    private fun setIndex(index: Int) {
        if (videoListObs.value == null) return
        if (index < 0 || index >= videoListObs.value!!.size) return
        val fileBean = videoListObs.value!!.get(index)
        play(fileBean)
    }

    /**
     * 播放指定视频列表（使用 ConcatenatingMediaSource）
     */
    fun playVideoList(videoList: ArrayList<FileBean>) {
        if (videoList.isEmpty()) {
            Timber.w("视频列表为空")
            return
        }
        
        try {
            Timber.tag("LocalPlayer").d("开始播放视频列表，共${videoList.size}个视频")
            
            // 打印第一个视频的详细信息用于诊断
            if (videoList.isNotEmpty()) {
                val firstVideo = videoList[0]
                val fileSize = File(firstVideo.path).length()
                Timber.tag("LocalPlayer").i("第一个视频: ${firstVideo.name}")
                Timber.tag("LocalPlayer").i("文件大小: ${fileSize / (1024 * 1024)}MB")
                Timber.tag("LocalPlayer").i("文件路径: ${firstVideo.path}")
            }
            
            // 创建数据源工厂
            val dataSourceFactory = DefaultDataSource.Factory(HopeBaseApp.getContext())
            
            // 创建 ConcatenatingMediaSource
            concatenatingMediaSource = ConcatenatingMediaSource()
            
            // 添加所有视频到拼接源
            videoList.forEachIndexed { index, fileBean ->
                val uri = Uri.fromFile(File(fileBean.path))
                val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(uri))
                concatenatingMediaSource?.addMediaSource(mediaSource)
                Timber.tag("LocalPlayer").d("添加视频 ${index + 1}/${videoList.size}: ${fileBean.name}")
            }
            
            // 设置给播放器
            player?.setMediaSource(concatenatingMediaSource!!)
            player?.prepare()
            
            // 启用重复模式，实现循环播放
            player?.repeatMode = com.google.android.exoplayer2.Player.REPEAT_MODE_ALL
            Timber.tag("LocalPlayer").d("已启用循环播放模式")
            
            // 根据可见性决定是否播放
            if (isFragmentVisible) {
                player?.playWhenReady = true
                Timber.tag("LocalPlayer").d("Fragment可见，立即播放")
            } else {
                player?.playWhenReady = false
                shouldResumePlay = true
                Timber.tag("LocalPlayer").d("Fragment不可见，准备但不播放")
            }
            
            // 重置缓冲计时（不重置consecutiveBufferTimeouts，保留熔断计数）
            bufferStartTime = System.currentTimeMillis()
            
            Timber.tag("LocalPlayer").i("视频列表加载完成，开始播放")
        } catch (e: Exception) {
            Timber.tag("LocalPlayer").e(e, "加载视频列表失败")
            e.printStackTrace()
        }
    }
    
    /**
     * 播放指定视频（通过seekTo在ConcatenatingMediaSource中切换，不破坏列表）
     */
    fun play(item: FileBean) {
        if (videoListObs.value == null) return
        val targetIndex = videoListObs.value!!.indexOf(item)
        if (targetIndex < 0) return
        playIndex = targetIndex
        
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
        }
        
        try {
            Timber.tag("LocalPlayer").d("开始播放视频: ${item.name}, 索引: $playIndex")
            
            val currentPlayerState = player?.playbackState
            
            if (currentPlayerState == Player.STATE_IDLE || concatenatingMediaSource == null) {
                // 播放器未初始化或没有ConcatenatingMediaSource，加载整个列表
                playVideoList(videoListObs.value!!)
                // 延迟seek到目标位置
                mScope.launch(Dispatchers.Main) {
                    delay(500)
                    player?.seekToDefaultPosition(targetIndex)
                    if (isFragmentVisible) {
                        player?.playWhenReady = true
                    }
                }
            } else {
                // 播放器已有列表，直接seek到目标位置
                player?.seekToDefaultPosition(targetIndex)
                if (isFragmentVisible) {
                    player?.playWhenReady = true
                    player?.play()
                } else {
                    player?.playWhenReady = false
                    shouldResumePlay = true
                }
            }
            
            // 重置缓冲计时
            bufferStartTime = System.currentTimeMillis()
            
            Timber.tag("LocalPlayer").i("播放 ${item.name} - 路径: ${item.path}")
        } catch (e: Exception) {
            Timber.tag("LocalPlayer").e(e, "播放视频时发生错误: ${item.name}")
            e.printStackTrace()
            handlePlayError(item)
        }
    }
    
    /**
     * 处理播放错误
     */
    private fun handlePlayError(failedItem: FileBean) {
        mScope.launch(Dispatchers.Main) {
            try {
                Timber.tag("LocalPlayer").w("播放失败，尝试跳过: ${failedItem.name}")
                delay(500)
                next()
            } catch (e: Exception) {
                Timber.tag("LocalPlayer").e(e, "错误恢复失败")
            }
        }
    }

    fun stop() {
        player?.stop()
    }

    fun release() {
        releasePlayer()
    }
    
    /**
     * 释放播放器资源
     */
    private fun releasePlayer() {
        internalPlayerListener?.let { player?.removeListener(it) }
        player?.release()
        player = null
        concatenatingMediaSource = null
        Timber.tag("LocalPlayer").d("播放器资源已释放")
    }
    
    /**
     * 获取当前播放器
     */
    fun getActivePlayer(): ExoPlayer? {
        return player
    }
    
    /**
     * 设置Fragment可见性状态
     */
    fun setFragmentVisibility(visible: Boolean) {
        isFragmentVisible = visible
        val activePlayer = getActivePlayer()
        val currentState = activePlayer?.playbackState
        val currentPlayWhenReady = activePlayer?.playWhenReady
        
        Timber.tag("LocalPlayer").d("setFragmentVisibility: visible=$visible, state=$currentState, playWhenReady=$currentPlayWhenReady")
        
        if (visible) {
            // Fragment显示，恢复播放
            when (currentState) {
                com.google.android.exoplayer2.Player.STATE_READY -> {
                    // 已准备好，强制恢复播放
                    if (!activePlayer!!.playWhenReady) {
                        activePlayer.playWhenReady = true
                        Timber.tag("LocalPlayer").w("Fragment显示，READY状态但未播放，强制恢复")
                    } else {
                        Timber.tag("LocalPlayer").d("Fragment显示，播放器正常运行")
                    }
                }
                com.google.android.exoplayer2.Player.STATE_BUFFERING -> {
                    // 缓冲中，确保会自动播放
                    if (!activePlayer!!.playWhenReady) {
                        activePlayer.playWhenReady = true
                        Timber.tag("LocalPlayer").w("Fragment显示，缓冲中强制设置播放标记")
                    } else {
                        Timber.tag("LocalPlayer").d("Fragment显示，缓冲中等待就绪")
                    }
                }
                Player.STATE_IDLE -> {
                    // 空闲状态，重新加载视频列表
                    val currentList = videoListObs.value
                    if (currentList != null && currentList.isNotEmpty()) {
                        Timber.tag("LocalPlayer").w("Fragment显示且播放器空闲，重新加载视频列表")
                        playVideoList(currentList)
                    }
                }
                com.google.android.exoplayer2.Player.STATE_ENDED -> {
                    // 播放结束，重新开始
                    Timber.tag("LocalPlayer").w("Fragment显示且播放已结束，从头开始")
                    activePlayer?.seekTo(0, 0)
                    activePlayer?.playWhenReady = true
                }
                else -> {
                    Timber.tag("LocalPlayer").w("Fragment显示，未知播放器状态: $currentState")
                }
            }
            shouldResumePlay = false
        } else {
            // Fragment隐藏，暂停播放
            activePlayer?.playWhenReady = false
            shouldResumePlay = true
            Timber.tag("LocalPlayer").d("Fragment隐藏，暂停播放")
        }
    }

    /**
     * 静音控制
     */
    fun mute(isMute: Boolean) {
        val activePlayer = getActivePlayer()
        if (isMute)
            activePlayer?.volume = 0f
        else 
            activePlayer?.volume = 1.0f
    }
    
    /**
     * 检查视频文件兼容性
     */
    fun checkVideoCompatibility(filePath: String): Boolean {
        return try {
            val file = File(filePath)
            if (!file.exists()) {
                Timber.w("视频文件不存在: $filePath")
                return false
            }
            
            // 检查文件大小
            val fileSize = file.length()
            if (fileSize > 2L * 1024 * 1024 * 1024) {
                Timber.w("视频文件过大: ${fileSize / (1024 * 1024)}MB")
            }
            
            // 检查文件扩展名
            val extension = filePath.substringAfterLast('.', "").lowercase()
            val supportedExtensions = listOf("mp4", "avi", "mkv", "mov", "wmv", "flv", "webm", "3gp")
            
            if (!supportedExtensions.contains(extension)) {
                Timber.w("不支持的视频格式: .$extension")
                return false
            }
            
            when (extension) {
                "mkv" -> Timber.d("MKV 格式视频，可能存在兼容性问题")
                "avi" -> Timber.d("AVI 格式视频，可能存在兼容性问题")
            }
            
            true
        } catch (e: Exception) {
            Timber.e(e, "检查视频兼容性时出错")
            false
        }
    }
    
    /**
     * 记录缓冲事件
     */
    fun recordBufferEvent() {
        val currentTime = System.currentTimeMillis()
        
        if (currentTime - lastBufferTime > BUFFER_WINDOW_MS) {
            recentBufferCount = 0
        }
        
        recentBufferCount++
        lastBufferTime = currentTime
        
        // 获取当前视频信息用于诊断
        val currentVideoName = if (videoListObs.value != null && playIndex < videoListObs.value!!.size) {
            videoListObs.value!![playIndex].name
        } else "未知"
        
        Timber.tag("LocalPlayer").d("缓冲事件 #${recentBufferCount} (30秒窗口内) - 视频: $currentVideoName, 索引: $playIndex")
        
        if (recentBufferCount > 5) {
            Timber.tag("LocalPlayer").e("严重警告: 30秒内缓冲${recentBufferCount}次！")
            Timber.tag("LocalPlayer").e("可能原因: 1.视频编码问题 2.分辨率过高 3.硬件解码失败 4.存储读取慢")
            Timber.tag("LocalPlayer").e("建议: 检查视频格式、降低分辨率、或强制软件解码")
        }
    }
    
    /**
     * 检查缓冲是否超时
     */
    fun checkBufferTimeout(currentTime: Long = System.currentTimeMillis()): Boolean {
        if (bufferStartTime <= 0) return false
        
        val bufferDuration = currentTime - bufferStartTime
        
        // 本地文件缓冲超过6秒认为有问题
        if (bufferDuration > 6000) {
            consecutiveBufferTimeouts++
            Timber.tag("LocalPlayer").e("缓冲超时: ${bufferDuration}ms，连续超时次数: $consecutiveBufferTimeouts")
            
            if (consecutiveBufferTimeouts >= MAX_BUFFER_TIMEOUTS) {
                Timber.tag("LocalPlayer").e("连续缓冲超时${consecutiveBufferTimeouts}次，跳过当前视频")
                bufferStartTime = 0
                consecutiveBufferTimeouts = 0
                return true
            }
            
            bufferStartTime = 0
            return true
        }
        return false
    }
    
    /**
     * 重置缓冲超时计数器
     */
    fun resetBufferTimeoutCounter() {
        consecutiveBufferTimeouts = 0
        Timber.tag("LocalPlayer").d("重置缓冲超时计数器")
    }

}
