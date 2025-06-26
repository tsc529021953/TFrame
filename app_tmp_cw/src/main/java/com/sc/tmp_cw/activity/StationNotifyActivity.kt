package com.sc.tmp_cw.activity

import androidx.databinding.Observable
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.nbhope.lib_frame.app.AppManager
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.utils.AnimationUtil
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.nbhope.lib_frame.utils.TimerHandler
import com.sc.tmp_cw.R
import com.sc.tmp_cw.constant.MessageConstant
import com.sc.tmp_cw.databinding.ActivityStationNotifyBinding
import com.sc.tmp_cw.databinding.ActivityUrgentNotifyBinding
import com.sc.tmp_cw.service.TmpServiceDelegate
import com.sc.tmp_cw.vm.StationNotifyViewModel
import com.sc.tmp_cw.vm.UrgentNotifyViewModel
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2025/1/10 15:00
 * @version 0.0.0-1
 * @description
 */
@Route(path = MessageConstant.ROUTH_STATION_NOTIFY)
class StationNotifyActivity : BaseBindingActivity<ActivityStationNotifyBinding, StationNotifyViewModel>() {

    companion object {
        const val ANIMATION_TIME = 4000L
    }

    override var layoutId: Int = R.layout.activity_station_notify

    var timerHandler: TimerHandler? = null

    private var listener = androidx.lifecycle.Observer<Any> {
        it as RemoteMessageEvent
        when (it.cmd) {
            MessageConstant.CMD_STATION_NOTICE -> {
                val m = TmpServiceDelegate.service()?.stationNotifyObs?.get()
                Timber.i("CMD_STATION_NOTICE $m ${viewModel.isAnimationEnd}")
                if (m == null || m!! < 0 || m!! > 10 || m!! == 1) {
                    // 动画完成之后再触发
                    if (viewModel.isAnimationEnd)
                        finish()
                } else {
                    refreshAnimation(m)
                }
            }
            MessageConstant.SERVICE_INIT_SUCCESS -> {
                initService()
            }
        }
    }

    override fun subscribeUi() {
        initService()
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

    override fun onResume() {
        super.onResume()
    }

    override fun initData() {
        LiveEBUtil.regist(RemoteMessageEvent::class.java, this, listener)
    }

    override fun linkViewModel() {

    }

    override fun onDestroy() {
        super.onDestroy()
        timerHandler?.stop()
        LiveEBUtil.post(RemoteMessageEvent(MessageConstant.CMD_STATION_NOTIFY_END, ""))
        LiveEBUtil.unRegist(RemoteMessageEvent::class.java, listener)
    }

    @Inject
    override lateinit var viewModel: StationNotifyViewModel

    private fun initService() {
        if (TmpServiceDelegate.service() != null) {
            binding.service = TmpServiceDelegate.service()!!
            val m = TmpServiceDelegate.service()?.stationNotifyObs?.get()
            if (m != null && m!! >= 0 && m!! < 10 && m!! != 1) {
                binding.carIv.post {
                    refreshAnimation(m)
                }
            }
        }
    }

    private fun refreshAnimation(data: Int) {
        System.out.println("refreshAnimation $data")
        when (data) {
            0 -> {
                viewModel.isAnimationEnd = false
                // 移动到中间
                AnimationUtil.setInitPoint(binding.carIv, 0f)
                // 开始动画移动到最右边
                AnimationUtil.slideToEnd(binding.carIv, ANIMATION_TIME) {
//                    finish() // 离站动画结束后
                    val m = TmpServiceDelegate.service()?.stationNotifyObs?.get()
                    Timber.i("stationNotifyObs $m")
                    if (m != null && (m!! < 0 || m!! > 10 || m!! == 1)) {
                        finish()
                    } else {
                        viewModel.isAnimationEnd = true
                    }
                }
            }
            2 -> {
                viewModel.isAnimationEnd = false
                // 移动到最左边
                AnimationUtil.setInitPoint(binding.carIv, -1f, false)

                // 开始动画移动到中间
                AnimationUtil.showSlidingView(binding.carIv, false, ANIMATION_TIME, -1) {
                    val m = TmpServiceDelegate.service()?.stationNotifyObs?.get()
                    if (m != null && (m!! < 0 || m!! > 10 || m!! == 1)) {
                        finish()
                    } else {
                        viewModel.isAnimationEnd = true
//                        viewModel?.mScope.launch {
//                            delay(1000)
//                            if (viewModel.isAnimationEnd && !this@StationNotifyActivity.isFinishing && !this@StationNotifyActivity.isDestroyed) {
//                                this@StationNotifyActivity.runOnUiThread {
//                                    Timber.i("延时关闭动画界面！")
//                                    this@StationNotifyActivity.finish()
//                                }
//                            }
//                        }
                    }
                }
            }
            else -> viewModel.isAnimationEnd = true
        }
    }
}
