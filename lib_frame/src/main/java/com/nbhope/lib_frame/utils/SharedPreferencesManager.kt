package com.nbhope.lib_frame.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.lib.network.networkconfig.TraySpManager
import com.nbhope.lib_frame.constants.HopeConstants
import timber.log.Timber
import java.util.*

/**
 * @Author qiukeling
 * @Date 2019-08-13-09:11
 * @Email qiukeling@nbhope.cn
 */
class SharedPreferencesManager constructor(private val app: Application) {
    companion object {
        private const val PREFS_NAME = "config.xml"
        private const val KEY_PLAY_MODE = "play_mode"
        private const val USER_TOKEN = "user_token"
        private var AUTH_CODE = "auth_code"
//        /*+ HopeUtils.getSN()*/
        private var REFERENCE_ID = "reference_id"
//        /*+ HopeUtils.getSN()*/
        private const val MIGU_TAB = "migu_tab"
        private const val LOGIN_INFO = "login_info"
        private const val SEARCH_HISTORY = "search_history"
        private const val UHOME_CONFIG = "uhome_config"//智能家居中控配置
        private const val ISFRISTZIP = "isfristzip" //是否为第一次解压
        private const val HJQINFO = "hjqinfo"  //和家亲插件数据
        private const val SMARTHOME_DASkDROP_STATE = "smarthome_dasktop_state"
        private const val ISSHOWTM = "ISSHOWTM" //是否显示天猫的提示
        private const val IS_NEWAPPINFO_FIRST = "IS_NEWAPPINFO_FIRST" //是否第一次使用新的界面
        private const val SM_DT_SCENE_SIZE = "SM_DT_SCENE_SIZE" //智能家居桌面情景数量
        private const val APPSKEY = "APPSKEY" //应用库锁

        /*思必驰相关*/
        private const val WAKE_UP = "wake_option"
        private const val ENABLE_DUPLEX = "duplex_option"
        private const val DUPLEX_TIME = "duplex_time"
        private const val WAKE_UP_TIME = "wake_time_option"
        private const val NEAR_WAKE_UP = "near_wake_up_option"
        private const val WAKE_UP_DATA = "wake_up_option"
        private const val WAKE_UP_TEXT_P = "wake_text_py_option"
        private const val SENSITIVE = "sensitive_option"
        private const val CUSTOMDIALOG = "customdialog" //自定义语音弹框
        private const val VOICEFILTERTYPE = "voicefiltertype" //语音外接数据过滤的类型
        private const val WAKE_TIME_TYPE = "wake_time_model" //唤醒类型
        private const val WAKE_TIME_DURATION = "wake_time_duration" //唤醒时间端设置
        private const val VOICE_SPEAKER_INFO = "voice_speaker_info" //设置音色信息
        private const val VOICE_TEXT_TIME = "voice_text_time" //语音次数


        /*对讲广播相关*/
        private const val INTERPHONE_GUIDE = "interphone_guide"
        private const val INTERPHONE_DISTURB = "interphone_disturb"

        private const val INTERPHONE_NAME = "INTERPHONE_NAME"  //对讲广播设备名称
        /*对讲广播相关*/

        /*底座相关*/
        private const val TUNNEL_COUNT = "tunnel_count"//分区数量
        private const val SOUND_CHANNEL = "sound_channel"//音源通道
        private const val TUNNEL_STATE = "tunnel_state"//分区状态
        private const val TUNNEL_VOLUME = "tunnel_volume"//分区音量
        private const val TUNNEL_NAME = "tunnel_name"//分区名字
        private const val DUAL_AUDIO_NAME = "Dual_Audio_name"//音源名字
        private const val KTV_SWITCH = "KTV_SWITCH"//k歌开关
        private const val KTV_REVERBERATION = "KTV_REVERBERATION"//混响值
        private const val KTV_MIKE_VOLUME = "KTV_MIKE_VOLUME"//麦克风音量
        private const val SERIAL_ADDRESS = "SERIAL_ADDRESS"//串口地址
        private const val SERIAL_PORT_RATE = "SERIAL_PORT_RATE"//平板串口波特率
        private const val SERIAL_BASE_PORT_RATE = "SERIAL_BASE_PORT_RATE"//底座串口波特率
        private const val TUNNEL_MERGE_STATE = "TUNNEL_MERGE_STATE"//分区合并和分离状态
        private const val TUNNEL_VERSION_NAME = "TUNNEL_VERSION_NAME"//底座的版本号
        private const val HOPE_CRC_CHECK_MODE = "HOPE_CRC_CHECK_MODE"//向往CRC校验模式
        private const val OUT_VOLUME_MODE = "OUT_VOLUME_MODE"//R4的外放和喇叭切换模式
        /*底座相关*/

        /*蜻蜓咪咕激活*/
        private var MIGU_EXPIRETIME = "migu_expire_time" /*+ HopeUtils.getSN()*/
        private var MIGU_ACTIVETIME = "migu_active_time" /*+ HopeUtils.getSN()*/
        private var VIP_ACTIVETIME = "vip_active_time" /*+ HopeUtils.getSN()*/
        private var VIP_EXPIRETIME = "vip_expire_time" /*+ HopeUtils.getSN()*/
        private var VIP_TIP = "vip_tip" /*+ HopeUtils.getSN()*/
        private var QT_EXPIRETIME = "qt_expire_time" /*+ HopeUtils.getSN()*/
        private var MIGU_NEXTPAY_TIME = "migu_nextpay_time" /*+ HopeUtils.getSN()*/
        private var VIP_NEXTPAY_TIME = "vip_nextpay_time" /*+ HopeUtils.getSN()*/
        private var MIGU_FREETIME = "migu_free_time" /*+ HopeUtils.getSN()*/
        private const val ACTIVATE_CHECKED = "ACTIVATE_CHECKED"
        private const val MUSIC_PATH_TYPE = "MUSIC_PATH_TYPE"
        /*蜻蜓咪咕激活*/

        /*本地音乐上传*/
        private const val MUSIC_UPLOAD = "music_upload"

        /*小米三元组*/
        private const val XIAOMI_AUTH = "xiaomi_auth"

        /*地理位置-JSON*/
        private const val DEVICE_ADDRESS = "device_address"

        private const val ADDRESS_CODE = "address_code"

        private const val SPEECH_FAMILY = "speech_family"

        private const val PANEL_INDEX = "panel_index"


        /*拉菲页面*/
        private const val LAFEI_TAB = "lafei_tab"


        /*低音炮保存的状态*/
        private const val BASS_MODE = "BASS_MODE"//低音炮的类型,是4.1还是5.1
        private const val BASS_EFFECT_MODE = "BASS_EFFECT_MODE"//低音炮设备的音效模式
        private const val BASS_EFFECT_POSITION_VOLUME = "BASS_EFFECT_POSITION_VOLUME"//低音炮设备的音效模式
        private const val BASS_SOURCE = "BASS_SOURCE"//低音炮设备的音源
        private const val BASS_FRONT_VOLUME = "BASS_FRONT_VOLUME"//低音炮前置的音量
        private const val BASS_CENTER_VOLUME = "BASS_CENTER_VOLUME"//低音炮中置的音量
        private const val BASS_SURROUND_VOLUME = "BASS_SURROUND_VOLUME"//低音炮环绕的音量
        private const val BASS_MAINSPEAKER_VOLUME = "BASS_REAR_VOLUME"//低音炮主音箱的音量
        private const val BASS_TYPE_VOLUME = "BASS_TYPE_VOLUME"//低音炮音量,分4.1和5.1
        private const val BASS_TYPE_TOTAL_VOLUME = "BASS_TYPE_TOTAL_VOLUME"//低音炮总音量,分4.1和5.1
        private const val BASS_TYPE_RATE = "BASS_TYPE_RATE"//低音炮频率,分4.1和5.1
        private const val BASS_PHASE = "BASS_PHASE"//低音炮相位
        private const val BASS_BOOST = "BASS_BOOST"//低音加强
        private const val BASS_SPEECH_ENHANCE = "BASS_SPEECH_ENHANCE"//语音增强
        private const val BASS_MODE_CUS_NAME = "BASS_MODE_CUS_NAME"//自定义名字

        private const val BASS_KTV_VOLUME = "BASS_KTV_VOLUME"//低音炮k歌音量值
        private const val BASS_KTV_MIX = "BASS_KTV_MIX"//低音炮k歌混响
        private const val BASS_KTV_INFLECT = "BASS_KTV_INFLECT"//低音炮k歌变音
        private const val BASS_TV_MODE_TYPE = "BASS_TV_MODE_TYPE"//低音炮电视模式类型

        private var MEMBER_SHIP = "MEMBER_SHIP" /**+ HopeUtils.getSN()**/// 会员等级
        private var MIGUERROR_CODE = "MIGUERROR_CODE" /**+ HopeUtils.getSN()**/// 咪咕搜索失败原因

        /*米家情景控制初始化*/
        private const val SCENE_LIST = "SCENE_LIST"//情景模式列表
        private const val LAUNCHER_SCENELIST = "LAUNCHER_SCENELIST"
        private const val DEVICE_ID_MI = "DEVICE_ID_MI"//米家设备ID
        private const val MI_IS_BINDING = "MI_IS_BINDING"//米家设备ID
        private const val CHECK_MIOT_OPEN = "CHECK_MIOT_OPEN" //米家桌面开启状态
        private const val Miot_Or_HopeLauncher = "Miot_Or_HopeLauncher"
        private const val CHECK_SKB_OPEN = "CHECK_SKB_OPEN"

        private var IS_STARTED_SYNC_PLAY = "IS_STARTED_SYNC_PLAY" //同步播放是否已开启
        private var DEVICE_NAME = "DEVICE_NAME" //自定义的设备名称

        /*定位合法非法*/
        private var LOCATION_LEGAL = "LOCATION_LEGAL"

        private const val U_HOME_LOCAL_IP = "U_HOME_LOCAL_IP" // 本地网关ip
        private const val U_HOME_LOCAL_TEMPLATE_PAGE = "U_HOME_LOCAL_TEMPLATE_PAGE"
    }

    private val sp = app.getSharedPreferences(PREFS_NAME, Context.MODE_MULTI_PROCESS)
    private val tary = TraySpManager(app)
    private val fileData = FileSpManager()

    private fun edit(): SharedPreferences.Editor = sp.edit()


    /**
     * 从sp中读取播放模式
     */
    fun playMode(player: String): Int {
        return tary.getInt(KEY_PLAY_MODE + "_$player", 1)
    }

    /**
     * [mode]:播放器播放模式
     * 把播放模式存入sp中
     */
    fun setPlayMode(mode: Int, player: String) {
        tary.putInt(KEY_PLAY_MODE + "_$player", mode)
    }


    fun saveUserToken(token: String) {
        tary.putString(USER_TOKEN, token)
    }

    fun userToken(): String = tary!!.getString(USER_TOKEN, "")!!

    fun saveAuthCode(authCode: String) {
        tary.putString(AUTH_CODE, authCode)
    }

    fun authCode(): String = tary!!.getString(AUTH_CODE, "")!!

    fun saveReferenceId(referenceId: Long) {
        tary.putLong(REFERENCE_ID, referenceId)
    }

    fun referenceId(): Long = tary!!.getLong(REFERENCE_ID, 0L)

    fun saveMiguTab(miguTab: String) {
        tary.putString(MIGU_TAB, miguTab)
    }

    fun miguTab(): String = tary!!.getString(MIGU_TAB, "")!!

    fun getSN(): String = HopeUtils.getSN()
//    fun getSN(): String = "SNCompat.getSN(app)"

    fun getLoginInfo(): String = tary?.getString(LOGIN_INFO, "")!!


    fun saveLoginInfo(loginInfo: String) {
        tary?.putString(LOGIN_INFO, loginInfo)
    }

    fun getUhomeConfig(): Int = tary!!.getInt(UHOME_CONFIG, 0)//获取智能家居中控配置,0云端,1本地

    fun saveUhomeConfig(config: Int) {
        tary.putInt(UHOME_CONFIG, config)
    }

    /**----------------------搜索------------------------------ **/
    fun saveSearchHistory(history: List<String>) {
        val str = history.joinToString(",") { i -> i }.trim()
        tary.putString(SEARCH_HISTORY, str)
    }

    fun getSearchHistory(): List<String>? = tary.getString(SEARCH_HISTORY, "")?.split(",")


    /*对讲广播相关*/
    fun getIsShowGuide(): Boolean = tary!!.isBoolean(INTERPHONE_GUIDE, true)

    fun saveIsShowGuide() {
        tary!!.putBoolean(INTERPHONE_GUIDE, false)
    }

    fun getIsNotDisturb(): Boolean = tary!!.isBoolean(INTERPHONE_DISTURB, false)

    fun saveIsNotDisturb(isNotDisturb: Boolean) {
        tary!!.putBoolean(INTERPHONE_DISTURB, isNotDisturb)
    }


    /*对讲广播相关*/


    /*底座相关*/

    //获取分区数量
    fun getTunnelCount(): Int = tary!!.getInt(TUNNEL_COUNT, 0)

    //保存分区的数量
    fun saveTunnelCount(count: Int) {
        tary!!.putInt(TUNNEL_COUNT, count)
    }

    //获取音源通道
    fun getSoundChannel(): Int = tary!!.getInt(SOUND_CHANNEL, 1)//1本地,2外接,3蓝牙，4hdmi，5光纤

    //设置音源通道
    fun saveSoundChannel(channel: Int) {
        tary!!.putInt(SOUND_CHANNEL, channel)
    }

    //获取分区开关状态,可以传对应的音源
    fun getTunnelState(tunnelNumber: Int, soundSource: Int = -1): Int = tary!!.getInt(TUNNEL_STATE + tunnelNumber + (if (soundSource == -1) getSoundChannel() else soundSource), 1)//0关,1开,从0开始

    //设置分区开关状态,可以传对应的音源
    fun saveTunnelState(tunnelNumber: Int, state: Int, soundSource: Int = -1) {
        tary!!.putInt(TUNNEL_STATE + tunnelNumber + (if (soundSource == -1) getSoundChannel() else soundSource), state)//从0开始
    }

    //获取分区音量,可以传对应的音源
    fun getTunnelVolume(tunnelNumber: Int, soundSource: Int = -1): Int = tary!!.getInt(TUNNEL_VOLUME + tunnelNumber + (if (soundSource == -1) getSoundChannel() else soundSource), 50)

    //设置分区音量
    fun saveTunnelVolume(tunnelNumber: Int, volume: Int, soundSource: Int = -1) {
        tary!!.putInt(TUNNEL_VOLUME + tunnelNumber + (if (soundSource == -1) getSoundChannel() else soundSource), volume)
    }

    //获取分区名字
//    fun getTunnelName(tunnelNumber: Int): String = tary!!.getString(TUNNEL_NAME + tunnelNumber, (if (HopeConstants.isDual()) "房间" else "分区") + TunnelUtils.tunnelNumToStr(tunnelNumber))!!

    //设置分区名字
    fun saveTunnelName(name: String, tunnelNumber: Int) {
        tary!!.putString(TUNNEL_NAME + tunnelNumber, name)
    }

    //获取音源昵称名字
    fun getDualAudioNickName(player: String): String = tary!!.getString(DUAL_AUDIO_NAME + player, "音源" + (if (player == "main") "一" else "二"))!!

    //设置音源昵称名字
    fun saveDualAudioNickName(name: String, player: String) {
        tary!!.putString(DUAL_AUDIO_NAME + player, name)
    }

    //获取k歌开关状态
    fun getKtvSwitch(): Int = tary!!.getInt(KTV_SWITCH, 0)//0关闭，1开启

    //设置k歌开关
    fun saveKtvSwitch(state: Int) {
        tary!!.putInt(KTV_SWITCH, state)
    }

    //获取k歌混响值
//    fun getKtvReverberation(): Int = tary!!.getInt(KTV_REVERBERATION,if (HopeConstants.CPU == "rk3566") 5 else 78)

    //设置k歌混响值
    fun saveKtvReverberation(num: Int) {
        tary!!.putInt(KTV_REVERBERATION, num)
    }

    //获取话筒音量
//    fun getKtvMikeVolume(num: Int): Int = tary!!.getInt(KTV_MIKE_VOLUME + num,if (HopeConstants.CPU == "rk3566") 5 else 50)

    //设置话筒音量
    fun saveKtvMikeVolume(num: Int, volume: Int) {
        tary!!.putInt(KTV_MIKE_VOLUME + num, volume)
    }

    //获取串口地址位
    fun getSerialAddress(): Int = tary!!.getInt(SERIAL_ADDRESS, 21)//默认位0x15

    //设置串口地址位
    fun saveSerialAddress(address: Int) {
        tary!!.putInt(SERIAL_ADDRESS, address)
    }

    //获取平板波特率
    fun getCpuPortRate(): Int = tary!!.getInt(SERIAL_PORT_RATE, 0)//默认波特率0

    //设置平板波特率
    fun saveCpuPortRate(port: Int) {
        tary!!.putInt(SERIAL_PORT_RATE, port)
    }

    //获取底座波特率
    fun getBasePortRate(): Int = tary!!.getInt(SERIAL_BASE_PORT_RATE, 9600)//默认波特率9600

    //设置底座波特率
    fun saveBasePortRate(port: Int) {
        tary!!.putInt(SERIAL_BASE_PORT_RATE, port)
    }

    //获取分区合并和分离状态
    fun getTunnelMergeState(): Int = tary!!.getInt(TUNNEL_MERGE_STATE, 0)//默认位1分离,0合并

    //设置分区合并和分离状态
    fun saveTunnelMergeState(state: Int) {
        tary!!.putInt(TUNNEL_MERGE_STATE, state)
    }

    //获取分区版本号
    fun getTunnelVersionName(): String = tary!!.getString(TUNNEL_VERSION_NAME, "1.0")!!//获取底座的版本号

    //设置分区版本号
    fun saveTunnelVersionName(version: String) {
        tary!!.putString(TUNNEL_VERSION_NAME, version)
    }

    //获取Crc校验模式
    fun getHopeCRCMode(): Int = tary!!.getInt(HOPE_CRC_CHECK_MODE, 0)!!//获取Crc校验模式

    //设置Crc校验模式
    fun saveHopeCRCMode(mode: Int) {
        tary!!.putInt(HOPE_CRC_CHECK_MODE, mode)
    }

    //获取Crc校验模式
    fun getOutVolumeMode(): Int = tary!!.getInt(OUT_VOLUME_MODE, 1)!!//获取R4的喇叭和外放模式

    //设置Crc校验模式
    fun saveOutVolumeMode(mode: Int) {
        tary!!.putInt(OUT_VOLUME_MODE, mode)
    }

    /*思必驰相关*/
    //语音唤醒开关值
    fun wakeUp(): Boolean {
        return tary.isBoolean(WAKE_UP, false)
    }

    fun saveWakeUp(enable: Boolean) {
        return tary.putBoolean(WAKE_UP, enable)

    }

    //全双工开关值
    fun enableDuplex() = tary!!.isBoolean(ENABLE_DUPLEX, false)
    fun saveDuplex(enable: Boolean) {
        tary!!.putBoolean(ENABLE_DUPLEX, enable)
    }

    //全双工开关时间
    fun duplexTime() = tary!!.getLong(DUPLEX_TIME, 15000L)
    fun saveDuplexTime(time: Long) {
        tary!!.putLong(DUPLEX_TIME, time)
    }

    //就近唤醒开关值
    fun enableNearWakeUp() = tary!!.isBoolean(NEAR_WAKE_UP, false)
    fun saveNearWakeUp(enable: Boolean) {
        tary!!.putBoolean(NEAR_WAKE_UP, enable)
    }

    //唤醒阈值
    fun sensitive() = tary!!.getInt(SENSITIVE, 2)
    fun saveSensitive(sensitive: Int) {
        tary!!.putInt(SENSITIVE, sensitive)
    }

//    fun getWakeupData(): String {
//        var default = Gson().toJson(arrayListOf(WakeUpBean("你好小智", "ni hao xiao zhi")))
//        return tary.getString(WAKE_UP_DATA, default)!!
//    }

    fun saveWakeupData(wakeupText: String) {
        return tary.putString(WAKE_UP_DATA, wakeupText)
    }

    fun wakeupTextPy(): String? {
        return tary.getString(WAKE_UP_TEXT_P, "ni hao xiao zhi")
    }

    fun saveWakeupTextPy(wakeupTextPy: String) {
        return tary.putString(WAKE_UP_DATA, wakeupTextPy)
    }

    /*底座相关*/

    /*蜻蜓咪咕激活*/
    fun saveMiguExpiretime(time: Long) {
        Timber.d("saveMiguExpiretime $time")
        tary!!.putLong(MIGU_EXPIRETIME, time)
    }

    fun saveMiguActivetime(time: Long) {
        tary!!.putLong(MIGU_ACTIVETIME, time)
    }

    fun miguActivetime() = tary!!.getLong(MIGU_ACTIVETIME, 0L)

    fun miguExpiretime() = tary!!.getLong(MIGU_EXPIRETIME, 0L)

    fun saveMiguNextWillPayDate(date: Int) {
        tary!!.putInt(MIGU_NEXTPAY_TIME, date)
    }

    fun getMiguNextWillPayDate() = tary!!.getInt(MIGU_NEXTPAY_TIME, 14)

    fun saveVipNextWillPayDate(date: Int) {
        tary!!.putInt(VIP_NEXTPAY_TIME, date)
    }

    fun getVipNextWillPayDate() = tary!!.getInt(VIP_NEXTPAY_TIME, 14)

    fun saveMiguFreeTime(isFreeTime: Boolean) {
        tary.putBoolean(MIGU_FREETIME, isFreeTime)
    }

    fun getMIguFreeTime() = tary!!.isBoolean(MIGU_FREETIME, false)

    fun saveQtExpiretime(time: Long) {
        tary!!.putLong(QT_EXPIRETIME, time)
    }

    fun qtExpiretime() = tary!!.getLong(QT_EXPIRETIME, 0L)


    fun saveVipExpiretime(time: Long) {
        Timber.d("saveVipExpiretime $time")
        tary!!.putLong(VIP_EXPIRETIME, time)
    }

    fun saveVipActivetime(time: Long) {
        tary!!.putLong(VIP_ACTIVETIME, time)
    }

    fun vipActivetime() = tary!!.getLong(VIP_ACTIVETIME, 0L)

    fun vipExpiretime() = tary!!.getLong(VIP_EXPIRETIME, 0L)

    fun checkVip(): Boolean {
        val vipTime = vipExpiretime()
        val time = Date().time
        Timber.i("核对激活时间:vipTime:$vipTime")
        return time < vipTime
    }

    fun saveNeedVipTip(need:Boolean) {
        tary!!.putBoolean(VIP_TIP, need)
    }

    fun needVipTip(): Boolean {
        return tary!!.isBoolean(VIP_TIP, false)
    }

//    fun checkActivate(): Boolean {
//        val miguTime = miguExpiretime()
//        val qtTime = qtExpiretime()
//        val time = Date().time
//        Timber.i("核对激活时间:" + "${HopeConstants.IS_RADIO} miguTime:$miguTime qtTime:$qtTime time:$time")
//        return if (HopeConstants.IS_RADIO) {
//            time < miguTime && time < qtTime
//        } else {
//            time < miguTime
//        }
//    }

    fun checkActivateType(): Int {
        val miguTime = miguExpiretime()
        val willShowTipsTime = 24 * 60 * 60 * 1000 * getMiguNextWillPayDate()
        val time = System.currentTimeMillis()
//        val time=miguTime-willShowTipsTime+24 * 60 * 60 * 1000 *1
        Timber.i("核对激活时间miguTime:$miguTime  currentTime:$time   willShowTipsTime :$willShowTipsTime")
        return when {
            miguTime == 0L -> { //未激活
                1
            }
            time > miguTime -> { //已过期
                3
            }
            time + willShowTipsTime > miguTime -> { //将要过期
                4
            }
            else -> {
               0
            }

        }
    }

//    fun checkVipType(): Int {
//        val vipTime = vipExpiretime()
//        val willShowTipsTime = 24 * 60 * 60 * 1000 * getVipNextWillPayDate()
//        val time = System.currentTimeMillis()
////        val time=miguTime-willShowTipsTime+24 * 60 * 60 * 1000 *1
//        Timber.i("核对激活时间vipTime:$vipTime  currentTime:$time   willShowTipsTime :$willShowTipsTime")
//        return when {
//            time > vipTime -> { //已过期
//                if (checkActivate()){
//                    6
//                } else {
//                    7
//                }
//            }
//            time + willShowTipsTime > vipTime -> { //将要过期
//                8
//            }
//            else -> {
//                5
//            }
//        }
//    }

    fun saveActivateChecked(boolean: Boolean) {
        tary!!.putBoolean(ACTIVATE_CHECKED, boolean)
    }

    fun activateChecked(): Boolean = tary!!.isBoolean(ACTIVATE_CHECKED, false)

    /**
     * 记录用户选择的播放地址类型
     * 0：listenUrl
     * 1：hqListenUrl
     * 2：sqListenUrl
     */
    fun saveMusicUrlType(type:Int) {
        tary!!.putInt(MUSIC_PATH_TYPE, type)
    }

    fun getMusicUrlType() :Int{
        return tary!!.getInt(MUSIC_PATH_TYPE, 0)
    }

    /*本地音乐上传*/
    fun checkMusicUpload(): Boolean = tary!!.isBoolean(MUSIC_UPLOAD, false)

    fun saveMusicUpload(musicUpload: Boolean) {
        tary!!.putBoolean(MUSIC_UPLOAD, musicUpload)
    }

    fun miotAuth(): String = tary!!.getString(XIAOMI_AUTH, "")!!

    fun saveMiotAuth(miotAuth: String) {
        tary!!.putString(XIAOMI_AUTH, miotAuth)
    }

    fun dialogCustom(customDialog: Boolean) {
        tary!!.putBoolean(CUSTOMDIALOG, customDialog)
    }

    fun getDialoCustom(): Boolean = tary!!.isBoolean(CUSTOMDIALOG, false)

    fun saveVoiceOutFilterType(type: Int) {
        tary!!.putInt(VOICEFILTERTYPE, type)
    }

    fun getVoiceOutFilterType() = tary!!.getInt(VOICEFILTERTYPE, 1)

    fun setFristZipMusic(frist: Boolean) {
        tary.putBoolean(ISFRISTZIP, frist)
    }

    /**
     * 默认为第一次解压
     */
    fun isFristZip(): Boolean {
        return tary!!.isBoolean(ISFRISTZIP, true)
    }

    fun savehjqInfo(hjqinfo: String) {
        tary.putString(HJQINFO, hjqinfo)
    }

    fun getHjqInfo(): String {
        var json = JsonObject()
        json.addProperty("register", false)
        json.addProperty("userkey", "0000000000000000")
        json.addProperty("deviceId", "CMCC-000000-000000000000")
        return tary!!.getString(HJQINFO, json.toString())!!
    }

    fun getTemp(objectId: String?, defaultValue: String): String? {
        return tary.getString("temp$objectId", defaultValue)
    }

    fun saveTemp(objectId: String?, Value: String) {
        return tary.putString("temp$objectId", Value)
    }

    /**
     * 智能家居桌面开关
     */
    fun getSHDaskTopState(): Boolean {
        return tary.isBoolean(SMARTHOME_DASkDROP_STATE, false)
    }

    fun setSHDaskTopState(state: Boolean) {
        tary.putBoolean(SMARTHOME_DASkDROP_STATE, state)
    }

    fun setShowTMTips(show: Boolean) {
        tary.putBoolean(ISSHOWTM, show)
    }

    fun isShowTMTips(): Boolean {
        return tary.isBoolean(ISSHOWTM, true)
    }

    //新老版本对原有数据不同，当进行覆盖升级会出现数据混乱，因此以此来判断数据是否数据是否修改
    fun isNewAppInfoFirst(version: Int): Boolean {
        return tary.isBoolean(IS_NEWAPPINFO_FIRST + version, false)
    }

    //新老版本对原有数据不同，当进行覆盖升级会出现数据混乱，因此以此来判断数据是否数据是否修改
    fun setIsNewAppInfoFirst(isNew: Boolean, version: Int) {
        tary.putBoolean(IS_NEWAPPINFO_FIRST + version, isNew)
    }

    fun saveAppsKey(key: String) {
        fileData.putData(APPSKEY, key)
    }

    fun getAppKey(): String {
        return fileData.getData(APPSKEY)
    }

    fun getThirdTargetPackage() {


    }

    fun getAddress(): String {
        return tary.getString(DEVICE_ADDRESS, "") ?: ""
    }

    fun getAddressCode():String {
        return tary.getString(ADDRESS_CODE,"") ?: ""
    }

    fun getLaFeiTab(): String? = tary.getString(LAFEI_TAB, "[\"音乐\"]")

    fun setAddress(address: String) {
        tary.putString(DEVICE_ADDRESS, address)
    }

    fun setAddressCode(addressCode: String) {
        tary.putString(ADDRESS_CODE, addressCode)
    }

    fun saveLaFeiTab(tabs: String) {
        tary.putString(LAFEI_TAB, tabs)
    }

    /*低音炮相关数据*/
    fun setBassMode(bassMode: String) {//设置低音炮的模式,是4.1还是5.1
        tary.putString(BASS_MODE, bassMode)
    }

    fun getBassMode(): String {//获取低音炮当前的模式
        return tary.getString(BASS_MODE, "4.1")!!
    }

    fun setBassEffectModePosition(bassMode: String, bassEffectPosition: Int) {//设置低音炮的音效
        tary.putInt(BASS_EFFECT_MODE + bassMode, bassEffectPosition)
    }

    fun getBassEffectModePosition(bassMode: String): Int {//获取低音炮当前的音效
        return tary.getInt(BASS_EFFECT_MODE + bassMode, 0)!!
    }

    fun setBassEffectPositionVolume(bassType: String, effectPosition: Int, volumePosition: Int, volume: Int) {//设置音效每个position的值
        tary.putInt(BASS_EFFECT_POSITION_VOLUME + bassType + effectPosition + volumePosition, volume)
    }

    fun getBassEffectPositionVolume(bassType: String, effectPosition: Int, volumePosition: Int): Int {//获取音效每个position的值
        return tary.getInt(BASS_EFFECT_POSITION_VOLUME + bassType + effectPosition + volumePosition, 6)
    }

    fun setBassSource(bassSource: Int) {//设置低音炮的音源 0本地,1蓝牙,2电视,3ktv,4光纤
        tary.putInt(BASS_SOURCE, bassSource)
    }

    fun getBassSource(): Int {//获取低音炮当前的音源
        return tary.getInt(BASS_SOURCE, 0)
    }

    fun setBassFrontVolume(volume: Int) {//设置前置音量
        tary.putInt(BASS_FRONT_VOLUME, volume)
    }

    fun getBassFrontVolume(): Int {//获取前置音量
        return tary.getInt(BASS_FRONT_VOLUME, 19)
    }

    fun setBassCenterVolume(volume: Int) {//设置中置音量
        tary.putInt(BASS_CENTER_VOLUME, volume)
    }

    fun getBassCenterVolume(): Int {//获取中置音量
        return tary.getInt(BASS_CENTER_VOLUME, 16)
    }

    fun setBassSurroundVolume(volume: Int) {//设置环绕音量
        tary.putInt(BASS_SURROUND_VOLUME, volume)
    }

    fun getBassSurroundVolume(): Int {//获取环绕音量
        return tary.getInt(BASS_SURROUND_VOLUME, 19)
    }

    fun setBassMainSpeakerVolume(volume: Int) {//设置主音箱音量
        tary.putInt(BASS_MAINSPEAKER_VOLUME, volume)
    }

    fun getBassMainSpeakerVolume(): Int {//获取主音箱音量
        return tary.getInt(BASS_MAINSPEAKER_VOLUME, 21)
    }

    fun setBassTypeVolume(bassType: String, volume: Int) {//设置低音炮音箱
        tary.putInt(BASS_TYPE_VOLUME + bassType, volume)
    }

    fun getBassTypeVolume(bassType: String): Int {//获取低音炮音箱
        return tary.getInt(BASS_TYPE_VOLUME + bassType,if (bassType=="4.1") 13 else 13)
    }

    fun setBassTypeTotalVolume(bassType: String, volume: Int) {//设置低音炮总音箱
        tary.putInt(BASS_TYPE_TOTAL_VOLUME + bassType, volume)
    }

    fun getBassTypeTotalVolume(bassType: String): Int {//获取低音炮总音箱
        return tary.getInt(BASS_TYPE_TOTAL_VOLUME + bassType, if (bassType=="4.1") 12 else 19)
    }

    fun setBassTypeRate(bassType: String, volume: Int) {//设置低音炮频率
        tary.putInt(BASS_TYPE_RATE + bassType, volume)
    }

    fun getBassTypeRate(bassType: String): Int {//获取低音炮频率
        return tary.getInt(BASS_TYPE_RATE + bassType, if (bassType=="4.1") 12 else 10)
    }

    fun setBassPhase(bassType: String, switch: Boolean) {//设置低音炮相位
        tary.putBoolean(BASS_PHASE + bassType, switch)
    }

    fun getBassPhase(bassType: String): Boolean {//获取低音炮相位
        return tary.isBoolean(BASS_PHASE + bassType, true)
    }

    fun setBassBoost(bassType: String, switch: Boolean) {//设置低音增强
        tary.putBoolean(BASS_BOOST + bassType, switch)
    }

    fun getBassBoost(bassType: String): Boolean {//获取低音增强
        return tary.isBoolean(BASS_BOOST + bassType, false)
    }

    fun setBassSpeakEnhance(bassType: String, switch: Boolean) {//设置语音增强
        tary.putBoolean(BASS_SPEECH_ENHANCE + bassType, switch)
    }

    fun getBassSpeakEnhance(bassType: String): Boolean {//获取语音增强
        return tary.isBoolean(BASS_SPEECH_ENHANCE + bassType, false)
    }

    fun setBassModeCusName(bassType: String,number:Int,name:String) {//设置低音炮自定义名字
        tary.putString(BASS_MODE_CUS_NAME + bassType+number, name)
    }

    fun getBassModeCusName(bassType: String,number:Int): String {//获取低音炮自定义名字
        return tary.getString(BASS_MODE_CUS_NAME + bassType+number, "自定义$number")!!
    }

    fun setBassKTVMikeVolume(num: Int, value: Int) {//设置低音炮k歌话筒音量
        tary.putInt(BASS_KTV_VOLUME + num, value)
    }

    fun getBassKTVMikeVolume(num: Int): Int {//获取低音炮k歌话筒音量
        return tary.getInt(BASS_KTV_VOLUME + num, 5)
    }
    fun setBassKTVMix(value: Int) {//设置低音炮k歌混响
        tary.putInt(BASS_KTV_MIX , value)
    }

    fun getBassKTVMix(): Int {//获取低音炮k歌混响
        return tary.getInt(BASS_KTV_MIX , 5)
    }

    fun setBassKTVInflect(value: Int) {//设置低音炮k歌变音
        tary.putInt(BASS_KTV_INFLECT , value)
    }

    fun getBassKTVInflect(): Int {//获取低音炮k歌变音
        return tary.getInt(BASS_KTV_INFLECT , 0)
    }

    fun setBassTVMode(value: Int) {//设置电视模式类型，hdmi 4还是光纤 5
        tary.putInt(BASS_TV_MODE_TYPE , value)
    }

    fun getBassTvMode(): Int {//
        return tary.getInt(BASS_TV_MODE_TYPE , 4)
    }

    fun getMemberShip(): String {
        //return tary.getString(MEMBER_SHIP, "1")!!
        return tary.getString(MEMBER_SHIP, "2")!!
    }

    fun setMemberShip(ship: String) {
        tary.putString(MEMBER_SHIP, ship)
    }

    fun saveMIguErrorCode(code: String) {
        tary.putString(MIGUERROR_CODE, code)
    }

    fun getMIguErrorCode(): String {
        return tary.getString(MIGUERROR_CODE, "0")!!
    }

    fun setStartedSync(isStarted: Boolean) {
        tary.putBoolean(IS_STARTED_SYNC_PLAY, isStarted)
    }

    /**
     * 设置唤醒类型，
     * @param type 0 全天候关闭  1 全天候可唤醒 2 指定的时间范围内可唤醒
     */
    fun setWakeType(type: Int) {
        tary.putInt(WAKE_TIME_TYPE, type)
    }

    fun getWakeType(): Int {
        return tary.getInt(WAKE_TIME_TYPE, 1)
    }

    fun setWakeTimeDuration(duration: String) {
        tary.putString(WAKE_TIME_DURATION, duration)
    }

    fun getWakeTimeDuration(): String? {
        return tary.getString(WAKE_TIME_DURATION, "07_00_22_00")
    }

    fun getSpeaker(): String? {
        return tary.getString(VOICE_SPEAKER_INFO, "zhilingf")
    }

    fun setSpeaker(speaker: String) {
        tary.putString(VOICE_SPEAKER_INFO, speaker)
    }

    fun saveSpeechFamily(familyId: String) {
        tary.putString(SPEECH_FAMILY, familyId)
    }

    fun getSpeechFamily(): String {
        return tary.getString(SPEECH_FAMILY, "")!!
    }

    fun savePanelIndex(panelIndex: Int) {
        tary.putInt(PANEL_INDEX, panelIndex)
    }

    fun getPanelIndex(): Int {
        return tary.getInt(PANEL_INDEX, 0)
    }


    /*低音炮相关数据*/

    /* 定位相关 */

    fun saveLocLegal(legal: Boolean) {
        Timber.d("saveLocLegal $legal")
        tary.putBoolean(LOCATION_LEGAL, legal)
    }

    fun getLocLegal(): Boolean {
        return tary.isBoolean(LOCATION_LEGAL, true)
    }

    /*米家情景模式列表*/
    fun sceneList(): String {
        return tary.getString(SCENE_LIST, "first")!!
    }


    fun setsceneList(data:String) {
        tary.putString(SCENE_LIST, data)
    }

    fun launchersceneList():String {
        return tary.getString(LAUNCHER_SCENELIST, "first")!!
    }

    fun setlaunchersceneList(data:String) {
        tary.putString(LAUNCHER_SCENELIST, data)
    }

    /*米家设备ID*/
    fun deviceId():String{
        return tary.getString(DEVICE_ID_MI, "")!!
    }

    fun setdeviceId(data:String) {
        tary.putString(DEVICE_ID_MI, data)
    }

    fun setMIisbinding(boolean: Boolean) {//设置米家绑定状态
        tary.putBoolean(MI_IS_BINDING, boolean)
    }

    fun getMIisbinding(): Boolean {//获取米家绑定状态
        return tary.isBoolean(MI_IS_BINDING, false)
    }

    fun setcheckMiotOpen(boolean: Boolean) {//设置米家桌面开启状态
        tary.putBoolean(CHECK_MIOT_OPEN, boolean)
    }

    fun checkMiotOpen(): Boolean {//获取米家桌面开启状态
        return tary.isBoolean(CHECK_MIOT_OPEN, false)
    }

    fun setMiotOrHopeLauncher(boolean: Boolean){ //当前桌面模式为米家或向往智能家居 默认为 false为向往
        tary.putBoolean(Miot_Or_HopeLauncher, boolean)
    }

    fun checkMiotOrHopeLauncher(): Boolean {//获取米家桌面开启状态
        return tary.isBoolean(Miot_Or_HopeLauncher, false)
    }

    fun setcheckSkbOpen(boolean: Boolean) {//设置赛柯博桌面开启状态
        tary.putBoolean(CHECK_SKB_OPEN, boolean)
    }

    fun checkSkbOpen(): Boolean {//获取赛柯博桌面开启状态
        return tary.isBoolean(CHECK_SKB_OPEN, false)
    }

    fun getVoiceTimes(): String {
        return tary.getString(VOICE_TEXT_TIME, "1970-01-01,0")!!
    }

    fun saveVoiceTimes(dayAndTimes: String) {
        tary.putString(VOICE_TEXT_TIME, dayAndTimes)
    }

    // 获取设备类型
    fun getString(key: String, type: String): String = tary!!.getString(key, type)!!

    fun setString(key: String, value: String) = tary!!.putString(key, value)

    fun setUHomeLocalIP(ip: String) {
        tary!!.putString(U_HOME_LOCAL_IP, ip)
    }

    fun getUHomeLocalIP(): String? = tary!!.getString(U_HOME_LOCAL_IP, "")

    fun setUHomeLocalTemplatePage(v: String) {
        tary!!.putString(U_HOME_LOCAL_TEMPLATE_PAGE, v)
    }

    fun getUHomeLocalTemplatePage(): String? = tary!!.getString(U_HOME_LOCAL_TEMPLATE_PAGE, "")
}