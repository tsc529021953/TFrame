package com.nbhope.phmina.base

/**
 * Halo 类型
 *
 * @author EthanCo
 * @since 2017/2/7
 */
enum class ClientType(val state: Int) {
    IDLE(0),  //闲置状态
    BUSY(1),  //忙碌
    DND(2)    //勿扰
}