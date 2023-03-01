package com.sc.hetest.activity

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.viewModelScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.SPUtils
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.utils.PermissionUtil
import com.sc.hetest.R
import com.sc.hetest.bean.InfoItem
import com.sc.hetest.common.HEPath
import com.sc.hetest.databinding.*
import com.sc.hetest.fragment.USBCamFragment
import com.sc.hetest.vm.InfoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * author: sc
 * date: 2022/12/10
 */
@Route(path = HEPath.USB_CAM_PATH)
class USBCamActivity : BaseBindingActivity<ActivityUsbCamBinding, InfoViewModel>(){

    private var PERMISSIONS: Array<String> = arrayOf(
        Manifest.permission.CAMERA,
    )

    override var layoutId: Int = R.layout.activity_usb_cam

    var bluetoothAdapter: BluetoothAdapter? = null

    var fragment: USBCamFragment? = null

    override fun subscribeUi() {
        binding.failTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.USB_CAM_PATH, InfoItem.STATE_FAIL)
            finish()
        }
        binding.successTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.USB_CAM_PATH, InfoItem.STATE_SUCCESS)
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

//        val intent = Intent()
//        intent.action = "android.provider.Telephony.SECRET_CODE"
//        intent.data = Uri.parse("android secret code://66")
//        intent.component = ComponentName("com.sc.hetest", "com.sc.hetest.receiver.StartReceiver")
//        sendBroadcast(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10086) {
            if (PermissionUtil.hasPermissionsGranted(this, PERMISSIONS)) {
                openCam()
            }
        }
    }

    private fun openCam(){
//        binding.infoTv.visibility = View.GONE
        viewModel.info.set("正在为您检索USB摄像头！")
        fragment = USBCamFragment()
        val ft: FragmentTransaction = this.supportFragmentManager
            .beginTransaction()
        fragment?.setInfoViewModel(viewModel, object : Handler.Callback{
            override fun handleMessage(msg: Message): Boolean {
                viewModel.viewModelScope.launch(Dispatchers.Main) {
                    binding.infoTv.visibility = View.GONE
                }
                return true
            }
        })
        ft.add(R.id.content_ly, fragment!!).commit()
    }

    @Inject
    override lateinit var viewModel: InfoViewModel
    override fun linkViewModel() {
        binding.vm = viewModel
    }

    private fun releaseMediaPlayer() {

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}