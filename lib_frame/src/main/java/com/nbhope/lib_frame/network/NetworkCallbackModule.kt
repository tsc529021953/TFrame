package com.nbhope.lib_frame.network

import android.net.Network
import android.net.NetworkCapabilities

/**
 *Created by ywr on 2021/11/11 15:31
 */
interface NetworkCallbackModule {
    /**
     * 网络可用时
     */
    fun onAvailable(network: Network?)

    /**
     * 网络丢失时
     */
    fun onLost(network: Network?)

    /**
     * 网络类型改变时
     */
    fun onCapabilitiesChanged(network: Network?, networkCapabilities: NetworkCapabilities)
}