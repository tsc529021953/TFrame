package com.sc.hetest.activity

import android.Manifest
import android.os.Build
import android.provider.Settings
import android.widget.SeekBar
import androidx.lifecycle.viewModelScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.SPUtils
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.common.BasePath
import com.nbhope.lib_frame.utils.HopeUtils
import com.nbhope.lib_frame.utils.PermissionUtil
import com.sc.hetest.R
import com.sc.hetest.bean.InfoItem
import com.sc.hetest.common.HEPath
import com.sc.hetest.databinding.ActivityBglBinding
import com.sc.hetest.vm.LightViewModel
import com.xbh.sdk3.Picture.PictureHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * author: sc
 * date: 2022/12/10
 */
@Route(path = HEPath.BG_LIGHT_PATH)
class BGLActivity : BaseBindingActivity<ActivityBglBinding, LightViewModel>() {

    private var PERMISSIONS: Array<String> = arrayOf(
        Manifest.permission.WRITE_SETTINGS,
    )

    override var layoutId: Int = R.layout.activity_bgl

    var mPictureHelper: PictureHelper? = null

    override fun subscribeUi() {
        binding.failTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.BG_LIGHT_PATH, InfoItem.STATE_FAIL)
            finish()
        }
        binding.successTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.BG_LIGHT_PATH, InfoItem.STATE_SUCCESS)
            finish()
        }
        binding.lightSb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (HopeUtils.isSystemSign(baseContext)) {
                    mPictureHelper?.backlight = binding.lightSb.progress
                } else {
                    // 设置系统亮度
                    try {
                        Settings.System.putInt(
                            contentResolver,
                            Settings.System.SCREEN_BRIGHTNESS,
                            binding.lightSb.progress
                        );
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                val light = binding.lightSb.progress
                viewModel.lightStr.set(light.toString())
                viewModel.light.set(light!!)
            }
        })
    }

    override fun initData() {
        if (!PermissionUtil.hasPermissionsGranted(this, PERMISSIONS)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(PERMISSIONS, 10086)
                viewModel.info.set("正在申请修改设置相关权限")
            }
        }
        viewModel.initData()
        val info = StringBuilder()
        if (HopeUtils.isSystemSign(this)) {
            info.append("5秒将为您关闭屏幕\n屏幕关闭五秒后将被唤醒")
            mPictureHelper = PictureHelper()
            val light = mPictureHelper?.backlight
            binding.lightSb.max = 100
            viewModel.lightStr.set(light.toString())
            viewModel.light.set(light!!)
            viewModel.viewModelScope.launch(Dispatchers.Main) {
                delay(5000)
                viewModel.info.set("关闭屏幕")
                mPictureHelper?.gotoSleep()
                delay(5000)
                viewModel.info.set("唤醒屏幕")
                mPictureHelper?.wakeup()
            }

        } else {
            info.append("App非系统签名，将无法控制亮度！\n")
            // 获取亮度
            // 获取系统亮度
            binding.lightSb.max = 255
            val light = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS);
            viewModel.lightStr.set(light.toString())
            viewModel.light.set(light!!)
        }
        viewModel.info.set(info.toString())


    }

    @Inject
    override lateinit var viewModel: LightViewModel
    override fun linkViewModel() {
        binding.vm = viewModel
    }


}