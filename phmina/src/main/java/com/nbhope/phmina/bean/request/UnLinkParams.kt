package com.nbhope.phmina.bean.request

/**
 *Created by ywr on 2021/6/24 8:59
 * 主机主动断开从机的链接
 */
class UnLinkParams : BaseParams() {
    companion object{
        val MINOR="minor"  // 主机
        val MAIN="main"  //从机
    }
    var tagSn: String? = null

    /**
     * client 客户端请求结束       service 结束
     */
    var from= MINOR
}