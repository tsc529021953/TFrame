package com.sc.tmp_cw.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.sc.tmp_cw.constant.MessageConstant
import com.sc.tmp_cw.inter.ITmpService
import timber.log.Timber

/**
 * @author  tsc
 * @date  2024/4/12 13:29
 * @version 0.0.0-1
 * @description
 */
open class TmpServiceDelegate {

    protected var mService: TmpServiceImpl? = null

    private var isBind = false

    companion object {
        private var instance: TmpServiceDelegate? = null

        fun getInstance(): TmpServiceDelegate? {
            if (instance == null)
                instance = TmpServiceDelegate()
            return instance;
        }

        fun service(): ITmpService? {
            if (instance == null)
                instance = TmpServiceDelegate()
            return instance!!.mService;
        }
    }

    fun init(content: Context) {
        bindService(content)
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
            LiveEBUtil.post(RemoteMessageEvent(MessageConstant.SERVICE_INIT_SUCCESS, ""))
        }
    }
}
