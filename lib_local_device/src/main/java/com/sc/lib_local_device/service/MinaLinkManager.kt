package com.sc.lib_local_device.service

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.lib.halo.IHaloManager
import com.nbhope.phmina.base.Config
import com.nbhope.phmina.base.MinaConstants
import com.nbhope.phmina.base.Utils
import com.nbhope.phmina.bean.data.Cmd
import com.nbhope.phmina.bean.request.IntiveLinkParams
import timber.log.Timber


/**
 *Created by ywr on 2021/7/21 17:01
 *
 * 建立连接的逻辑 ：
 * a.服务端逻辑
 *  1. 局域网组播查找可用的设备
 *  2. 创建服务端并向选中设备发起邀请连接tcpservice   (a,创建服务 b.服务创建成功后发送mutilcast )
 * b.客户端逻辑
 *  1.接收到邀请后创建tcpclient 连接指定的服务端
 *  2.注册客户端信息
 *
 *对讲逻辑：
 * a.发起端
 *  1. 创建音频推送端
 *  2. 发起通话邀请
 *  3. 接收邀请反馈，  a 接受 连接对方推送RTsp服务端推送流  b.拒绝 关闭当前rtsp服务器
 * b.接收端
 *  1.接收到邀请后通知用户是否接受邀请
 *  2.用户操作 a.接受 1创建音频推送端rtsp服务器 2创建成功后想发起端发送可以通话的信息
 *            b.拒绝 1.发送拒绝信息 2.关闭连接，清除当前连接的数据
 *
 */
abstract class MinaLinkManager(val iHaloManager: IHaloManager) : IHaloManager.HandReceiverMsg {
    init {
        iHaloManager.setHandClietReceiverMsg(this)
    }

    data class QueryLinkInfo(val tagSn: String)

    private var linkInfo: QueryLinkInfo? = null
    fun createMultiCast() {
        iHaloManager.createMutiCast()
    }

    fun distoryMultiCast() {
        iHaloManager.distoryMutiCast()
    }

    fun createTcpClient(ip:String) {
        iHaloManager.createTcpClient(ip)
    }

    fun distoryClient() {
        iHaloManager.distoryTcpClient()
    }

    fun createTcpService() {
        iHaloManager.createTcpService()
    }

    fun distoryService() {
        iHaloManager.distoryTcpService()
    }


    fun distoryAll() {
        iHaloManager.distoryAll()
    }

    /**
     * 先开启服务端，服务端开启成功后发起邀请
     */
    fun intiveLink(tagSn: String) {
        this.linkInfo = QueryLinkInfo(tagSn)
        iHaloManager.createTcpService()
    }


    fun intiveLinkTo(tagSn: String?, ipAddress: String?) {
        var cmd = Cmd(MinaConstants.CMD_INVITE_LINK_M, IntiveLinkParams().also {
            it.tagSn = tagSn
            it.ip = ipAddress
            it.port = Config.MINA_SERVICE_PORT
        })
        iHaloManager.mutilSendMsg(Gson().toJson(cmd))
    }


    abstract fun notifyTcpServiceState(opened: Boolean)


    abstract fun notifyClientState(opened: Boolean)


    override fun notifyClientReceiverMsg(cmd: String, params: JsonElement?, srcMsg: String?) {
        when (cmd) {
            MinaConstants.CMD_CLIENT_STATE -> {
                var state = params?.asJsonObject?.get("state")?.asString
                notifyClientState(state == "Opened")
            }
            else -> {
                notifyReceiverMsg(cmd, params, srcMsg)
            }
        }

    }

    override fun notifyMutilReceiverMsg(cmd: String, params: JsonElement?, srcMsg: String?) {
        Timber.i("notifyMutilReceiverMsg:$cmd")
        notifyReceiverMsg(cmd, params, srcMsg)
    }

    /**
     * 当服务端开启成功后发起邀请
     */
    override fun notifyServiceReceiverMsg(cmd: String, params: JsonElement?, srcMsg: String?) {
        when (cmd) {
            MinaConstants.CMD_SERVICE_STATE -> { //服务端状态
                var data = params?.asJsonObject
                val state = data?.get("state")?.asString
                if (state == "Opened") {
                    intiveLinkTo(linkInfo?.tagSn, Utils.getLANIP())  //邀请连接
                } else if (state == "Closed") {
                }
                notifyTcpServiceState(state == "Opened")
            }
            else -> {
                notifyReceiverMsg(cmd, params, srcMsg)
            }
        }

    }

    abstract fun notifyReceiverMsg(cmd: String, params: JsonElement?, srcMsg: String?)

    fun upDataDeviceInfo(name: String?, notDisturb: Boolean) {
        iHaloManager?.getClientInfo().also {
            name?.apply {
                it.name = name
            }
            it.type = if (notDisturb) 3 else 1
        }
    }
}