package com.lib.network.api

import com.lib.network.BuildConfig
import com.lib.network.vo.response.WeatherResponse
import retrofit2.Response
import retrofit2.http.*

const val AMAP_API_URL = "https://api.map.baidu.com"
var AMAP_API_KEY =
//    if (BuildConfig.CPU == "a133") {
//    "VevKE22APg7FMdehp7IxpUe3zblggKYa"
//} else if (BuildConfig.CPU == "a83t"){
//    "WDPYx8HSXgWoAZZSV2MdfFnadqlRKYO5"
//} else{
    "poIl3bnvuHdZ9nPjAyYZIe4wHGhbVmBP"
//}
var AMAP_API_MCODE =
//    if (BuildConfig.CPU == "a133") {
//    "F6:08:8C:ED:B9:9A:EC:82:A7:7D:F3:F9:01:97:06:BE:C0:65:E4:9A;com.nbhope.hopelauncher"
//}else if (BuildConfig.CPU == "a83t"){
//    "27:19:6E:38:6B:87:5E:76:AD:F7:00:E7:EA:84:E4:C6:EE:E3:3D:FA;com.nbhope.hopelauncher"
//}else{
    "41:79:1C:9B:8F:AF:15:E1:AC:D5:AA:F5:92:10:FD:42:46:7D:82:77;com.nbhope.hopelauncher"
//}

interface AmapApiService {

    @GET("weather/v1/")
    suspend fun weather(
        @Query("district_id") districtId: String,
        @Query("data_type") type: String? = "now",
        @Query("ak") ak: String? = AMAP_API_KEY,
        @Query(value = "mcode", encoded = true) mcode: String? = AMAP_API_MCODE
    ): Response<WeatherResponse>
}