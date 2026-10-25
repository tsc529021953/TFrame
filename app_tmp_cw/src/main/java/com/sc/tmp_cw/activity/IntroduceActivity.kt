package com.sc.tmp_cw.activity

import android.animation.Animator
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.View
import android.widget.TextView
import androidx.databinding.Observable
import androidx.lifecycle.viewModelScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
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
import com.sc.tmp_cw.utils.FlavorConfigUtil
import com.sc.tmp_cw.service.TmpServiceDelegate
import com.sc.tmp_cw.utils.TextReveal
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
        if (FlavorConfigUtil.isFlavorB()) setupSchemeKEnter()
        LiveEBUtil.regist(RemoteMessageEvent::class.java, this, listener)
        if (IntroduceViewModel.fileBean == null) return

        // 使用 post 确保视图已经准备好
        binding.root.post {
            // post回调可能在Activity已销毁后执行(快速返回/定时关闭),此时不能再对已销毁的Activity发起Glide加载
            if (isDestroyed || isFinishing) return@post
            loadContent()
        }
    }
    
    private fun loadContent() {
        // loadContent整体在主线程同步执行,销毁检查可覆盖其内所有Glide.with调用
        if (isDestroyed || isFinishing) return
        // 判断大图有没有
        val callback = { item: FileBean ->
            val path = "file://" + item.path
            // Glide 会自动在后台线程加载,不需要 runOnUiThread
            val glideRequest = Glide.with(binding!!.imageView)
                .load(path)
                .override(960, 540) // 限制最大尺寸,避免加载过大的图片
                .centerInside() // 保持比例居中显示
//                .skipMemoryCache(false) // 启用内存缓存
                .skipMemoryCache(true) // 跳过内存缓存
                .diskCacheStrategy(DiskCacheStrategy.NONE) // 禁用磁盘缓存
                .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL) // 启用磁盘缓存
                .placeholder(android.R.color.transparent) // 设置透明占位,避免闪烁

            // flavorB 时添加圆角
            if (FlavorConfigUtil.isFlavorB()) {
                glideRequest.transform(RoundedCorners(24))
                // 方案K: 先用卡片小图占位,转场落位时即有内容,大图加载完成后自然替换(近似thumb→大图crossfade)
                val thumb = IntroduceViewModel.fileBean
                if (thumb != null && thumb.path.isNotEmpty()) {
                    glideRequest.thumbnail(
                            Glide.with(binding!!.imageView)
                                .load("file://" + thumb.path)
                                .dontAnimate()
                                .transform(RoundedCorners(24)))
                }
            }

            glideRequest.into(binding!!.imageView)

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
        viewModel.titleObs.set(IntroduceViewModel.fileBean!!.name)
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
        // 方案K: 标题与下划线随转场浮现
        if (FlavorConfigUtil.isFlavorB()) startSchemeKTitleReveal()
    }

    // ==================== 方案K: 文字逐步显示(flavorB) ====================

    private val mainHandler = Handler(Looper.getMainLooper())

    /** 进入时间点,用于保证文字浮现不早于页面出现 */
    private var enterAt = 0L

    private val revealAnims = mutableListOf<Animator>()

    /** 正文加载完成(异步)后触发逐字浮现 */
    private val textObsCallback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable, propertyId: Int) {
            mainHandler.post {
                if (isDestroyed || isFinishing) return@post
                val content = viewModel.textObs.get()
                if (content.isNullOrEmpty()) return@post
                // 正文在进入约850ms后开始浮现,加载晚于该点则立即开始
                val delay = (enterAt + 850 - SystemClock.uptimeMillis()).coerceAtLeast(0)
                revealAnims.add(TextReveal.revealText(binding.textView, delay, 1200))
            }
        }
    }

    private fun setupSchemeKEnter() {
        enterAt = SystemClock.uptimeMillis()
        viewModel.textObs.addOnPropertyChangedCallback(textObsCallback)
    }

    /** 标题逐字浮现 + 下划线从左向右生长(main布局无标题区,用tag查找) */
    private fun startSchemeKTitleReveal() {
        val titleTv = binding.root.findViewWithTag<TextView>("reveal_title")
        if (titleTv != null) revealAnims.add(TextReveal.revealText(titleTv, 450, 450))
        val underline = binding.root.findViewWithTag<View>("reveal_underline")
        if (underline != null) TextReveal.growFromLeft(underline, 600, 450)
    }

    // ==================== 方案K END ====================

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
        viewModel.textObs.removeOnPropertyChangedCallback(textObsCallback)
        revealAnims.forEach { it.cancel() }
        revealAnims.clear()
        viewModel.textObs.set(resources.getString(R.string.no_introduce))
        LiveEBUtil.unRegist(RemoteMessageEvent::class.java, listener)
    }

    @Inject
    override lateinit var viewModel: IntroduceViewModel
}
