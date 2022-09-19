package com.nbhope.phmina.bean.request

import com.nbhope.phmina.base.Config


/**
 *Created by ywr on 2021/8/11 10:36
 */
class MusicParams<T> : BaseParams() {
    companion object {
        val PLAY = "play"
        val PAUSE = "pause"
        val START = "start"
        val STOP = "stop"
        val SEEKTO = "seekto"
        val SYNC_POSITION = "sync_position"
        val VOICE_VALUE = "voice_value"
        val LOCAL_LIST = "LOCAL_LIST" //本地音乐列表同步
        var LOCAL_TIME_LIST = "LOCAL_TIME_LIST" // 本地音乐关键帧列表
    }

    var ipAddress: String? = null
    var port: Int = Config.SYNC_SERVICE_PORT

    /**
     * 对于本地音乐或者网路音乐，直接播放 mode =0
     * 本地音乐先下载后播放 mode =1
     */
    var playMode: Int = 0

    /**
     * play  pause  seekto  position
     */
    var musicCmd: String? = null

    var cmdData: T? = null
}