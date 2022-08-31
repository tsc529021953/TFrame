package com.lib.frame.dynconfig

import android.annotation.SuppressLint
import android.content.Context
import com.lib.network.BuildConfig
import com.lib.network.networkconfig.TraySpManager
import timber.log.Timber

/**
 *Created by ywr on 2021/2/3 17:16
 * 需要动态配置，并不需要写入sp内的文件
 */
class HopeDynConfig() {
    private var tary: TraySpManager? =null
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var mConfig: HopeDynConfig? = null

        @JvmStatic
        val instence: HopeDynConfig
            get() {
                if (mConfig == null) {
                    synchronized(HopeDynConfig::class.java) {
                        if (mConfig == null) {
                            mConfig = HopeDynConfig()
                        }
                    }
                }
                return mConfig!!
            }
    }

    fun initDynConfig(context: Context){
     this.tary = TraySpManager(context)
    }


    fun getHttpIp(): String?{
//        Timber.d("HopeDyn getHttpIp:${BuildConfig.HTTP_IP}")
       return tary?.getString("HTTP_IP","127.0.0.1")
    }
    fun getHttpPort(): Int? {
//        Timber.d("HopeDyn getHttpPort:${BuildConfig.HTTP_PORT}")
        return tary?.getInt("HTTP_PORT",8085)
    }
    fun socketIp(): String? = tary?.getString("SOCKET_IP","127.0.0.1")
    // BuildConfig.SOCKET_PORT
    fun socketPort(): Int? = tary?.getInt("SOCKET_PORT", 8085)
    fun configHttp(httpIp: String, port: Int) {
       tary?.putString("HTTP_IP",httpIp)
        tary?.putInt("HTTP_PORT",port)
    }

    fun configTcp(socketIp: String, socketPort: Int) {
        tary?.putString("SOCKET_IP",socketIp)
        tary?.putInt("SOCKET_PORT",socketPort)
    }


}