package com.sc.lib_frame.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.sc.lib_frame.constants.HopeConstants
import com.sc.lib_frame.utils.HopeUtils
import timber.log.Timber
import java.util.concurrent.CopyOnWriteArrayList

/**
 *Created by ywr on 2021/11/11 15:28
 */
class NetworkCallback private constructor() : ConnectivityManager.NetworkCallback() {
    companion object {
        @Volatile
        private var instance: NetworkCallback? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: NetworkCallback().also { instance = it }
            }
    }


    private val networkCallbackImplList = CopyOnWriteArrayList<NetworkCallbackModule>()

    fun registNetworkCallback(networkCallbackModule: NetworkCallbackModule) {
        networkCallbackImplList.add(networkCallbackModule)
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        Timber.i("NetworkCallback _onAvailable: 网络已连接")


        HopeConstants.IS_NETWORK_AVAILABLE = true
        HopeUtils.getIP()?.let {
            if (HopeConstants.LOCAL_IP != it) {
                HopeConstants.LOCAL_IP = it
            }
        }
        networkCallbackImplList.map {
            it.onAvailable(network)
        }
    }


    override fun onLost(network: Network) {
        super.onLost(network)
        Timber.e("NetworkCallback onLost: 网络已断开")
        HopeConstants.IS_NETWORK_AVAILABLE = false


        networkCallbackImplList.map {
            it.onLost(network)
        }
    }

    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    Timber.i("NetworkCallback onCapabilitiesChanged: 网络类型为wifi")
                }
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    Timber.i("NetworkCallback onCapabilitiesChanged: 网络类型为以太网")
                }
                else -> {
                    Timber.i("NetworkCallback onCapabilitiesChanged: 其他网络")
                }
            }
        }


        networkCallbackImplList.map {
            it.onCapabilitiesChanged(network, networkCapabilities)
        }
    }
}