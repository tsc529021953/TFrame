package com.sc.tmp_cw.view

import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource
import com.nbhope.lib_frame.base.BaseBindingFragment
import com.sc.tmp_cw.R
import com.sc.tmp_cw.databinding.FragmentStreamMediaBinding
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

    override fun linkViewModel() {

    }

    override fun subscribeUi() {
        binding.testTv.setOnClickListener {
            System.out.println("Hi Bro")
        }
    }

    override fun initData() {
        initPlayer()
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

        val mediaSource: MediaSource = RtspMediaSource.Factory().createMediaSource(MediaItem.fromUri(viewModel.rstpURL))
        viewModel.player.prepare(mediaSource)
        viewModel.player.play()
    }
}
