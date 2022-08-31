package com.lib.network.vo

import java.io.Serializable

/**
 * @Author qiukeling
 * @Date 2020-03-19-10:11
 * @Email qiukeling@nbhope.cn
 */
class UhomeItem : MultiItemEntity, Serializable {

    companion object {
        const val COMMON = 0 //普通开关

        const val TEXT = 1 //状态显示 温湿度

        const val MULTIMODE = 2 //开关停

        const val PLAYER = 3 //播放器

        const val SCENE = 4 //情景

        const val INFRARED = 5 //普通红外

        const val EMPTY = 6 //空设备

        const val AIR = 7 //空调

        const val CURTAIN = 8 //窗帘

        const val DIMMING = 9 //调光灯

        const val TEMP_LAMP = 10 //色温灯

        const val TV = 11 //电视


        //public static int SCENE_PANEL = 5;//设备列表的情景面板

        //public static int SCENE_PANEL = 5;//设备列表的情景面板
        const val ACTION_SAME_AS_BOTTOM = 0 //执行操作和底部相同

        const val ACTION_DO_NOTHING = 1 //不执行任何操作

        const val ACTION_SUN_DIALOG = 2 //弹出阳光样式的对话框

        const val ACTION_PERCENT_DIALOG = 3 //弹出百分比样式的对话框

        const val ACTION_THREE_OPTIONS_DIALOG = 4 //弹出3个选项的控制

        const val ACTION_TOP_DO_NOTHING = 5 //顶部不执行任何操作
    }


    var isCheck = false
    var isCheckDaskTop = false
    var familyId: String? = null
    var roomId: String? = null
    var deviceAction: String? = null
    var deviceModel: String? = null
    var deviceCata: String? = null
    var devicePanel: String? = null
    var hopeCata = 0
    var objectCata: Long = 0
    var objectId: String? = null
    var objectMac: String? = null
    var iconModel: String? = null
    var objectName: String? = null
    var platformId: Long = 0
    var refrenceId: Long = 0
    var roomName: String? = null
        get() = if (field.isNullOrEmpty() || field.equals("null")) {
            "默认房间"
        } else {
            if (field!!.length > 4) {
                field = field!!.substring(0, 3)
            }
            field
        }


    var status: StatusBean? = null

    var images: List<String>? = null
    var extAttr: String? = null
    var placeCata: Int? = 0

    class StatusBean {
        // 当前状态，（off：关，on：开，stop：停）
        var status: String? = null

        // 当前亮度
        var light: String? = null

        // 前前百分比
        var percent: String? = null

        // 温度
        var temper: String? = null

        // 色温
        var ctp: String? = null

        // 风速
        var winder: String? = null

        // 模式
        var model: String? = null

        // 湿度
        var dampness: String? = null

        // 是否报警
        var alarm: Boolean? = null

        // 是否正常
        var normal: Boolean? = null

        // 存在，不存在
        var exists: Boolean? = null

        // 饱和度
        var satura: String? = null

        // 色度
        var hue: String? = null

        // 当前电量
        var electric: String? = null

        // gas浓度
        var gas: String? = null

        // Co浓度
        var co: String? = null

        // 幅度
        var level: String? = null

        // 在线状态
        var online = false

        // 节点编号
        var endNumber: Int? = null

        // 功率
        var power: String? = null

        // 电流
        var elecurrt: String? = null

        // 清除行程
        var clear: String? = null

        // 转向
        var change: String? = null

        // 噪音值
        var noise: String? = null

        // 浓度
        var potency: String? = null

        override fun toString(): String {
            return "StatusBean(status=$status, light=$light, percent=$percent, temper=$temper, ctp=$ctp, winder=$winder, model=$model, dampness=$dampness, alarm=$alarm, normal=$normal, exists=$exists, satura=$satura, hue=$hue, electric=$electric, gas=$gas, co=$co, level=$level, online=$online, endNumber=$endNumber, power=$power, elecurrt=$elecurrt, clear=$clear, change=$change, noise=$noise, potency=$potency)"
        }
    }

    override var itemType: Int = 0
        get() = if (objectCata == 0L) {
            if (devicePanel != null && devicePanel!!.isNotEmpty()) {
                devicePanel!!.toIntOrNull() ?: 0
            } else {
                0
            }
        } else {
            4
        }

    val itemCate: Int
        get() = if (deviceModel != null) {
            if (deviceModel == "kg") {
                COMMON
            } else if (deviceModel == "cgq") {
                TEXT //
            } else if (deviceModel == "cl" || deviceModel == "qt") {
                CURTAIN //
            } else if (deviceModel == "tgd" || deviceModel == "dj") {
                DIMMING
            } else if (deviceModel == "swd") {
                TEMP_LAMP//
            } else if (deviceModel == "bfq" || deviceModel == "bjyy" || deviceModel == "dgnzk") {
                PLAYER
            } else if (deviceModel == "kt") {
                AIR
            } else if (deviceModel == "dsj") {
                TV
            } else if (deviceModel == "tgswd") {
                TEMP_LAMP
            } else {
                0
            }
        } else if (objectCata == 0L) {
            if (hopeCata == 100001 || hopeCata == 100002) {
                TV
            } else if (hopeCata == 100003 || hopeCata == 104199) {
                AIR
            } else if (hopeCata == 100160 || hopeCata == 104499) {
                DIMMING
            } else if (hopeCata == 100513 || hopeCata == 100514 || hopeCata == 100515) {
                CURTAIN
            } else if (hopeCata == 103399) {
                TEMP_LAMP
            } else if (!devicePanel.isNullOrEmpty() && ("1" == devicePanel || "3" == devicePanel)) {
                devicePanel!!.toIntOrNull() ?: 0
            } else {
                0
            }
        } else {
            4
        }

    override fun toString(): String {
        return "UhomeItem(isCheck=$isCheck, isCheckDaskTop=$isCheckDaskTop, familyId=$familyId, roomId=$roomId, deviceAction=$deviceAction, deviceModel=$deviceModel, deviceCata=$deviceCata, devicePanel=$devicePanel, hopeCata=$hopeCata, objectCata=$objectCata, objectId=$objectId, objectMac=$objectMac, iconModel=$iconModel, objectName=$objectName, platformId=$platformId, refrenceId=$refrenceId, roomName=$roomName, status=$status, images=$images, extAttr=$extAttr, placeCata=$placeCata, itemType=$itemType, itemCate=$itemCate)"
    }


}