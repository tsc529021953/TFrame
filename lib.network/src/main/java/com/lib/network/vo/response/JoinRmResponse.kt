package com.lib.network.vo.response

/**
 * @Author qiukeling
 * @Date 2020/4/17-11:25 AM
 * @Email qiukeling@nbhope.cn
 */
class JoinRmResponse {
    var remv:Status? = null
    var join:Status? = null

    class Status{
        var code:Int = 0
        var desc:String? = null
        var message:String? = null
    }
}