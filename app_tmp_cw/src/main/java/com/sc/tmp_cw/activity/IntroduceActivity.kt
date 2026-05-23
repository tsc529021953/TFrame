package com.sc.tmp_cw.activity

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.bean.FileBean
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.utils.FileUtil
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.nbhope.lib_frame.utils.TimerHandler
import com.sc.tmp_cw.R
import com.sc.tmp_cw.base.CWBaseBindingActivity
import com.sc.tmp_cw.constant.MessageConstant
import com.sc.tmp_cw.databinding.ActivityIntroduceBinding
import com.sc.tmp_cw.service.TmpServiceDelegate
import com.sc.tmp_cw.vm.IntroduceViewModel
import com.sc.tmp_cw.vm.SceneryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2025/1/10 15:00
 * @version 0.0.0-1
 * @description
 */
@Route(path = MessageConstant.ROUTH_INTRODUCE)
class IntroduceActivity : CWBaseBindingActivity<ActivityIntroduceBinding, IntroduceViewModel>() {

    companion object {
    }

    override var layoutId: Int = R.layout.activity_introduce

    private var listener = androidx.lifecycle.Observer<Any> {
        it as RemoteMessageEvent
        when (it.cmd) {
            MessageConstant.CMD_URGENT_NOTICE -> {
//                finish()
            }
        }
    }

    override fun subscribeUi() {
        binding.backBtn.setOnClickListener {
            finish()
        }
        super.rootLy = binding.bgLy
        super.subscribeUi()
    }

    override fun initData() {
        binding.vm = viewModel
        LiveEBUtil.regist(RemoteMessageEvent::class.java, this, listener)
        if (IntroduceViewModel.fileBean == null) return
        
        // 使用 post 确保视图已经准备好
        binding.root.post {
            loadContent()
        }
    }
    
    private fun loadContent() {
        // 判断大图有没有
        val callback = { item: FileBean ->
            val path = "file://" + item.path
            // Glide 会自动在后台线程加载,不需要 runOnUiThread
            Glide.with(binding!!.imageView)
                .load(path)
                .override(960, 540) // 限制最大尺寸,避免加载过大的图片
                .centerInside() // 保持比例居中显示
//                .skipMemoryCache(false) // 启用内存缓存
                .skipMemoryCache(true) // 跳过内存缓存
                .diskCacheStrategy(DiskCacheStrategy.NONE) // 禁用磁盘缓存
                .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL) // 启用磁盘缓存
                .placeholder(android.R.color.transparent) // 设置透明占位,避免闪烁
                .into(binding!!.imageView)

            val item3 = IntroduceViewModel.textList?.find { it.name.startsWith(IntroduceViewModel.fileBean!!.name) }
            var msg = resources.getString(R.string.no_introduce)
            if (item3 != null) {
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    try {
                        val content = FileUtil.readFile(item3.path)
                        viewModel.textObs.set(content ?: msg)
                    } catch (e: Exception) {
                        Timber.e(e, "读取文件失败: ${item3.path}")
                        viewModel.textObs.set(msg)
                    }
                }
            } else {
                viewModel.textObs.set(msg)
            }
        }
        val item = IntroduceViewModel.bigList?.find { it.name.startsWith(IntroduceViewModel.fileBean!!.name) }
        if (item == null) {
            // 判断视频有没有
            val item2 = IntroduceViewModel.videoList?.find { it.name.startsWith(IntroduceViewModel.fileBean!!.name) }
            if (item2 != null) {
                viewModel.typeObs.set(false)
                initPlayer()
                play(item2)
            } else {
                callback.invoke(IntroduceViewModel.fileBean!!)
            }
        } else {
            // 显示大图
            callback.invoke(item)
        }
    }

    private fun initPlayer() {
        viewModel.player = SimpleExoPlayer.Builder(this).build()
        viewModel.player!!.playWhenReady = true
        binding.videoView.player = viewModel.player
//
        viewModel.player!!.addListener(object : Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                Timber.d("onPlayerStateChanged $playWhenReady, $playbackState")
                when (playbackState){
                    Player.STATE_BUFFERING-> {
                        Timber.i("onPlayerStateChanged加载中")
                    }
                    Player.STATE_READY-> {
                        Timber.i("onPlayerStateChanged准备完成")
                    }
                    Player.STATE_ENDED-> {
                        Timber.i("onPlayerStateChanged播放完成")
//                        viewModel.playStatusObs.set(false)
//                        viewModel.next { onIndexChanged() }
                    }
                    Player.STATE_IDLE-> {
                        Timber.i("onPlayerStateChanged空闲状态")
                    }
                }
            }
        })
    }

    fun play(item: FileBean) {
        val uri = Uri.fromFile(File(item.path))
        var mediaItem = MediaItem.fromUri(uri)
        viewModel.player!!.setMediaItem(mediaItem)
        viewModel.player!!.prepare()
        viewModel.player!!.play()
    }

    override fun linkViewModel() {

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.player?.release()
        viewModel.textObs.set(resources.getString(R.string.no_introduce))
        LiveEBUtil.unRegist(RemoteMessageEvent::class.java, listener)
    }

    @Inject
    override lateinit var viewModel: IntroduceViewModel
}
