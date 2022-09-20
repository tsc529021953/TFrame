package com.lib.halo

import com.ethanco.halo.turbo.Halo
import com.ethanco.halo.turbo.ads.IHandler
import com.ethanco.halo.turbo.ads.ISession
import com.ethanco.halo.turbo.impl.handler.HexLogHandler
import com.ethanco.halo.turbo.type.Mode
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.nbhope.lib_frame.utils.HopeUtils
import com.nbhope.phmina.base.Config
import com.nbhope.phmina.base.MinaConstants
import com.nbhope.phmina.bean.data.ClientInfo
import com.nbhope.phmina.bean.data.Cmd
import com.nbhope.phmina.bean.request.DiscoverQueryParams
import com.nbhope.phmina.bean.request.IntiveLinkParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.MulticastSocket
import java.util.*
import java.util.concurrent.Executors


/**
 * 组播处理者
 *
 * @author EthanCo
 * @since 2017/1/19
 */
class MulticastHandler(private val haloManager: IHaloManager, name: String) :
    HaloThread(name), IHandler {
    private val multtag = "MulticastHandler"
    private val gson = Gson()
    private var mSession: ISession? = null
    private var localSn = haloManager.getLoaclSn()

    private var isFinished = true

    private var ms : MulticastSocket? = null

    private var group: InetAddress = InetAddress.getByName(Config.mulIp)

    private var timer = Timer() //定时器本身就是一个单线程

    override fun onLooperPrepared() {
//        super.onLooperPrepared()
        Timber.i("MSTAG onLooperPrepared ${isRunning()}")
        if (!isRunning()) {
            isFinished = false
            ms = MulticastSocket(Config.MINA_MULTI_PORT)
            Timber.i("MSTAG onLooperPrepared ${ms?.reuseAddress} ${ms?.timeToLive}")
            ms?.joinGroup(group)
            ms?.loopbackMode = false
            startHaloRs(true)
            timer = Timer()
            //2、调用方法,处理定时任务
            timer.schedule(object : TimerTask() {
                override fun run() {
                    Timber.i("MSTAG 定时器到达")
                    try {
                        ms?.leaveGroup(group)
                        Thread.sleep(500)
                        ms?.joinGroup(group)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                        Timber.i("MSTAG $e")
                    }
                }
            }, 60000, 60000)
            // 开启一个
            if (!group.isMulticastAddress()){
                Timber.e("MSTAG please use multicast ip 224.0.0.0 to 239.255.255.255 ")
            }

            startscal()
            var buf = ByteArray(Config.mulBufferSize)
            var datagramPacket = DatagramPacket(buf, buf.size, group, Config.MINA_MULTI_PORT)
            while (!isFinished) {
                Timber.i("MSTAG 等待数据")
                Timber.i("MSTAG  ${ms?.isConnected} ${ms?.isBound} ${ms?.isClosed}")
                try {
                    ms?.receive(datagramPacket)
                    val message = String(buf, 0, datagramPacket.length)
                    Timber.i("MSTAG $message")
                    val receive = JsonParser().parse(message).asJsonObject
                    onMessageReceiver(null, message, receive["cmd"].asString, receive.get("params"))
                } catch (e: Exception) {
                    e.printStackTrace()
                    Timber.e("MSTAG ${e.message}")
                }
                Thread.sleep(100)
            }
            timer.cancel()
            Timber.i("MSTAG 释放")

        }
    }

    override fun quit(): Boolean {
        Timber.i("MSTAG quit ${isRunning()}")
        isFinished = true
        ms?.close()
        ms = null
        return super.quit()
    }

    override fun quitSafely(): Boolean {
        Timber.i("MSTAG quitSafely ${isRunning()}")
        isFinished = true
        ms?.close()
        ms = null
        return super.quitSafely()
    }

//    override fun sessionOpened(session: ISession?) {
//        super.sessionOpened(session)
//        mSession?.close()
//        mSession = session
//        startscal()
//    }
//
//    override fun sessionClosed(session: ISession?) {
//        super.sessionClosed(session)
//        mSession = null
//    }
//
    override fun onMessageReceiver(
        session: ISession?,
        srcMsg: String,
        cmd: String,
        params: JsonElement
    ) {
        when (cmd) {
            MinaConstants.CMD_DISCOVER -> {
                // 自己的
//                Timber.i("发送自身设备信息 ${haloManager.getClientInfo().hopeSn}" +
//                        " ${haloManager.getClientInfo().localIp} ${HopeUtils.getIP()}")
                Timber.i("XTAG 发送自身设备信息 ${haloManager.getClientInfo().localIp}")
                sendMsg(
                    session,
                    gson.toJson(Cmd(MinaConstants.CMD_DISCOVER_RS, haloManager.getClientInfo().also { it.deviceType = 1 })),
                    ""
                )
            }
            MinaConstants.CMD_DISCOVER_RS -> {
                Timber.i("接收到其他设备信息 $srcMsg")
                haloManager.getHandClietReceiverMsg()?.notifyMutilReceiverMsg(cmd, params, srcMsg)
            }
            MinaConstants.CMD_INVITE_LINK_M -> {
                val response = gson.fromJson(params, IntiveLinkParams::class.java)
                Timber.i("接收到连接邀请 ${response.ip} ${response.hopeSn}")
//                Log.i(tag, "hopeSn:${response.hopeSn}  localSn:$localSn ${haloManager?.getClientInfo().type}")
                // 是否只有正常模式才可接受同步
                if (response.tagSn == localSn && haloManager?.getClientInfo().type == 1) {  // 如果sn号相等 ，发起连接 不处于免打扰状态
                    Timber.i("开始连接到服务器${response.ip}")
                    haloManager?.getClientInfo().also {
                        it.linkedSn = response.hopeSn
                        it.linkedName = response.name
                    }
                    haloManager?.distoryTcpClient()
                    haloManager?.createTcpClient(response.ip)
                    haloManager.getHandClietReceiverMsg()
                        ?.notifyMutilReceiverMsg(cmd, params, srcMsg)
                }
            }
        }
    }

    @Synchronized
    override fun sendMsg(session: ISession?, msg: String, other: Any) {
        if (!isRunning()) {
            Timber.i("非运行中，请检测网络-mutl")
            return
        }
        var data = msg.toByteArray()
        val dataPacket = DatagramPacket(data, data.size, group, Config.MINA_MULTI_PORT)
        GlobalScope.launch(Dispatchers.IO) {
            ms?.send(dataPacket)
        }
//        mSession?.write(msg)
        Timber.i("MSTAG sendMsg $msg")
    }


    override fun getHalo(): Halo {
//        return Halo()
        return Halo.Builder()
            .setMode(Mode.MULTICAST)
            .setSourcePort(Config.MINA_MULTI_PORT)
            .setTargetPort(Config.MINA_MULTI_PORT)
            .setTargetIP("224.0.2.5")
            .setBufferSize(Config.mulBufferSize)
            .addHandler(HexLogHandler(multtag))
            .addHandler(this)
            .setThreadPool(Executors.newCachedThreadPool()) //TODO 线程池合并
            .build()
    }

    override fun startHaloRs(rs: Boolean?) {
        Timber.i("startHaloRs:$rs")
        haloManager.broadCastMutlCreate(rs)
    }

    fun startscal() {
        var da = ClientInfo()
        val cmd = Cmd(MinaConstants.CMD_DISCOVER, da)
        Timber.i("XTAG startscal 发送自身设备信息 ${da.localIp}")
        sendMsg(mSession, Gson().toJson(cmd), "")
//        var cmd = Cmd(MinaConstants.CMD_DISCOVER, DiscoverQueryParams().also { item ->
//            item.hopeSn = localSn
//        })
//        sendMsg(mSession, Gson().toJson(cmd), "")
    }

    override fun isRunning(): Boolean {
        return !isFinished
    }
}
//class MulticastHandler(private val haloManager: IHaloManager, name: String) :
//    HaloThread(name), IHandler {
//    private val multtag = "MulticastHandler"
//    private val gson = Gson()
//    private var mSession: ISession? = null
//    private var localSn = haloManager.getLoaclSn()
//    override fun sessionOpened(session: ISession?) {
//        super.sessionOpened(session)
//        mSession?.close()
//        mSession = session
//        startscal()
//    }
//
//    override fun sessionClosed(session: ISession?) {
//        super.sessionClosed(session)
//        mSession = null
//    }
//
//    override fun onMessageReceiver(
//        session: ISession?,
//        srcMsg: String,
//        cmd: String,
//        params: JsonElement
//    ) {
//        when (cmd) {
//            MinaConstants.CMD_DISCOVER -> {
//                Timber.i("发送自身设备信息 ${haloManager.getClientInfo().hopeSn}" +
//                        " ${haloManager.getClientInfo().localIp} ${HopeUtils.getIP()}")
//                sendMsg(
//                    session,
//                    gson.toJson(Cmd(MinaConstants.CMD_DISCOVER_RS, haloManager.getClientInfo())),
//                    ""
//                )
//            }
//            MinaConstants.CMD_DISCOVER_RS -> {
//                Timber.i("接收到其他设备信息 $srcMsg")
//                haloManager.getHandClietReceiverMsg()?.notifyMutilReceiverMsg(cmd, params, srcMsg)
//            }
//            MinaConstants.CMD_INVITE_LINK_M -> {
//                val response = gson.fromJson(params, IntiveLinkParams::class.java)
//                Timber.i("接收到连接邀请 ${response.ip} ${response.hopeSn}")
////                Log.i(tag, "hopeSn:${response.hopeSn}  localSn:$localSn ${haloManager?.getClientInfo().type}")
//                // 是否只有正常模式才可接受同步
//                if (response.tagSn == localSn && haloManager?.getClientInfo().type == 1) {  // 如果sn号相等 ，发起连接 不处于免打扰状态
//                    Timber.i("开始连接到服务器${response.ip}")
//                    haloManager?.getClientInfo().also {
//                        it.linkedSn = response.hopeSn
//                        it.linkedName = response.name
//                    }
//                    haloManager?.distoryTcpClient()
//                    haloManager?.createTcpClient(response.ip)
//                    haloManager.getHandClietReceiverMsg()
//                        ?.notifyMutilReceiverMsg(cmd, params, srcMsg)
//                }
//            }
//        }
//    }
//
//    @Synchronized
//    override fun sendMsg(session: ISession?, msg: String, other: Any) {
//        if (!isRunning()) {
//            Timber.i("非运行中，请检测网络-mutl")
//            return
//        }
//        mSession?.write(msg)
//    }
//
//
//    override fun getHalo(): Halo {
//        return Halo.Builder()
//            .setMode(Mode.MULTICAST)
//            .setSourcePort(Config.MINA_MULTI_PORT)
//            .setTargetPort(Config.MINA_MULTI_PORT)
//            .setTargetIP(Config.mulIp)
//            .setBufferSize(Config.mulBufferSize)
//            .addHandler(HexLogHandler(multtag))
//            .addHandler(this)
//            .setThreadPool(Executors.newCachedThreadPool()) //TODO 线程池合并
//            .build()
//    }
//
//    override fun startHaloRs(rs: Boolean?) {
//        Timber.i("startHaloRs:$rs")
//        haloManager.broadCastMutlCreate(rs)
//    }
//
//    fun startscal() {
//        var cmd = Cmd(MinaConstants.CMD_DISCOVER, DiscoverQueryParams().also { item ->
//            item.hopeSn = localSn
//        })
//        sendMsg(mSession, Gson().toJson(cmd), "")
//    }
//}