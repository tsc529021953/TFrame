package com.sc.xwservice

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.os.Build
import android.os.Bundle
import com.sc.lib_frame.bean.TMessage
import com.sc.lib_frame.utils.LiveEBUtil
import com.sc.lib_frame.utils.PermissionUtil
import com.sc.lib_frame.utils.ViewUtil.Companion.immersionTitle
import com.sc.lib_frame.utils.json.BaseGsonUtils
import com.sc.xwservice.app.App.Companion.TAG
import com.sc.xwservice.config.MessageConst
import timber.log.Timber

/**
 * @author  tsc
 * @date  2022/8/1 13:12
 * @version 0.0.0-1
 * @description
 */
class XWActivity : Activity() {

    companion object {
        var PERMISSIONS = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
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
        Timber.i("$TAG onCreate HelloBro!!! XW")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!PermissionUtil.hasPermissionsGranted(this, PERMISSIONS)) {
                this.requestPermissions(PERMISSIONS, REQUEST_CODE)
                return
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 请求权限
        Timber.i("$TAG onResume")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtil.hasPermissionsGranted(this, PERMISSIONS) || isRequestPER) {
                moveBack()
            }
        } else moveBack()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 当权限获取到 通知服务获取天气信息
        if (requestCode == REQUEST_CODE) {
            isRequestPER = true
            if (!grantResults.contains(PERMISSION_DENIED))
                LiveEBUtil.post(TMessage(MessageConst.PERMISSION_GET, ""))
            else moveBack()
        }
        Timber.i("$TAG 获权结果 ${BaseGsonUtils.GsonString(grantResults)}")
    }

    fun moveBack(){
        Timber.i("$TAG 回到后台")
        moveTaskToBack(true)
    }
}