package com.xs.xs_by.bean

/**
 * @author  tsc
 * @date  2024/4/26 17:14
 * @version 0.0.0-1
 * @description
 */
class AppInfo(var name: String = "", var packageName: String = "") {


    fun copy(info: AppInfo) {
        this.name = info.name
        this.packageName = info.packageName
    }

    override fun toString(): String {
        return "AppInfo(name='$name', packageName='$packageName')"
    }


}
