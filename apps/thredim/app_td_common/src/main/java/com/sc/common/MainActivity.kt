package com.sc.common

import android.Manifest
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.network.NetworkCallback
import com.nbhope.lib_frame.utils.AppUtil
import com.sc.common.databinding.ActivityMainBinding
import com.sc.common.vm.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * @author  tsc
 * @date  2024/4/25 17:47
 * @version 0.0.0-1
 * @description

 */
class MainActivity : BaseBindingActivity<ActivityMainBinding, MainViewModel>() {

    companion object {

        const val REQUEST_CODE = 10085

        var PERMISSIONS = arrayListOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )

        const val PLAY_IMAGE_TIME = 15000L
        const val CTRL_LAYOUT_VIEW_TIME = 10000L

    }

    override var layoutId: Int = R.layout.activity_main

    @Inject
    override lateinit var viewModel: MainViewModel

    @Inject
    lateinit var networkCallback: NetworkCallback

    private var listener = androidx.lifecycle.Observer<Any> {
        it as RemoteMessageEvent
        when (it.cmd) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        System.out.println("onCreate ??? $requestedOrientation ${requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE}")
        if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

    }

    override fun subscribeUi() {
        binding.vm = viewModel
        if (checkPermissions(true)) {
            init()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && checkPermissions(false)) {
            System.out.println("onRequestPermissionsResult ")
            init()
        }
    }

    override fun initData() {
        // 打开某个app
        if (AppUtil.isInstalled(this, "com.android.chrome")) {
            AppUtil.startApp(this, "com.android.chrome")
        } else if (AppUtil.isInstalled(this, "com.hisense.hibrowser"))
            AppUtil.startApp(this, "com.hisense.hibrowser")
        viewModel.mScope.launch {
            delay(1000)
            this@MainActivity.runOnUiThread {
                this@MainActivity.moveTaskToBack(false)
            }
        }
//        this.moveTaskToBack(false)
    }

    override fun linkViewModel() {

    }

    override fun onDestroy() {
        super.onDestroy()

    }

    private fun init() {
        //
        viewModel.initData()
    }

    private fun checkPermissions(request: Boolean = false): Boolean {
        return true
//        var hasPermission = true
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            for (i in 0 until PERMISSIONS.size) {
//                val permission = PERMISSIONS[i]
//                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
//                    hasPermission = false
//                }
//            }
//            if (!hasPermission && request) {
//                //
//                this.requestPermissions(PERMISSIONS.toTypedArray(), REQUEST_CODE)
//            }
//        }
//        return hasPermission
    }
}
