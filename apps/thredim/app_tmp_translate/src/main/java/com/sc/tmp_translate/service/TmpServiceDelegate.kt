package com.sc.tmp_translate.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableFloat
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.sc.tmp_translate.bean.TransRecordBean
import com.sc.tmp_translate.constant.MessageConstant
import com.sc.tmp_translate.inter.ITmpService
import com.sc.tmp_translate.utils.record.PcmAudioPlayer
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

    override fun reBuild() {
        mService?.reBuild()
    }

    override fun getFontSizeObs(): ObservableFloat? {
        return mService?.getFontSizeObs()
    }

    override fun setFontSize(size: Float) {
        mService?.setFontSize(size)
    }

    override fun getTransLangObs(): ObservableField<String>? {
        return mService?.getTransLangObs()
    }

    override fun setTransLang(lang: String) {
        mService?.setTransLang(lang)
    }

    override fun getMoreDisplayObs(): ObservableBoolean? {
        return mService?.getMoreDisplayObs()
    }

    override fun setMoreDisplay(more: Boolean) {
        mService?.setMoreDisplay(more)
    }

    override fun getTextPlayObs(): ObservableBoolean? {
        return mService?.getTextPlayObs()
    }

    override fun setTextPlay(play: Boolean) {
        mService?.setTextPlay(play)
    }

    override fun getTranslatingObs(): ObservableBoolean? {
        return mService?.getTranslatingObs()
    }

    override fun setTranslating(play: Boolean) {
        mService?.setTranslating(play)
    }

    override fun notifyTransPage(trans: Boolean) {
        mService?.notifyTransPage(trans)
    }

    override fun getTransStateObs(index: Int): ObservableBoolean? {
        return mService?.getTransStateObs(index)
    }

    override fun setTransState(play: Boolean,index: Int) {
        mService?.setTransState(play, index)
    }

    override fun setTransState(index: Int) {
        mService?.setTransState(index)
    }

    override fun getTransPlayObs(): ObservableField<String>? {
        return mService?.getTransPlayObs()
    }

    override fun getPlayStatusObs(): ObservableField<PcmAudioPlayer.State>? {
        return mService?.getPlayStatusObs()
    }

    override fun setTransPlay(bean: TransRecordBean) {
        mService?.setTransPlay(bean)
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
            Timber.d("onServiceConnected trans $mService")
            LiveEBUtil.post(RemoteMessageEvent(MessageConstant.CMD_BIND_SUCCESS, ""))
        }
    }
}
