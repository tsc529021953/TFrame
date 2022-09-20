package com.lib.halo

import android.os.Build
import android.util.Log
import com.google.gson.JsonObject
import com.nbhope.phmina.base.HaloType
import com.nbhope.phmina.base.MinaConstants
import com.nbhope.phmina.base.Utils
import com.nbhope.phmina.bean.data.ClientInfo
import com.nbhope.phmina.bean.data.GroupInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

import java.lang.ref.WeakReference

/**
 * HopeHalo门面
 *
 * @author yanwenrui
 * @since 20210628
 *
 * 发起端的创建完服务端后，会虚拟一个客户端用于通信 参见 createVirtualLink 目的是减少发起端的压力
 */
class HaloManagerImp : IHaloManager {
    private val tag = "HaloManagerImp"
    private var isTcpService = false
    private var mCurrentServiceIp: String? = null
    private var mReceiverMsgHandClietReceiverMsg: IHaloManager.HandReceiverMsg? = null
    private var mClientInfo: ClientInfo = ClientInfo()

    private val haloMap = HashMap<HaloType, WeakReference<HaloThread>>()
    private var virtualCsLink: VirtualCSLink? = null
    private var mTimeTamp = 0L
    private var mConnectionQuality = 1
    private var retryOpenMutl=0 //尝试重新启动3次


    /**
     *
     * @param halo
     */
    private fun startHalo(type: HaloType, other: String? = "") {
        val haloThread = createHaloThread(type, other!!)
        haloThread.start()
        haloMap[type] = WeakReference(haloThread)
    }


    private fun createHaloThread(type: HaloType, other: String): HaloThread {
        return when (type) {
            HaloType.MUTICAST -> {
                MulticastHandler(this, "MUTICAST")
            }
            HaloType.TCP_CLIENT -> {
                TcpClientHandler(
                        this, other, HopeKeepAliveListener(), "TCP_CLIENT",
                        other == MinaConstants.VIRTUAL_CLIENT
                )
            }
            HaloType.TCP_SERVER -> {
                TcpServiceHandler(this, "TCP_SERVER")
            }

        }
    }

    /**
     * 结束指定的服务
     */
    private fun stopHalo(type: HaloType) {
        try {
            var haloType = haloMap[type]?.get()
            haloType?.quitSafely()
            haloMap.remove(type)
            haloType = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun getHaloThread(type: HaloType): WeakReference<HaloThread>? {
        return haloMap[type]
    }

    override fun setTcpService(isTcpService: Boolean) {
        this.isTcpService = isTcpService
    }

    override fun isTcpService(): Boolean {
        return isTcpService
    }

    /**
     * 当设备为服务端时为节省资源船舰虚拟的cs通道
     */
    override fun createTcpService(ip: String?) {
        //正在运行中不可再次开启
        getHaloThread(HaloType.TCP_SERVER)?.get()?.apply {
            if (this.isRunning()) {
                Log.i(tag, "服务端运行中请勿反复启动")
                return
            }
        }
        this.mCurrentServiceIp = ip
        this.isTcpService = true
        startHalo(HaloType.TCP_SERVER, "")
    }

    private fun createVirtualLink() {
        getHaloThread(HaloType.TCP_CLIENT)?.apply {
            virtualCsLink =
                    VirtualCSLink(
                            getHaloThread(HaloType.TCP_CLIENT)?.get(),
                            getHaloThread(HaloType.TCP_SERVER)?.get()
                    )

            (this.get() as TcpClientHandler).virtualInit()
        }
    }

    override fun distoryTcpService() {
        isTcpService = false
        stopHalo(HaloType.TCP_SERVER)
        stopHalo(HaloType.TCP_CLIENT)
        mClientInfo.also {
            it.group = GroupInfo()
            it.linkState = false
            it.type = if (it.type == 3) it.type else 1
        }
    }

    override fun createTcpClient(ip: String?) {
        //正在运行中不可再次开启
        getHaloThread(HaloType.TCP_CLIENT)?.get()?.apply {
            if (this.isRunning()) {
                Log.i(tag, "客户端运行中请勿反复启动")
                return
            }
        }
        this.mCurrentServiceIp = ip
        Log.i("halo", "createTcpClient:${ip}")

        if (!isTcpService) {
            startHalo(HaloType.TCP_CLIENT, ip!!)
        }
    }

    override fun createVirtualClient() {
        startHalo(HaloType.TCP_CLIENT, MinaConstants.VIRTUAL_CLIENT)
        createVirtualLink()
    }


    override fun distoryTcpClient() {
        stopHalo(HaloType.TCP_CLIENT)
    }

    override fun createMutiCast() {
        Timber.i("XTAG createMutiCast 准备创建")
        //正在运行中不可再次开启
        getHaloThread(HaloType.MUTICAST)?.get()?.apply {
            if (this.isRunning()) {
                Log.i(tag, "客户端运行中请勿反复启动")
                return
            }
        }
        retryOpenMutl=0
        startHalo(HaloType.MUTICAST, "")
    }

    override fun distoryMutiCast() {
        stopHalo(HaloType.MUTICAST)
    }

    override fun getCurrentTcpServiceIp(): String? {
        return mCurrentServiceIp
    }

    override fun getHandClietReceiverMsg(): IHaloManager.HandReceiverMsg? {
        return mReceiverMsgHandClietReceiverMsg
    }

    override fun setHandClietReceiverMsg(handler: IHaloManager.HandReceiverMsg?) {
        this.mReceiverMsgHandClietReceiverMsg = handler
    }

    override fun getClientInfo(): ClientInfo {
        return mClientInfo
    }

    override fun setClientInfo(info: ClientInfo) {
        this.mClientInfo = info
    }


    override fun getLoaclSn(): String {
        return Utils.getSn()
    }

    override fun sendToLocalcs(msg: String, isService: Boolean) {
        if (isService) {
            virtualCsLink?.sendToClient(msg)
        } else {
            virtualCsLink?.sendToService(msg)
        }
    }

    override fun distoryAll() {
        stopHalo(HaloType.MUTICAST)
        stopHalo(HaloType.TCP_CLIENT)
        stopHalo(HaloType.TCP_SERVER)
    }

    override fun broadCastServiceCreate(timestamp: Long) {
        mTimeTamp = timestamp
        val params = JsonObject()
        params.addProperty("state", "Opened")
        getHandClietReceiverMsg()?.notifyServiceReceiverMsg(MinaConstants.CMD_SERVICE_STATE, params, null)
    }

    override fun broadCastMutlCreate(rs: Boolean?) {
        //重试10次排除可能存在的网络异常
        Timber.i("rs:$rs  retryOpenMutl:$retryOpenMutl")
        if (rs==true){
            retryOpenMutl=0
        }
       if (rs==false&&retryOpenMutl<3){
          GlobalScope.launch(Dispatchers.Main) {
              delay(3000)
              startHalo(HaloType.MUTICAST)
              retryOpenMutl++
          }
       }
    }

    override fun getServiceCreateTime(): Long {
        return mTimeTamp
    }

    override fun getConnectionQuality(): Int {
        return mConnectionQuality
    }

    override fun clientSendMsg(msg: String) {
        getHaloThread(HaloType.TCP_CLIENT)?.get()?.apply {
            this.sendMsg(null, msg, isTcpService)
        }
    }

    override fun mutilSendMsg(msg: String) {
        getHaloThread(HaloType.MUTICAST)?.get()?.apply {
            this.sendMsg(null, msg, Any())
        }
    }

    override fun isRunning(type: HaloType): Boolean {
        getHaloThread(type)?.get()?.apply {
            return isRunning()
        }
        return false
    }
}