package com.sc.hetest.activity

import android.Manifest
import android.os.Build
import android.view.View
import androidx.annotation.NonNull
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.SPUtils
import com.google.common.util.concurrent.ListenableFuture
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.common.BasePath
import com.nbhope.lib_frame.utils.PermissionUtil
import com.sc.hetest.R
import com.sc.hetest.bean.InfoItem
import com.sc.hetest.common.HEPath
import com.sc.hetest.databinding.ActivityCamBinding
import com.sc.hetest.vm.InfoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * author: sc
 * date: 2022/12/10
 */
@Route(path = HEPath.MAIN_CAM_PATH)
class CamActivity : BaseBindingActivity<ActivityCamBinding, InfoViewModel>(){

    private var PERMISSIONS: Array<String> = arrayOf(
        Manifest.permission.CAMERA,
    )

    override var layoutId: Int = R.layout.activity_cam

    private lateinit var cameraProvider: ProcessCameraProvider
    private var preview: Preview? = null
    private var camera: Camera? = null

    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null

    override fun subscribeUi() {
        binding.failTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.MAIN_CAM_PATH, InfoItem.STATE_FAIL)
            finish()
        }
        binding.successTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.MAIN_CAM_PATH, InfoItem.STATE_SUCCESS)
            finish()
        }
    }

    override fun initData() {
        viewModel.initData()
        viewModel.info.set("当前暂无摄像头相关权限")
        if (PermissionUtil.hasPermissionsGranted(this, PERMISSIONS)) {
            openCam()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(PERMISSIONS, 10086)
            viewModel.info.set("正在申请摄像头相关权限")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10086) {
            if (PermissionUtil.hasPermissionsGranted(this, PERMISSIONS)) {
                openCam()
            }
        }
    }

    private fun openCam() {
        viewModel.info.set("正在打开摄像头")
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
            ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                if (cameraProvider == null) {
                    viewModel.info.set("未获取到相机！")
                    return@addListener
                }
                viewModel.viewModelScope.launch(Dispatchers.Main) {
                    delay(3000)
                    viewModel.info.set("准备预览")
                    bindPreview(cameraProvider, binding.viewFinder)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindPreview(
        cameraProvider: ProcessCameraProvider,
        previewView: PreviewView
    ) {
        //解除所有绑定，防止CameraProvider重复绑定到Lifecycle发生异常
        try {
            cameraProvider.unbindAll()
            preview = Preview.Builder().build()
            preview?.setSurfaceProvider(previewView.surfaceProvider)
            camera = cameraProvider.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_FRONT_CAMERA, preview
            )
            binding.infoTv.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
            viewModel.info.set("打开前置摄像头失败 \n$e")
        }
    }

    @Inject
    override lateinit var viewModel: InfoViewModel

    override fun linkViewModel() {
        binding.vm = viewModel
    }

    private fun release() {
        cameraProvider.unbindAll()
    }

    override fun onPause() {
        super.onPause()
        release()
    }

    override fun onDestroy() {
        super.onDestroy()
        release()
    }


}