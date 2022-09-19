package com.nbhope.phmina.bean.request

import com.nbhope.phmina.bean.data.ClientInfo

/**
 *Created by ywr on 2021/6/24 8:59
 */
class RegisterQuyParams : BaseParams() {
    var clientInfo: ClientInfo? = null
    var state = false  //true 连接成功 fales 断开连接

}