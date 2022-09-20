package com.sc.lib_local_device.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.JsonObject
import com.nbhope.lib_frame.arouterpath.RouterPath
import com.nbhope.phmina.base.HaloType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * @author  tsc
 * @date  2022/9/1 16:31
 * @version 0.0.0-1
 * @description
 */
@Route(path = RouterPath.uhome_local, name = "本地网关服务")
class MainServiceDelegate : MainService {
    private var serviceImpl: MainServiceImpl? = null

    override fun onChangeType() {
        serviceImpl?.onChangeType()
    }

    override fun connectServer(ip: String) {
        serviceImpl?.connectServer(ip)
    }

    override fun getStatus(type: HaloType): Boolean {
        return serviceImpl?.getStatus(type) == true
    }

    override fun sendMulMessage(msg: String) {
        serviceImpl?.sendMulMessage(msg)
    }

    override fun sendClientMessage(msg: String) {
        serviceImpl?.sendClientMessage(msg)
    }


    override fun init(context: Context?) {
        Timber.d("LTAG UHL init")
        if (context == null) {
            Timber.e("context is null")
        } else {
            GlobalScope.launch(Dispatchers.IO) {
                delay(5000)
                bindService(context)
            }
        }
    }

    private fun bindService(context: Context) {
        context.bindService(Intent(context, MainServiceImpl::class.java), mConnection, Context.BIND_AUTO_CREATE)
    }

    private val mConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            serviceImpl = (service as MainServiceImpl.MainBinder).getSpeechService()
            Timber.d("LTAG onServiceConnected")
        }
    }

}