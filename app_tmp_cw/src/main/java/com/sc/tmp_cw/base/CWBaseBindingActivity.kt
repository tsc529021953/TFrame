package com.sc.tmp_cw.base

import android.view.View
import androidx.databinding.ViewDataBinding
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.nbhope.lib_frame.utils.TimerHandler
import com.sc.tmp_cw.constant.MessageConstant
import com.sc.tmp_cw.databinding.ActivitySceneryBinding
import com.sc.tmp_cw.vm.SceneryViewModel

/**
 * @author  tsc
 * @date  2025/1/22 14:53
 * @version 0.0.0-1
 * @description
 */
abstract class CWBaseBindingActivity<T : ViewDataBinding, VM : BaseViewModel>: BaseBindingActivity<T, VM>() {

    var timerHandler: TimerHandler? = null

    var rootLy: View? = null

    override fun subscribeUi() {
        rootLy?.setOnClickListener {
            reset()
        }
        timerHandler = TimerHandler(MessageConstant.FINISH_TIME) {
            timerHandler?.stop()
            finish()
            // 发送返回主页
            LiveEBUtil.post(RemoteMessageEvent(MessageConstant.CMD_BACK_HOME, ""))
        }
        timerHandler?.start()
    }

    fun reset() {
        timerHandler?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        timerHandler?.stop()
    }
}
