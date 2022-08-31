package com.lib.network.vo

/**
 * @Author qiukeling
 * @Date 2020-03-20-10:56
 * @Email qiukeling@nbhope.cn
 */
class UhomeSecure {
    /**
     * { "familyId": "6fa343d137594e32943ff14fdeb0d36c",
     * "isArm": 0,//0：表示布防，1：表示撤防，默认
     * "isOccurred": 1, // 是否正在报警，0：正常，1：报警
     * "name": "在家安防",
     * "secType": 0, // 0：在家安防，1：外出安防
     * "securityId": "02714671deb3460b8d3e2aaa7e781277" }*/

    var familyId:String? = null
    var isArm:Int = 1
    var isOccurred:Int = 0
    var name:Int = 0
    var secType:Int = 0
    var securityId:String? = null

}