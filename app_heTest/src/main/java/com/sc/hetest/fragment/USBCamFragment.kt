package com.sc.hetest.fragment

import android.R
import android.content.Context
import android.hardware.usb.UsbDevice
import android.os.Handler
import android.os.Message
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.viewModelScope
import com.jiangdg.ausbc.CameraClient
import com.jiangdg.ausbc.MultiCameraClient
import com.jiangdg.ausbc.base.CameraFragment
import com.jiangdg.ausbc.base.MultiCameraFragment
import com.jiangdg.ausbc.callback.ICameraStateCallBack
import com.jiangdg.ausbc.camera.CameraUVC
import com.jiangdg.ausbc.camera.CameraUvcStrategy
import com.jiangdg.ausbc.camera.bean.CameraRequest
import com.jiangdg.ausbc.render.effect.EffectBlackWhite
import com.jiangdg.ausbc.render.env.RotateType
import com.jiangdg.ausbc.widget.AspectRatioTextureView
import com.jiangdg.ausbc.widget.IAspectRatio
import com.sc.hetest.databinding.FragmentUsbCamBinding
import com.sc.hetest.vm.InfoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * author: sc
 * date: 2022/12/13
 */
class USBCamFragment :
//    CameraFragment()
    MultiCameraFragment(), ICameraStateCallBack {

    var viewModel: InfoViewModel? = null

    var callBack: Handler.Callback? = null
    fun setInfoViewModel(viewModel: InfoViewModel, callBack: Handler.Callback) {
        this.viewModel = viewModel
        this.callBack = callBack
    }

    private lateinit var mViewBinding: FragmentUsbCamBinding

    private var showIndex = -1

    private var cameras = ArrayList<MultiCameraClient.ICamera>()
//
//    override fun getCameraView(): IAspectRatio? {
//        return AspectRatioTextureView(requireContext())
//    }
//
//    override fun getCameraViewContainer(): ViewGroup? {
//        return mViewBinding.camLy
//    }
//
//    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View? {
//        mViewBinding = FragmentUsbCamBinding.inflate(inflater, container, false)
//        return mViewBinding.root
//    }
//
//    override fun getGravity(): Int = Gravity.CENTER


//        override fun getCameraClient(): CameraClient? {
//        return CameraClient.newBuilder(requireContext())
//            .setEnableGLES(true)   // use opengl render
//            .setRawImage(true)     // capture raw or filter image
////            .setDefaultEffect(EffectBlackWhite(requireContext())) // default effect
//            .setCameraStrategy(CameraUvcStrategy(requireContext())) // camera type
//            .setCameraRequest(getCameraRequest()) // camera configurations
//            .setDefaultRotateType(RotateType.ANGLE_0) // default camera rotate angle
//            .openDebug(true) // is debug mode
//            .build()
//    }

    private fun getCameraRequest(): CameraRequest {
        return CameraRequest.Builder()
            .setFrontCamera(false) // only for camera1/camera2
//            .setPreviewWidth(640)  // initial camera preview width
//            .setPreviewHeight(360) // initial camera preview height
            .create()
    }

    override fun onCameraAttached(camera: MultiCameraClient.ICamera) {
        // a camera be attached
        Timber.i("HETAG onCameraAttached $camera")
    }

    override fun onCameraDetached(camera: MultiCameraClient.ICamera) {
        // a camera be detached
        Timber.i("HETAG onCameraDetached $camera")
    }

    override fun onCameraConnected(camera: MultiCameraClient.ICamera) {
        // a camera be connected
        Timber.i("HETAG onCameraConnected $camera")
        if (!cameras.contains(camera))
            cameras.add(camera)
        val index = cameras.indexOf(camera)
        viewModel?.info?.set("当前检索到的USB摄像头数为${cameras.size}！")
        if (showIndex < 0) {
            callBack?.handleMessage(Message())
            camera.openCamera(mViewBinding.camLy, getCameraRequest())
            camera.setCameraStateCallBack(this)
            showIndex = index
        } else if (cameras.size >= 2) {
            viewModel?.viewModelScope?.launch (Dispatchers.Main) {
                mViewBinding.touchSetting.visibility = View.VISIBLE
            }
        }
    }

    override fun onCameraDisConnected(camera: MultiCameraClient.ICamera) {
        Timber.i("HETAG onCameraDisConnected $camera")
        // a camera be disconnected
    }


    override fun onCameraState(
        self: MultiCameraClient.ICamera,
        code: ICameraStateCallBack.State,
        msg: String?
    ) {
        Timber.i("HETAG onCameraState $code $msg $self")
        // a camera be opened or closed or error
    }

    override fun generateCamera(ctx: Context, device: UsbDevice): MultiCameraClient.ICamera {
        return CameraUVC(ctx, device)
    }

    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View {
//        return rootView
        mViewBinding = FragmentUsbCamBinding.inflate(inflater, container, false)
        viewModel?.info?.set("当前检索到的USB摄像头数为0！")
        mViewBinding.touchSetting.visibility = View.GONE
        mViewBinding.touchSetting.setOnClickListener {
            if (cameras.size < 2) return@setOnClickListener

            cameras[showIndex].closeCamera()
            if (showIndex + 1 >= cameras.size) {
                showIndex = 0
            } else
                showIndex++
            cameras[showIndex].openCamera(mViewBinding.camLy, getCameraRequest())
            cameras[showIndex].setCameraStateCallBack(this)
        }
        return mViewBinding.root
    }

//    private fun initNameAdapter() {
//        val list = viewModel.getList()
//        nameAdapter = ArrayAdapter(this, R.layout.simple_spinner_item , list)
//        nameAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.nameSp.adapter = nameAdapter!!
//        binding.nameSp.setSelection(0)
//        binding.nameSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                viewModel.spUtil.sp = list[position]
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//
//            }
//
//        }
//    }
}