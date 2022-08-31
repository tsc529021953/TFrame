package com.lib.network.vo

/**
 * @Author qiukeling
 * @Date 2020-03-20-10:56
 * @Email qiukeling@nbhope.cn
 */
class UhomeScene {
    /**
     *{
     * "sceneId": "2",
     * "sceneName": "灯光全关",
     * "sceneNo": "05fea79768d140fa8060511c18b698f2",
     * "selected": false, // 当前设备是否已加到主屏，true：已加此时返回的terminaId属性表示当前第三方情景所属的向往设备编号，false：未加
     * "status": "0", // 0表示全关、1表示全开（0、1是系统默认创建的情景模式，用来控制具有onoff控制方式的设备）、2表示用户创建的情景模式
     * "familyId": "",//房屋（家庭）编号，如果为空则不用处理
     * "deviceUid": "50294D000000" // 情景所绑定的网关id，只针对部分第三方情景
     * }
     */

    var sceneId:String? = null
    var sceneName:String? = null
    var sceneNo:String? = null
    var selected:Boolean = false
    var selectedDesktop:Boolean=false
    var status:String? = null
    var familyId:String? = null
    var deviceUid:String? = null
    var objectMac:String? = null
    var extAttr: String? = null
}