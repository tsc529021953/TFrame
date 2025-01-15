package com.sc.tmp_cw.view

import androidx.databinding.Observable
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource
import com.nbhope.lib_frame.base.BaseBindingFragment
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.nbhope.lib_frame.utils.NetworkUtil
import com.sc.tmp_cw.R
import com.sc.tmp_cw.constant.MessageConstant
import com.sc.tmp_cw.databinding.FragmentStreamMediaBinding
import com.sc.tmp_cw.service.TmpServiceDelegate
import com.sc.tmp_cw.vm.MainViewModel
import com.sc.tmp_cw.vm.StreamMediaViewModel
import timber.log.Timber
import javax.inject.Inject


/**
 * @author  tsc
 * @date  2025/1/8 15:24
 * @version 0.0.0-1
 * @description
 */
class StreamMediaFragment: BaseBindingFragment<FragmentStreamMediaBinding, StreamMediaViewModel>() {

    override var layoutId: Int = R.layout.fragment_stream_media

    @Inject
    override lateinit var viewModel: StreamMediaViewModel

    private var listener = androidx.lifecycle.Observer<Any> {
        it as RemoteMessageEvent
        when (it.cmd) {
            MessageConstant.SERVICE_INIT_SUCCESS -> {
                initListener()
            }
        }
    }

    override fun linkViewModel() {

    }

    override fun subscribeUi() {

    }

    override fun onResume() {
        super.onResume()
        if (!viewModel.rtspURL.isNullOrEmpty())
            viewModel.player?.play()
    }

    override fun onPause() {
        super.onPause()
        viewModel.player?.pause()
    }

    override fun initData() {
        initPlayer()
        LiveEBUtil.regist(RemoteMessageEvent::class.java, this, listener)
        initListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        LiveEBUtil.unRegist(RemoteMessageEvent::class.java, listener)
    }

    private fun initPlayer() {
        viewModel.player = SimpleExoPlayer.Builder(activity!!).build()
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
//                        viewModel.playStatusObs.set(false)
//                        viewModel.next { onIndexChanged() }
                    }
                }
            }
        })


    }

    fun initListener() {
        if (TmpServiceDelegate.service() == null) return
        val callback = {
            if (TmpServiceDelegate.service()!!.rtspUrlObs.get() != viewModel.rtspURL) {
                viewModel.rtspURL = TmpServiceDelegate.service()!!.rtspUrlObs.get().toString()
                onUrlLoaded()
            }
        }
        if (!TmpServiceDelegate.service()!!.rtspUrlObs.get().isNullOrEmpty()) {
            callback.invoke()
        } else if (!viewModel.initListener) {
            viewModel.initListener = true
            TmpServiceDelegate.service()!!.rtspUrlObs.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    callback.invoke()
                }
            })
        }
    }

    fun onUrlLoaded() {
        if (!NetworkUtil.isNetworkAvailable(requireContext())) return
        val mediaSource: MediaSource = RtspMediaSource.Factory().createMediaSource(MediaItem.fromUri(viewModel.rtspURL))
        Timber.i("onUrlLoaded ${viewModel.rtspURL}")
        viewModel.player.prepare(mediaSource)
        viewModel.player.play()
    }
}
