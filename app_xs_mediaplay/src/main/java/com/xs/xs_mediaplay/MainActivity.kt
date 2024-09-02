package com.xs.xs_mediaplay

import android.Manifest
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import com.xs.xs_mediaplay.databinding.ActivityMainBinding
import com.xs.xs_mediaplay.vm.MainViewModel
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.network.NetworkCallback
import javax.inject.Inject
import androidx.databinding.Observable
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.nbhope.lib_frame.utils.toast.ToastUtil
import com.xs.xs_mediaplay.bean.ThemeBean
import com.xs.xs_mediaplay.constant.BYConstants
import com.xs.xs_mediaplay.dialog.BYTipDialog
import com.xs.xs_mediaplay.service.TmpServiceDelegate

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

    companion object {

        const val REQUEST_CODE = 10085

        var PERMISSIONS = arrayListOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        )

    }

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
        binding.vm = viewModel
        checkPermissions()
//        binding.backBtn.setOnClickListener {
//            finish()
//        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && checkPermissions(false)) {

        }
    }

    override fun initData() {

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

    fun init() {
        //
        viewModel.initData()
    }

    fun checkPermissions(request: Boolean = false): Boolean {
        var hasPermission = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (i in 0 until PERMISSIONS.size) {
                val permission = PERMISSIONS[i]
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    hasPermission = false
                }
            }
            if (!hasPermission && request) {
                //
                this.requestPermissions(PERMISSIONS.toArray() as Array<out String>, REQUEST_CODE)
            }
        }
        return hasPermission
    }
}
