package com.lib.network.vo.response

/**
 * @Author qiukeling
 * @Date 2020-03-19-10:10
 * @Email qiukeling@nbhope.cn
 */
class UhomeTemplatePageResponse {
    var total: Int = 0
    var list: List<TemplateBean>? = null


    class TemplateBean {
        var templateId: Int? = null
        var modules: ArrayList<ModuleBean>? = null
    }

    class ModuleBean {
        var devices: ArrayList<DeviceBean>? = null
        var height: Int? = null
        var moduleType: Int? = null
    }

    class DeviceBean {
        var extAttr: String? = null
        var hopeCata: Int? = null
        var objectCata: Int? = null
        var objectId: String? = null
        var objectName: String? = null
        var platformId: Long? = null
        var deviceCata: String? = null
        var deviceIcon1: String? = null
        var deviceIcon2: String? = null
        var objectMac: String? = null
        var deviceModel: String? = null
        var iconModel: String? = null
        var familyId: String? = null
    }
}