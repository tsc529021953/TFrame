package com.sc.app_tpm_player

import android.Manifest
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.network.NetworkCallback
import com.nbhope.phfame.utils.VoiceUtil
import com.sc.app_tpm_player.databinding.ActivityHomeBinding
import com.sc.app_tpm_player.vm.MainViewModel
import timber.log.Timber
import java.io.File
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2024/4/25 17:47
 * @version 0.0.0-1
 * @description

 */
class HomeActivity : BaseBindingActivity<ActivityHomeBinding, MainViewModel>() {

    companion object {

        const val REQUEST_CODE = 10085

        var PERMISSIONS = arrayListOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )

        const val PLAY_IMAGE_TIME = 15000L
        const val CTRL_LAYOUT_VIEW_TIME = 10000L
        const val CONTROL_BAR_HIDE_DELAY = 5000L


    }

    override var layoutId: Int = R.layout.activity_home

    @Inject
    override lateinit var viewModel: MainViewModel

    @Inject
    lateinit var networkCallback: NetworkCallback

    private lateinit var gestureDetector: GestureDetector
    private var isControlBarVisible = false
    private val hideHandler = Handler(Looper.getMainLooper())
    private val hideRunnable = Runnable { hideControlBar() }

    private var listener = androidx.lifecycle.Observer<Any> {
        it as RemoteMessageEvent
        when (it.cmd) {

        }
    }

    override fun initParam(savedInstanceState: Bundle?) {
        super.initParam(savedInstanceState)
        setupGestureDetector()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        System.out.println("onCreate requestedOrientation $requestedOrientation $requestedOrientation")
//        if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//        }
    }

    override fun subscribeUi() {
        binding.vm = viewModel
        binding.videoLy.visibility = View.GONE
        binding.backBtn.visibility = View.GONE
        binding.controlBar.visibility = View.GONE
        binding.video1Btn.setOnClickListener {
            ctrlPlayView(true)
            viewModel.play(0)
        }
        binding.video2Btn.setOnClickListener {
            ctrlPlayView(true)
            viewModel.play(1)
        }
        binding.video3Btn.setOnClickListener {
            ctrlPlayView(true)
            viewModel.play(2)
        }
        binding.backBtn.setOnClickListener {
            viewModel.stop()
            ctrlPlayView(false)
        }
        binding.playPauseBtn.setOnClickListener {
            resetHideTimer()
            if (viewModel.playStatusObs.get()) {
                viewModel.pause()
            } else {
                viewModel.play()
            }
        }
        binding.volumeDownBtn.setOnClickListener {
            resetHideTimer()
            VoiceUtil.decVolume(this)
        }
        binding.volumeUpBtn.setOnClickListener {
            resetHideTimer()
            VoiceUtil.incVolume(this)
        }
        binding.rewindBtn.setOnClickListener {
            resetHideTimer()
            viewModel.player?.seekBack()
        }
        binding.fastForwardBtn.setOnClickListener {
            resetHideTimer()
            viewModel.player?.seekForward()
        }
        binding.controlBar.setOnClickListener {
            resetHideTimer()
        }
    }

    fun ctrlPlayView(open: Boolean) {
        if (open) {
            binding.videoLy.visibility = View.VISIBLE
            binding.backBtn.visibility = View.VISIBLE
        } else {
            binding.videoLy.visibility = View.GONE
            binding.backBtn.visibility = View.GONE
        }
    }

    override fun initData() {
        initPlayer()
        viewModel.filesObs.observe(this) {
            Log.i("HomeActivity", "videoFiles: $it")
            if (viewModel.filesObs != null)
                refreshView(viewModel.filesObs.value!!)
        }
        viewModel.initData()
    }

    private fun initPlayer() {
        viewModel.player = ExoPlayer.Builder(this).setSeekBackIncrementMs(5000).setSeekForwardIncrementMs(5000).build()
        viewModel.player?.playWhenReady = true
        binding.videoView.player = viewModel.player

        viewModel.player?.addListener(object : Player.Listener{
            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                //
                Timber.i("onPlayerError %s", error.message)
                error.printStackTrace()
                try {
                    viewModel.stop()
//                    viewModel.next()
                } catch (e: Exception) {
                    Timber.i("错误播放失败${e.message}")
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                viewModel.playStatusObs.set(isPlaying)
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                Timber.d("onPlayerStateChanged $playWhenReady, $playbackState")
                when (playbackState){
                    Player.STATE_BUFFERING-> {
//                        Timber.i("onPlayerStateChanged加载中")
                    }
                    Player.STATE_READY-> {
//                        Timber.i("onPlayerStateChanged准备完成")
                    }
                    Player.STATE_ENDED-> {
                        Timber.i("onPlayerStateChanged播放完成")
                        ctrlPlayView(false)
                    }
                }
            }
        })
    }

    private fun refreshView(files: List<File>) {
        if (files.size > 0) {
            binding.video1Btn.visibility = View.VISIBLE
            binding.video1Btn.text = files[0].nameWithoutExtension
        } else binding.video1Btn.visibility = View.GONE
        if (files.size > 1) {
            binding.video2Btn.visibility = View.VISIBLE
            binding.video2Btn.text = files[1].nameWithoutExtension
        } else binding.video2Btn.visibility = View.GONE
        if (files.size > 2) {
            binding.video3Btn.visibility = View.VISIBLE
            binding.video3Btn.text = files[2].nameWithoutExtension
        } else binding.video3Btn.visibility = View.GONE
    }

    override fun linkViewModel() {

    }

    override fun onBackPressed() {
        if (binding.backBtn.visibility != View.GONE)
            binding.backBtn.performClick()
        else super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel?.release()
        ctrlPlayView(false)
    }

    private fun setupGestureDetector() {
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (e1 != null) {
                    val deltaY = e2.y - e1.y
//                    if (deltaY < -100 && velocityY < 0) {
//                        showControlBar()
//                        return true
//                    }
                    val screenHeight = resources.displayMetrics.heightPixels

                    // 检查起始位置是否在屏幕底部 20% 区域
                    val isInBottomArea = e1.y > screenHeight * 0.8

                    // 检查是否是向上滑动且速度足够快
                    val isSwipeUp = deltaY < -50 && velocityY < -500

                    if (isInBottomArea && isSwipeUp) {
                        showControlBar()
                        return true
                    }
                }
                return super.onFling(e1, e2, velocityX, velocityY)
            }
        })
    }

    private fun showControlBar() {
        if (!isControlBarVisible) {
            binding.controlBar.visibility = View.VISIBLE
            isControlBarVisible = true
            resetHideTimer()
        }
    }

    private fun hideControlBar() {
        if (isControlBarVisible) {
            binding.controlBar.visibility = View.GONE
            isControlBarVisible = false
            hideHandler.removeCallbacks(hideRunnable)
        }
    }

    private fun resetHideTimer() {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, CONTROL_BAR_HIDE_DELAY)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev != null) {
            gestureDetector.onTouchEvent(ev)
        }
        return super.dispatchTouchEvent(ev)
    }
}
