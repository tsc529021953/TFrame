package com.sc.xs_control.vm

import androidx.databinding.ObservableBoolean
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
 * @description 设备控制页ViewModel，按钮与指令发射器绑定发送UDP指令
 * 按钮与《指令代码.xlsx》一一对应：PC(开/关)、一体机(开/关)、电视1~6(开/关)
 * （一键开关全开/全关在主界面顶栏，见 MainViewModel）
 */
class DeviceViewModel @Inject constructor(val spManager: SharedPreferencesManager) : BaseViewModel() {

    companion object {

        const val TAG = "XCTAG"
    }

    /** PC 开关状态 */
    var pcObs = ObservableBoolean(false)

    /** 一体机 开关状态 */
    var aioObs = ObservableBoolean(false)

    /** 电视1~6 开关状态 */
    val tvObs = Array(6) { ObservableBoolean(false) }

    private val tmpService: ITmpService
        get() = TmpServiceDelegate.getInstance()

    fun initData() {

    }

    /** 全开（同步本地状态，顶栏一键开关也走此逻辑） */
    fun setAllState(on: Boolean) {
        pcObs.set(on)
        aioObs.set(on)
        for (i in 0 until 6) tvObs[i].set(on)
    }

    /** 大厅电子屏 开（字符串指令：开="0"） */
    fun onPcOn() {
        pcObs.set(true)
        Timber.i("$TAG PC 开")
        tmpService.sendPcCmd(true)
    }

    /** PC 关 */
    fun onPcOff() {
        pcObs.set(false)
        Timber.i("$TAG PC 关")
        tmpService.sendPcCmd(false)
    }

    /** 换装体验(一体机) 开（字符串指令：开="0"） */
    fun onAioOn() {
        aioObs.set(true)
        Timber.i("$TAG 一体机 开")
        tmpService.sendAioCmd(true)
    }

    /** 一体机 关 */
    fun onAioOff() {
        aioObs.set(false)
        Timber.i("$TAG 一体机 关")
        tmpService.sendAioCmd(false)
    }

    /** 电视 index: 1~6 开 */
    fun onTvOn(index: Int) {
        tvObs[index - 1].set(true)
        Timber.i("$TAG 电视$index 开")
        tmpService.sendTvCmd(index, true)
    }

    /** 电视 index: 1~6 关 */
    fun onTvOff(index: Int) {
        tvObs[index - 1].set(false)
        Timber.i("$TAG 电视$index 关")
        tmpService.sendTvCmd(index, false)
    }

    // 播放/暂停：字符串指令 "play"/"pause"，发往 192.168.1.90~96 的 2368 端口

    /** 大厅电子屏 播放 */
    fun onPcPlay() {
        Timber.i("$TAG 大厅电子屏 播放")
        tmpService.sendPcMediaCmd(true)
    }

    /** 大厅电子屏 暂停 */
    fun onPcPause() {
        Timber.i("$TAG 大厅电子屏 暂停")
        tmpService.sendPcMediaCmd(false)
    }

    /** 电视 index: 1~6 播放 */
    fun onTvPlay(index: Int) {
        Timber.i("$TAG 电视$index 播放")
        tmpService.sendTvMediaCmd(index, true)
    }

    /** 电视 index: 1~6 暂停 */
    fun onTvPause(index: Int) {
        Timber.i("$TAG 电视$index 暂停")
        tmpService.sendTvMediaCmd(index, false)
    }
}
