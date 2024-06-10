package com.xs.xs_by

import android.content.pm.ActivityInfo
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.Toast
import com.xs.xs_by.databinding.ActivityMainBinding
import com.xs.xs_by.vm.MainViewModel
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.network.NetworkCallback
import com.nbhope.lib_frame.network.NetworkCallbackModule
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import androidx.databinding.Observable
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.nbhope.lib_frame.app.AppManager
import com.nbhope.lib_frame.arouterpath.RouterPath
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.nbhope.lib_frame.utils.toast.ToastUtil
import com.xs.xs_by.bean.ThemeBean
import com.xs.xs_by.constant.BYConstants
import com.xs.xs_by.dialog.BYTipDialog
import com.xs.xs_by.service.TmpServiceDelegate
import com.xs.xs_by.service.TmpServiceImpl

/**
 * @author  tsc
 * @date  2024/4/25 17:47
 * @version 0.0.0-1
 * @description
 * 1. wifi/line
 * 2. timer
 * 3. applist
 */
@Route(path = BYConstants.MAIN_VIEW)
class MainActivity : BaseBindingActivity<ActivityMainBinding, MainViewModel>() {

    override var layoutId: Int = R.layout.activity_main

    @Inject
    override lateinit var viewModel: MainViewModel

    @Inject
    lateinit var networkCallback: NetworkCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    override fun subscribeUi() {
//        networkCallback.registNetworkCallback(networkCallbackModule)
//        viewModel.startAnimation(binding.textTv)
        binding.vm = viewModel
        binding.titleTv.text = MainViewModel.themeBean.name
        binding.stateSc.setOnCheckedChangeListener { p0, p1 ->
            if (p0.isPressed) {
                BYTipDialog.showInfoTip(this@MainActivity,
                    p1,
                    if (p1) resources.getString(R.string.title_open) else resources.getString(R.string.title_close),
                    if (p1) resources.getString(R.string.dialog_tip_by_ok_btn_bg_open_title) else resources.getString(R.string.dialog_tip_by_ok_btn_bg_close_title),
                    resources.getString(R.string.text_cancel),
                    resources.getString(R.string.text_sure),
                    callBack = {
                        viewModel.themeStateObs.set(p1)
                        ctrlTheme(MainViewModel.themeBean.id, p1)
                        return@showInfoTip true
                    }, cancelCallBack = {
                        binding.stateSc.isChecked = !p1
                        return@showInfoTip true
                    })
            }
        }
        binding.centerIv.setOnClickListener {
            if (viewModel.themeStateObs.get()) {
                ARouter.getInstance().build(BYConstants.ONE_CTRL).navigation(this)
            } else {
                ToastUtil.showS("请开启主题后使用！")

            }
        }
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    override fun initData() {
        viewModel.initData()
        viewModel.themeStateObs.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(p0: Observable?, p1: Int) {
//                if (p0. == viewModel.themeStateObs.get()) return
                if (viewModel.themeStateObs.get()) {
                    viewModel.startAnimation(binding.centerIv)
                } else viewModel.pauseAnimation(binding.centerIv)
            }
        })
        getList()
        LiveEBUtil.regist(RemoteMessageEvent::class.java, this, androidx.lifecycle.Observer<Any> {
            it as RemoteMessageEvent
//            if (!(AppManager.appManager?.topActivity?.toString() ?: "").contains(this.javaClass.simpleName))
//                return@Observer
            when (it.cmd) {
                BYConstants.CMD_THEME -> {
                    // 主题开关
                    val theme = Gson().fromJson(it.data, ThemeBean::class.java)
                    if (binding.stateSc.isChecked != theme.switch)
                        binding.stateSc.isChecked = theme.switch
                    if (viewModel.themeStateObs.get() != theme.switch)
                        viewModel.themeStateObs.set(theme.switch)
                    if (MainViewModel.themeBean.name != theme.name) {
                        MainViewModel.themeBean.name = theme.name
                        binding.titleTv.text = theme.name
                    }
                }
            }
        })
    }

    override fun linkViewModel() {

    }

    override fun onDestroy() {
        super.onDestroy()
//        viewModel.stopAnimation(binding.textTv)
    }

    override fun finish() {
        BYTipDialog.showInfoTip(this,
            true,
            resources.getString(R.string.title_tip),
            resources.getString(R.string.dialog_tip_launcher_close_title),
            resources.getString(R.string.text_cancel),
            resources.getString(R.string.text_sure),
            callBack = {
                TmpServiceDelegate.getInstance().write(BYConstants.CMD_GROUP)
                super.finish()
                return@showInfoTip true
            }, cancelCallBack = {
                super.finish()
                return@showInfoTip true
            })
    }

    private fun getList() {
        // 发送获取列表
        val json = JsonObject()
        json.addProperty("cmd", BYConstants.CMD_LIST)
        TmpServiceDelegate.getInstance().write(json.toString())
    }

    private fun ctrlTheme(id: String, open: Boolean) {
        val json = JsonObject()
        json.addProperty("cmd", BYConstants.CMD_CTRL)
        json.addProperty("type", BYConstants.CMD_THEME)
        json.addProperty("switch", open)
        json.addProperty("id", id)
        TmpServiceDelegate.getInstance().write(json.toString())
    }
}
