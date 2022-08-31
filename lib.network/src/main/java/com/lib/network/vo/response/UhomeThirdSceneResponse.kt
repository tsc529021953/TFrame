package com.lib.network.vo.response

import com.lib.network.vo.UhomeFamily
import com.lib.network.vo.UhomeItem
import com.lib.network.vo.UhomeScene
import com.lib.network.vo.UhomeSecure

/**
 * @Author qiukeling
 * @Date 2020-03-20-10:48
 * @Email qiukeling@nbhope.cn
 */
class UhomeThirdSceneResponse {
    var total: String? = null
    var display = 0
    var secure: List<UhomeSecure>? = null
    var scene: List<UhomeScene>? = null
}