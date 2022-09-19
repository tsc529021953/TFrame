
package com.nbhope.lib_frame.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.nbhope.lib_frame.constants.HopeConstants
import com.nbhope.lib_frame.utils.HopeUtils
import timber.log.Timber
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by zhouwentao on 2020/4/27.
 */

@Singleton
class NetworkCallback @Inject constructor(): ConnectivityManager.NetworkCallback() {

    private val networkCallbackImplList  = CopyOnWriteArrayList<NetworkCallbackModule>()

    fun registNetworkCallback(networkCallbackModule: NetworkCallbackModule){
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
        Timber.e( "NetworkCallback onLost: 网络已断开")
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
                    Timber.i( "NetworkCallback onCapabilitiesChanged: 网络类型为wifi")
                }
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    Timber.i( "NetworkCallback onCapabilitiesChanged: 网络类型为以太网")
                }
                else -> {
                    Timber.i( "NetworkCallback onCapabilitiesChanged: 其他网络")
                }
            }
        }


        networkCallbackImplList.map {
            it.onCapabilitiesChanged(network,networkCapabilities)
        }
    }

}
