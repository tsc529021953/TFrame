package com.sc.tmp_cw.activity

import android.net.Uri
import android.os.Environment
import androidx.databinding.Observable
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.utils.AnimationUtil
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.nbhope.lib_frame.utils.TimerHandler
import com.sc.tmp_cw.R
import com.sc.tmp_cw.constant.MessageConstant
import com.sc.tmp_cw.databinding.ActivityStationNotifyBinding
import com.sc.tmp_cw.service.TmpServiceDelegate
import com.sc.tmp_cw.utils.FlavorConfigUtil
import com.sc.tmp_cw.vm.StationNotifyViewModel
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.File
import java.util.*
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2025/1/10 15:00
 * @version 0.0.0-1
 * @description
 */
@Route(path = MessageConstant.ROUTH_STATION_NOTIFY)
class StationNotifyActivity : BaseBindingActivity<ActivityStationNotifyBinding, StationNotifyViewModel>() {

    companion object {
        const val ANIMATION_TIME = 4000L

        /** flavorB 进/出站视频文件名(存放在sdcard/TMP/下,与AppInfo.json同路径): 2=已到站(进站) 0=离站(出站) */
        const val VIDEO_TAG = "station_video"
        const val VIDEO_ENTER = "station_enter.mp4"
        const val VIDEO_EXIT = "station_exit.mp4"
    }

    override var layoutId: Int = R.layout.activity_station_notify

    var timerHandler: TimerHandler? = null

    /** flavorB视频模式: flavorB布局带tag=station_video的PlayerView时启用(main布局无此控件) */
    private var videoView: PlayerView? = null

    private var player: SimpleExoPlayer? = null

    /** 当前正在播放的状态(2进站/0出站),同状态连续信号不重播,播完自然finish */
    private var playingState = -1

    private var listener = androidx.lifecycle.Observer<Any> {
        it as RemoteMessageEvent
        when (it.cmd) {
            MessageConstant.CMD_STATION_NOTICE -> {
                val m = TmpServiceDelegate.service()?.stationNotifyObs?.get()
                Timber.i("CMD_STATION_NOTICE $m ${viewModel.isAnimationEnd}")
                if (videoView != null) {
                    // flavorB: 2到站播进站视频,0离站播出站视频,1预到站只由stationObs自动刷新顶部文案,播完再finish
                    when (m) {
                        0, 2 -> playVideo(m!!)
                        1 -> { /* 预到站(即将到站): 只改文案,不做界面切换 */ }
                        // 无效状态(开门声/安全广播等非报站广播也映射-1): 无视频在播才关闭,避免切断播放中的视频
                        else -> if (playingState == -1) finish()
                    }
                } else if (m == null || m!! < 0 || m!! > 10 || m!! == 1) {
                    // 动画完成之后再触发
                    if (viewModel.isAnimationEnd)
                        finish()
                } else {
                    refreshAnimation(m)
                }
            }
            MessageConstant.SERVICE_INIT_SUCCESS -> {
                initService()
            }
        }
    }

    override fun subscribeUi() {
        initService()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun initData() {
        LiveEBUtil.regist(RemoteMessageEvent::class.java, this, listener)
    }

    override fun linkViewModel() {

    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
        timerHandler?.stop()
        LiveEBUtil.post(RemoteMessageEvent(MessageConstant.CMD_STATION_NOTIFY_END, ""))
        LiveEBUtil.unRegist(RemoteMessageEvent::class.java, listener)
    }

    @Inject
    override lateinit var viewModel: StationNotifyViewModel

    private fun initService() {
        if (TmpServiceDelegate.service() != null) {
            binding.service = TmpServiceDelegate.service()!!
            val m = TmpServiceDelegate.service()?.stationNotifyObs?.get()
            videoView = binding.root.findViewWithTag<PlayerView>(VIDEO_TAG)
            if (videoView != null) {
                // flavorB: 打开时即处于到站/离站状态则直接播对应视频;否则(且无视频在播,如服务重连时状态已推进)关闭
                if (m != null && (m == 0 || m == 2)) {
                    val state = m
                    binding.root.post { playVideo(state) }
                } else if (playingState == -1) {
                    finish()
                }
                return
            }
            if (m != null && m!! >= 0 && m!! < 10 && m!! != 1) {
                binding.carIv.post {
                    refreshAnimation(m)
                }
            } else if (m!! == 1) {
                // 如果打开的时候是即将到达
                viewModel.mScope.launch {
                    delay(ANIMATION_TIME) // 延迟动画时间
                    val m = TmpServiceDelegate.service()?.stationNotifyObs?.get()
                    if (m != 0 && m != 2) {
                        this@StationNotifyActivity.runOnUiThread {
                            Timber.i("关闭即将到达动画")
                            finish()
                        }
                    }
                }
            }
        }
    }

    // ==================== flavorB: 进出站视频 ====================

    private fun playVideo(state: Int) {
        if (isDestroyed || isFinishing) return
        // 同状态连续信号(出站后会持续发送)不重播,视频播完即finish
        if (playingState == state) return
        playingState = state
        if (player == null) {
            player = SimpleExoPlayer.Builder(this).build()
            player!!.playWhenReady = true
            videoView!!.player = player
            player!!.addListener(object : Player.Listener {
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_ENDED -> {
                            // 视频播放完,关闭界面
                            Timber.i("站点视频播放完成 finish")
                            finish()
                        }
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    // 视频缺失/播放失败时直接关闭,避免界面卡住
                    Timber.e("站点视频播放失败 finish: ${error.message}")
                    finish()
                }
            })
        }
        val name = if (state == 2) VIDEO_ENTER else VIDEO_EXIT
        val videoFile = File(Environment.getExternalStorageDirectory().absolutePath + MessageConstant.PATH_BASE_FILE, name)
        if (!videoFile.exists()) {
            // 视频未放到sdcard/TMP/时直接关闭,避免界面卡住
            Timber.e("站点视频不存在 ${videoFile.absolutePath} finish")
            finish()
            return
        }
        Timber.i("播放站点视频 ${videoFile.absolutePath}")
        player!!.setMediaItem(MediaItem.fromUri(Uri.fromFile(videoFile)))
        player!!.prepare()
        player!!.play()
    }

    // ==================== flavorB END ====================

    private fun refreshAnimation(data: Int) {
        System.out.println("refreshAnimation $data")
        when (data) {
            0 -> {
                viewModel.isAnimationEnd = false
                // 移动到中间
//                AnimationUtil.setInitPoint(binding.carIv, -(binding.carIv.width * 0.8f))
                binding.carIv.translationX = -(binding.carIv.width * 0.8f)
                // 开始动画移动到最右边
                AnimationUtil.slideToEnd(binding.carIv, ANIMATION_TIME) {
                    val m = TmpServiceDelegate.service()?.stationNotifyObs?.get()
                    Timber.i("stationNotifyObs $m")
                    if (m != null && (m!! < 0 || m!! > 10 || m!! == 1)) {
                        finish()
                    } else {
                        viewModel.isAnimationEnd = true
                    }
                }
            }
            2 -> {
                viewModel.isAnimationEnd = false
                // 移动到最左边
                AnimationUtil.setInitPoint(binding.carIv, -1f, false)

                // 开始动画移动到中间
                AnimationUtil.showSlidingView(binding.carIv, false, ANIMATION_TIME, -1, percent = 0.2f) {
                    val m = TmpServiceDelegate.service()?.stationNotifyObs?.get()
                    if (m != null && (m!! < 0 || m!! > 10 || m!! == 1)) {
                        finish()
                    } else {
                        viewModel.isAnimationEnd = true
                    }
                }
            }
            else -> viewModel.isAnimationEnd = true
        }
    }
}
