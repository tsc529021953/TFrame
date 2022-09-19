package com.nbhope.lib_frame.constants

import android.os.Build

/**
 * 常量类
 * Created by zhouwentao on 2020/4/27.
 */
object HopeConstants {

//    var IS_JENKINS = BuildConfig.IS_JENKINS

    //网络是否可用
    var IS_NETWORK_AVAILABLE = false

    //本机IP
    @JvmStatic
    var LOCAL_IP: String? = null

    //本机SN
    @JvmStatic
    var LOCAL_SN: String? = null

    //Bugly ID
//    var BUGLY_ID: String = "712b220c3f"
//    var THIRD_BUGLY_ID: String = "3f0a247c46"
//    var BUGLY_ID: String? = "f526ecc1fb"


    //阿里百川热更新 APPID
    var HOTFIX_APP_ID = "30045151"

    //阿里百川热更新 APPSECRET
    var HOTFIX_APP_SECRET = "f6f647cc9783052e3beabb99878f9df2"

    //阿里百川热更新 RSA
    var HOTFIX_APP_RAS = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC94qk1uXNnp8CY1wvmqR3z7iht3NwrVuveckGNp/3m90q+nS/6iPkAjTeJML+DYKLdKi9aa/O+t4XYhX/GMX3SJ0H8snQJG8+FGv04qI6RNhlLsQmkp6XCaggJdpPJGbbZBk/S4hjQAzUGHQgNhMk3BwFumobjOr/YKTlwZtynJhJ9VOsVrYKj8XI4UgepQ69eV6RKDmGiq3i+34tjgrrUYGQinQ1wGz/9ztBpIBx4jFPoRMNWNasNm5Z+ETst9ud+QoxoiAlcjAwUms0gXpde3k/q04qlKZ+gykUHfktqPI+8uUZAxpzyDpR+v2/SDLRew444ERRs2dCGSAPbvH51AgMBAAECggEADUvKx21F7HMtpEYimjMrWsJJaG/3pr0+ZOQhPPcPa1XTkQdWrbdboDNDOUMFlgB1RwFGMZGvjqIxKBko+krK4aVIFJa2U1D0NgDOzGHZIcfk+4zY/QOUMsnT6D26WXGIgSfU/RJB5Ibk9pmfz7qFkGnYKtN9MHoTpuI0GG/LcZS0x7ASyBLQw/+B6uxt5O1LnkESa7HaDwo5CrpppARB7jHmNyDh11RzXKbqKMjarcOErEK+amAztIqSaPWaFZ2FUp/nQ39Un//nwzxuAfHggak4TOu9nesK9/uTD9AsQBExRpie3ycN5jro2OP5oKw50MkwtxfEgpsXOfImQbAw/QKBgQD5gsMuaPFVB3o0ceeNtXwhD8Ci/3LC4g51cBXVdlydKN4qUO0hkBDqPYgVLmcPBNIlsMXz+z3H+vnX+Vh1niBmq7WXe6nSAhgSf+V98gyCmPcrVM78Pc+FJ16D2cXsq567m+Se3fC2HKGsUcYC8HdQfEFjDWf2h7nP56LT7h2ZawKBgQDC0un2rv0tZgD7CA5iclit9mkABXlv9E+T9mXyCg0uxVjPYPBpRPCw19uwesOR9s/35NqKHmgROMYQNvS9IEntUTofE6lhZbEvWa0mqONSVI/XNBygKZ3HEs0Kyz7dlm0b0xJFU+OfPrQVw35jo/LcC9himgmXmSrVjRuNi6LfnwKBgQD1lO/5aQ9ICsEMh6RKhXu0quWHSXiyOn/StUy4OvSzNztcWSTdaQhNFd0wQ+jqDmus704Xc41+nRd2rRSVR9tKUu28ONqQOsFoy/ucuDX6AQce3i2QdmIgA/zyN9Govc7Rh4JBn99Bz/KNQjtsPzSgwnw1O0e9jh+kc7B/ehomGwKBgAIW/xl9UQvuny4SLQ1TSq47CW9Nn7ratQvSRc+t2exZg5Vd5dZLPgW3mwyulHB5ZEu1cb6vitA8eqtr23433XMlPulcbaG01Iy8eoYCo8WbUJuvXGs/Zwjeo8Js4bTAy1TUE8sYlkV8B7SAD1gERzOjEOQl4Np2cyYtSFFhYRLjAoGAYD2k5Beadg8uG+V6KQhFw80pmEQvR0LsxdsbC7T27pmPApVBIjQYTg33khtci2Zo5ItyzJKPqUcYne4DDP8RJXU5pmE5X6Ov7ztjN30xxrfn6Tm20pq9HPFYoceldSzYX00EGX2CaDX80/MKuIbAONTlM1Ce7CjEvB96VZhxMSA="

    const val HOPE_PLATFORM_ID = "750837261197414400"


    //*****************************************************BuildConfig*****************************************//

//    //本机名字
//    //当是DEBUG包且不是JENKINS打的时候,为了方便调试可以在gradle.properties里改
//    //RELEASE包均读固件MODEL作为设备名字
//    var DEVICE_NAME = Build.MODEL
//
//    //播放器类型 release  debug
//    var isDebug = BuildConfig.BUILD_TYPE == "debug"
//
//    //播放器版本类型
//    var PLAYER_MODE = BuildConfig.PLAYER_MODE
//    var PLAYER_SINGLE = "single"
//    private var PLAYER_DUAL = "dual"
//
//    //语音版本
//    var VOICE_TYPE = BuildConfig.VOICE
//    var VOICE_SPEECH = "speech"
//    var VOICE_TIANMAO_SDK = "tianmaosdk"
//    var VOICE_TIANMAO_APP = "tianmaoapp"
//    var VOICE_NONE = "none"
//
//    //判断设备是否有向往底座,目前s5pro,musicBox-3B没有向往底座
//    var IS_HOPE_BASE_STAND: Boolean = BuildConfig.HOPE_BASE_STAND
//
//    //判断设备分区音量条是否显示,目前s5pro,musicBox-3B不显示
//    var IS_HOPE_VOLUME_SHOW: Boolean = BuildConfig.HOPE_VOLUME_SHOW
//
//    //#CPU型号，跟签名相关
//    //#当CPU为RK3368时只支持单路播放器
//    var CPU: String = BuildConfig.CPU
//
//    //KTV版本
//    var IS_KTV: Boolean = BuildConfig.IS_KTV
//
//    //判断分区是否独立,也就是分区三,在三个音源切换中,是否保持一致,目前就S7和有轨音乐是独立的
//    var IS_TUNNEL_INDEPENDENT: Boolean = BuildConfig.IS_TUNNEL_INDEPENDENT
//
//    //判断是否没有电台
//    var IS_RADIO: Boolean = BuildConfig.IS_RADIO
//
//    //判断是否是向往版本
//    var IS_HOPE: Boolean = BuildConfig.IS_HOPE
//
//    var BuglyId: String = BuildConfig.BUGLY_ID
//
//    var COMPANY: String = BuildConfig.COMPANY
//
//    //判断分区设置中,是否有音源栏显示,目前是单路和HOPE-Z8.Z10,为false
//    var IS_TUNNEL_SOURCE_SHOW: Boolean = isDual()
//
//    //在没有向往底座的设备中,音乐的通道数
//    var NOBASE_MUSIC_CHANNEL_COUNT: Int = BuildConfig.NOBASE_MUSIC_CHANNEL_COUNT
//
//    var IS_XIAOMI: Boolean = BuildConfig.IS_XIAOMI
//
//    val XIAOMI_BINDED_CODE = "-6"
//
//    //向往的code码,包含了功能和代号
//    val HOPE_CODE = BuildConfig.HOPE_CODE
//
//    val ISNEW_DESIGN= BuildConfig.THEME!="000" && BuildConfig.THEME!="001"
//
//    ///界面和主题
//    val THEME=BuildConfig.THEME
//
//    ///是否首页滑动的新智能家居
//    val IS_HOME_SLIDE_UHOME=BuildConfig.IS_HOME_SLIDE_UHOME


//
//    @JvmStatic
//    var SCREEN_SIZE: String = BuildConfig.SCREEN_SIZE
//
//    //判断有没有切换分区的功能
//    val IS_HAS_SWITCH_SOURCE = BuildConfig.IS_HAS_SWITCH_SOURCE
//
//    //语音唤醒的cpu的型号,老设备是AC108,3X,3B等新设备是7210
//    val WAKE_UP_CPU = BuildConfig.WAKE_UP_CPU
//
//    //判断串口是否能同时接收和发送
//    val isDoubleChannel = BuildConfig.IS_DOUBLE_CHANNEL
//
//    //是否思必驰语音外接
//    val IS_VOICE_OUT = BuildConfig.IS_VOICE_OUT
//
//    //思必驰语音配置分支
//    val SPEECH_ALIAS = BuildConfig.SPEECH_ALIAS
//
//    //单路设备,控制双路底座
//    val IS_SINGLE_CONTROL_DUAL_BASE = BuildConfig.IS_SINGLE_CONTROL_DUAL_BASE
//
//    //是否隐藏蓝牙
//    val IS_HIDE_BLUETOOTH = BuildConfig.IS_HIDE_BLUETOOTH


//    fun isDual(): Boolean {
//        return PLAYER_MODE == PLAYER_DUAL
//    }
//
//    fun isSingle(): Boolean {
//        return PLAYER_MODE == PLAYER_SINGLE
//    }
//
//    fun isSpeech(): Boolean {
//        return VOICE_TYPE == VOICE_SPEECH
//    }
//    @JvmStatic
//    fun isTianMaoApp(): Boolean {
//        return VOICE_TYPE == VOICE_TIANMAO_APP
//    }
//
//    fun isTianMaoSDK(): Boolean {
//        return VOICE_TYPE == VOICE_TIANMAO_SDK
//    }
//
//    fun isVoiceNone(): Boolean {
//        return VOICE_TYPE == VOICE_NONE
//    }

//    fun isScreenDev(): Boolean {
//        return BuildConfig.HAS_SCREEN
//    }    //*****************************************************BuildConfig*****************************************//
//
//    fun noBaseMusicChannelCount(): Int {
//        return NOBASE_MUSIC_CHANNEL_COUNT
//    }
//
//    fun isScreenSmall(): Boolean {
//        return BuildConfig.SCREEN_SIZE == "small"
//    }
//
//    fun isScreenNormal(): Boolean {
//        return BuildConfig.SCREEN_SIZE == "normal"
//    }
//
//    fun isScreenBig(): Boolean {
//        return BuildConfig.SCREEN_SIZE == "big"
//    }
//
//    fun isChuqiao(): Boolean {
//        return COMPANY == "HD01" || COMPANY == "HD02"
//    }
//
//    fun isLangGe():Boolean {
//        return COMPANY == "HD04"
//    }
//
//
//    fun getSmartHome(): String? {
//       return BuildConfig.THIRT_SMART_HOME
//    }
}