package com.illusory.tmp_launcher

import android.net.Network
import android.net.NetworkCapabilities
import com.illusory.tmp_launcher.databinding.ActivityMainBinding
import com.illusory.tmp_launcher.vm.MainViewModel
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
        binding.app1Ly.setOnClickListener { viewModel.openMainAppItem(this, 0) }
        binding.app2Ly.setOnClickListener { viewModel.openMainAppItem(this, 1) }
        binding.app3Ly.setOnClickListener { viewModel.openMainAppItem(this, 2) }
        binding.app4Ly.setOnClickListener { viewModel.openMainAppItem(this, 3) }
        networkCallback.registNetworkCallback(networkCallbackModule)
    }

    override fun initData() {
        viewModel.initData()
        viewModel.initAppData(this) {
            if (MainViewModel.AppList.size != 4) return@initAppData
            this.runOnUiThread {
                binding.app1Tv.text = MainViewModel.AppList[0].name
                binding.app2Tv.text = MainViewModel.AppList[1].name
                binding.app3Tv.text = MainViewModel.AppList[2].name
                binding.app4Tv.text = MainViewModel.AppList[3].name
            }
        }
    }

    override fun linkViewModel() {

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    var networkCallbackModule: NetworkCallbackModule = object : NetworkCallbackModule {
        override fun onAvailable(network: Network?) {
            onNetStateChange(1)
        }

        override fun onLost(network: Network?) {
            onNetStateChange(0)
        }

        override fun onCapabilitiesChanged(network: Network?, networkCapabilities: NetworkCapabilities) {
            if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                when {
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        Timber.i( "onCapabilitiesChanged: 网络类型为wifi")
                        onNetStateChange(1)
                    }
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        Timber.i( "onCapabilitiesChanged: 网络类型为以太网")
                        onNetStateChange(2)
                    }
                    else -> {
                        Timber.i( "onCapabilitiesChanged: 其他网络")
                        onNetStateChange(1)
                    }
                }
            }
        }
    }

    fun onNetStateChange(type: Int) {
        this.runOnUiThread {
            when(type) {
                0 -> {
                    binding.netIv.setImageResource(R.drawable.ic_no_network)
                }
                2 -> {
                    binding.netIv.setImageResource(R.drawable.ic_network)
                }
                else -> {
                    binding.netIv.setImageResource(R.drawable.ic_wifi)
                }
            }
        }
    }

}
