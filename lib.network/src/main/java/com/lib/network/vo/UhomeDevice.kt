package com.lib.network.vo

/**
 * @Author qiukeling
 * @Date 2020-03-20-15:52
 * @Email qiukeling@nbhope.cn
 */
class UhomeDevice {
    var roomName: String? = null
    var deviceId: String? = null
    var deviceName: String? = null
    var deviceUid: String? = null
    var roomId: String? = null
    var onlineStatus = false
    var deviceCata: String? = null
    var devicePanel: String? = null
    var platformId: Long = 0
    var irDeviceId: String? = null
    var deviceAction: String? = null
    var hopeCata = 0
    var familyId: String? = null
    var status: UhomeItem.StatusBean? = null
    var images: List<String>? = null
    var selected = false
    var selectedDesktop = false
    var extAttr: String? = null
    var objectMac: String? = null
    var iconModel: String? = null
    var deviceModel: String? = null
}