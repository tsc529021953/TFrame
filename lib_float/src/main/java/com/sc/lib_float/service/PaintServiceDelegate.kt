package com.sc.lib_float.service

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.sc.lib_float.inter.IPaintService
import timber.log.Timber

/**
 * @author  tsc
 * @date  2024/4/1 14:26
 * @version 0.0.0-1
 * @description
 */
class PaintServiceDelegate : IPaintService {

    private var mService: PaintServiceImpl? = null

    private var isBind = false

    companion object{
        private var _instance: IPaintService? = null

        fun getInstance(): IPaintService {
            if (_instance == null)
                _instance = PaintServiceDelegate()
            return _instance!!;
        }
    }

    override fun init(content: Context) {
        bindService(content)
    }

    private fun bindService(context: Context) {
        isBind = context.bindService(Intent(context, PaintServiceImpl::class.java), mConnection, Context.BIND_AUTO_CREATE)
    }

    private val mConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Timber.d("onServiceDisconnected ComponentName $name")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mService = (service as PaintServiceImpl.PaintBinder).getPaintService()
            Timber.d("onServiceConnected")
        }
    }

    override fun showFloat() {

    }

    override fun hideFloat(delayMillis: Long) {

    }
}
