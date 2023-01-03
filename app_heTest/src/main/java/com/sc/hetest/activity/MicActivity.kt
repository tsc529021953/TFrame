package com.sc.hetest.activity

import android.Manifest
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Message
import androidx.lifecycle.viewModelScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.SPUtils
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.common.BasePath
import com.nbhope.lib_frame.utils.PermissionUtil
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.nbhope.phmusic.player.AudioFocusManager
import com.nbhope.phmusic.player.SilentPlayer
import com.sc.hetest.R
import com.sc.hetest.bean.InfoItem
import com.sc.hetest.common.HECommon
import com.sc.hetest.common.HEPath
import com.sc.hetest.databinding.ActivityHornBinding
import com.sc.hetest.databinding.ActivityMainBinding
import com.sc.hetest.databinding.ActivityMicBinding
import com.sc.hetest.databinding.ActivityVerInfoBinding
import com.sc.hetest.vm.HornViewModel
import com.sc.hetest.vm.MainViewModel
import com.sc.hetest.vm.MicViewModel
import com.sc.hetest.vm.VerInfoViewModel
import com.sc.lib_audio.audio.AudioUtil
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * author: sc
 * date: 2022/12/10
 */
@Route(path = HEPath.MIC_PATH)
class MicActivity : BaseBindingActivity<ActivityMicBinding, MicViewModel>() {

    companion object {

    }

    private var PERMISSIONS: Array<String> = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.MODIFY_AUDIO_SETTINGS,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override var layoutId: Int = R.layout.activity_mic

    var audioUtil: AudioUtil? = null

    var micImg : Drawable? = null

    override fun subscribeUi() {
        binding.failTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.MIC_PATH, InfoItem.STATE_FAIL)
            finish()
        }
        binding.successTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.MIC_PATH, InfoItem.STATE_SUCCESS)
            finish()
        }
        micImg = binding.micIv.drawable

    }

    override fun initData() {
        viewModel.initData()
        viewModel.musicInfo.set("当前暂无录音权限")

        if (PermissionUtil.hasPermissionsGranted(this, PERMISSIONS)) {
            viewModel.musicInfo.set("已打开录音")
            initAudio()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(PERMISSIONS, 10086)
            viewModel.musicInfo.set("正在申请录音权限")
        }
    }

    var isInit = false

    private fun initAudio() {
        if (isInit) return
        isInit = true
        Timber.i("HETAG initAudio")
        viewModel.viewModelScope.launch {
            var fd = assets.openFd(HornActivity.MUSIC_NAME)
            SilentPlayer.play(
                fd,
                AudioFocusManager(this@MicActivity),
                true,
                object : AudioFocusManager.OnAudioFocusChangeListener {
                    override fun onAudioFocusChange(focusChange: Int) {
                        releaseMediaPlayer()
                    }
                })
            audioUtil = AudioUtil()
            audioUtil?.setOnMicVolumeCallBack(object : Handler.Callback{
                override fun handleMessage(msg: Message): Boolean {
                    binding.micIv.post {
                        val vo = msg.obj as Double
                        micImg?.level = (vo * 50.0 + 3000.0).toInt()
//                    Timber.i("HETAG 当前音量 ${vo}分贝 ${micImg?.level}")
                        viewModel.musicInfo.set("${String.format("%.2f", vo)}分贝")
                    }
                    return true
                }
            })
            audioUtil?.start()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10086) {
            if (PermissionUtil.hasPermissionsGranted(this, PERMISSIONS)) {
                initAudio()
                viewModel.musicInfo.set("权限获取成功，已打开录音")
            }
        }
    }

    @Inject
    lateinit var spManager: SharedPreferencesManager

    @Inject
    override lateinit var viewModel: MicViewModel
    override fun linkViewModel() {
        binding.vm = viewModel
//        viewModel.musicInfo.set("正在播放：$MUSIC_NAME")
    }

    private fun releaseMediaPlayer() {
        SilentPlayer.stopMediaNow()
        audioUtil?.stop()
    }

    override fun onPause() {
        super.onPause()
        releaseMediaPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaPlayer()
    }
}