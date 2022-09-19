package com.nbhope.phmina.bean.request


/**
 *Created by ywr on 2021/6/24 8:59
 */
class IntiveLinkParams : BaseParams() {
    var isTcpService = false
    var timestamp: Long = 1L

    /**
     *  Placeholder for unknown bandwidth. This is the initial value and will stay at this value
     * if a bandwidth cannot be accurately found.   UNKNOWN 0
     *
     * Bandwidth under 150 kbps. POOR  1
     * Bandwidth between 150 and 550 kbps.  MODERATE  2
     * Bandwidth between 550 and 2000 kbps.  GOOD 3
     * EXCELLENT - Bandwidth over 2000 kbps. EXCELLENT 4

     * */
    var connectionQuality = -1  //  1

    var tagSn: String? = null
    var intiveSn: String? = null
    var ip: String? = null
    var port: Int? = 0
    var groupId: Int? = -1
}