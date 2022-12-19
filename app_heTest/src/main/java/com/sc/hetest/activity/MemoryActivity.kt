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
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.sc.hetest.R
import com.sc.hetest.bean.InfoItem
import com.sc.hetest.common.HEPath
import com.sc.hetest.databinding.ActivityInfoBinding
import com.sc.hetest.utils.ProcessUtil
import com.sc.hetest.utils.StorageQueryUtil
import com.sc.hetest.vm.InfoViewModel
import java.text.DecimalFormat
import javax.inject.Inject

/**
 * author: sc
 * date: 2022/12/10
 */
@Route(path = HEPath.MEMORY_PATH)
class MemoryActivity : BaseBindingActivity<ActivityInfoBinding, InfoViewModel>(){

    companion object{

    }

    override var layoutId: Int = R.layout.activity_info

    var bluetoothAdapter: BluetoothAdapter? = null

    override fun subscribeUi() {
        binding.failTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.MEMORY_PATH, InfoItem.STATE_FAIL)
            finish()
        }
        binding.successTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.MEMORY_PATH, InfoItem.STATE_SUCCESS)
            finish()
        }
    }

    override fun initData() {
        viewModel.initData()
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        getMemoryInfo()
    }

    private fun getMemoryInfo(){
        val info = StringBuilder()
        val format = DecimalFormat("0.0")
        val acail = ConvertUtils.byte2MemorySize(ProcessUtil.getAvailSpace(this), MemoryConstants.GB)
        val total = ConvertUtils.byte2MemorySize(ProcessUtil.getTotalSpace(this), MemoryConstants.GB)

        var acail2 = ConvertUtils.byte2MemorySize(ProcessUtil.getAvailableInternalMemorySize(), MemoryConstants.GB)
        var total2 = ConvertUtils.byte2MemorySize(ProcessUtil.getTotalInternalMemorySize(), MemoryConstants.GB)
        val list = StorageQueryUtil.queryWithStorageManager(this)

        info.append("剩余内存大小：${format.format(acail)} GB\n")
        info.append("所有内存大小：${format.format(total)} GB\n")
        info.append("内存缓存个数：${CacheMemoryUtils.getInstance().cacheCount} \n")
        info.append("磁盘缓存个数：${CacheDiskUtils.getInstance().cacheCount} \n")
        info.append("磁盘缓存大小：${CacheDiskUtils.getInstance().cacheSize} \n")
        if (list.size >= 2) {
            val base = if (list.size > 2) 1000f else 1024f
            info.append("剩余磁盘大小：${StorageQueryUtil.getUnit(list[1].toFloat(), base)}\n")
            if (list.size > 2)
                info.append("系统所占大小：${ StorageQueryUtil.getUnit(list[3].toFloat(), base)}\n")
            info.append("所有磁盘大小：${StorageQueryUtil.getUnit(list[0].toFloat(), base)}\n")
        } else {
            info.append("磁盘统计不计入系统占用\n")
            info.append("剩余磁盘大小：${format.format(acail2)} GB\n")
            info.append("所有磁盘大小：${format.format(total2)} GB\n")
        }

        viewModel.info.set(info.toString())
    }

    @Inject
    lateinit var spManager: SharedPreferencesManager

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