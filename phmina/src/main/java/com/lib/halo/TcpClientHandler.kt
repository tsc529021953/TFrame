package com.lib.halo

import android.util.Log
import com.ethanco.halo.turbo.Halo
import com.ethanco.halo.turbo.ads.IKeepAliveListener
import com.ethanco.halo.turbo.ads.ISession
import com.ethanco.halo.turbo.bean.KeepAlive
import com.ethanco.halo.turbo.impl.handler.StringLogHandler
import com.ethanco.halo.turbo.type.Mode
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.nbhope.phmina.base.Config
import com.nbhope.phmina.base.MinaConstants
import com.nbhope.phmina.base.Utils
import com.nbhope.phmina.bean.data.Cmd
import com.nbhope.phmina.bean.data.GroupInfo
import com.nbhope.phmina.bean.request.LinkStateParams
import com.nbhope.phmina.bean.request.RegisterQuyParams
import timber.log.Timber

/**
 * TCP处理者
 *
 * @author EthanCo
 * @since 2017/1/19
 * tcpClientHandler 的数据的收发存在两种情况
 * 1. 当设备为主机时
 *      client 的数据时由TcpService直接通过中间通信类VirtualCSLink进行数据的通信，不创建的Halo网络通信机制
 * 2. 当设备为从机时
 *      client 当multicastHandler收到邀请后创建,并通过网络连接到主机的TcpServiceHandler 进行通信
 */
class TcpClientHandler(
    private val halomanager: IHaloManager,
    val tagIp: String,
    private val keeling: IKeepAliveListener,
    name: String,
    private val virtualCleint: Boolean
) : HaloThread(name) {
    private val tag_client = "TcpClientHandler"
    private var mSession: ISession? = null
    fun virtualInit() {
        Log.i(tag, "virtualInit")
        halomanager.getClientInfo().also {
            it.linkedSn = Utils.getSn()
        }
        doregister(null)
        quitSafely()
    }


    override fun onLooperPrepared() {
        if (!virtualCleint) {
            super.onLooperPrepared()
        }
    }


    override fun onMessageReceiver(
        session: ISession?,
        srcMsg: String,
        cmd: String,
        params: JsonElement
    ) {
        notifyReceiverMessage(cmd, params, srcMsg)
    }


    override fun sessionOpened(session: ISession?) {
        super.sessionOpened(session)
        Timber.i("XTAG Handler sessionOpened")
        mSession?.close()
        mSession = session
        doregister(session)
        var params = JsonObject()
        params.addProperty("state", "Opened")
        halomanager.getHandClietReceiverMsg()
            ?.notifyClientReceiverMsg(MinaConstants.CMD_CLIENT_STATE, params, null)
    }

    override fun sessionClosed(session: ISession?) {
        super.sessionClosed(session)
        Timber.i("XTAG Handler sessionClosed")
        mSession = null
        halomanager.distoryTcpClient()
        var params = JsonObject()
        halomanager.getClientInfo().also {
            it.type = if (it.type == 3) it.type else 1
            it.group = GroupInfo()
            it.linkState = false
            it.linkedSn = null
        }
        params.addProperty("state", "Closed")
        halomanager.getHandClietReceiverMsg()
            ?.notifyClientReceiverMsg(MinaConstants.CMD_CLIENT_STATE, params, null)
    }

    private fun doregister(session: ISession?) {
        val cmd = Cmd(MinaConstants.CMD_S_REGISTER, RegisterQuyParams().also { it ->
            it.clientInfo = halomanager.getClientInfo().also { info ->
                info.linkState = true
                info.type = 2
            }
        })
        sendMsg(Gson().toJson(cmd))
    }

    fun sendMsg(msg: String) {
        sendMsg(mSession, msg, virtualCleint)
    }

    private fun notifyReceiverMessage(cmd: String, params: JsonElement, srcMsg: String) {
        when (cmd) {
            MinaConstants.CMD_INVITE_LINK_M_RS -> {
                var data = Gson().fromJson(params, LinkStateParams::class.java)
                data.clientInfos?.find { it?.hopeSn == Utils.getSn() }?.apply {
                    halomanager.getClientInfo().also {
                        it.group.id = this.group.id
                    }
                }
            }
        }
        halomanager.getHandClietReceiverMsg()?.notifyClientReceiverMsg(cmd, params, srcMsg)
    }

    @Synchronized
    override fun sendMsg(session: ISession?, msg: String, other: Any) {
        if (other is Boolean) {
            if (other) {
                halomanager.sendToLocalcs(msg, false)
            } else {
                if (!isRunning()) {
                    Timber.i("非运行中，请检测网络-client")
                    return
                }
                mSession?.write(msg)
            }
        }
    }

    override fun getHalo(): Halo {
        Timber.i("XTAG TcpClientHandler 连接服务")
        return Halo.Builder()
            .setMode(Mode.MINA_NIO_TCP_CLIENT)
            .setBufferSize(Config.tcpBufferSize)
            .setSourcePort(Config.MINA_CLIENT_PORT)
            .setTargetIP(tagIp)
            .setTargetPort(Config.MINA_SERVICE_PORT)
            .addHandler(StringLogHandler(tag_client))
//                .setCodec(textLineCodecFactory)
            .setKeepAlive(KeepAlive(6, 30,  keeling))
            .addHandler(this)
            .build()
    }


    override fun startHaloRs(rs: Boolean?) {
        Log.i(tag, "startHaloRs :$rs")
        if (rs == true) {
            //服务端开启成功，发送广播通知其他的可能存在的服务关闭或退出

        } else {
            quitSafely()
            halomanager.getClientInfo()
        }
    }

}