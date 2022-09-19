package com.lib.halo

import com.google.gson.JsonElement
import com.nbhope.phmina.base.HaloType
import com.nbhope.phmina.base.Utils
import com.nbhope.phmina.bean.data.ClientInfo
import java.lang.ref.WeakReference

/**
 *Created by ywr on 2021/6/24 8:46
 *client multi service  的同意管理类，主要包含创建，信息发送，生命周期控制和提醒，接收到消息的转发
 */
interface IHaloManager {
    /**
     * 根绝type 获取Halo实例
     */
    fun getHaloThread(type: HaloType): WeakReference<HaloThread>?

    /**
     * 设置当前设备是否为主机，主机同时也服务端
     */
    fun setTcpService(isTcpService: Boolean)
    fun isTcpService(): Boolean

    /**
     * 创建tcp服务器，ip为设备的ip地址
     */
    fun createTcpService(ip: String? = Utils.getLANIP())
    fun distoryTcpService()

    /**
     * 创建tcpclient
     * @param ip 服务器地址
     */
    fun createTcpClient(ip: String?)

    /**
     * 创建虚拟客户端，客户端的数据发送直接通过接口转递到tcp服务端，由其代发
     */
    fun createVirtualClient()
    fun distoryTcpClient()

    /**
     * 创建udp广播，发信设备，连接设备
     */
    fun createMutiCast()
    fun distoryMutiCast()
    fun getCurrentTcpServiceIp(): String?
    fun getHandClietReceiverMsg(): HandReceiverMsg?

    /**
     * 设置client的接受监听器
     */
    fun setHandClietReceiverMsg(handler: HandReceiverMsg?)

    /**
     * 获取客户端信息，即本地客户端的信息
     */
    fun getClientInfo(): ClientInfo
    fun setClientInfo(info: ClientInfo)
    fun getLoaclSn(): String

    /**
     * 发送数据到本地客户端或服务端
     * @param isService true 服务端  false 客户端
     */
    fun sendToLocalcs(msg: String, isService: Boolean)
    fun distoryAll()

    /**
     * 通知服务端创建成功
     * @param timestamp 创建成功的时间点
     */
    fun broadCastServiceCreate(timestamp: Long)

    /**
     * 通知广播是否创建成功
     */
    fun broadCastMutlCreate(rs: Boolean?)
    fun getServiceCreateTime(): Long

    /**
     * 获取网络质量，因为后期逻辑更改费用
     */
    @Deprecated("逻辑修改弃用")
    fun getConnectionQuality(): Int

    /**
     * 客户端发送信息
     */
    fun clientSendMsg(msg: String)

    /**
     * 广播发送信息
     */
    fun mutilSendMsg(msg: String)

    /**
     * 根绝type判断HaloThread 是否运行中
     */
    fun isRunning(type: HaloType): Boolean

    interface HandReceiverMsg {
        /**
         *客户端接收到的信息通知
         */
        fun notifyClientReceiverMsg(cmd: String, params: JsonElement?, srcMsg: String?)

        /**
         * 广播接收到信息的通知
         */
        fun notifyMutilReceiverMsg(cmd: String, params: JsonElement?, srcMsg: String?)

        /**
         * 服务端信息的通知
         */
        fun notifyServiceReceiverMsg(cmd: String, params: JsonElement?, srcMsg: String?)
    }
}