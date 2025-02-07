package com.sc.tmp_cw.constant

object MessageConstant {

    const val FINISH_TIME = 30000L
    const val MAIN_ANIMATION_TIME = 5000L
    const val MAIN_ANIMATION_TIME_INTERVAL = MAIN_ANIMATION_TIME * 4

    const val ROUTH_SCENERY = "/activity/scenery"
    const val ROUTH_URGENT_NOTIFY = "/activity/urgent_notify"
    const val ROUTH_STATION_NOTIFY = "/activity/station_notify"
    const val ROUTH_SETTING = "/activity/setting"
    const val ROUTH_INTRODUCE = "/activity/introduce"
    const val ROUTH_PARAM = "/activity/param"
    const val ROUTH_PLAYLIST = "/activity/playlist"

    const val PATH_BASE_FILE = "/TMP/"
    const val PATH_CONFIG_FILE = "AppInfo.json"
    const val PATH_TRAVEL = "/TMP/Travel/"
    const val PATH_CATE = "/TMP/Cate/"
    const val PATH_VIDEO = "/TMP/VIDEO/"

    const val SP_MARQUEE_SPEED = "SP_MARQUEE_SPEED" // 走马灯速度
    const val SP_PLAYLIST = "SP_PLAYLIST" // 播放列表记录
    const val SP_PLAYLIST_CHECK = "SP_PLAYLIST_CHECK" // 校验列表是否变更

    const val CMD_PLAY = "play"
    const val CMD_PAUSE = "pause"
    const val CMD_STOP = "stop"
    const val CMD_PRE = "pre"
    const val CMD_UPPER = "upper"
    const val CMD_NEXT = "next"
    const val CMD_LOWER = "lower"
    const val CMD_VOICE = "voice"
    const val CMD_POSITION = "position"
    const val CMD_URGENT_NOTICE = "CMD_URGENT_NOTICE"
    const val CMD_STATION_NOTICE = "CMD_URGENT_NOTICE"
    const val CMD_RTSP_URL_LOADED = "CMD_RTSP_URL_LOADED"
    const val CMD_BACK_HOME = "CMD_BACK_HOME"

    const val SERVICE_INIT_SUCCESS = "SERVICE_INIT_SUCCESS"
}
