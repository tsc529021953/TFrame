package com.sc.test

import android.Manifest
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.Observer
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.network.NetworkCallback
import com.sc.test.databinding.ActivityHomeBinding
import com.sc.test.vm.MainViewModel
import timber.log.Timber
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2024/4/25 17:47
 * @version 0.0.0-1
 * @description

 */

class HomeActivity : BaseBindingActivity<ActivityHomeBinding, MainViewModel>() {

    companion object {

        const val REQUEST_CODE = 10085

        var PERMISSIONS = arrayListOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )

        const val PLAY_IMAGE_TIME = 15000L
        const val CTRL_LAYOUT_VIEW_TIME = 10000L

    }

    override var layoutId: Int = R.layout.activity_home

    @Inject
    override lateinit var viewModel: MainViewModel

    @Inject
    lateinit var networkCallback: NetworkCallback

    private var listener = Observer<Any> {
        it as RemoteMessageEvent
        when (it.cmd) {

        }
    }

    override fun initParam(savedInstanceState: Bundle?) {
        super.initParam(savedInstanceState)
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
        Timber.i("$TAG ${Build.VERSION.SDK_INT} ${Build.VERSION.RELEASE}")
        viewModel.readAssetFile(this)
    }

    override fun linkViewModel() {

    }

    override fun onDestroy() {
        super.onDestroy()

    }

    private fun init() {

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
