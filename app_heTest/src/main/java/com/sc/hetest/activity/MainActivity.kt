package com.sc.hetest.activity

import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.SPUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.animation.AlphaInAnimation
import com.chad.library.adapter.base.listener.OnItemLongClickListener
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.common.BasePath
import com.nbhope.lib_frame.utils.AppUtil
import com.nbhope.lib_frame.utils.HopeUtils
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.nbhope.lib_frame.utils.ViewUtil.Companion.immersionTitle
import com.nbhope.lib_frame.view.SpaceItemDecoration
import com.nbhope.lib_frame.widget.WrapGridLayoutManager
import com.sc.hetest.R
import com.sc.hetest.adapter.InfoItemAdapter
import com.sc.hetest.bean.InfoItem
import com.sc.hetest.common.HECommon
import com.sc.hetest.common.HEPath
import com.sc.hetest.databinding.ActivityMainBinding
import com.sc.hetest.vm.MainViewModel
import timber.log.Timber
import javax.inject.Inject
import kotlin.system.exitProcess

/**
 * author: sc
 * date: 2022/12/10
 */
@Route(path = BasePath.MAIN_PATH)
class MainActivity : BaseBindingActivity<ActivityMainBinding, MainViewModel>(){

    override var layoutId: Int = R.layout.activity_main

    @Inject
    lateinit var spManager: SharedPreferencesManager

    private val pageMap = mapOf<String, String>(
        "版本信息" to HEPath.VER_INFO_PATH,
        "触屏" to HEPath.TOUCH_PATH,
        "LCD" to HEPath.LCD_PATH,
//        "电源" to HEPath.POWER_PATH,
        "喇叭" to HEPath.HORN_PATH,
        "麦克风" to HEPath.MIC_PATH,
        "WIFI" to HEPath.WIFI_PATH,
        "蓝牙" to HEPath.BT_PATH,
        "背景灯" to HEPath.BG_LIGHT_PATH,
        "内存" to HEPath.MEMORY_PATH,
        "串口" to HEPath.SERIAL_PATH,
//        "主摄像头" to HEPath.MAIN_CAM_PATH,
//        "触屏校准" to HEPath.TOUCH_ALINE_PATH,
//        "运动检测" to HEPath.SPORT_PATH,
        "USB摄像头" to HEPath.USB_CAM_PATH,
//        "货柜检测" to HEPath.HG_PATH
        "4G模块" to HEPath.FG_PATH,
        "扫码器" to HEPath.SCAN_PATH,
    )

    private var infoList: ArrayList<InfoItem>? = null

    private var adapter: InfoItemAdapter? = null

    @Autowired(name = HECommon.COM_PATH)
    @JvmField
    var path = ""

    @Autowired(name = HECommon.COM_STATE)
    @JvmField
    var state = 0

    override fun linkViewModel() {
//        binding.vm = viewModel
    }

    @Inject
    override lateinit var viewModel: MainViewModel

    override fun subscribeUi() {
        immersionTitle(this)

        binding.quitBtn.setOnClickListener {
            finish()
//            exitProcess(0)
        }
        initAdapter()
    }

    private fun initAdapter() {
        adapter = InfoItemAdapter(ArrayList())
        adapter?.setHasStableIds(true);
        adapter?.adapterAnimation = AlphaInAnimation()
        val layoutManager: LinearLayoutManager = WrapGridLayoutManager(this, 5, true)
        val animatorLeft = binding.viewRv.itemAnimator;
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
            val item = infoList?.get(position)
            ARouter.getInstance().build(item?.path).navigation(this )
        }
        binding.viewRv.layoutManager = layoutManager
        binding.viewRv.addItemDecoration(SpaceItemDecoration(arrayOf(10, 0, 10, 15).map {
            HopeUtils.dip2px(
                this,
                it.toFloat()
            )
        }.toMutableList()))
        binding.viewRv.adapter = adapter
        binding.viewRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    override fun initData() {
//        // 获取
//        var wm = getSystemService(WINDOW_SERVICE) as WindowManager
//        val point = Point()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            wm.defaultDisplay.getRealSize(point)
//        } else {
//            wm.defaultDisplay.getSize(point)
//        }
//        Timber.i("HETAG screen $point $state $path")

        infoList = ArrayList<InfoItem>()
        pageMap.forEach { info ->
            infoList?.add(InfoItem().also {
                it.title = info.key
                it.path = info.value
                if (SPUtils.getInstance().contains(it.path)) {
                    it.state = SPUtils.getInstance().getInt(it.path, InfoItem.STATE_NORMAL)
                }
            })
        }
        adapter?.setNewInstance(infoList)
    }

}