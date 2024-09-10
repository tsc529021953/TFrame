package com.xs.xs_mediaplay

//import com.google.android.exoplayer2.ExoPlayerFactory
//import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import android.Manifest
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.network.NetworkCallback
import com.nbhope.lib_frame.utils.FileUtil
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.nbhope.lib_frame.utils.TimerHandler
import com.xs.xs_mediaplay.constant.MessageConstant
import com.xs.xs_mediaplay.databinding.ActivityMainBinding
import com.xs.xs_mediaplay.vm.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


/**
 * @author  tsc
 * @date  2024/4/25 17:47
 * @version 0.0.0-1
 * @description
 * 1. wifi/line
 * 2. timer
 * 3. applist
 */
//@Route(path = BYConstants.MAIN_VIEW)
class MainActivity : BaseBindingActivity<ActivityMainBinding, MainViewModel>() {

    companion object {

        const val REQUEST_CODE = 10085

        var PERMISSIONS = arrayListOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )

        const val PLAY_IMAGE_TIME = 15000L
        const val CTRL_LAYOUT_VIEW_TIME = 10000L

    }

    override var layoutId: Int = R.layout.activity_main

    @Inject
    override lateinit var viewModel: MainViewModel

    @Inject
    lateinit var networkCallback: NetworkCallback

    private var listener = androidx.lifecycle.Observer<Any> {
        it as RemoteMessageEvent
        when (it.cmd) {
            MessageConstant.CMD_PLAY -> {
                if (!viewModel.playStatusObs.get()) {
                    viewModel.playOrPause()
                }
            }
            MessageConstant.CMD_PAUSE, MessageConstant.CMD_STOP -> {
                if (viewModel.playStatusObs.get()) {
                    viewModel.playOrPause()
                }
            }
            MessageConstant.CMD_PRE, MessageConstant.CMD_UPPER -> {
                viewModel.pre { onIndexChanged() }
            }
            MessageConstant.CMD_NEXT, MessageConstant.CMD_LOWER -> {
                viewModel.pre { onIndexChanged() }
            }
            MessageConstant.CMD_VOICE -> {
                System.out.println("CMD_VOICE ${it.data} ${it.data.toFloatOrNull()}")
                val voice = it.data.toFloatOrNull() ?: return@Observer
                viewModel.player.volume = voice
            }
            MessageConstant.CMD_POSITION -> {
                if (viewModel.isImage == true) return@Observer
                val position = it.data.toLongOrNull() ?: return@Observer
                viewModel.player.seekTo(position)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        System.out.println("onCreate ??? $requestedOrientation ${requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE}")
        if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        viewModel.timerHandler = TimerHandler(PLAY_IMAGE_TIME) {
            viewModel.next { onIndexChanged() }
            System.out.println("onPlayerStateChanged时间到达")
        }
    }

    override fun subscribeUi() {
        binding.vm = viewModel
        if (checkPermissions(true)) {
            init()
        }
        binding.backIv.setOnClickListener {
            finish()
        }
        binding.playIv.setOnClickListener {
            viewModel.playOrPause()
        }
        binding.preIv.setOnClickListener {
            viewModel.pre { onIndexChanged() }
        }
        binding.nextIv.setOnClickListener {
            viewModel.next { onIndexChanged() }
        }
        binding.titleLy.visibility = View.GONE
        binding.ctrlLy.visibility = View.GONE
        val callback = {
            System.out.println("touch ${binding.titleLy.visibility == View.GONE}")
            if (binding.titleLy.visibility == View.GONE) {
                binding.titleLy.visibility = View.VISIBLE
                binding.ctrlLy.visibility = View.VISIBLE
                viewModel.mScope.launch {
                    delay(CTRL_LAYOUT_VIEW_TIME)
                    this@MainActivity.runOnUiThread {
                        binding.titleLy.visibility = View.GONE
                        binding.ctrlLy.visibility = View.GONE
                    }
                }
            }
        }
        binding.imageView.setOnClickListener { callback.invoke() }
        binding.videoView.setOnClickListener { callback.invoke() }
        binding.touchView.setOnClickListener { callback.invoke() }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && checkPermissions(false)) {
            System.out.println("onRequestPermissionsResult ")
            init()
        }
    }

    override fun initData() {
        viewModel.filesObs.observe(this) {
            Timber.i("视频图片数量 ${it.size}")
            viewModel.playIndex = 0
            onIndexChanged()
        }

        viewModel.player = SimpleExoPlayer.Builder(this).build()
        viewModel.player.playWhenReady = true
        binding.videoView.player = viewModel.player
//
        viewModel.player.addListener(object : Player.Listener {
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
                        viewModel.playStatusObs.set(false)
                        viewModel.next { onIndexChanged() }
                    }
                }
            }
        })
        LiveEBUtil.regist(RemoteMessageEvent::class.java, this, listener)
    }

    override fun linkViewModel() {

    }

    override fun onDestroy() {
        super.onDestroy()
//        viewModel.stopAnimation(binding.textTv)
        LiveEBUtil.unRegist(RemoteMessageEvent::class.java, listener)
        viewModel.player.release()
        viewModel.timerHandler?.stop()
    }

    private fun init() {
        //
        viewModel.initData()
    }

    private fun checkPermissions(request: Boolean = false): Boolean {
        var hasPermission = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (i in 0 until PERMISSIONS.size) {
                val permission = PERMISSIONS[i]
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    hasPermission = false
                }
            }
            if (!hasPermission && request) {
                //
                this.requestPermissions(PERMISSIONS.toTypedArray(), REQUEST_CODE)
            }
        }
        return hasPermission
    }

    private fun onIndexChanged() {
        if (viewModel.isImage == true)
            viewModel.timerHandler.stop()
        else if (viewModel.isImage == false) {
            viewModel.player.stop()
            viewModel.playStatusObs.set(false)
        }

        if (viewModel.playIndex >= viewModel.filesObs.value!!.size) return
        val file = viewModel.filesObs.value?.get(viewModel.playIndex) ?: return
        Timber.i("onIndexChanged file ${file.name} ${file.extension} ${FileUtil.VIDEO_EXTENSIONS.contains(file.extension)}")
        if (FileUtil.VIDEO_EXTENSIONS.contains(file.extension)) {
            viewModel.isImage = false
            // 视频播放
            binding.videoView.visibility = View.VISIBLE
            binding.imageView.visibility = View.GONE

            val uri = Uri.fromFile(file)
            var mediaItem = MediaItem.fromUri(uri)
            viewModel.player.setMediaItem(mediaItem)
            viewModel.player.prepare()
            viewModel.player.play()
        } else {
            viewModel.isImage = true
            // 图片显示
            this.runOnUiThread {
                binding.videoView.visibility = View.GONE
                binding.imageView.visibility = View.VISIBLE
                val path = "file://" + file.absolutePath
                Glide.with(binding.imageView)
                    .load(path)
                    .into(binding.imageView)
                // 定时器打开
                viewModel.timerHandler.start()
            }
        }
        viewModel.playStatusObs.set(true)
    }
}
