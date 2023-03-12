package com.sc.hetest.activity

import android.bluetooth.BluetoothAdapter
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.constant.MemoryConstants
import com.blankj.utilcode.util.CacheDiskUtils
import com.blankj.utilcode.util.CacheMemoryUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.SPUtils
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.common.BasePath
import com.nbhope.lib_frame.utils.AppUtil
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.sc.hetest.R
import com.sc.hetest.bean.InfoItem
import com.sc.hetest.common.HEPath
import com.sc.hetest.databinding.ActivityInfoBinding
import com.sc.hetest.utils.ProcessUtil
import com.sc.hetest.utils.StorageQueryUtil
import com.sc.hetest.vm.InfoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import timber.log.Timber
import java.text.DecimalFormat
import javax.inject.Inject

/**
 * author: sc
 * date: 2022/12/10
 */
@Route(path = HEPath.FG_PATH)
class FGActivity : BaseBindingActivity<ActivityInfoBinding, InfoViewModel>() {

    companion object {

    }

    override var layoutId: Int = R.layout.activity_info

    override fun subscribeUi() {
        binding.failTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.FG_PATH, InfoItem.STATE_FAIL)
            finish()
        }
        binding.successTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.FG_PATH, InfoItem.STATE_SUCCESS)
            finish()
        }
    }

    override fun initData() {
        viewModel.initData()
        val info = StringBuilder()
        val list = ArrayList(AppUtil.getAllCustomerApps(this, java.util.ArrayList()))
        val packName = "com.chartcross.gpstestplus"
        var contain = false
        list.forEach {
            Timber.i("HETAG app ${it.packageName}")
            if (it.packageName == packName) contain = true
        }
        if (contain) {
            info.append("正在为您打开4G测试界面\n")
            viewModel.launch {
                delay(1000)
                AppUtil.startApp(this@FGActivity, packName)
            }
        } else {
            info.append("未找到4G测试App\n")
        }
        viewModel.info.set(info.toString())

    }

    @Inject
    override lateinit var viewModel: InfoViewModel
    override fun linkViewModel() {
        binding.vm = viewModel
    }
}