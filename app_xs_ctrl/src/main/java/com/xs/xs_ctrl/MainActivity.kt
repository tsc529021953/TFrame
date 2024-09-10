package com.xs.xs_ctrl

//import com.google.android.exoplayer2.ExoPlayerFactory
//import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import android.Manifest
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.network.NetworkCallback
import com.nbhope.lib_frame.utils.*
import com.xs.xs_ctrl.constant.MessageConstant
import com.xs.xs_ctrl.databinding.ActivityMainBinding
import com.xs.xs_ctrl.service.TmpServiceDelegate
import com.xs.xs_ctrl.vm.MainViewModel
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
@Route(path = MessageConstant.PATH_MAIN)
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

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        System.out.println("onCreate ??? $requestedOrientation ${requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE}")


    }

    override fun subscribeUi() {
        binding.vm = viewModel
        if (checkPermissions(true)) {
            init()
        }
        binding.backIv.setOnClickListener {
            finish()
        }
        binding.aiLy.setOnClickListener {
            viewModel.isLightViewObs.set(false)
        }
        binding.lightLy.setOnClickListener {
            viewModel.isLightViewObs.set(true)
        }

        initLight()
        initAI()
        initMedia()
    }

    private fun initMedia() {
        binding.media11.voiceIv.setOnClickListener {
            var voice = viewModel.playVolume01Obs.get()
            if (voice == 0) {
                viewModel.playVolume01Obs.set(50)
                voice = 50
            } else {
                viewModel.playVolume01Obs.set(0)
                voice = 0
            }
            writeMedia(MessageConstant.AI_MEDIA_01, MessageConstant.CMD_VOICE + ":${voice / 100.0f}")
        }
        binding.media11.voiceSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                viewModel.playVolume01Obs.set(p0?.progress ?: 0)
                writeMedia(MessageConstant.AI_MEDIA_01, MessageConstant.CMD_VOICE+ ":${(p0?.progress  ?: 0) / 100.0f}")
            }

        })
        binding.media11.preIv.setOnClickListener {  writeMedia(MessageConstant.AI_MEDIA_01, MessageConstant.CMD_PRE) }
        binding.media11.playIv.setOnClickListener {
            if (viewModel.playStatus01Obs.get()) {
                viewModel.playStatus01Obs.set(false)
                writeMedia(MessageConstant.AI_MEDIA_01, MessageConstant.CMD_PAUSE)
            } else {
                viewModel.playStatus01Obs.set(true)
                writeMedia(MessageConstant.AI_MEDIA_01, MessageConstant.CMD_PLAY)
            }
        }
        binding.media11.nextIv.setOnClickListener {  writeMedia(MessageConstant.AI_MEDIA_01, MessageConstant.CMD_NEXT) }

        binding.media21.voiceIv.setOnClickListener {
            var voice = viewModel.playVolume02Obs.get()
            if (voice == 0) {
                viewModel.playVolume02Obs.set(50)
                voice = 50
            } else {
                viewModel.playVolume02Obs.set(0)
                voice = 0
            }
            writeMedia(MessageConstant.AI_MEDIA_02, MessageConstant.CMD_VOICE + ":${voice / 100.0f}")
        }
        binding.media21.voiceSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                viewModel.playVolume02Obs.set(p0?.progress ?: 0)
                writeMedia(MessageConstant.AI_MEDIA_02, MessageConstant.CMD_VOICE+ ":${(p0?.progress  ?: 0) / 100.0f}")
            }

        })
        binding.media21.preIv.setOnClickListener {  writeMedia(MessageConstant.AI_MEDIA_02, MessageConstant.CMD_PRE) }
        binding.media21.playIv.setOnClickListener {
            if (viewModel.playStatus02Obs.get()) {
                viewModel.playStatus02Obs.set(false)
                writeMedia(MessageConstant.AI_MEDIA_02, MessageConstant.CMD_PAUSE)
            } else {
                viewModel.playStatus02Obs.set(true)
                writeMedia(MessageConstant.AI_MEDIA_02, MessageConstant.CMD_PLAY)
            }
        }
        binding.media21.nextIv.setOnClickListener {  writeMedia(MessageConstant.AI_MEDIA_02, MessageConstant.CMD_NEXT) }

        binding.media31.voiceIv.setOnClickListener {
            var voice = viewModel.playVolume03Obs.get()
            if (voice == 0) {
                viewModel.playVolume03Obs.set(50)
                voice = 50
            } else {
                viewModel.playVolume03Obs.set(0)
                voice = 0
            }
            writeMedia(MessageConstant.AI_MEDIA_03, MessageConstant.CMD_VOICE + ":${voice / 100.0f}")
        }
        binding.media31.voiceSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                viewModel.playVolume03Obs.set(p0?.progress ?: 0)
                writeMedia(MessageConstant.AI_MEDIA_03, MessageConstant.CMD_VOICE+ ":${(p0?.progress  ?: 0) / 100.0f}")
            }

        })
        binding.media31.preIv.setOnClickListener {  writeMedia(MessageConstant.AI_MEDIA_03, MessageConstant.CMD_PRE) }
        binding.media31.playIv.setOnClickListener {
            if (viewModel.playStatus03Obs.get()) {
                viewModel.playStatus03Obs.set(false)
                writeMedia(MessageConstant.AI_MEDIA_03, MessageConstant.CMD_PAUSE)
            } else {
                viewModel.playStatus03Obs.set(true)
                writeMedia(MessageConstant.AI_MEDIA_03, MessageConstant.CMD_PLAY)
            }
        }
        binding.media31.nextIv.setOnClickListener {  writeMedia(MessageConstant.AI_MEDIA_03, MessageConstant.CMD_NEXT) }

        binding.media41.voiceIv.setOnClickListener {
            var voice = viewModel.playVolume04Obs.get()
            if (voice == 0) {
                viewModel.playVolume04Obs.set(50)
                voice = 50
            } else {
                viewModel.playVolume04Obs.set(0)
                voice = 0
            }
            writeMedia(MessageConstant.AI_MEDIA_04, MessageConstant.CMD_VOICE + ":${voice / 100.0f}")
        }
        binding.media41.voiceSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                viewModel.playVolume04Obs.set(p0?.progress ?: 0)
                writeMedia(MessageConstant.AI_MEDIA_04, MessageConstant.CMD_VOICE+ ":${(p0?.progress  ?: 0) / 100.0f}")
            }

        })
        binding.media41.preIv.setOnClickListener {  writeMedia(MessageConstant.AI_MEDIA_04, MessageConstant.CMD_PRE) }
        binding.media41.playIv.setOnClickListener {
            if (viewModel.playStatus04Obs.get()) {
                viewModel.playStatus04Obs.set(false)
                writeMedia(MessageConstant.AI_MEDIA_04, MessageConstant.CMD_PAUSE)
            } else {
                viewModel.playStatus04Obs.set(true)
                writeMedia(MessageConstant.AI_MEDIA_04, MessageConstant.CMD_PLAY)
            }
        }
        binding.media41.nextIv.setOnClickListener {  writeMedia(MessageConstant.AI_MEDIA_04, MessageConstant.CMD_NEXT) }

        binding.media51.voiceIv.setOnClickListener {
            var voice = viewModel.playVolume05Obs.get()
            if (voice == 0) {
                viewModel.playVolume05Obs.set(50)
                voice = 50
            } else {
                viewModel.playVolume05Obs.set(0)
                voice = 0
            }
            writeMedia(MessageConstant.AI_MEDIA_05, MessageConstant.CMD_VOICE + ":${voice / 100.0f}")
        }
        binding.media51.voiceSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                viewModel.playVolume05Obs.set(p0?.progress ?: 0)
                writeMedia(MessageConstant.AI_MEDIA_05, MessageConstant.CMD_VOICE+ ":${(p0?.progress  ?: 0) / 100.0f}")
            }

        })
        binding.media51.preIv.setOnClickListener {  writeMedia(MessageConstant.AI_MEDIA_05, MessageConstant.CMD_PRE) }
        binding.media51.playIv.setOnClickListener {
            if (viewModel.playStatus05Obs.get()) {
                viewModel.playStatus05Obs.set(false)
                writeMedia(MessageConstant.AI_MEDIA_05, MessageConstant.CMD_PAUSE)
            } else {
                viewModel.playStatus05Obs.set(true)
                writeMedia(MessageConstant.AI_MEDIA_05, MessageConstant.CMD_PLAY)
            }
        }
        binding.media51.nextIv.setOnClickListener {  writeMedia(MessageConstant.AI_MEDIA_05, MessageConstant.CMD_NEXT) }

        binding.media61.voiceIv.setOnClickListener {
            var voice = viewModel.playVolume06Obs.get()
            if (voice == 0) {
                viewModel.playVolume06Obs.set(50)
                voice = 50
            } else {
                viewModel.playVolume06Obs.set(0)
                voice = 0
            }
            writeMedia(MessageConstant.AI_MEDIA_06, MessageConstant.CMD_VOICE + ":${voice / 100.0f}")
        }
        binding.media61.voiceSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                viewModel.playVolume06Obs.set(p0?.progress ?: 0)
                writeMedia(MessageConstant.AI_MEDIA_06, MessageConstant.CMD_VOICE+ ":${(p0?.progress  ?: 0) / 100.0f}")
            }

        })
        binding.media61.preIv.setOnClickListener {  writeMedia(MessageConstant.AI_MEDIA_06, MessageConstant.CMD_PRE) }
        binding.media61.playIv.setOnClickListener {
            if (viewModel.playStatus06Obs.get()) {
                viewModel.playStatus06Obs.set(false)
                writeMedia(MessageConstant.AI_MEDIA_06, MessageConstant.CMD_PAUSE)
            } else {
                viewModel.playStatus06Obs.set(true)
                writeMedia(MessageConstant.AI_MEDIA_06, MessageConstant.CMD_PLAY)
            }
        }
        binding.media61.nextIv.setOnClickListener {  writeMedia(MessageConstant.AI_MEDIA_06, MessageConstant.CMD_NEXT) }

        binding.media71.voiceIv.setOnClickListener {
            var voice = viewModel.playVolume07Obs.get()
            if (voice == 0) {
                viewModel.playVolume07Obs.set(50)
                voice = 50
            } else {
                viewModel.playVolume07Obs.set(0)
                voice = 0
            }
            writeMedia(MessageConstant.AI_MEDIA_07, MessageConstant.CMD_VOICE + ":${voice / 100.0f}")
        }
        binding.media71.voiceSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                viewModel.playVolume07Obs.set(p0?.progress ?: 0)
                writeMedia(MessageConstant.AI_MEDIA_07, MessageConstant.CMD_VOICE+ ":${(p0?.progress  ?: 0) / 100.0f}")
            }

        })
        binding.media71.preIv.setOnClickListener {  writeMedia(MessageConstant.AI_MEDIA_07, MessageConstant.CMD_PRE) }
        binding.media71.playIv.setOnClickListener {
            if (viewModel.playStatus07Obs.get()) {
                viewModel.playStatus07Obs.set(false)
                writeMedia(MessageConstant.AI_MEDIA_07, MessageConstant.CMD_PAUSE)
            } else {
                viewModel.playStatus07Obs.set(true)
                writeMedia(MessageConstant.AI_MEDIA_07, MessageConstant.CMD_PLAY)
            }
        }
        binding.media71.nextIv.setOnClickListener {  writeMedia(MessageConstant.AI_MEDIA_07, MessageConstant.CMD_NEXT) }
    }

    private fun initAI() {
        binding.aiOpenAll.setOnClickListener {
            viewModel.mScope.launch {
                write1(MessageConstant.AI_OPEN_01, false)
                write2(MessageConstant.AI_OPEN_02, false)
                delay(200)
                write1(MessageConstant.AI_OPEN_03, false)
                delay(200)
                write1(MessageConstant.AI_OPEN_04, false)
                delay(200)
                write1(MessageConstant.AI_OPEN_05, false)
                delay(200)
                write1(MessageConstant.AI_OPEN_06, false)
                delay(200)
                write1(MessageConstant.AI_OPEN_07, false)
                delay(200)
                write1(MessageConstant.AI_OPEN_08, false)
            }
        }
        binding.aiCloseAll.setOnClickListener {
            viewModel.mScope.launch {
                write1(MessageConstant.AI_CLOSE_01, false)
                write2(MessageConstant.AI_CLOSE_02, false)
                delay(200)
                write1(MessageConstant.AI_CLOSE_03, false)
                delay(200)
                write1(MessageConstant.AI_CLOSE_04, false)
                delay(200)
                write1(MessageConstant.AI_CLOSE_05, false)
                delay(200)
                write1(MessageConstant.AI_CLOSE_06, false)
                delay(200)
                write1(MessageConstant.AI_CLOSE_07, false)
                delay(200)
                write1(MessageConstant.AI_CLOSE_08, false)
            }
        }
        binding.device11.openTv.setOnClickListener {
            write1(MessageConstant.AI_OPEN_01)
        }
        binding.device11.closeTv.setOnClickListener {
            write1(MessageConstant.AI_CLOSE_01)
        }
        binding.device12.openTv.setOnClickListener {
            write2(MessageConstant.AI_OPEN_02)
        }
        binding.device12.closeTv.setOnClickListener {
            write2(MessageConstant.AI_CLOSE_02)
        }
        binding.device21.openTv.setOnClickListener {
            write1(MessageConstant.AI_OPEN_03)
        }
        binding.device21.closeTv.setOnClickListener {
            write1(MessageConstant.AI_CLOSE_03)
        }
        binding.device31.openTv.setOnClickListener {
            write1(MessageConstant.AI_OPEN_04)
        }
        binding.device31.closeTv.setOnClickListener {
            write1(MessageConstant.AI_CLOSE_04)
        }
        binding.device41.openTv.setOnClickListener {
            write1(MessageConstant.AI_OPEN_05)
        }
        binding.device41.closeTv.setOnClickListener {
            write1(MessageConstant.AI_CLOSE_05)
        }
        binding.device51.openTv.setOnClickListener {
            write1(MessageConstant.AI_OPEN_06)
        }
        binding.device51.closeTv.setOnClickListener {
            write1(MessageConstant.AI_CLOSE_06)
        }
        binding.device61.openTv.setOnClickListener {
            write1(MessageConstant.AI_OPEN_07)
        }
        binding.device61.closeTv.setOnClickListener {
            write1(MessageConstant.AI_CLOSE_07)
        }
        binding.device71.openTv.setOnClickListener {
            write1(MessageConstant.AI_OPEN_08)
        }
        binding.device71.closeTv.setOnClickListener {
            write1(MessageConstant.AI_CLOSE_08)
        }


    }

    private fun initLight() {
        binding.lightOpenAll.setOnClickListener {
            viewModel.launch {
                write1(MessageConstant.WALL_LIGHT_OPEN, false)
                delay(500)
                write1(MessageConstant.TOP_LIGHT_OPEN)
            }
        }
        binding.lightCloseAll.setOnClickListener {
            viewModel.launch {
                write1(MessageConstant.WALL_LIGHT_CLOSE, false)
                delay(500)
                write1(MessageConstant.TOP_LIGHT_CLOSE)
            }
        }
        binding.wallOpenTv.setOnClickListener {
            write1(MessageConstant.WALL_LIGHT_OPEN)
        }
        binding.wallCloseTv.setOnClickListener {
            write1(MessageConstant.WALL_LIGHT_CLOSE)
        }
        binding.topOpenTv.setOnClickListener {
            write1(MessageConstant.TOP_LIGHT_OPEN)
        }
        binding.topCloseTv.setOnClickListener {
            write1(MessageConstant.TOP_LIGHT_CLOSE)
        }
    }

    private fun write1(msg: String, timer: Boolean = true) {
//        if (!timer) {
//            TmpServiceDelegate.getInstance().write(msg)
//            return
//        }
//        ValueHolder.setValue(200) {
            TmpServiceDelegate.getInstance().write(msg)
//        }
    }

    private fun write2(msg: String, timer: Boolean = true) {
//        if (!timer) {
//            TmpServiceDelegate.getInstance().write2(msg)
//            return
//        }
//        ValueHolder.setValue(200) {
            TmpServiceDelegate.getInstance().write2(msg)
//        }
    }

    private fun writeMedia(ip: String, msg: String, timer: Boolean = true) {
//        if (!timer) {
//            TmpServiceDelegate.getInstance().writeMedia(ip, msg)
//            return
//        }
//        ValueHolder.setValue(200) {
            TmpServiceDelegate.getInstance().writeMedia(ip, msg)
//        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && checkPermissions(false)) {
            System.out.println("onRequestPermissionsResult ")
            init()
        }
    }

    override fun initData() {

        LiveEBUtil.regist(RemoteMessageEvent::class.java, this, listener)
    }

    override fun linkViewModel() {

    }

    override fun onDestroy() {
        super.onDestroy()
        LiveEBUtil.unRegist(RemoteMessageEvent::class.java, listener)
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
}
