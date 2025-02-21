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
