package com.xs.xs_by.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.network.NetworkCallback
import timber.log.Timber
import javax.inject.Inject
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.xs.xs_by.R
import com.xs.xs_by.constant.BYConstants
import com.xs.xs_by.databinding.ActivityLauncherBinding
import com.xs.xs_by.service.TmpServiceDelegate
import com.xs.xs_by.vm.LauncherViewModel
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.nbhope.lib_frame.app.AppManager
import com.xs.xs_by.dialog.BYIPDialog
import com.xs.xs_by.dialog.BYTipDialog
import com.xs.xs_by.utils.SPUtils

/**
 * @author  tsc
 * @date  2024/4/25 17:47
 * @version 0.0.0-1
 * @description
 * 1. wifi/line
 * 2. timer
 * 3. applist
 */
class LauncherActivity : BaseBindingActivity<ActivityLauncherBinding, LauncherViewModel>() {

    companion object {

    }

    override var layoutId: Int = R.layout.activity_launcher

    @Inject
    override lateinit var viewModel: LauncherViewModel

    @Inject
    lateinit var networkCallback: NetworkCallback



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    override fun subscribeUi() {
        viewModel.startAnimation(binding.centerIv)
        binding.centerIv.setOnClickListener {
            // 发送通知消息
            TmpServiceDelegate.getInstance().write(BYConstants.CMD_QUERY)
        }
        binding.settingIv.setOnClickListener {
            BYIPDialog.showInfoTip(this,
                BYConstants.ip,
                BYConstants.port,
                BYConstants.ip2,
                BYConstants.port2,
                resources.getString(R.string.text_cancel),
                resources.getString(R.string.text_sure),
                callBack = { ip, port, ip2, port2 ->
                    SPUtils.putValue(this, BYConstants.SP_IP, ip)
                    SPUtils.putValue(this, BYConstants.SP_PORT, port)
                    SPUtils.putValue(this, BYConstants.SP_IP2, ip2)
                    SPUtils.putValue(this, BYConstants.SP_PORT2, port2)
                    BYConstants.ip = ip
                    BYConstants.port = port
                    BYConstants.ip2 = ip2
                    BYConstants.port2 = port2
                    TmpServiceDelegate.getInstance().reBuild()
                    return@showInfoTip true
                }, cancelCallBack = {
                    return@showInfoTip true
                })
        }
    }

    override fun initData() {
        viewModel.initData()
        LiveEBUtil.regist(RemoteMessageEvent::class.java, this, liveRemoteObserver)
    }

    override fun linkViewModel() {

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        Timber.i("XTAG onPause")
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopAnimation(binding.centerIv)
        LiveEBUtil.unRegist(RemoteMessageEvent::class.java, liveRemoteObserver)
    }

    private val liveRemoteObserver = Observer<Any> { it ->
        it as RemoteMessageEvent
//        if (AppManager.appManager.topActivity)
        Timber.i("XTAG aidlMessage ${it?.cmd} $it ${AppManager.appManager?.topActivity}")
        if (!(AppManager.appManager?.topActivity?.toString() ?: "").contains(this.javaClass.simpleName))
            return@Observer
        when (it.cmd) {
            BYConstants.CMD_TABLE -> {
                // 跳转到下一个
                ARouter.getInstance().build(BYConstants.MAIN_VIEW).navigation(this)
            }

            BYConstants.CMD_GROUP -> {
                // 弹出提示框
                BYTipDialog.showInfoTip(this,
                    true,
                    resources.getString(R.string.title_tip),
                    resources.getString(R.string.dialog_tip_launcher_open_title),
                    resources.getString(R.string.text_cancel),
                    resources.getString(R.string.text_sure),
                    callBack = {
                        TmpServiceDelegate.getInstance().write(BYConstants.CMD_TABLE)
                        return@showInfoTip true
                    }, cancelCallBack = {
                        return@showInfoTip true
                    })
            }
        }
    }
}
