package com.lib.network.vo

import androidx.databinding.ObservableBoolean


/**
 *Created by ywr on 2021/4/6 17:01
 */
class AutoSensorAction {

    /**
     * {
    "triggerExpress": "{\"wet\":\"\u003E20\"}",
    "triggerName": "湿度高于二十"
    }
     */

    var triggerExpress: String? = null
    var triggerName: String? = null
    var select = ObservableBoolean(false)

}