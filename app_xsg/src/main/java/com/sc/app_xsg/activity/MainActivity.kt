package com.sc.app_xsg.activity

import android.Manifest
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.sc.app_xsg.R
import com.sc.lib_frame.utils.PermissionUtil
import com.sc.lib_frame.utils.ViewUtil.Companion.immersionTitle
import com.sc.lib_frame.utils.json.BaseGsonUtils
import com.sc.app_xsg.app.AppHope.Companion.TAG
import com.sc.app_xsg.databinding.ActivityHomeBinding
import com.sc.app_xsg.databinding.ActivityMainBinding
import com.sc.app_xsg.vm.HomeViewModel
import com.sc.app_xsg.vm.MainViewModel
import com.sc.lib_frame.app.HopeBaseApp
import com.sc.lib_frame.base.BaseBindingActivity
import com.sc.lib_local_device.common.DeviceCommon
import com.sc.lib_frame.common.BasePath
import timber.log.Timber

/**
 * @author  tsc
 * @date  2022/8/1 13:12
 * @version 0.0.0-1
 * @description
 * 1.申请权限
 * 2.读取本地信息 控制/显示
 * 3.读取是否有上次记录的设备
 * 4.开启组播发布模块
 */
class MainActivity : BaseBindingActivity<ActivityMainBinding, MainViewModel>() {

    companion object {
        var PERMISSIONS = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_FINE_LOCATION
            Manifest.permission.READ_PHONE_STATE
        )

        const val REQUEST_CODE = 10086

        const val MESSAGE = "获取天气信息！"
    }

    var isRequestPER = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isRequestPER = false
        // UI
        setContentView(R.layout.activity_main)
        immersionTitle(this)
        findViewById<Button>(R.id.view_btn).setOnClickListener {
//            DeviceCommon.saveDeviceType(AppHope. ,DeviceCommon.DeviceType.View)
            goHome()
        }
        findViewById<Button>(R.id.ctrl_btn).setOnClickListener {
            DeviceCommon.deviceType = DeviceCommon.DeviceType.Ctrl
            goHome()
        }

        Timber.i("$TAG onCreate HelloBro!!! XSG")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!PermissionUtil.hasPermissionsGranted(this, PERMISSIONS)) {
                this.requestPermissions(PERMISSIONS, REQUEST_CODE)
                return
            }
        }
        mainInit()
    }

    fun mainInit() {
        if (DeviceCommon.deviceType != DeviceCommon.DeviceType.UN_KNOW) {
            // 隐藏logo界面
            findViewById<ImageView>(R.id.launcher_iv).visibility = View.GONE

        } else {
            // 打开logo界面
            // 延时开启
            var handle = Handler()
            handle.postDelayed(Runnable {
                Timber.i("$TAG 启动动画显示完成 ${DeviceCommon.deviceType} ${BasePath.HOME_PATH}")
                // 跳转界面
                goHome()
            }, 1000)
        }
    }

    fun goHome() {
        // 进入首页
        ARouter.getInstance().build(BasePath.HOME_PATH).navigation(HopeBaseApp.getContext())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 当权限获取到 通知服务获取天气信息
        if (requestCode == REQUEST_CODE) {
            isRequestPER = true
            mainInit()
//            if (!grantResults.contains(PERMISSION_DENIED))
//                LiveEBUtil.post(TMessage(MessageConst.PERMISSION_GET, ""))
//            else moveBack()
        }
        Timber.i("$TAG 获权结果 ${BaseGsonUtils.GsonString(grantResults)}")
    }

    fun moveBack() {
        Timber.i("$TAG 回到后台")
        moveTaskToBack(true)
    }

    override var layoutId: Int = R.layout.activity_main

    override fun subscribeUi() {

    }

    override fun initData() {

    }

    override fun linkViewModel() {
        binding.vm = viewModel
    }

    override lateinit var viewModel: MainViewModel
}