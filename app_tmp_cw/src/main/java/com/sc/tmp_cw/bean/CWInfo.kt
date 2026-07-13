package com.sc.tmp_cw.bean

/**
 * @author  tsc
 * @date  2025/1/13 13:38
 * @version 0.0.0-1
 * @description
 */
class CWInfo {

    var port = 16680

    var ip = "234.55.66.800"
    var title = ""
    var stations: List<StationBean>? = null
    var urgentNotify: List<NotifyBean>? = null
    var rtspUrl: String = ""

    // ===== 武汉通信协议 (FlavorB) 配置 =====
    /** 协议类型: "auto"=自动检测, "wuhan"=武汉协议, "legacy"=旧协议 */
    var protocolType: String = "auto"
    /** PISC TCP 服务器 IP */
    var tcpServerIp: String = "10.254.21.7"
    /** PISC TCP 服务器端口 */
    var tcpServerPort: Int = 5000
    /** 武汉 UDP 组播接收地址 */
    var wuhanUdpRxIp: String = "224.100.1.8"
    /** 武汉 UDP 组播接收端口 */
    var wuhanUdpRxPort: Int = 6008
    /** 武汉 UDP 组播发送地址 */
    var wuhanUdpTxIp: String = "224.100.1.8"
    /** 武汉 UDP 组播发送端口 */
    var wuhanUdpTxPort: Int = 6018
    /** 设备ID (武汉协议0xA1心跳用) */
    var deviceId: Int = 20
    /** 车厢位置 (武汉协议0xA1心跳用) */
    var carriagePosition: Int = 1

    inner class StationBean {
        var id = -1

        var cn = ""

        var en = ""

        var hc: ArrayList<String> = arrayListOf()
        var hcColor: ArrayList<String> = arrayListOf()

        override fun toString(): String {
            return "StationBean(id=$id, cn='$cn', en='$en', hc=$hc, hcColor=$hcColor)"
        }


    }

    inner class NotifyBean {
        var id = -1

        var msg = ""
    }
}
