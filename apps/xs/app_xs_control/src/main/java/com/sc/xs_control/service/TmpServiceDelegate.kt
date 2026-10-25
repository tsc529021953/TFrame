package com.sc.xs_control.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.databinding.ObservableBoolean
import com.sc.xs_control.inter.ITmpService
import timber.log.Timber

/**
 * @author  tsc
 * @date  2026/10/8
 * @version 0.0.0-1
 * @description 指令服务代理
 */
class TmpServiceDelegate : ITmpService {

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

    override fun reBuild() {
        mService?.reBuild()
    }

    override fun getNetObs(): ObservableBoolean? {
        return mService?.getNetObs()
    }

    override fun sendDeviceAllCmd(on: Boolean) {
        mService?.sendDeviceAllCmd(on)
    }

    override fun sendPcCmd(on: Boolean) {
        mService?.sendPcCmd(on)
    }

    override fun sendAioCmd(on: Boolean) {
        mService?.sendAioCmd(on)
    }

    override fun sendPcMediaCmd(isPlay: Boolean) {
        mService?.sendPcMediaCmd(isPlay)
    }

    override fun sendTvMediaCmd(index: Int, isPlay: Boolean) {
        mService?.sendTvMediaCmd(index, isPlay)
    }

    override fun sendPcVoiceCmd(mute: Boolean) {
        mService?.sendPcVoiceCmd(mute)
    }

    override fun sendTvVoiceCmd(index: Int, mute: Boolean) {
        mService?.sendTvVoiceCmd(index, mute)
    }

    override fun sendTvCmd(index: Int, on: Boolean) {
        mService?.sendTvCmd(index, on)
    }

    override fun sendLightAllCmd(on: Boolean) {
        mService?.sendLightAllCmd(on)
    }

    override fun sendLightCeilingAllCmd(on: Boolean) {
        mService?.sendLightCeilingAllCmd(on)
    }

    override fun sendLightCeilingAreaCmd(index: Int, on: Boolean) {
        mService?.sendLightCeilingAreaCmd(index, on)
    }

    override fun sendLightWallAllCmd(on: Boolean) {
        mService?.sendLightWallAllCmd(on)
    }

    override fun sendLightWallAreaCmd(index: Int, on: Boolean) {
        mService?.sendLightWallAreaCmd(index, on)
    }

    override fun sendLightSpaceAllCmd(on: Boolean) {
        mService?.sendLightSpaceAllCmd(on)
    }

    override fun sendLightSpaceAreaCmd(index: Int, on: Boolean) {
        mService?.sendLightSpaceAreaCmd(index, on)
    }

    private fun bindService(context: Context) {
        isBind = context.bindService(Intent(context, TmpServiceImpl::class.java), mConnection, Context.BIND_AUTO_CREATE)
        Timber.i("XCTAG bindService $isBind")
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
