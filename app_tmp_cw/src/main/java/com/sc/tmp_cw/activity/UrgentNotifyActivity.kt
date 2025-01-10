package com.sc.tmp_cw.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.nbhope.lib_frame.utils.TimerHandler
import com.sc.tmp_cw.R
import com.sc.tmp_cw.constant.MessageConstant
import com.sc.tmp_cw.databinding.ActivityUrgentNotifyBinding
import com.sc.tmp_cw.vm.UrgentNotifyViewModel
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2025/1/10 15:00
 * @version 0.0.0-1
 * @description
 */
@Route(path = MessageConstant.ROUTH_URGENT_NOTIFY)
class UrgentNotifyActivity : BaseBindingActivity<ActivityUrgentNotifyBinding, UrgentNotifyViewModel>() {

    override var layoutId: Int = R.layout.activity_urgent_notify

    var timerHandler: TimerHandler? = null

    private var listener = androidx.lifecycle.Observer<Any> {
        it as RemoteMessageEvent
        when (it.cmd) {
            MessageConstant.CMD_URGENT_NOTICE -> {

            }
        }
    }

    override fun subscribeUi() {
//        binding.backBtn.setOnClickListener {
//            finish()
//        }
//        binding.bgLy.setOnClickListener {
//            timerHandler?.start()
//        }
//        timerHandler = TimerHandler(5000) {
//            finish()
//        }
//        timerHandler?.start()
    }

    override fun initData() {
        LiveEBUtil.regist(RemoteMessageEvent::class.java, this, listener)
    }

    override fun linkViewModel() {

    }

    override fun onDestroy() {
        super.onDestroy()
        timerHandler?.stop()
        LiveEBUtil.unRegist(RemoteMessageEvent::class.java, listener)
    }

    @Inject
    override lateinit var viewModel: UrgentNotifyViewModel
}
