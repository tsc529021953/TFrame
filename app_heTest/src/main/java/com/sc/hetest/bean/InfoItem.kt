package com.sc.hetest.bean

/**
 * author: sc
 * date: 2022/12/10
 */
class InfoItem {

    companion object{
        const val STATE_NORMAL = 0
        const val STATE_SUCCESS = 1
        const val STATE_FAIL = 2
    }

    var title = ""

    var path = ""

    var state = STATE_NORMAL

}