package com.xs.xs_by

import android.net.Network
import android.net.NetworkCapabilities
import com.xs.xs_by.databinding.ActivityMainBinding
import com.xs.xs_by.vm.MainViewModel
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.network.NetworkCallback
import com.nbhope.lib_frame.network.NetworkCallbackModule
import timber.log.Timber
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2024/4/25 17:47
 * @version 0.0.0-1
 * @description
 * 1. wifi/line
 * 2. timer
 * 3. applist
 */
class MainActivity: BaseBindingActivity<ActivityMainBinding, MainViewModel>() {

    override var layoutId: Int = R.layout.activity_main

    @Inject
    override lateinit var viewModel: MainViewModel

    @Inject
    lateinit var networkCallback: NetworkCallback

    override fun subscribeUi() {
//        networkCallback.registNetworkCallback(networkCallbackModule)
        viewModel.startAnimation(binding.textTv)
    }

    override fun initData() {
        viewModel.initData()
    }

    override fun linkViewModel() {

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopAnimation(binding.textTv)
    }

//    var networkCallbackModule: NetworkCallbackModule = object : NetworkCallbackModule {
//        override fun onAvailable(network: Network?) {
//            onNetStateChange(1)
//        }
//
//        override fun onLost(network: Network?) {
//            onNetStateChange(0)
//        }
//
//        override fun onCapabilitiesChanged(network: Network?, networkCapabilities: NetworkCapabilities) {
//            if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
//                when {
//                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
//                        Timber.i( "onCapabilitiesChanged: 网络类型为wifi")
//                        onNetStateChange(1)
//                    }
//                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
//                        Timber.i( "onCapabilitiesChanged: 网络类型为以太网")
//                        onNetStateChange(2)
//                    }
//                    else -> {
//                        Timber.i( "onCapabilitiesChanged: 其他网络")
//                        onNetStateChange(1)
//                    }
//                }
//            }
//        }
//    }
//
//    fun onNetStateChange(type: Int) {
//        this.runOnUiThread {
//            when(type) {
//                0 -> {
//                    binding.netIv.setImageResource(R.drawable.ic_no_network)
//                }
//                2 -> {
//                    binding.netIv.setImageResource(R.drawable.ic_network)
//                }
//                else -> {
//                    binding.netIv.setImageResource(R.drawable.ic_wifi)
//                }
//            }
//        }
//    }

}
