package com.nbhope.phmina.bean.request

import com.nbhope.phmina.bean.data.ClientInfo

/**
 *Created by ywr on 2021/6/24 8:59
 */
class LinkStateParams : BaseParams() {
    var clientInfos: List<ClientInfo?>? = null
    var state = false  //true 连接成功 fales 断开连接

}