package com.nbhope.phmina.bean.request

import android.os.Build
import com.nbhope.phmina.base.Utils

/**
 *Created by ywr on 2021/6/28 15:04
 */
open class BaseParams {
    var hopeSn: String? = Utils.getSn()
    var name: String? = null
        get() = if (field == null) {
            getDefName()
        } else {
            field
        }

    private fun getDefName(): String? {
        return if (Utils.getSn() == null) {
            "unKnow"
        } else {
            val length = Utils.getSn().length
            val start = if (length > 6) length - 7 else 0
            Utils.getSn().substring(start)
        }
    }
}