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
 * @description 灯光控制页ViewModel，按钮与指令发射器绑定发送UDP指令
 * 按钮与《指令代码.xlsx》一一对应：顶面/墙面/空间区域灯光 一键(开/关)及各区域(开/关)
 * （一键开关全开/全关在主界面顶栏，见 MainViewModel）
 */
class LightViewModel @Inject constructor(val spManager: SharedPreferencesManager) : BaseViewModel() {

    companion object {

        const val TAG = "XCTAG"
    }

    /** 顶面照明 一键开关状态 */
    var ceilingAllObs = ObservableBoolean(false)

    /** 顶面照明 区域1~3 状态 */
    val ceilingAreaObs = Array(3) { ObservableBoolean(false) }

    /** 墙面照明 一键开关状态 */
    var wallAllObs = ObservableBoolean(false)

    /** 墙面照明 区域1~3 状态 */
    val wallAreaObs = Array(3) { ObservableBoolean(false) }

    /** 空间区域灯光 一键开关状态 */
    var spaceAllObs = ObservableBoolean(false)

    /** 空间区域灯光 区域1~3 状态 */
    val spaceAreaObs = Array(3) { ObservableBoolean(false) }

    private val tmpService: ITmpService
        get() = TmpServiceDelegate.getInstance()

    fun initData() {

    }

    /** 全开/全关（同步本地状态，顶栏一键开关也走此逻辑） */
    fun setAllState(on: Boolean) {
        ceilingAllObs.set(on)
        wallAllObs.set(on)
        spaceAllObs.set(on)
        for (i in 0 until 3) {
            ceilingAreaObs[i].set(on)
            wallAreaObs[i].set(on)
            spaceAreaObs[i].set(on)
        }
    }

    /** 顶面照明 一键 开 */
    fun onLightCeilingAllOn() {
        ceilingAllObs.set(true)
        for (i in 0 until 3) ceilingAreaObs[i].set(true)
        Timber.i("$TAG 顶面照明一键 开")
        tmpService.sendLightCeilingAllCmd(true)
    }

    /** 顶面照明 一键 关 */
    fun onLightCeilingAllOff() {
        ceilingAllObs.set(false)
        for (i in 0 until 3) ceilingAreaObs[i].set(false)
        Timber.i("$TAG 顶面照明一键 关")
        tmpService.sendLightCeilingAllCmd(false)
    }

    /** 顶面照明 区域 index: 1~3 开 */
    fun onLightCeilingAreaOn(index: Int) {
        ceilingAreaObs[index - 1].set(true)
        Timber.i("$TAG 顶面照明区域$index 开")
        tmpService.sendLightCeilingAreaCmd(index, true)
    }

    /** 顶面照明 区域 index: 1~3 关 */
    fun onLightCeilingAreaOff(index: Int) {
        ceilingAreaObs[index - 1].set(false)
        Timber.i("$TAG 顶面照明区域$index 关")
        tmpService.sendLightCeilingAreaCmd(index, false)
    }

    /** 墙面照明 一键 开 */
    fun onLightWallAllOn() {
        wallAllObs.set(true)
        for (i in 0 until 3) wallAreaObs[i].set(true)
        Timber.i("$TAG 墙面照明一键 开")
        tmpService.sendLightWallAllCmd(true)
    }

    /** 墙面照明 一键 关 */
    fun onLightWallAllOff() {
        wallAllObs.set(false)
        for (i in 0 until 3) wallAreaObs[i].set(false)
        Timber.i("$TAG 墙面照明一键 关")
        tmpService.sendLightWallAllCmd(false)
    }

    /** 墙面照明 区域 index: 1~3 开 */
    fun onLightWallAreaOn(index: Int) {
        wallAreaObs[index - 1].set(true)
        Timber.i("$TAG 墙面照明区域$index 开")
        tmpService.sendLightWallAreaCmd(index, true)
    }

    /** 墙面照明 区域 index: 1~3 关 */
    fun onLightWallAreaOff(index: Int) {
        wallAreaObs[index - 1].set(false)
        Timber.i("$TAG 墙面照明区域$index 关")
        tmpService.sendLightWallAreaCmd(index, false)
    }

    /** 空间区域灯光 一键 开 */
    fun onLightSpaceAllOn() {
        spaceAllObs.set(true)
        for (i in 0 until 3) spaceAreaObs[i].set(true)
        Timber.i("$TAG 空间区域灯光一键 开")
        tmpService.sendLightSpaceAllCmd(true)
    }

    /** 空间区域灯光 一键 关 */
    fun onLightSpaceAllOff() {
        spaceAllObs.set(false)
        for (i in 0 until 3) spaceAreaObs[i].set(false)
        Timber.i("$TAG 空间区域灯光一键 关")
        tmpService.sendLightSpaceAllCmd(false)
    }

    /** 空间区域灯光 区域 index: 1~3 开 */
    fun onLightSpaceAreaOn(index: Int) {
        spaceAreaObs[index - 1].set(true)
        Timber.i("$TAG 空间区域灯光区域$index 开")
        tmpService.sendLightSpaceAreaCmd(index, true)
    }

    /** 空间区域灯光 区域 index: 1~3 关 */
    fun onLightSpaceAreaOff(index: Int) {
        spaceAreaObs[index - 1].set(false)
        Timber.i("$TAG 空间区域灯光区域$index 关")
        tmpService.sendLightSpaceAreaCmd(index, false)
    }
}
