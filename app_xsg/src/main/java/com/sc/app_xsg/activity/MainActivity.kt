package com.sc.app_xsg.activity

import android.Manifest
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.Handler
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.sc.app_xsg.R
import com.sc.lib_frame.utils.PermissionUtil
import com.sc.lib_frame.utils.ViewUtil.Companion.immersionTitle
import com.sc.lib_frame.utils.json.BaseGsonUtils
import com.sc.app_xsg.app.App.Companion.TAG
import com.sc.app_xsg.common.MainPath
import com.sc.lib_frame.base.BaseApp
import com.sc.lib_local_device.common.DeviceCommon
import com.sc.lib_frame.common.BasePath
import com.sc.lib_frame.sp.SharedPreferencesManager
import timber.log.Timber

/**
 * @author  tsc
 * @date  2022/8/1 13:12
 * @version 0.0.0-1
 * @description
 */
@Route(path = BasePath.MAIN_PATH)
class MainActivity : Activity() {

    companion object {
        var PERMISSIONS = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_FINE_LOCATION
        )

        const val REQUEST_CODE = 10086

        const val MESSAGE = "获取天气信息！"
    }

    var isRequestPER = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isRequestPER = false
        setContentView(R.layout.activity_main)
        immersionTitle(this)
        Timber.i("$TAG onCreate HelloBro!!! XSG")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!PermissionUtil.hasPermissionsGranted(this, PERMISSIONS)) {
                this.requestPermissions(PERMISSIONS, REQUEST_CODE)
                return
            }
        }

        //
        mainInit()
    }

    fun mainInit() {
        // 参数读取 设备类型
        DeviceCommon.initDeviceType();

        // 开启
        var handle = Handler()
        handle.postDelayed(Runnable {
            Timber.i("$TAG 启动动画显示完成 ${BaseApp.getContext() == null} ${BasePath.HOME_PATH}")
            // 进入首页
            ARouter.getInstance().build(BasePath.HOME_PATH).navigation(BaseApp.getContext())
        }, 1000)
    }

    override fun onResume() {
        super.onResume()
        // 请求权限
        Timber.i("$TAG onResume")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtil.hasPermissionsGranted(this, PERMISSIONS) || isRequestPER) {
//                moveBack()
            }
        }
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

    fun moveBack(){
        Timber.i("$TAG 回到后台")
        moveTaskToBack(true)
    }
}