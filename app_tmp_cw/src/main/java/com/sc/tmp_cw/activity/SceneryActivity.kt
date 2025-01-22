package com.sc.tmp_cw.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.nbhope.lib_frame.utils.TimerHandler
import com.sc.tmp_cw.R
import com.sc.tmp_cw.base.CWBaseBindingActivity
import com.sc.tmp_cw.constant.MessageConstant
import com.sc.tmp_cw.databinding.ActivitySceneryBinding
import com.sc.tmp_cw.service.TmpServiceDelegate
import com.sc.tmp_cw.vm.SceneryViewModel
import timber.log.Timber
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2025/1/10 15:00
 * @version 0.0.0-1
 * @description
 */
@Route(path = MessageConstant.ROUTH_SCENERY)
class SceneryActivity : CWBaseBindingActivity<ActivitySceneryBinding, SceneryViewModel>() {

    override var layoutId: Int = R.layout.activity_scenery

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
        super.rootLy = binding.bgLy
        super.subscribeUi()
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
    override lateinit var viewModel: SceneryViewModel
}
