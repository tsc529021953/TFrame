package com.lib.network.vo.response

import com.lib.network.vo.UhomeDevice
import com.lib.network.vo.UhomeFamily

/**
 * @Author qiukeling
 * @Date 2020-03-20-10:48
 * @Email qiukeling@nbhope.cn
 */
class UhomeThirdDeviceResponse {
    var total: String? = null
    var domain: String? = null
    var family: FamilyBean? = null
    var device: DeviceBean? = null

    class FamilyBean{
        var code = 0
        var message: String? = null
        var list: List<UhomeFamily>? = null
    }

    class DeviceBean{
        var code = 0
        var message: String? = null
        var list: List<UhomeDevice>? = null
    }
}