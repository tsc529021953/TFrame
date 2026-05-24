package com.sc.tmp_cw.view

import android.view.Gravity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.nbhope.lib_frame.base.BaseBindingFragment
import com.nbhope.lib_frame.bean.FileBean
import com.nbhope.lib_frame.widget.WrapLinearLayoutManager
import com.sc.tmp_cw.MainActivity
import com.sc.tmp_cw.R
import com.sc.tmp_cw.adapter.FileVideoAdapter
import com.sc.tmp_cw.databinding.FragmentLocalVideoBinding
import com.sc.tmp_cw.vm.LocalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive
import timber.log.Timber
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2025/1/6 19:38
 * @version 0.0.0-2
 * @description 本地视频播放Fragment - 使用ConcatenatingMediaSource实现无缝播放
 */
class LocalFragment: BaseBindingFragment<FragmentLocalVideoBinding, LocalViewModel>(), FileVideoAdapter.FileImgCallback {

    override var layoutId: Int = R.layout.fragment_local_video

    @Inject
    override lateinit var viewModel: LocalViewModel

    var adapter: FileVideoAdapter? = null

    override fun linkViewModel() {

    }


    override fun subscribeUi() {
        binding.listBtn.setOnClickListener {
            binding.drawerLy.openDrawer(Gravity.LEFT)
        }
        adapter = FileVideoAdapter(viewModel.videoListObs.value ?: arrayListOf(), this)
        val ly = WrapLinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rv.adapter = adapter
        binding.rv.layoutManager = ly
        MainActivity.iMain?.show(arrayListOf(MainActivity.TAG_LIST))
        
        // 启动缓冲监控任务
        startBufferMonitor()
    }
    
    /**
     * 启动缓冲监控，检测长时间缓冲并尝试恢复
     */
    private fun startBufferMonitor() {
        viewModel.mScope.launch(Dispatchers.Main) {
            while (isActive) {
                delay(3000)
                
                // 全局熔断检查
                if (viewModel.isRecoveryExhausted()) {
                    continue
                }
                
                val player = viewModel.player
                val currentState = player?.playbackState
                
                if (currentState == Player.STATE_BUFFERING) {
                    if (viewModel.checkBufferTimeout()) {
                        Timber.tag("LocalPlayer").w("检测到缓冲超时，跳过当前视频")
                        try {
                            viewModel.next()
                        } catch (e: Exception) {
                            Timber.tag("LocalPlayer").e(e, "缓冲超时处理失败")
                        }
                    }
                } else {
                    viewModel.resetBufferTimeoutCounter()
                }
            }
        }
    }

    private fun initPlayer() {
        viewModel.initPlayer()
        binding.videoView.player = viewModel.player
        addPlayerListener()
    }
    
    private fun addPlayerListener() {
        // 通过ViewModel注册监听器，重建播放器时会自动重新绑定
        viewModel.setPlayerListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Timber.tag("LocalPlayer").e("onPlayerError: %s", error.message)
                error.printStackTrace()
                
                // 检测Surface/MediaCodec相关错误
                val isSurfaceError = error.message?.contains("dequeueBuffer") == true ||
                                   error.message?.contains("storeMetaDataInBuffers") == true ||
                                   error.errorCode == PlaybackException.ERROR_CODE_IO_UNSPECIFIED ||
                                   error.errorCode == PlaybackException.ERROR_CODE_DECODER_INIT_FAILED ||
                                   error.errorCode == PlaybackException.ERROR_CODE_DECODING_FAILED
                
                val isMediaCodecError = error.message?.contains("MediaCodec") == true ||
                                      error.errorCode == PlaybackException.ERROR_CODE_DECODING_FORMAT_EXCEEDS_CAPABILITIES ||
                                      error.errorCode == PlaybackException.ERROR_CODE_DECODING_FORMAT_UNSUPPORTED ||
                                      error.errorCode == PlaybackException.ERROR_CODE_DECODER_QUERY_FAILED
                
                if (isSurfaceError) {
                    Timber.tag("LocalPlayer").e("检测到Surface/MediaCodec错误，尝试恢复播放")
                    if (!viewModel.isRecoveryExhausted()) {
                        handleSurfaceError()
                    } else {
                        Timber.tag("LocalPlayer").e("恢复已达上限，跳过Surface错误恢复")
                    }
                } else if (isMediaCodecError) {
                    Timber.tag("LocalPlayer").w("检测到MediaCodec错误，快速跳过当前视频")
                    if (!viewModel.isRecoveryExhausted()) {
                        try {
                            viewModel.next()
                        } catch (e: Exception) {
                            Timber.tag("LocalPlayer").e("跳过视频失败: ${e.message}")
                        }
                    }
                } else {
                    if (!viewModel.isRecoveryExhausted()) {
                        try {
                            Timber.tag("LocalPlayer").w("播放错误，尝试跳过当前视频")
                            viewModel.next()
                        } catch (e: Exception) {
                            Timber.tag("LocalPlayer").i("错误播放失败${e.message}")
                        }
                    } else {
                        Timber.tag("LocalPlayer").e("恢复已达上限，不再跳过")
                    }
                }
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState){
                    Player.STATE_BUFFERING-> {
                        val bufferedPercentage = viewModel.player?.bufferedPercentage ?: 0
                        val currentPosition = viewModel.player?.currentPosition ?: 0
                        Timber.tag("LocalPlayer").d("缓冲中... playWhenReady=$playWhenReady, 已缓冲=${bufferedPercentage}%, 位置=${currentPosition}ms")
                        
                        if (viewModel.bufferStartTime == 0L) {
                            viewModel.bufferStartTime = System.currentTimeMillis()
                        }
                        viewModel.recordBufferEvent()
                    }
                    Player.STATE_READY-> {
                        val bufferDuration = if (viewModel.bufferStartTime > 0) {
                            System.currentTimeMillis() - viewModel.bufferStartTime
                        } else 0
                        val duration = viewModel.player?.duration ?: 0
                        val currentPosition = viewModel.player?.currentPosition ?: 0
                        Timber.tag("LocalPlayer").i("准备就绪 (playWhenReady=$playWhenReady, 缓冲耗时: ${bufferDuration}ms, 位置: ${currentPosition}ms/${duration}ms)")

                        viewModel.resetBufferTimeoutCounter()
                        viewModel.bufferStartTime = 0
                        // 播放成功，重置所有恢复计数
                        viewModel.idleRetryCount = 0
                        viewModel.isSwitching = false
                        viewModel.resetRecoveryAttempts()
                        
                        if (bufferDuration > 2000) {
                            Timber.tag("LocalPlayer").w("警告: 初始缓冲时间过长 (${bufferDuration}ms)")
                        }
                        
                        // 关键修复：只要Fragment可见且播放器READY，就确保播放
                        if (viewModel.isFragmentVisible) {
                            if (!playWhenReady) {
                                Timber.tag("LocalPlayer").w("READY状态但playWhenReady=false，强制启动播放")
                                viewModel.player?.play()
                            } else {
                                Timber.tag("LocalPlayer").d("播放器正常播放中")
                            }
                        } else {
                            Timber.tag("LocalPlayer").d("Fragment不可见，暂停播放")
                        }
                    }
                    Player.STATE_ENDED-> {
                        Timber.tag("LocalPlayer").i("播放完成，repeatMode=${viewModel.player?.repeatMode}")
                        viewModel.playStatusObs.set(false)
                        viewModel.resetBufferTimeoutCounter()
                        // ConcatenatingMediaSource + REPEAT_MODE_ALL 会自动循环到第一个视频
                    }
                    Player.STATE_IDLE-> {
                        Timber.tag("LocalPlayer").w("播放器状态: 空闲 (playWhenReady=$playWhenReady)")
                        val videoList = viewModel.videoListObs.value
                        if (videoList != null && videoList.isNotEmpty() && viewModel.isFragmentVisible) {
                            // 全局熔断检查优先于idleRetryCount检查
                            if (viewModel.isRecoveryExhausted()) {
                                Timber.tag("LocalPlayer").e("IDLE状态但全局恢复已达上限，不再自动恢复")
                            } else if (viewModel.idleRetryCount < viewModel.MAX_IDLE_RETRIES) {
                                Timber.tag("LocalPlayer").w("检测到空闲状态，重新加载视频列表 (重试${viewModel.idleRetryCount + 1}/${viewModel.MAX_IDLE_RETRIES})")
                                viewModel.idleRetryCount++
                                viewModel.playVideoList(videoList)
                            } else {
                                Timber.tag("LocalPlayer").e("IDLE重试已达上限，不再自动恢复")
                            }
                        }
                    }
                }
            }
        })
    }
    
    /**
     * 处理Surface错误，尝试恢复播放
     */
    private fun handleSurfaceError() {
        viewModel.mScope.launch(Dispatchers.Main) {
            try {
                Timber.tag("LocalPlayer").w("开始Surface错误恢复流程")

                // 1. 停止当前播放
                viewModel.player?.stop()
                delay(300)

                // 2. 重新初始化播放器（内部会自动rebind监听器并重置恢复计数器）
                Timber.tag("LocalPlayer").d("重新初始化播放器")
                viewModel.initPlayer()
                delay(200)

                // 3. 重新设置Surface
                binding.videoView.player = viewModel.player
                delay(200)

                // 4. 重新加载视频列表并播放
                val currentList = viewModel.videoListObs.value
                if (currentList != null && currentList.isNotEmpty()) {
                    Timber.tag("LocalPlayer").d("重新加载视频列表")
                    viewModel.playVideoList(currentList)

                    // 5. 等待播放器READY后再seek到之前的位置
                    var retryCount = 0
                    while (viewModel.player?.playbackState != Player.STATE_READY && retryCount < 20) {
                        delay(200)
                        retryCount++
                    }
                    val savedIndex = viewModel.playIndex.coerceIn(0, currentList.size - 1)
                    viewModel.player?.seekToDefaultPosition(savedIndex)
                    viewModel.player?.play()

                    Timber.tag("LocalPlayer").i("Surface错误恢复成功，seek到索引: $savedIndex")
                } else {
                    Timber.tag("LocalPlayer").e("恢复失败：视频列表为空")
                }
            } catch (e: Exception) {
                Timber.tag("LocalPlayer").e(e, "Surface错误恢复失败")
                if (!viewModel.isRecoveryExhausted()) {
                    try {
                        viewModel.next()
                    } catch (e2: Exception) {
                        Timber.tag("LocalPlayer").e(e2, "最终恢复也失败")
                    }
                }
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Timber.tag("LocalPlayer").d("onHiddenChanged hidden=$hidden")
        if (!hidden) {
            MainActivity.iMain?.show(arrayListOf(MainActivity.TAG_LIST))
            viewModel.mute(false)
            viewModel.setFragmentVisibility(true)
        } else {
            viewModel.mute(true)
            viewModel.setFragmentVisibility(false)
        }
    }

    override fun onResume() {
        super.onResume()
        Timber.tag("LocalPlayer").d("onResume, isVisible=${this.isVisible}")
        
        // 打印播放器状态用于调试
        val playerState = viewModel.player?.playbackState
        val playWhenReady = viewModel.player?.playWhenReady
        Timber.tag("LocalPlayer").d("onResume时播放器状态: state=$playerState, playWhenReady=$playWhenReady")
        
        // setFragmentVisibility返回true表示已恢复播放，无需checkVideo再恢复
        val recoveredByVisibility = viewModel.setFragmentVisibility(true)
        
        val cb = {
            viewModel.mute(false)
            MainActivity.iMain?.show(arrayListOf(MainActivity.TAG_LIST))
        }
        if (this.isVisible) {
            cb.invoke()
        } else {
            viewModel.mScope.launch {
                delay(1000)
                viewModel.mScope.launch(Dispatchers.Main) {
                    if (this@LocalFragment.isVisible) {
                        cb.invoke()
                    }
                }
            }
        }
        // 只有setFragmentVisibility未成功恢复时，才通过checkVideo尝试恢复
        if (!recoveredByVisibility) {
            viewModel.checkVideo(true, true)
        }
    }

    override fun onPause() {
        super.onPause()
        Timber.tag("LocalPlayer").d("onPause")
        
        viewModel.setFragmentVisibility(false)
        viewModel.mute(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.release()
    }

    override fun initData() {
        viewModel.videoListObs.observe(this) {
            if (it == null) return@observe
            adapter?.setNewInstance(it)
            Timber.tag("LocalPlayer").i("initData 视频列表大小: ${it.size}")
            if (it.size > 0) {
                val playerState = viewModel.player?.playbackState
                if (playerState == Player.STATE_IDLE || playerState == null) {
                    Timber.tag("LocalPlayer").d("initData: 开始加载视频列表")
                    viewModel.playVideoList(it)
                } else {
                    Timber.tag("LocalPlayer").d("initData: 播放器已处于状态 $playerState")
                }
            } else {
                Timber.tag("LocalPlayer").w("initData: 视频列表为空")
                viewModel.stop()
            }
        }
        viewModel.initData()
        initPlayer()
    }

    override fun onItemClick(item: FileBean) {
        viewModel.play(item)
    }

}
