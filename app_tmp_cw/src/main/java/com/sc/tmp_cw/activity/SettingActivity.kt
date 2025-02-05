package com.sc.tmp_cw.activity

import android.os.Environment
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.common.BasePath
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.utils.AppUtils
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.nbhope.lib_frame.utils.TimerHandler
import com.nbhope.lib_frame.utils.toast.ToastUtil
import com.sc.tmp_cw.R
import com.sc.tmp_cw.constant.MessageConstant
import com.sc.tmp_cw.databinding.ActivitySettingBinding
import com.sc.tmp_cw.service.TmpServiceDelegate
import com.sc.tmp_cw.vm.SettingViewModel
import timber.log.Timber
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2025/1/10 15:00
 * @version 0.0.0-1
 * @description
 */
@Route(path = MessageConstant.ROUTH_SETTING)
class SettingActivity : BaseBindingActivity<ActivitySettingBinding, SettingViewModel>() {

    companion object {
        const val FINISH_TIME = 30000L
    }

    override var layoutId: Int = R.layout.activity_setting

    private var listener = androidx.lifecycle.Observer<Any> {
        it as RemoteMessageEvent
        when (it.cmd) {
            MessageConstant.CMD_URGENT_NOTICE -> {
                finish()
            }
        }
    }

    override fun subscribeUi() {
        binding.backBtn.setOnClickListener {
            finish()
        }
        binding.priorityTv.setOnClickListener {
            ToastUtil.showS(R.string.no_make)
        }
        binding.localResTv.setOnClickListener {
            // 打开本地资源位置
            AppUtils.openRockExplorer(this, Environment.getExternalStorageDirectory().absolutePath + MessageConstant.PATH_BASE_FILE)
        }
        binding.logTv.setOnClickListener {
//            ToastUtil.showS(R.string.no_make)
            // 打开日志位置
            ARouter.getInstance().build(BasePath.LOG_PATH).navigation(this)
        }
        binding.exSaveTv.setOnClickListener {
            // 打开文件管理器
            AppUtils.openRockExplorer(this)
        }
        binding.autoTv.setOnClickListener {
            ToastUtil.showS(R.string.no_make)
        }
        binding.paramTv.setOnClickListener {
            ARouter.getInstance().build(MessageConstant.ROUTH_PARAM).navigation(this)
        }
        binding.videoTv.setOnClickListener {
            ARouter.getInstance().build(MessageConstant.ROUTH_PLAYLIST).navigation(this)
        }
        binding.systemTv.setOnClickListener {
            AppUtils.openSetting(this)
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

    @Inject
    override lateinit var viewModel: SettingViewModel
}
