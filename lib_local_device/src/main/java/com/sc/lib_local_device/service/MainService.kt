package com.sc.lib_local_device.service

import com.alibaba.android.arouter.facade.template.IProvider
import com.google.gson.JsonObject
import com.nbhope.phmina.base.HaloType

/**
 * @Author qiukeling
 * @Date 2020/5/18-2:54 PM
 * @Email qiukeling@nbhope.cn
 *
 * 1. 开启组播
 */
interface MainService : IProvider {

//    fun updateGatewayIP()
    fun onChangeType()

    fun connectServer(ip: String)

    fun getStatus(type: HaloType): Boolean

    fun sendMulMessage(msg: String)

    fun sendClientMessage(msg: String)
}