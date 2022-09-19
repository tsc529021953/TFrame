package com.nbhope.phmina.bean.request

import com.nbhope.phmina.base.Config

/**
 *Created by ywr on 2021/6/24 8:59
 */
class IntiveComm : BaseParams() {
    var groupId: Int? = -1
    var tagSn: List<String?>? = null //目标地址
    var ipAddress: String? = "" //服务端地址
    var port: Int? = Config.MINA_SERVICE_PORT

    /**
     * @param type  1 邀请创建通话  2 邀请其他人通话 3天猫版本无法通话
     */
    var type: Int? = 1
    var isAccept = false  // 是否接受邀请

    var description = ""
}