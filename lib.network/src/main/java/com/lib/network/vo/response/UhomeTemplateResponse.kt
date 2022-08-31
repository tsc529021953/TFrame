package com.lib.network.vo.response
import com.lib.network.vo.UhomeItem

/**
 * @Author qiukeling
 * @Date 2020-03-19-10:10
 * @Email qiukeling@nbhope.cn
 */
class UhomeTemplateResponse {
    var total: Int = 0
    var width: Int = 0
    var height: Int = 0
    var templateId: Int = 0
    var modules: List<ModuleBean>? = null

    class ModuleBean {
        var devices: ArrayList<UhomeItem>? = null
        var height: Int = 0
        var width: Int = 0
        var moduleType: Int = 0
    }
}