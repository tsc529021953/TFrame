package com.nbhope.phmina.base

/**
 * 配置
 * 未来如有需要，可扩展 存储至SP中
 *
 * @author EthanCo
 * @since 2017/2/7
 */
object Config {
    val MINA_CLIENT_PORT=11421
    val SYNC_SERVICE_PORT=11423
    
    
    //TCP 服务端 端口号
    val MINA_SERVICE_PORT = 11420
    //组播 源端口号
    val MINA_MULTI_PORT=11422

    //TCP 缓存大小
    val tcpBufferSize = 2048




    //组播 IP地址
    val mulIp = "224.0.2.1"

    //组播 缓存大小
    val mulBufferSize = 102400





}