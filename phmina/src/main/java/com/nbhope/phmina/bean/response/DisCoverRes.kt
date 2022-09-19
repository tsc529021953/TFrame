package com.nbhope.phmina.bean.response

import com.nbhope.phmina.bean.request.BaseParams

/**
 *Created by ywr on 2021/6/24 9:06
 */
class DisCoverRes : BaseParams() {
    var isTcpService = false
    var tagSn: String? = ""
    var tcpServicePort = 0
    var tcpServiceIp: String? = ""
}