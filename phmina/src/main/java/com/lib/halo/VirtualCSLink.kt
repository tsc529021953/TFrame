package com.lib.halo

import com.ethanco.halo.turbo.mina.MySession
import com.google.gson.JsonParser

/**
 *Created by ywr on 2021/6/28 16:10
 */
class VirtualCSLink(private var mClient: HaloThread?, private var mService: HaloThread?) {
    fun sendToService(msg: String) {
        val receive = JsonParser().parse(msg).asJsonObject
        mService?.onMessageReceiver(MySession(), msg, receive["cmd"].asString, receive.get("params"))
    }

    fun sendToClient(msg: String) {
        val receive = JsonParser().parse(msg).asJsonObject
        mClient?.onMessageReceiver(MySession(), msg, receive["cmd"].asString, receive.get("params"))
    }
}