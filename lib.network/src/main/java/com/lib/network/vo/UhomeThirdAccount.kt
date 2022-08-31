package com.lib.network.vo

/**
 * @Author qiukeling
 * @Date 2020-03-21-09:30
 * @Email qiukeling@nbhope.cn
 */
class UhomeThirdAccount {
    /**
     * platName : 阿里智能
     * platIcon : http://192.168.2.5:8080/multipart/open/image/2017/11/20171122/759173148784234496.jpg
     * refrenceId : 774702725827940352
     * authUrl : https://openapi.tuyacn.com/login/open/tuya/login/v1/index.html?client_id=8ta4mdefeuvedtpjh57j&redirect_uri=http://www.baidu.com&state=
     */
    var platName: String? = null
    var platIcon: String? = null
    var refrenceId: Long = 0
    var authUrl: String? = null
    override fun toString(): String {
        return "UhomeThirdAccount(platName=$platName, platIcon=$platIcon, refrenceId=$refrenceId, authUrl=$authUrl)"
    }


}