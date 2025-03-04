package com.sc.tmp_cw.view

import android.net.Uri
import android.media.MediaPlayer
import android.view.Gravity
import android.view.Surface
import android.view.SurfaceHolder
//import androidx.media3.common.Player
//import androidx.media3.exoplayer.ExoPlayer
//import androidx.media3.exoplayer.SimpleExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
//import com.google.android.exoplayer2.MediaItem
//import com.google.android.exoplayer2.Player
//import com.google.android.exoplayer2.SimpleExoPlayer
import com.nbhope.lib_frame.base.BaseBindingFragment
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.bean.FileBean
import com.nbhope.lib_frame.utils.HopeUtils
import com.nbhope.lib_frame.view.SpaceItemDecoration
import com.nbhope.lib_frame.widget.WrapGridLayoutManager
import com.nbhope.lib_frame.widget.WrapLinearLayoutManager
import com.sc.tmp_cw.MainActivity
import com.sc.tmp_cw.R
import com.sc.tmp_cw.adapter.FileImgAdapter
import com.sc.tmp_cw.adapter.FileVideoAdapter
import com.sc.tmp_cw.constant.MessageConstant
import com.sc.tmp_cw.databinding.FragmentLocalVideoBinding
import com.sc.tmp_cw.vm.IntroduceViewModel
import com.sc.tmp_cw.vm.LocalViewModel
import com.sc.tmp_cw.vm.TravelViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.io.File
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2025/1/6 19:38
 * @version 0.0.0-1
 * @description
 */
class LocalFragment: BaseBindingFragment<FragmentLocalVideoBinding, LocalViewModel>(), FileVideoAdapter.FileImgCallback, SurfaceHolder.Callback {

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
//        val line = resources.getDimension(R.dimen.travel_btn_ly_space).toInt()
//        binding.rv.addItemDecoration(SpaceItemDecoration(arrayListOf(line, 0, line, line)))
        binding.rv.adapter = adapter
        binding.rv.layoutManager = ly
        MainActivity.iMain?.show(arrayListOf(MainActivity.TAG_LIST))
    }

    private fun initPlayer() {
//    /*mediaplay*/
//        viewModel.surfaceHolder = binding.videoView.holder
//        viewModel.surfaceHolder?.addCallback(this)
//        viewModel.player = MediaPlayer()
//        viewModel.player?.setOnPreparedListener(MediaPlayer.OnPreparedListener { mp -> mp.start() })

        viewModel.player = IjkMediaPlayer()
        binding.videoView.holder.addCallback(this)
        viewModel.player?.setOnCompletionListener {
            viewModel.playStatusObs.set(false)
            try {
                viewModel.player?.reset()
                viewModel.player?.setDisplay(viewModel.surfaceHolder)
            } catch (e: Exception) {}

            viewModel.next()
        }
        viewModel.player?.setOnErrorListener(object : IMediaPlayer.OnErrorListener{
            override fun onError(p0: IMediaPlayer?, p1: Int, p2: Int): Boolean {
                Timber.i("player error $p1 $p2")
                return true
            }
        })
        viewModel.player?.setSpeed(2f)

//        viewModel.player = ExoPlayer.Builder(requireContext()).build()
//        viewModel.player!!.playWhenReady = true
//        binding.videoView.player = viewModel.player
////
//        viewModel.player!!.addListener(object : Player.Listener {
//
//            override fun onPlaybackStateChanged(playbackState: Int) {
//                super.onPlaybackStateChanged(playbackState)
//                when (playbackState) {
//                    Player.STATE_BUFFERING -> {
////                        Timber.i("onPlayerStateChanged加载中")
//                    }
//
//                    Player.STATE_READY -> {
////                        Timber.i("onPlayerStateChanged准备完成")
//                    }
//
//                    Player.STATE_ENDED -> {
//                        Timber.i("onPlayerStateChanged播放完成")
//                        viewModel.playStatusObs.set(false)
//                        viewModel.next()
//                    }
//                }
//            }
//            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
////                Timber.d("onPlayerStateChanged $playWhenReady, $playbackState")
//                when (playbackState){
//                    Player.STATE_BUFFERING-> {
////                        Timber.i("onPlayerStateChanged加载中")
//                    }
//                    Player.STATE_READY-> {
////                        Timber.i("onPlayerStateChanged准备完成")
//                    }
//                    Player.STATE_ENDED-> {
//                        Timber.i("onPlayerStateChanged播放完成")
//                        viewModel.playStatusObs.set(false)
//                        viewModel.next()
//                    }
//                }
//            }
//        })
//        viewModel.player!!.setPlaybackSpeed(2f)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        System.out.println("local onHiddenChanged $hidden")
        if (!hidden) {
            MainActivity.iMain?.show(arrayListOf(MainActivity.TAG_LIST))
            viewModel.mute(false)
            viewModel.checkVideo()
        } else viewModel.mute(true)
    }

    override fun onResume() {
        super.onResume()
        System.out.println("local onResume ${this.isVisible}")
//        if (adapter != null && adapter!!.data.size > 0) {
//            viewModel.player?.play()
//        }
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
        viewModel.checkVideo(true, true)
    }


    override fun onPause() {
        super.onPause()
        System.out.println("local onPause ")
//        if (adapter != null && adapter!!.data.size > 0) {
//            viewModel.player?.pause()
//        }
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
            if (it.size > 0) {
                viewModel.play(it[0])
            } else viewModel.stop()
        }
        viewModel.initData()
        initPlayer()
    }

    override fun onItemClick(item: FileBean) {
        viewModel.play(item)
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
//        val surface: Surface = p0.surface
//        viewModel.player?.setSurface(surface)
        viewModel.surfaceHolder = p0
        viewModel.player?.setDisplay(p0)
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {

    }


}
