package com.lib.halo

import android.util.Log
import com.ethanco.halo.turbo.Halo
import com.ethanco.halo.turbo.ads.ISession
import com.ethanco.halo.turbo.bean.KeepAlive
import com.ethanco.halo.turbo.impl.handler.StringLogHandler
import com.ethanco.halo.turbo.mina.IMySession
import com.ethanco.halo.turbo.type.Mode
import com.ethanco.json.convertor.convert.ObjectJsonConvertor
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.nbhope.phmina.base.Config
import com.nbhope.phmina.base.MinaConstants
import com.nbhope.phmina.bean.data.Cmd
import com.nbhope.phmina.bean.data.GroupInfo
import com.nbhope.phmina.bean.request.*
import timber.log.Timber
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * TCP处理者
 *  为节省资源，当设备是服务端时，不再创建客户端，
 *  创建一个虚拟的客户端用于接收处理数据
 */
class TcpServiceHandler(val haloManager: IHaloManager, name: String) : HaloThread(name) {
    var groupId = -1

    init {
        groupId = createGroupId()
    }

    /**
     *
     */
    private val registerList = CopyOnWriteArrayList<TcpSession>()  //防止同时读写导致崩溃
    private val serviceTag = "TcpServiceHandler"
    private val localSn = haloManager.getLoaclSn()

    override fun onMessageReceiver(
        session: ISession?,
        srcMsg: String,
        cmd: String,
        params: JsonElement
    ) {
        Log.i(tag, "XTAG onMessageReceiver$cmd  ,${params.toString()}")
        haloManager.getHandClietReceiverMsg()?.notifyClientReceiverMsg(cmd, params, null)
        when (cmd) {
            MinaConstants.CMD_CHANGE_GROUP -> {
                // 通知切换图组
            }
            MinaConstants.CMD_CHANGE_INDEX -> {
                // 通知切换图片
            }
//            MinaConstants.CMD_S_REGISTER -> {
//                doRegister(session, params, srcMsg)
//            }
//            MinaConstants.CMD_S_QURLIST -> {
//                doHandQurList(session, params)
//            }
//            MinaConstants.CMD_INVITE_COMM -> {
//                doIntiveComm(session, srcMsg, params)
//            }
//            MinaConstants.CMD_ICECANDIDATE_COMM -> {
//                doIntiveComm(session, srcMsg, params)
//            }
//            MinaConstants.CMD_INVITE_COMM_RS -> {
//                doIntiveComm(session, srcMsg, params)
//            }
//            MinaConstants.CMD_T_TEST -> {
//                doTransMsg(session, params)
//            }
//            MinaConstants.CMD_T_MUSIC_CTR_S -> {
//                doMusicCtrSend(session, params)
//            }
//            MinaConstants.CMD_T_MUSIC_ERRPR -> {
//                onMusicPlayError(srcMsg, params)
//            }
//            MinaConstants.CMD_S_QURUNLINK -> {
//                // 本机断开（主机、从机按钮断开）
//                unLinkSomeSession(srcMsg, params)
//            }
//            MinaConstants.CMD_S_NTP_S -> {
//                doNtpTiming(srcMsg, params)
//            }
        }
    }

    private fun doNtpTiming(srcMsg: String, params: JsonElement) {
        var data = Gson().fromJson<NtpTimeParams>(params, NtpTimeParams::class.java)
        data.crc = System.currentTimeMillis()
        registerList.find { data.hopeSn == it.clientInfo.hopeSn }?.apply {
            val cmd = Cmd(MinaConstants.CMD_S_NTP_R, data.also {
                it.csc = System.currentTimeMillis()
            })
            sendMsgDispach(this.session, Gson().toJson(cmd).toString(), data.hopeSn)
        }
    }

    /**
     * 查找到指定的session 并断开连接
     */
    private fun unLinkSomeSession(srcMsg: String, params: JsonElement) {
        var data = Gson().fromJson(params, UnLinkParams::class.java)
        var tagsn = if (data.from == UnLinkParams.MAIN) data.tagSn else data.hopeSn
        tagsn?.apply {
            var item = registerList.find { it.clientInfo.hopeSn == this }
            item?.session?.close()
            getSsession(this)?.session?.close()
        }
    }

    private fun onMusicPlayError(srcMsg: String, params: JsonElement) {
        val baseParams = Gson().fromJson(params, BaseParams::class.java)
        registerList.find { baseParams.hopeSn == it.clientInfo.hopeSn }?.apply {
            sendMsgDispach(this.session, srcMsg, this.clientInfo.hopeSn)
        }
    }

    private fun doMusicCtrSend(session: ISession?, params: JsonElement) {
        var receiverData = JsonObject()
        receiverData.addProperty("cmd", MinaConstants.CMD_T_MUSIC_CTR_R)
        receiverData.add("params", params)
        registerList.filter { it.clientInfo.hopeSn != localSn }.forEach {
            sendMsgDispach(it.session, receiverData.toString(), it.clientInfo.hopeSn)
        }

    }

    private fun doIntiveComm(session: ISession?, srcMsg: String, params: JsonElement) {
        val receiver = Gson().fromJson(params, IntiveComm::class.java)

        getSsession(receiver.tagSn).forEach {
            sendMsgDispach(it?.session, srcMsg, it?.clientInfo?.hopeSn)
        }
    }

    private fun doTransMsg(session: ISession?, params: JsonElement) {
        val receiver = Gson().fromJson(params, TestParams::class.java)
        registerList.forEach { session ->
            val cmd = Cmd(MinaConstants.CMD_T_TEST, TestParams().also {
                it.hopeSn = receiver.tagSn
                it.tagSn = session.clientInfo.hopeSn!!
            })
            sendMsgDispach(session.session, Gson().toJson(cmd), session.clientInfo.hopeSn)
        }
    }

    /**
     * 处理请求数据列表
     */
    private fun doHandQurList(session: ISession?, params: JsonElement?) {
        var params = Gson().fromJson(params, BaseParams::class.java)
        val list = registerList.filter { it.isLinkState }.map { it.clientInfo }
        val cmd = Cmd(MinaConstants.CMD_S_QURLIST_CB, list)
        sendMsgDispach(session, Gson().toJson(cmd), params.hopeSn)
    }

    private fun doRegister(session: ISession?, params: JsonElement, srcMsg: String) {
        val params = Gson().fromJson(params, RegisterQuyParams::class.java)
        val rs = registerList.find { it.clientInfo.hopeSn == params.hopeSn }
        if (rs == null) {
            registerList.add(TcpSession().also {
                it.isLinkState = true
                session?.apply {
                    it.session = session as IMySession
                    Timber.i("doRegister_sessionID:${it.session.id}")
                }
                it.clientInfo = params.clientInfo
                it.clientInfo.group.id = groupId
                it.clientInfo.linkState = true
            })

        } else {
            rs.clientInfo = params.clientInfo
        }
        notifyLinkedClientInfo(true)
    }

    private fun notifyLinkedClientInfo(state: Boolean) {

        var cmd = Cmd(MinaConstants.CMD_INVITE_LINK_M_RS, LinkStateParams().also {
            it.clientInfos = registerList.map {
                it.clientInfo
            }
            it.state = state
        })
        registerList.forEach {
            sendMsgDispach(it.session, Gson().toJson(cmd), it.clientInfo.hopeSn)
        }
    }

    override fun sessionClosed(session: ISession?) {
        super.sessionClosed(session)
        if (session is IMySession) {
            var item = registerList.find { it.session.id == session.id }.also {
                it?.clientInfo?.linkState = false
                it?.clientInfo?.group?.id = -1
            }
            Timber.i("sessionClosed_sessionID:${session.id}  itemData:$item")
            notifyLinkedClientInfo(false)
            registerList.remove(item)
//            if (registerList.size <= 1) {
//                quitSafely()//关闭服务器
//                Timber.i("sessionClosed 服务端连接数量<=1 自动关闭服务端")
//                val params = JsonObject()
//                params.addProperty("state", "Closed")
//                haloManager.getClientInfo().also {
//                    it.group = GroupInfo()
//                    it.linkState = false
//                    // 此处type不会为3，因为在创建服务过后，服务已经变成了2，忙碌模式
//                    //l 需要獲取勿擾模式的值
//                    it.type = if (it.type == 3) it.type else 1
//                }
//                haloManager.getHandClietReceiverMsg()?.notifyServiceReceiverMsg(
//                    MinaConstants.CMD_SERVICE_STATE,
//                    params,
//                    null
//                )
//                haloManager.setTcpService(false)
//            }
        }
    }

    override fun sessionOpened(session: ISession?) {
        super.sessionOpened(session)
        Timber.i("XTAG 有对象连接")
    }

    /**
     * type: 0 sn 1
     */
    override fun sendMsg(session: ISession?, msg: String, other: Any) {
        session?.write(msg)
    }

    @Synchronized
    fun sendMsgDispach(session: ISession?, msg: String, tagSn: String?) {
        if (!isRunning()) {
            Timber.i("非运行中，请检测网络-service")
            return
        }
        var islocal = tagSn == localSn
        if (islocal) {
            haloManager.sendToLocalcs(msg, true)
        } else {
            session?.write(msg)
        }
    }

    override fun getHalo(): Halo {
        return Halo.Builder()
            .setMode(Mode.MINA_NIO_TCP_SERVER)
            .setSourcePort(Config.MINA_SERVICE_PORT)
            .setBufferSize(Config.tcpBufferSize)
            .addHandler(StringLogHandler(serviceTag))
            .addHandler(this)
            .addConvert(ObjectJsonConvertor()) //如果是对象会转换为Json
//                .setCodec(textLineCodecFactory)
            .setKeepAlive(KeepAlive(30, 30,  HopeKeepAliveListener()))
            .build()
    }

    override fun startHaloRs(rs: Boolean?) {
        Log.i(tag, "startHaloRs :$rs")
        if (rs == true) {
            //服务端开启成功，发送广播通知其他的可能存在的服务关闭或退出
            haloManager.broadCastServiceCreate(System.currentTimeMillis())
            haloManager.createVirtualClient()

        }
    }

    private fun getSsession(sn: String): TcpSession? {
        return registerList.find { it.clientInfo.hopeSn == sn }.also { }
    }

    private fun getSsession(sn: List<String?>?): List<TcpSession?> {
        return registerList.filter {
            sn?.contains(it.clientInfo.hopeSn) == true
        }
    }

    private fun createGroupId(): Int {
        if (groupId == -1)
            groupId = Random().nextInt(1000)
        return groupId
    }

}