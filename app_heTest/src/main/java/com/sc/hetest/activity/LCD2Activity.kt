package com.sc.hetest.activity

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.SPUtils
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.common.BasePath
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.sc.hetest.R
import com.sc.hetest.bean.InfoItem
import com.sc.hetest.bean.ScreenInfo
import com.sc.hetest.common.HEPath
import com.sc.hetest.databinding.ActivityInfoBinding
import com.sc.hetest.vm.InfoViewModel
import timber.log.Timber
import javax.inject.Inject


/**
 * author: sc
 * date: 2022/12/10
 */
//@Route(path = HEPath.LCD_PATH)
class LCD2Activity : BaseBindingActivity<ActivityInfoBinding, InfoViewModel>(){

    override var layoutId: Int = R.layout.activity_info

    override fun subscribeUi() {
        binding.failTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.LCD_PATH, InfoItem.STATE_FAIL)
            finish()
        }
        binding.successTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.LCD_PATH, InfoItem.STATE_SUCCESS)
            finish()
        }
    }

    override fun initData() {
        viewModel.initData()
    }

    @Inject
    lateinit var spManager: SharedPreferencesManager

    @Inject
    override lateinit var viewModel: InfoViewModel
    override fun linkViewModel() {
        binding.vm = viewModel
        val info = StringBuilder()
        val screenInfo = getScreenInfo(this)
        info.append("屏幕大小：${screenInfo?.sizeStr} 英寸\n")
        info.append("屏幕分辨率：${screenInfo?.screenRealMetrics} \n")
        info.append("屏幕密度(像素比例)：${screenInfo?.density} \n")
        info.append("屏幕密度(每寸像素)：${screenInfo?.densityDpiStr} \n")
        info.append("屏幕x方向每英寸像素点数：${screenInfo?.xdpi} \n")
        info.append("屏幕y方向每英寸像素点数：${screenInfo?.ydpi} \n")
        viewModel.info.set(info.toString())
    }

    private fun releaseMediaPlayer() {

    }

    /**
     * 屏幕分辨率
     *
     * @param mContext
     * @return
     */
    private fun getScreenInfo(mContext: Context): ScreenInfo? {
        val result = ScreenInfo()
        var widthPixels: Int
        var heightPixels: Int
        val w = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val d: Display = w.defaultDisplay
        val metrics = DisplayMetrics()
        d.getMetrics(metrics)
        // since SDK_INT = 1;
        widthPixels = metrics.widthPixels
        heightPixels = metrics.heightPixels
        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                widthPixels = Display::class.java.getMethod("getRawWidth").invoke(d) as Int
                heightPixels = Display::class.java.getMethod("getRawHeight").invoke(d) as Int
            } catch (ignored: Exception) {
                ignored.printStackTrace()
            }
        }
        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 17) {
            try {
                val realSize = Point()
                Display::class.java.getMethod("getRealSize", Point::class.java).invoke(d, realSize)
                widthPixels = realSize.x
                heightPixels = realSize.y
            } catch (ignored: Exception) {
                ignored.printStackTrace()
            }
        }
        result.widthPixels = widthPixels
        result.heightPixels = heightPixels
        result.screenRealMetrics = "$widthPixels X $heightPixels"
        result.density = metrics.density
        result.density_default = DisplayMetrics.DENSITY_DEFAULT
        result.densityDpi = metrics.densityDpi
        result.densityDpiStr = metrics.densityDpi.toString() + " dpi"
        result.scaledDensity = metrics.scaledDensity
        result.xdpi = metrics.xdpi
        result.ydpi = metrics.ydpi
        result.size = Math.sqrt(
            Math.pow(widthPixels.toDouble(), 2.0) + Math.pow(
                heightPixels.toDouble(),
                2.0
            )
        ) / metrics.densityDpi
        result.sizeStr =
            String.format("%.2f", result.size)
//        + mContext.getResources().getString(R.string.sys_inches_unit)
        return result
    }
}