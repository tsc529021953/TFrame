package com.nbhope.lib_frame.event

import com.nbhope.lib_frame.bus.event.BaseEvent


/**
 * @Author qiukeling
 * @Date 2019-08-22-16:26
 * @Email qiukeling@nbhope.cn
 */
data class RemoteMessageEvent(val cmd:String, val data:String) : BaseEvent(){
    override fun toString(): String {
        return "RemoteMessageEvent(cmd='$cmd', data='$data')"
    }
}