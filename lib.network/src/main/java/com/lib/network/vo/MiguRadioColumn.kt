package com.lib.network.vo

/**
 * Created by Caesar
 * email : caesarshao@163.com
 */
class MiguRadioColumn {
    var columnId:String? = null
    var columnName:String? = null
    var radios:List<MiguRadioBean>? = null

    constructor(columnName: String?) {
        this.columnName = columnName
    }
}