package com.sc.lib_local_device.dao

/**
 * author: sc
 * date: 2022/9/20
 */
class CmdItem() {

    open var group  = 0

    open var index  = 0
    override fun toString(): String {
        return "CmdItem(group=$group, index=$index)"
    }


}