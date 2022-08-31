package com.lib.network.api

import com.google.gson.JsonObject
import com.lib.network.vo.*
import com.lib.network.vo.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("/hopeApi/member/register")
    suspend fun register(@FieldMap options: Map<String, String>): Response<HopeResponse<String>>

    @FormUrlEncoded
    @POST("/hopeApi/upgrade/mac")
    suspend fun reportSn(@FieldMap options: Map<String, String>): Response<HopeResponse<String>>

    @FormUrlEncoded
    @POST("hopeApi/member/reset")
    suspend fun reset(@FieldMap options: Map<String, String>): Response<HopeResponse<String>>

    @FormUrlEncoded
    @POST("/hopeApi/member/login")
    suspend fun login(@FieldMap options: Map<String, String>): Response<HopeResponse<LoginResponse>>

    @FormUrlEncoded
    @POST("/hopeApi/member/authen")
    suspend fun authLogin(@FieldMap options: Map<String, String>): Response<HopeResponse<LoginResponse>>

    @FormUrlEncoded
    @POST("/hopeApi/code/send")
    suspend fun getCode(@FieldMap options: Map<String, String>): Response<HopeResponse<String>>

    @FormUrlEncoded
    @POST("hopeApi/account/passwd")
    suspend fun modifyPasswd(@FieldMap options: Map<String, String>): Response<HopeResponse<String>>

    @FormUrlEncoded
    @POST("/hopeApi/device/register")
    suspend fun reportDeviceInfo(@FieldMap options: Map<String, String>): Response<HopeResponse<ReportResponse>>

    @FormUrlEncoded
    @POST("/hopeApi/music/report") //上传本地歌曲
    suspend fun musicReport(@FieldMap options: Map<String, String>): Response<HopeResponse<SongPage>>

    @FormUrlEncoded
    @POST("/hopeApi/music/initial") //上传noMedia歌曲
    suspend fun musicIntiial(@FieldMap options: Map<String, String>): Response<HopeResponse<String>>

    @FormUrlEncoded
    @POST("/hopeApi/sheet/initial") //上传本地歌单
    suspend fun sheetInitial(@FieldMap options: Map<String, String>): Response<HopeResponse<String>>

    @FormUrlEncoded
    @POST("/hopeApi/grant/authorize") //第三方认证
    suspend fun authorize(@FieldMap options: Map<String, String>): Response<HopeResponse<Authorize>>

    //======================================= 咪咕 =======================================/
    @FormUrlEncoded
    @POST("/hopeApi/platform/image") //广告页
    suspend fun adsInfo(@FieldMap options: Map<String, String>): Response<HopeResponse<List<MusicBannerInfo>>>

    @FormUrlEncoded
    @POST("/hopeApi/music/listIndex") //精选
    suspend fun listIndex(@FieldMap options: Map<String, String>): Response<HopeResponse<List<MiguSelectSheet>>>

    @FormUrlEncoded
    @POST("/hopeApi/music/listIndexDetail") //精选 更多
    suspend fun listIndexDetail(@FieldMap options: Map<String, String>): Response<HopeResponse<List<MiguSelectInfo>>>

    @FormUrlEncoded
    @POST("/hopeApi/music/listRank") //排行榜
    suspend fun miguRank(@FieldMap options: Map<String, String>): Response<HopeResponse<List<MusicRankSheet>>>

    @FormUrlEncoded
    @POST("/hopeApi/music/listCatalog") //咪咕标签页
    suspend fun miguTab(@FieldMap options: Map<String, String>): Response<HopeResponse<List<MiguScene>>>

    @FormUrlEncoded
    @POST("/hopeApi/music/listRadioDetail") //咪咕标签详情
    suspend fun miguTabDetail(@FieldMap options: Map<String, String>): Response<HopeResponse<List<MiguSceneDetail>>>

    @FormUrlEncoded
    @POST("/hopeApi/musicBlack/report") //黑名单音乐上传
    suspend fun musicBlackReport(@FieldMap options: Map<String, String>): Response<HopeResponse<String>>

    @FormUrlEncoded
    @POST("/hopeApi/musicBlack/list") //歌曲黑名单查询
    suspend fun musicBlackFilter(@FieldMap options: Map<String, String>): Response<HopeResponse<MusicFiltered>>


    @FormUrlEncoded
    @POST(" /hopeApi/sheetBlack/report") //歌单黑名单上报
    suspend fun sheetBlackReport(@FieldMap options: Map<String, String>): Response<HopeResponse<String>>

    @FormUrlEncoded
    @POST(" /hopeApi/sheetBlack/list") //歌单黑名查询 // sheetIds 为空表示查询所有的名单
    suspend fun sheetBlackList(@FieldMap options: Map<String, String>): Response<HopeResponse<MusicSheetFiltered>>

//======================================= 蜻蜓 =======================================/

    @FormUrlEncoded
    @POST("/hopeApi/music/loadCatalog")
    suspend fun loadCatalog(@FieldMap options: Map<String, String>): Response<HopeResponse<List<Category>>>

    @FormUrlEncoded
    @POST("/hopeApi/music/loadMusicCatalog")
    suspend fun loadMusicCatalog(@FieldMap options: Map<String, String>): Response<HopeResponse<List<QtAlbum>>>

    @FormUrlEncoded
    @POST("/hopeApi/music/listMusicSelective")
    suspend fun listMusicSelective(@FieldMap options: Map<String, String>): Response<HopeResponse<List<MiguSong>>>

    @FormUrlEncoded
    @POST("/hopeApi/music/catalog")
    suspend fun catalog(@FieldMap options: Map<String, String>): Response<HopeResponse<List<MiguSong>>>

    //======================================= 激活 =======================================/
    @Deprecated("接口已经弃用，使用最新的 @payactivate ")
    @FormUrlEncoded
    @POST("/hopeApi/factoryEquity/activate")
    suspend fun activate(@FieldMap options: Map<String, String>): Response<HopeResponse<ActivateStatus>>

    @FormUrlEncoded
    @POST("/hopeApi/pay/activate")
    suspend fun payactivate(@FieldMap options: Map<String, String>): Response<HopeResponse<PayActivateStatus>>

    @FormUrlEncoded
    @POST("/hopeApi/factoryEquity/checkActivate")
    suspend fun checkActivate(@FieldMap options: Map<String, String>): Response<HopeResponse<List<ActivateStatus>>>

    @FormUrlEncoded
    @POST("/hopeApi/pay/paymentChannel")
    suspend fun paymentChannel(@FieldMap options: Map<String, String>): Response<HopeResponse<PayMentInfo2>>

    @FormUrlEncoded
    @POST("hopeApi/order/generateCode")
    suspend fun generateCode(@FieldMap options: Map<String, String>): Response<HopeResponse<CodeBean>>

    @FormUrlEncoded
    @POST("/hopeApi/pay/renew")
    suspend fun renew(@FieldMap options: Map<String, String>): Response<HopeResponse<String>>

    @FormUrlEncoded
    @POST("/hopeApi/order/checkOrder")
    suspend fun checkOrder(@FieldMap options: Map<String, String>): Response<HopeResponse<Int>>


    //======================================= 云端音乐 =======================================/
    //根据歌曲id从服务器拉取歌曲
    @FormUrlEncoded
    @POST("/hopeApi/music/loadMusic")
    suspend fun loadCloudSong(@FieldMap options: Map<String, String>): Response<HopeResponse<List<CloudSong>>>

    //上传蜻蜓歌曲给服务器，咪咕歌曲服务器有备份
    @FormUrlEncoded
    @POST("/hopeApi/music/save")
    suspend fun uploadCloudSong(@FieldMap options: Map<String, String>): Response<HopeResponse<String>>

    //======================================= 智能家居 =======================================/
    //加载智能家居 主屏数据
    @FormUrlEncoded
    @Headers("CONNECT_TIMEOUT:30")
    @POST("/hopeApi/smart/index")
    suspend fun smartIndex(@FieldMap options: Map<String, String>): Response<HopeResponse<UhomeIndexResponse>>

    //厂商列表
    @FormUrlEncoded
    @Headers("CONNECT_TIMEOUT:30")
    @POST("/hopeApi/smart/load")
    suspend fun loadPlatforms(@FieldMap options: Map<String, String>): Response<HopeResponse<List<UHomeThirdParty>>>

    //房间列表
    @FormUrlEncoded
    @Headers("CONNECT_TIMEOUT:30")
    @POST("/hopeApi/smart/roomes")
    suspend fun loadRoomes(@FieldMap options: Map<String, String>): Response<HopeResponse<List<UHomeRoom>>>

    //第三方设备列表
    @FormUrlEncoded
    @Headers("CONNECT_TIMEOUT:30")
    @POST("/hopeApi/smart/device")
    suspend fun smartDevice(@FieldMap options: Map<String, String>): Response<HopeResponse<UhomeThirdDeviceResponse>>

    //加载设备详情
    @FormUrlEncoded
    @Headers("CONNECT_TIMEOUT:30")
    @POST("/hopeApi/device/load")
    suspend fun deviceLoad2(@FieldMap options: Map<String, String>): Response<HopeResponse<UhomeDeviceInfoResponse>>

    //房间设备列表
    @FormUrlEncoded
    @Headers("CONNECT_TIMEOUT:30")
    @POST("/hopeApi/smart/rdevice")
    suspend fun smartRoomDevice(@FieldMap options: Map<String, String>): Response<HopeResponse<UhomeThirdDeviceResponse>>

    //第三方情景列表
    @FormUrlEncoded
    @Headers("CONNECT_TIMEOUT:30")
    @POST("/hopeApi/smart/scene")
    suspend fun smartScene(@FieldMap options: Map<String, String>): Response<HopeResponse<UhomeThirdSceneResponse>>

    @FormUrlEncoded
    @Headers("CONNECT_TIMEOUT:30")
    @POST("/hopeApi/smart/unbind")
    suspend fun smartUnbind(@FieldMap options: Map<String, String>): Response<HopeResponse<String>>

    @FormUrlEncoded
    @Headers("CONNECT_TIMEOUT:30")
    @POST("/hopeApi/smart/platform")
    suspend fun smartPlatform(@FieldMap options: Map<String, String>): Response<HopeResponse<List<UhomeThirdAccount>>>

    @FormUrlEncoded
    @Headers("CONNECT_TIMEOUT:30")
    @POST("/hopeApi/smart/state")
    suspend fun smartState(@FieldMap options: Map<String, String>): Response<HopeResponse<String>>

    @FormUrlEncoded
    @Headers("CONNECT_TIMEOUT:30")
    @POST("/hopeApi/smart/bind")
    suspend fun smartBind(@FieldMap options: Map<String, String>): Response<HopeResponse<String>>

    //设备执行操作
    @FormUrlEncoded
    @Headers("CONNECT_TIMEOUT:30")
    @POST("/hopeApi/smart/exec")
    suspend fun smartExec(@FieldMap options: Map<String, String>): Response<HopeResponse<Any>>

    //第三方情景执行
    @FormUrlEncoded
    @Headers("CONNECT_TIMEOUT:30")
    @POST("/hopeApi/smart/persce")
    suspend fun sceneExec(@FieldMap options: Map<String, String>): Response<HopeResponse<Any>>

    //推送or移除设备或情景
    @FormUrlEncoded
    @Headers("CONNECT_TIMEOUT:30")
    @POST("/hopeApi/smart/joinAndRemove")
    suspend fun joinAndRemove(@FieldMap options: Map<String, String>): Response<HopeResponse<JoinRmResponse>>

    //从主屏移除
    @FormUrlEncoded
    @Headers("CONNECT_TIMEOUT:30")
    @POST("/hopeApi/smart/hide")
    suspend fun smartHide(@FieldMap options: Map<String, String>): Response<HopeResponse<String>>
    //从主屏移除
    @FormUrlEncoded
    @Headers("CONNECT_TIMEOUT:30")
    @POST("/hopeApi/smart/removeAll")
    suspend fun smartHideAll(@FieldMap options: Map<String, String>): Response<HopeResponse<Any>>

    //语音设备列表
    @FormUrlEncoded
    @Headers("CONNECT_TIMEOUT:30")
    @POST("/hopeApi/smart/list")
    suspend fun smartList(@FieldMap options: Map<String, String>): Response<HopeResponse<DuiWordData>>

    //语音分类列表
    @FormUrlEncoded
    @POST("/hopeApi/catalog/listChild")
    suspend fun listChild(@FieldMap options: Map<String, String>): Response<HopeResponse<List<UHomeCate>>>

    @FormUrlEncoded
    @Headers("CONNECT_TIMEOUT:30")
    @POST("/hopeApi/smart/voice")
    suspend fun smartVoice(@FieldMap options: Map<String, String>): Response<HopeResponse<JsonObject>>

    @FormUrlEncoded
    @POST("/hopeApi/automation/insert")
    suspend fun automationInster(@FieldMap options: Map<String, String>): Response<HopeResponse<String>>


    @FormUrlEncoded
    @POST("/hopeApi/automatic/exec")
    suspend fun automationdeviceList(@FieldMap options: Map<String, String>): Response<HopeResponse<List<AutoExeDevice>>>

    @FormUrlEncoded
    @POST("hopeApi/scene/action")
    suspend fun automationAction(@FieldMap options: Map<String, String>): Response<HopeResponse<List<AutoAction>>>

    @FormUrlEncoded
    @POST("hopeApi/automation/list")
    suspend fun automationList(@FieldMap options: Map<String, String>): Response<HopeResponse<List<AutoBean>>>

    @FormUrlEncoded
    @POST("hopeApi/automatic/device")
    suspend fun automationDevice(@FieldMap options: Map<String, String>): Response<HopeResponse<List<AutoMaticDevice>>>
    @FormUrlEncoded
    @POST("hopeApi/automatic/trigger")
    suspend fun automationtrigger(@FieldMap options: Map<String, String>): Response<HopeResponse<List<AutoSensorAction>>>

    @FormUrlEncoded
    @POST("hopeApi/smart/template/index")
    suspend fun templateIndex(@FieldMap options: Map<String, String>):  Response<HopeResponse<UhomeTemplateResponse>>

    @FormUrlEncoded
    @POST("hopeApi/smart/template/page")
    suspend fun templatePage(@FieldMap options: Map<String, String>):  Response<HopeResponse<UhomeTemplatePageResponse>>

    @FormUrlEncoded
    @POST("hopeApi/smart/template/join")
    suspend fun templateJoin(@FieldMap options: Map<String, String>):  Response<HopeResponse<String>>

    //======================================= 移动 =======================================/
    @FormUrlEncoded
    @POST("/hopeApi/sequenceInfo/cmei")
    suspend fun cmei(@FieldMap options: Map<String, String>): Response<HopeResponse<Cmei>>

    //======================================= 日志上传 =======================================/
    @Multipart
    @POST("/hopeApi/gateway/insert")
    suspend fun uploadLog(@PartMap options: Map<String,@JvmSuppressWildcards RequestBody>, @Part file: MultipartBody.Part): Response<HopeResponse<String>>


    @GET("contents/device/update?")
    suspend fun deviceUpdate(@Query("model") model: String, @Query("version") version: String, @Query("software") software: String, @Query("signature") signature: String,@Query("deviceSn") deviceSn: String): Response<HopeResponse<FirmInfo>>

    @GET("OTA/{device}/updateinfo.json")
    suspend fun basicOtaUpdate(@Path("device") device: String): OtaUpdateInfo?

    @FormUrlEncoded
    @POST("/hopeApi/device/devunbind")
    suspend fun devUnbind(@FieldMap options: Map<String, String>): Response<HopeResponse<String>>

    //======================================= 教育 =======================================/
    @FormUrlEncoded
    @POST("/hopeApi/resource/listResourceCata")
    suspend fun eduCate(@FieldMap options: Map<String, String>): Response<HopeResponse<List<EduCate>>>

    @FormUrlEncoded
    @POST("/hopeApi/resource/listResourceItem")
    suspend fun eduCourse(@FieldMap options: Map<String, String>): Response<HopeResponse<List<EduCourse>>>

    @FormUrlEncoded
    @POST("/hopeApi/resource/loadResourceUrl")
    suspend fun eduCourseResource(@FieldMap options: Map<String, String>): Response<HopeResponse<EduResource>>

    @FormUrlEncoded
    @POST("/hopeApi/device/place")
    suspend fun devicePlace(@FieldMap options: Map<String, String>): Response<HopeResponse<String>>

    //======================================= 低音炮 =======================================/

    @FormUrlEncoded
    @POST("/hopeApi/device/initEffect")
    suspend fun initEffect(@FieldMap options: Map<String, String>): Response<HopeResponse<String>>

    @FormUrlEncoded
    @POST("/hopeApi/device/saveEffect")
    suspend fun saveEffect(@FieldMap options: Map<String, String>): Response<HopeResponse<String>>

    @FormUrlEncoded
    @POST("/hopeApi/dui/familyBind")
    suspend fun familyBind(@FieldMap options: Map<String, String>): Response<HopeResponse<String>>

    @FormUrlEncoded
    @POST("/hopeApi/dui/familyUnbind")
    suspend fun familyUnbind(@FieldMap options: Map<String, String>): Response<HopeResponse<String>>

    @FormUrlEncoded
    @POST("/hopeApi/device/load")
    suspend fun deviceLoad(@FieldMap options: Map<String, String>): Response<HopeResponse<UhomeDevice>>

    @FormUrlEncoded
    @POST("/hopeApi/device/reportGrant")
    suspend fun reportGrant(@FieldMap options: Map<String, String>): Response<HopeResponse<String>>


    @Multipart
    @POST("/hopeApi/device/grant")
    suspend fun grant(@PartMap options: Map<String, @JvmSuppressWildcards RequestBody>): Response<HopeResponse<GrantBean>>


    /**
     * 自定义情景话术
     */
    @Multipart
    @POST("/hopeApi/voiceMean/otherDetail")
    suspend fun getSpeechSelf(@PartMap options: Map<String, @JvmSuppressWildcards RequestBody>): Response<HopeResponse<List<SpeechSelfResponse>>>


    @Multipart
    @POST("/hopeApi/resource/signature")
    suspend fun signature(@PartMap options: Map<String, @JvmSuppressWildcards RequestBody>): Response<HopeResponse<SignBeanRespone>>

}