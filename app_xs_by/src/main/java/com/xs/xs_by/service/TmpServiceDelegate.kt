package com.xs.xs_by.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.xs.xs_by.inter.ITmpService
import timber.log.Timber

/**
 * @author  tsc
 * @date  2024/4/12 13:29
 * @version 0.0.0-1
 * @description
 */
class TmpServiceDelegate: ITmpService {

    private var mService: TmpServiceImpl? = null

    private var isBind = false

    companion object {
        private var _instance: ITmpService? = null

        fun getInstance(): ITmpService {
            if (_instance == null)
                _instance = TmpServiceDelegate()
            return _instance!!;
        }
    }

    override fun init(content: Context) {
        bindService(content)
    }

    override fun showFloat() {
        mService?.showFloat()
    }

    override fun hideFloat(delayMillis: Long) {
        mService?.hideFloat(delayMillis)
    }

    override fun write(msg: String) {
        mService?.write(msg)
    }

    override fun write2(msg: String) {
        mService?.write2(msg)
    }

    override fun reBuild() {
        mService?.reBuild()
    }

    private fun bindService(context: Context) {
        isBind = context.bindService(Intent(context, TmpServiceImpl::class.java), mConnection, Context.BIND_AUTO_CREATE)
        Timber.i("TTAG bindService $isBind")
    }

    private val mConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Timber.d("onServiceDisconnected ComponentName $name")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mService = (service as TmpServiceImpl.BaseBinder).getPaintService()
            Timber.d("onServiceConnected")
        }
    }
}
