package com.sc.hetest.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.SPUtils
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.common.BasePath
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.nbhope.phmusic.player.AudioFocusManager
import com.nbhope.phmusic.player.SilentPlayer
import com.sc.hetest.R
import com.sc.hetest.bean.InfoItem
import com.sc.hetest.common.HECommon
import com.sc.hetest.common.HEPath
import com.sc.hetest.databinding.ActivityHornBinding
import com.sc.hetest.databinding.ActivityMainBinding
import com.sc.hetest.databinding.ActivityVerInfoBinding
import com.sc.hetest.vm.HornViewModel
import com.sc.hetest.vm.MainViewModel
import com.sc.hetest.vm.VerInfoViewModel
import com.xbh.sdk3.Audio.AudioHelper
import timber.log.Timber
import javax.inject.Inject

/**
 * author: sc
 * date: 2022/12/10
 */
@Route(path = HEPath.HORN_PATH)
class HornActivity : BaseBindingActivity<ActivityHornBinding, HornViewModel>(){

    companion object{
        const val MUSIC_NAME = "4954.wav"
    }

    override var layoutId: Int = R.layout.activity_horn

    var audioHelper = AudioHelper()

    override fun subscribeUi() {
        binding.failTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.HORN_PATH, InfoItem.STATE_FAIL)
            finish()
        }
        binding.successTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.HORN_PATH, InfoItem.STATE_SUCCESS)
            finish()
        }
    }

    override fun initData() {
        viewModel.initData()
        viewModel.volume = audioHelper.volume
        audioHelper.volume = 100
        Timber.i("KTAG volume ${audioHelper.volume} ${viewModel.volume}")
        var fd = assets.openFd(MUSIC_NAME)
        SilentPlayer.play(
            fd,
            AudioFocusManager(this),
            true,
            object : AudioFocusManager.OnAudioFocusChangeListener {
                override fun onAudioFocusChange(focusChange: Int) {
                    releaseMediaPlayer()
                }
            })
    }

    @Inject
    lateinit var spManager: SharedPreferencesManager

    @Inject
    override lateinit var viewModel: HornViewModel
    override fun linkViewModel() {
        binding.vm = viewModel
        viewModel.musicInfo.set("正在播放：$MUSIC_NAME")
    }

    private fun releaseMediaPlayer() {
        SilentPlayer.stopMediaNow()
//        tellTimer?.cancel()
//        tellTimer = null
    }

    override fun onPause() {
        super.onPause()
        releaseMediaPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        audioHelper.volume = viewModel.volume
        releaseMediaPlayer()
    }
}