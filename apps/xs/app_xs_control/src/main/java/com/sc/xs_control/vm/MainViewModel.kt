package com.sc.xs_control.vm

import android.os.Handler
import android.os.Looper
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.sc.xs_control.inter.ITmpService
import com.sc.xs_control.service.TmpServiceDelegate
import timber.log.Timber
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2026/10/8
 * @version 0.0.0-1
 * @description 主界面ViewModel：页签切换（设备控制/灯光控制两个Fragment）+ 顶栏一键开关
 * 设备控制页详见 DeviceViewModel，灯光控制页详见 LightViewModel
 * 按钮与《指令代码.xlsx》一一对应（一键开关全开/全关）
 * 顶栏时间/日期/星期由 TextClock 自行刷新，无需VM参与
 */
class MainViewModel @Inject constructor(val spManager: SharedPreferencesManager) : BaseViewModel() {

    companion object {

        const val TAG = "XCTAG"

        /** 界面页签：设备控制 */
        const val TAB_DEVICE = 0

        /** 界面页签：灯光控制 */
        const val TAB_LIGHT = 1

        /** service网络状态挂接重试间隔（毫秒） */
        private const val ATTACH_RETRY_INTERVAL = 1000L
    }

    /** 当前页签 0=设备控制 1=灯光控制 */
    var tabObs = ObservableInt(TAB_DEVICE)

    /**
     * 网络是否正常（侧栏底部联机状态显示）
     * 状态源在 TmpServiceImpl（attachNetObs 时从 service 取并镜像到本地）
     */
    var netObs = ObservableBoolean(false)

    /** service网络状态回调（镜像到 netObs） */
    private var netObsCallback: Observable.OnPropertyChangedCallback? = null

    /** service未绑定完成时的挂接重试 */
    private val attachHandler = Handler(Looper.getMainLooper())

    private var attachRunnable: Runnable? = null

    private val tmpService: ITmpService
        get() = TmpServiceDelegate.getInstance()

    fun initData() {

    }

    // ==================== 页签切换 ====================

    fun switchTab(tab: Int) {
        Timber.i("$TAG switchTab $tab")
        tabObs.set(tab)
    }

    // ==================== 网络状态（来自service） ====================

    /** 挂接service的网络状态：同步当前值并监听后续变化（service未绑定完成时自动重试直至成功） */
    fun attachNetObs() {
        if (netObsCallback != null) return
        val serviceObs = tmpService.getNetObs()
        if (serviceObs == null) {
            // bindService异步，界面创建时service往往还没连上，定时重试
            Timber.d("$TAG attachNetObs: service未就绪，稍后重试")
            scheduleAttachRetry()
            return
        }
        netObs.set(serviceObs.get())
        val callback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                netObs.set((sender as ObservableBoolean).get())
            }
        }
        serviceObs.addOnPropertyChangedCallback(callback)
        netObsCallback = callback
        Timber.i("$TAG attachNetObs: 已挂接service网络状态，当前=${serviceObs.get()}")
    }

    /** 1秒后重试挂接（已有未执行的重试时不重复排） */
    private fun scheduleAttachRetry() {
        if (attachRunnable != null) return
        val runnable = Runnable {
            attachRunnable = null
            attachNetObs()
        }
        attachRunnable = runnable
        attachHandler.postDelayed(runnable, ATTACH_RETRY_INTERVAL)
    }

    /** 解除service网络状态监听（避免泄漏，service生命周期长于界面） */
    fun detachNetObs() {
        attachRunnable?.let { attachHandler.removeCallbacks(it) }
        attachRunnable = null
        netObsCallback?.let { tmpService.getNetObs()?.removeOnPropertyChangedCallback(it) }
        netObsCallback = null
    }

    // ==================== 顶栏一键开关（按当前页签发送对应指令） ====================

    /** 设备 一键开关 全开 */
    fun onDeviceAllOn() {
        Timber.i("$TAG 设备一键开关 全开")
        tmpService.sendDeviceAllCmd(true)
    }

    /** 设备 一键开关 全关 */
    fun onDeviceAllOff() {
        Timber.i("$TAG 设备一键开关 全关")
        tmpService.sendDeviceAllCmd(false)
    }

    /** 灯光 一键开关 全开 */
    fun onLightAllOn() {
        Timber.i("$TAG 灯光一键开关 全开")
        tmpService.sendLightAllCmd(true)
    }

    /** 灯光 一键开关 全关 */
    fun onLightAllOff() {
        Timber.i("$TAG 灯光一键开关 全关")
        tmpService.sendLightAllCmd(false)
    }
}
