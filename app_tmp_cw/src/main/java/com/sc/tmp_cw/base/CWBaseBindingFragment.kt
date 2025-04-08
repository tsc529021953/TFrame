package com.sc.tmp_cw.base

import android.view.View
import androidx.databinding.ViewDataBinding
import com.nbhope.lib_frame.base.BaseBindingFragment
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.constants.HopeConstants
import com.nbhope.lib_frame.utils.TimerHandler
import com.sc.tmp_cw.constant.MessageConstant
import timber.log.Timber

/**
 * @author  tsc
 * @date  2025/1/22 15:21
 * @version 0.0.0-1
 * @description
 */
abstract class CWBaseBindingFragment<T : ViewDataBinding, VM : BaseViewModel> : BaseBindingFragment<T, VM>() {

    var timerHandler: TimerHandler? = null

    var rootLy: View? = null

    var finish: (() -> Unit)? = null

    override fun subscribeUi() {
        rootLy?.setOnClickListener {
            reset()
        }
        timerHandler = TimerHandler(MessageConstant.FINISH_TIME) {
            try {
                Timber.i("Fragment定时到达 ${this.isVisible}")
                if (this.isVisible) {
                    stop()
                    finish?.invoke()
                }
            } catch (e:Exception) {

            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            System.out.println("定时显示")
            reset()
        } else {
            System.out.println("定时隐藏")
            stop()
        }
    }

    fun reset() {
        if (timerHandler != null && timerHandler!!.interval != MessageConstant.FINISH_TIME) {
            timerHandler!!.interval = MessageConstant.FINISH_TIME
        }
        timerHandler?.start()
    }

    fun stop() {
        timerHandler?.stop()
    }

    override fun onResume() {
        super.onResume()
        reset()
    }

    override fun onPause() {
        super.onPause()
        stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        stop()
    }

}
