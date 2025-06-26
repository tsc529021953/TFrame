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
    var frameworkVersion: Int = -1
    var modifyVersion: Int = -1
    var lifeSignal: Int = -1
    var checkSignal: Int = -1
    var boardStatus: Int = -1

    var startCode: Int = -1
    var endCode: Int = -1
    var currentCode: Int = -1
    var nextCode: Int = -1

    var urgentNotifyCode: Int = -1
    var runDirection: Int = -1
    var carNumber: Int = -1
    var jiaoLuNumber: Int = -1
    var carCiNumber: Int = -1
    var lineNumber: Int = -1
    var ignoreStation: Int = -1
    var year: Int = -1
    var month: Int = -1
    var day: Int = -1
    var hour: Int = -1
    var minute: Int = -1
    var second: Int = -1

    override fun toString(): String {
        return "PISBean(start='$start', frameworkVersion=$frameworkVersion, modifyVersion=$modifyVersion, lifeSignal=$lifeSignal, checkSignal=$checkSignal, boardStatus=$boardStatus, startCode=$startCode, endCode=$endCode, currentCode=$currentCode, nextCode=$nextCode, urgentNotifyCode=$urgentNotifyCode, runDirection=$runDirection, carNumber=$carNumber, jiaoLuNumber=$jiaoLuNumber, carCiNumber=$carCiNumber, lineNumber=$lineNumber, ignoreStation=$ignoreStation, year=$year, month=$month, day=$day, hour=$hour, minute=$minute, second=$second)"
    }

//    override fun toString(): String {
//        return "PISBean(start='$start', frameworkVersion=$frameworkVersion, modifyVersion=$modifyVersion, lifeSignal=$lifeSignal, checkSignal=$checkSignal, boardStatus=$boardStatus, startCode=$startCode, endCode=$endCode, currentCode=$currentCode, nextCode=$nextCode, urgentNotifyCode=$urgentNotifyCode, runDirection=$runDirection, carNumber=$carNumber, jiaoLuNumber=$jiaoLuNumber, carCiNumber=$carCiNumber, lineNumber=$lineNumber, ignoreStation=$ignoreStation, year=$year, month=$month, day=$day, hour=$hour, minute=$minute, second=$second)"
//    }

}
