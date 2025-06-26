package com.sc.tmp_translate

import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Display
import androidx.appcompat.app.AppCompatActivity
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.sc.tmp_translate.databinding.ActivityDisplayBinding
import com.sc.tmp_translate.view.OtherDisplay
import com.sc.tmp_translate.vm.MainViewModel
import javax.inject.Inject


class DisplayActivity: BaseBindingActivity<ActivityDisplayBinding, MainViewModel>() {


    @Inject
    override lateinit var viewModel: MainViewModel

    override var layoutId: Int = R.layout.activity_display

    var number = 0

    var otherDisplay: OtherDisplay? = null

    override fun subscribeUi() {

        // 读取屏幕数量
        binding.infoTv.text = if (hasMoreDisplay(this)) "已检测到异显屏幕" else "暂未检测到异显屏幕"
        binding.numTv.text = number.toString()
        binding.numBtn.setOnClickListener {
            number++
            binding.numTv.text = number.toString()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //启动Activity让用户授权
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:com.sc.tmp_translate"))
                startActivityForResult(intent, 1010)
                return
            } else {
                init()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        init()
    }

    fun init() {
        val displays = getDisplays(this@DisplayActivity)
        if (displays.isNotEmpty()) {
            if (otherDisplay == null)
                otherDisplay = OtherDisplay(this, displays[displays.size - 1])
            try {
                log("显示 ${displays.size} ${otherDisplay}")
                otherDisplay?.show()
                log("成功")
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun log(msg: Any?) {
        System.out.println("异显：${msg ?: "null"}")
    }

    override fun initData() {
        
    }
    
    override fun linkViewModel() {
        
    }

    override fun onDestroy() {
        super.onDestroy()
        otherDisplay?.hide()
        log("释放")
    }

    fun hasMoreDisplay(context: Context): Boolean {
        return getDisplays(context).isNotEmpty()
    }

    fun getDisplays(context: Context): Array<Display> {
        val displayManager = context.getSystemService(AppCompatActivity.DISPLAY_SERVICE) as DisplayManager
        val displays = displayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION)
        return displays
    }


}