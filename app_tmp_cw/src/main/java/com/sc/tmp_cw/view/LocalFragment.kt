package com.sc.tmp_cw.view

import android.net.Uri
import android.view.Gravity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.nbhope.lib_frame.base.BaseBindingFragment
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.bean.FileBean
import com.nbhope.lib_frame.utils.HopeUtils
import com.nbhope.lib_frame.view.SpaceItemDecoration
import com.nbhope.lib_frame.widget.WrapGridLayoutManager
import com.nbhope.lib_frame.widget.WrapLinearLayoutManager
import com.sc.tmp_cw.R
import com.sc.tmp_cw.adapter.FileImgAdapter
import com.sc.tmp_cw.adapter.FileVideoAdapter
import com.sc.tmp_cw.constant.MessageConstant
import com.sc.tmp_cw.databinding.FragmentLocalVideoBinding
import com.sc.tmp_cw.vm.IntroduceViewModel
import com.sc.tmp_cw.vm.LocalViewModel
import com.sc.tmp_cw.vm.TravelViewModel
import timber.log.Timber
import java.io.File
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2025/1/6 19:38
 * @version 0.0.0-1
 * @description
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
//        val line = resources.getDimension(R.dimen.travel_btn_ly_space).toInt()
//        binding.rv.addItemDecoration(SpaceItemDecoration(arrayListOf(line, 0, line, line)))
        binding.rv.adapter = adapter
        binding.rv.layoutManager = ly
    }

    private fun initPlayer() {
        viewModel.player = SimpleExoPlayer.Builder(requireContext()).build()
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
                        viewModel.playStatusObs.set(false)
                        viewModel.next()
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (adapter != null && adapter!!.data.size > 0) {
            viewModel.player?.play()
        }
    }

    override fun onPause() {
        super.onPause()
        if (adapter != null && adapter!!.data.size > 0) {
            viewModel.player?.pause()
        }
    }

    override fun initData() {
        viewModel.videoListObs.observe(this) {
            if (it == null) return@observe
            adapter?.setNewInstance(it)
            if (it.size > 0) {
                viewModel.play(it[0])
            }
        }
        viewModel.initData()
        initPlayer()
    }

    override fun onItemClick(item: FileBean) {
        viewModel.play(item)
    }


}
