package com.sc.hetest.activity

import android.content.Context
import android.graphics.Point
import android.opengl.Visibility
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
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
import com.sc.hetest.databinding.ActivityLcdBinding
import com.sc.hetest.vm.InfoViewModel
import timber.log.Timber
import javax.inject.Inject


/**
 * author: sc
 * date: 2022/12/10
 */
@Route(path = HEPath.LCD_PATH)
class LCDActivity : BaseBindingActivity<ActivityLcdBinding, InfoViewModel>(){

    override var layoutId: Int = R.layout.activity_lcd

    companion object{
        const val LAST_COLOR_INDEX = 5
    }

    var lcds = arrayListOf<Int>(
        R.color.red,
        R.color.green,
        R.color.blue,
        R.color.white,
        R.color.black,
        R.mipmap.ct,
        R.mipmap.hb,
        R.mipmap.hj16
    )

    private var viewIndex = -1

    override fun subscribeUi() {
        binding.failTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.LCD_PATH, InfoItem.STATE_FAIL)
            finish()
        }
        binding.successTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.LCD_PATH, InfoItem.STATE_SUCCESS)
            finish()
        }
        binding.btnLy.visibility = View.GONE
        binding.bgLy.setOnClickListener {
            next()
        }
        next()
    }

    fun next() {
        viewIndex++
        if (viewIndex == lcds.size) {
            //
            viewIndex = -1
            binding.btnLy.visibility = View.VISIBLE
            return
        }
        binding.btnLy.visibility = View.GONE
        if (viewIndex >= 0 && viewIndex < lcds.size) {
            if (viewIndex < LAST_COLOR_INDEX) {
                // 加载颜色
                binding.bgLy.setBackgroundColor(this.resources.getColor(lcds[viewIndex]))
            } else {
                // 加载图片
                binding.bgLy.background = this.resources.getDrawable(lcds[viewIndex])
            }
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

        viewModel.info.set(info.toString())
    }

}