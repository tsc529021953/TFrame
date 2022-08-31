package com.lib.network.vo

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.ObservableBoolean
import java.util.*
import kotlin.collections.ArrayList

/**
 *Created by ywr on 2021/4/6 17:01
 */
class AutoAction {

    /**
     * {
    "actionName": "打开", // 动作名称
    "actionAttr": "STATE", // 动作属性
    "actionValue": "ON", // 动作参数
    "actionParameter": "", // 附加参数
    "actionSub": true, // 是否有下级菜单
    "subText": "[{"code":"ring","name":"铃声"},{"code":"music","name":"歌曲"}]", // 下级菜单
    "cataId": 100002, // 设备品类编号
    "platformId": 750837261197414400, // 平台编号
    "refrenceId": 866484270732951600 // 平台为当前情景动作主键编号
    }
     */

    var actionName: String? = null
    var actionAttr: String? = null
    var actionValue: String? = null
    var actionParameter: String? = null
    var actionSub: Boolean? = null
    var subText: String? = null
    var cataId: String? = null
    var platformId: Long? = null
    var refrenceId: Long? = null
    var select = ObservableBoolean(false)


    class SubText() {
        var code: String? = null
        var name: String? = null
        var playCata: String? = null
        var select = ObservableBoolean(false)
    }
}