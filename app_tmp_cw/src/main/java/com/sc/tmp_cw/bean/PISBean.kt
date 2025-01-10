package com.sc.tmp_cw.bean

/**
 * @author  tsc
 * @date  2025/1/10 17:20
 * @version 0.0.0-1
 * @description
 */
class PISBean {

    companion object {
        const val START_MESSAGE = "5041"
    }

    var start = START_MESSAGE
    var frameworkVersion: String = ""
    var modifyVersion: String = ""
    var lifeSignal: String = ""
    var checkSignal: String = ""
    var boardStatus: String = ""
    var startCode: String = ""
    var endCode: String = ""
    var currentCode: String = ""
    var nextCode: String = ""
    var urgentNotifyCode: String = ""
    var runDirection: String = ""
    var carNumber: String = ""
    var jiaoLuNumber: String = ""
    var carCiNumber: String = ""
    var lineNumber: String = ""
    var ignoreStation: String = ""
    var year: String = ""
    var month: String = ""
    var day: String = ""
    var hout: String = ""
    var minute: String = ""
    var second: String = ""
}
