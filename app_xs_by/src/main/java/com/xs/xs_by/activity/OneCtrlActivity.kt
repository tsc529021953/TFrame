package com.xs.xs_by.activity

import android.content.pm.ActivityInfo
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.CompoundButton
import com.xs.xs_by.databinding.ActivityMainBinding
import com.xs.xs_by.vm.MainViewModel
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.network.NetworkCallback
import com.nbhope.lib_frame.network.NetworkCallbackModule
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import androidx.databinding.Observable
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.nbhope.lib_frame.app.AppManager
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.xs.xs_by.R
import com.xs.xs_by.adapter.ViewPagerAdapter
import com.xs.xs_by.bean.OneCtrlBean
import com.xs.xs_by.bean.OneCtrlPage
import com.xs.xs_by.bean.ThemeBean
import com.xs.xs_by.constant.BYConstants
import com.xs.xs_by.databinding.ActivityOneCtrlBinding
import com.xs.xs_by.dialog.BYTipDialog
import com.xs.xs_by.inter.IOneItem
import com.xs.xs_by.service.TmpServiceDelegate
import com.xs.xs_by.vm.OneCtrlViewModel
import org.json.JSONObject

/**
 * @author  tsc
 * @date  2024/4/25 17:47
 * @version 0.0.0-1
 * @description
 * 1. wifi/line
 * 2. timer
 * 3. applist
 */
@Route(path = BYConstants.ONE_CTRL)
class OneCtrlActivity : BaseBindingActivity<ActivityOneCtrlBinding, OneCtrlViewModel>() {

    override var layoutId: Int = R.layout.activity_one_ctrl

    @Inject
    override lateinit var viewModel: OneCtrlViewModel

    @Inject
    lateinit var networkCallback: NetworkCallback

    private var adapter: ViewPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    override fun subscribeUi() {
        binding.vm = viewModel
        binding.backBtn.setOnClickListener {
            finish()
        }
        var list = OneCtrlViewModel.PAGE_LIST
        adapter = ViewPagerAdapter(this, list, binding.centerVp, object : IOneItem {
            override fun onCheckedChanged(var1: OneCtrlBean?, var2: Boolean) {
                if (var1 != null)
                    ctrlScene(var1, var2)
            }
        }, this@OneCtrlActivity)
        binding.centerVp.adapter = adapter
        binding.vpCi.setViewPager(binding.centerVp)
        adapter?.registerAdapterDataObserver(binding.vpCi.adapterDataObserver)
        binding.titleTv.text = MainViewModel.themeBean.name
    }

    override fun initData() {
        viewModel.initData()
        LiveEBUtil.regist(RemoteMessageEvent::class.java, this, androidx.lifecycle.Observer<Any> {
            it as RemoteMessageEvent
            if (!(AppManager.appManager?.topActivity?.toString() ?: "").contains(this.javaClass.simpleName))
                return@Observer
            when (it.cmd) {
                BYConstants.CMD_THEME -> {
                    // 主题开关
                    val theme = Gson().fromJson(it.data, ThemeBean::class.java)
                    if (!theme.switch) {
                        finish()
                    }
                }
                BYConstants.CMD_SCENE -> {
                    val json = JSONObject(it.data)
                    Timber.i("XTAG CMD_SCENE ${it.data}")
                    if (json.has("i") && json.has("j")) {
                        val x = json.getInt("i")
                        val y = json.getInt("j")
                        adapter?.notifyXY(x, y, OneCtrlViewModel.PAGE_LIST[x])
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


    fun ctrlScene(bean: OneCtrlBean, open: Boolean) {
        val json = JsonObject()
        json.addProperty("cmd", BYConstants.CMD_CTRL)
        json.addProperty("type", BYConstants.CMD_SCENE)
        json.addProperty("switch", open)
        json.addProperty("id", bean.id)
        TmpServiceDelegate.getInstance().write2(json.toString())
    }
}
