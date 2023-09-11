package com.sc.nft.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.JsonObject
import com.nbhope.lib_frame.arouterpath.RouterPath
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
@Route(path = RouterPath.service_nft, name = "本地网关服务")
class MainServiceDelegate : MainService {
    private var serviceImpl: MainServiceImpl? = null

    override fun reTimer() {
        serviceImpl?.reTimer()
    }

    override fun stopTimer() {
        serviceImpl?.stopTimer()
    }

    override fun init(context: Context?) {
        Timber.d("LTAG UHL init")
        if (context == null) {
            Timber.e("context is null")
        } else {
//            GlobalScope.launch(Dispatchers.IO) {
//                delay(5000)
//
//            }
            bindService(context)
        }
    }

    private fun bindService(context: Context) {
        val res =
            context.bindService(Intent(context, MainServiceImpl::class.java), mConnection, Context.BIND_AUTO_CREATE)
        Timber.i("NTAG res $res")
    }

    private val mConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Timber.d("NTAG onServiceDisconnected")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            serviceImpl = (service as MainServiceImpl.MainBinder).getSpeechService()
            Timber.d("NTAG onServiceConnected")
//            serviceImpl?.init()
        }
    }

}