package com.sc.lib_weather

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.webkit.ValueCallback
import com.sc.lib_weather.bean.WeatherBean
import com.sc.lib_weather.utils.WeatherUtil
//import fr.quentinklein.slt.LocationTracker
//import fr.quentinklein.slt.ProviderError
import timber.log.Timber
import kotlin.collections.ArrayList


/**
 * @author  tsc
 * @date  2022/8/1 10:43
 * @version 0.0.0-1
 * @description
 */
class WeatherListener {

    companion object {
        const val TAG = "WEATHER_TAG"

        val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        private var instance: WeatherListener? = null
        fun getInstance(): WeatherListener {
            if (instance == null) {
                instance = WeatherListener()
            }
            return instance!!
        }
    }

//    var locationTracker: LocationTracker? = null

    var weatherUtil: WeatherUtil? = null

    var cbList: ArrayList<ValueCallback<WeatherBean>> = ArrayList()

    init {
        initLocation()
    }

    private fun initLocation() {
        cbList.clear()
        // 首先获取定位信息
//        locationTracker = LocationTracker(
//            5 * 60 * 1000.toLong(),
//            100f,
//            true,
//            true,
//            true
//        )
//        locationTracker?.addListener(object : LocationTracker.Listener {
//            override fun onLocationFound(location: Location) {
//                Timber.e("$TAG location $location")
//            }
//
//            override fun onProviderError(providerError: ProviderError) {
//                Timber.e("$TAG $providerError")
//            }
//
//        })

    }

    fun dispose(){
        Timber.i("$TAG 释放天气模块")
        cbList.clear()
//        locationTracker?.stopListening()
    }

    fun registerListener(context: Context, callback: ValueCallback<WeatherBean>) {
        // 添加
        if (!cbList.contains(callback)) cbList.add(callback)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && context.checkSelfPermission(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED

            ) {
                return
            }
        }
        // 添加回调函数
//        if (locationTracker?.isListening != false) return
//        locationTracker?.startListening(context)
        return
    }

    fun unregisterListener(callback: ValueCallback<WeatherBean>) {
        if (cbList.contains(callback))
            cbList.remove(callback)
    }

}