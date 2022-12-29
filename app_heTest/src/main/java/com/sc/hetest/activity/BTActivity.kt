package com.sc.hetest.activity

import android.bluetooth.BluetoothAdapter
import android.view.View
import android.widget.ListAdapter
import android.widget.SimpleAdapter
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.SPUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.animation.AlphaInAnimation
import com.chad.library.adapter.base.listener.OnItemLongClickListener
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.common.BasePath
import com.nbhope.lib_frame.utils.HopeUtils
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.nbhope.lib_frame.view.SpaceItemDecoration
import com.nbhope.lib_frame.widget.WrapGridLayoutManager
import com.nbhope.phmusic.player.AudioFocusManager
import com.nbhope.phmusic.player.SilentPlayer
import com.sc.hetest.R
import com.sc.hetest.adapter.InfoItemAdapter
import com.sc.hetest.adapter.InfoListAdapter
import com.sc.hetest.bean.InfoItem
import com.sc.hetest.common.HECommon
import com.sc.hetest.common.HEPath
import com.sc.hetest.databinding.*
import com.sc.hetest.vm.*
import com.sc.lib_system.util.BluetoothUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.ArrayList
import javax.inject.Inject

/**
 * author: sc
 * date: 2022/12/10
 */
@Route(path = HEPath.BT_PATH)
class BTActivity : BaseBindingActivity<ActivityInfoListBinding, InfoListViewModel>(), BluetoothUtils.BluetoothInterface {

    companion object{

    }

    var list = ArrayList<BluetoothUtils.DeviceBean>()

    override var layoutId: Int = R.layout.activity_info_list

    var bluetoothAdapter: BluetoothAdapter? = null

    var adapter: InfoListAdapter? = null

    override fun subscribeUi() {
        binding.failTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.BT_PATH, InfoItem.STATE_FAIL)
            finish()
        }
        binding.successTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.BT_PATH, InfoItem.STATE_SUCCESS)
            finish()
        }
        initAdapter()
    }

    override fun initData() {
        viewModel.initData()
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        BluetoothUtils.getInstance().initBluetooth(this);
        BluetoothUtils.getInstance().setBluetoothListener(this);
        getBTInfo()
        Timber.i("HETAG BluetoothUtils.getInstance().isEnabled ${BluetoothUtils.getInstance().isEnabled}")
        if (!BluetoothUtils.getInstance().isEnabled) {
            BluetoothUtils.getInstance().enable()
            viewModel.viewModelScope.launch(Dispatchers.Main) {
                delay(1000)
                getBTInfo()
            }
        }
    }

    fun initAdapter() {
        adapter = InfoListAdapter(list)
        adapter?.setHasStableIds(true);
        adapter?.adapterAnimation = AlphaInAnimation()
        val layoutManager: LinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        val animatorLeft = binding.listRv.itemAnimator;
        if (animatorLeft is SimpleItemAnimator) {
            animatorLeft.supportsChangeAnimations = false;
        }
        adapter?.setOnItemLongClickListener(object : OnItemLongClickListener {
            override fun onItemLongClick(
                adapter: BaseQuickAdapter<*, *>,
                view: View,
                position: Int
            ): Boolean {

                return false
            }
        })
        adapter?.setOnItemClickListener { adapter, view, position ->
//            val item = infoList?.get(position)
//            ARouter.getInstance().build(item?.path).navigation(this )
        }
        binding.listRv.layoutManager = layoutManager
        binding.listRv.addItemDecoration(SpaceItemDecoration(arrayOf(5, 0, 5, 5).map {
            HopeUtils.dip2px(
                this,
                it.toFloat()
            )
        }.toMutableList()))
        binding.listRv.adapter = adapter
        binding.listRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    private fun getBTInfo(){
        val info = StringBuilder()
        if (bluetoothAdapter == null) {
            info.append("当前设备不支持蓝牙！")
            return
        }
        val res = bluetoothAdapter?.isEnabled == true
        info.append(if (res) "蓝牙已打开" else "蓝牙尚未打开,正在为您打开")
        info.append("\n")
        info.append("蓝牙名称：${bluetoothAdapter?.name} \n")
        info.append("MAC地址：${DeviceUtils.getMacAddress()} \n")
        if (res) {
            info.append("蓝牙数量：${list?.size} \n")
            BluetoothUtils.getInstance().startDiscovery()
        }
        viewModel.info.set(info.toString())
    }

    @Inject
    lateinit var spManager: SharedPreferencesManager

    @Inject
    override lateinit var viewModel: InfoListViewModel
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

    override fun getBluetoothList(deviceBeans: ArrayList<BluetoothUtils.DeviceBean>?) {
        if (deviceBeans != null) {
            Timber.i("HETAG ${deviceBeans.size}")
            list = deviceBeans
            adapter?.notifyDataSetChanged()
        }
    }
}